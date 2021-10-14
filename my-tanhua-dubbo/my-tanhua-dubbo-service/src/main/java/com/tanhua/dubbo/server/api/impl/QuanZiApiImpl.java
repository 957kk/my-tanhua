package com.tanhua.dubbo.server.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.tanhua.dubbo.server.api.IdService;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.api.TimeLineService;
import com.tanhua.dubbo.server.enums.IdType;
import com.tanhua.dubbo.server.pojo.Album;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.pojo.TimeLine;
import com.tanhua.dubbo.server.vo.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

@Service(version = "1.0.0")
@Slf4j
public class QuanZiApiImpl implements QuanZiApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdService idService;

    @Autowired
    private TimeLineService timeLineService;



    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public PageInfo<Publish> queryPublishList(Long userId, Integer page, Integer pageSize) {
        //分析：查询好友的动态，实际上查询时间线表
        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        Pageable pageable = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Order.desc("date")));

        Query query = new Query().with(pageable);
        List<TimeLine> timeLineList = this.mongoTemplate.find(
                query,
                TimeLine.class,
                "quanzi_time_line_" + userId);
        if (CollUtil.isEmpty(timeLineList)) {
            //没有查询到数据
            return pageInfo;
        }

        //获取时间线列表中的发布id的列表
        List<Object> ids = CollUtil.getFieldValues(timeLineList, "publishId");

        //根据动态id查询动态列表
        Query queryPublish = Query.query(Criteria.where("id").in(ids))
                .with(Sort.by(Sort.Order.desc("created")));

        List<Publish> publishList = this.mongoTemplate.find(queryPublish, Publish.class);
        pageInfo.setRecords(publishList);
        return pageInfo;
    }

    /**
     * 发布动态
     *
     * @param publish
     * @return 发布成功返回动态id
     */
    @Override
    public String savePublish(Publish publish) {
        //对publish对象校验
        if (!ObjectUtil.isAllNotEmpty(publish.getText(), publish.getUserId())) {
            //发布失败
            return null;
        }

        //设置主键id
        publish.setId(ObjectId.get());

        try {
            //设置自增长的pid
            publish.setPid(this.idService.createId(IdType.PUBLISH));
            publish.setCreated(System.currentTimeMillis());

            //写入到publish表中
            this.mongoTemplate.save(publish);

            //写入相册表
            Album album = new Album();
            album.setId(ObjectId.get());
            album.setCreated(System.currentTimeMillis());
            album.setPublishId(publish.getId());

            this.mongoTemplate.save(album, "quanzi_album_" + publish.getUserId());

            //写入好友的时间线表（异步写入）
            this.timeLineService.saveTimeLine(publish.getUserId(), publish.getId());
        } catch (Exception e) {
            //TODO 需要做事务的回滚，Mongodb的单节点服务，不支持事务，对于回滚我们暂时不实现了
            log.error("发布动态失败~ publish = " + publish, e);
        }

        return publish.getId().toHexString();
    }

    @Override
    public PageInfo<Publish> queryRecommendPublishList(Long userId, Integer page, Integer pageSize) {
        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        // 查询推荐结果数据
        String key = "QUANZI_PUBLISH_RECOMMEND_" + userId;
        String data = this.redisTemplate.opsForValue().get(key);
        if (StrUtil.isEmpty(data)) {
            return pageInfo;
        }

        //查询到的pid进行分页处理
        List<String> pids = StrUtil.split(data, ',');
        //计算分页
        //[0, 10]
        int[] startEnd = PageUtil.transToStartEnd(page - 1, pageSize);
        int startIndex = startEnd[0]; //开始
        int endIndex = Math.min(startEnd[1], pids.size()); //结束

        List<Long> pidLongList = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            pidLongList.add(Long.valueOf(pids.get(i)));
        }

        if (CollUtil.isEmpty(pidLongList)) {
            //没有查询到数据
            return pageInfo;
        }

        //根据pid查询publish
        Query query = Query.query(Criteria.where("pid").in(pidLongList))
                .with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishList = this.mongoTemplate.find(query, Publish.class);
        if (CollUtil.isEmpty(publishList)) {
            //没有查询到数据
            return pageInfo;
        }

        pageInfo.setRecords(publishList);
        return pageInfo;
    }
}
