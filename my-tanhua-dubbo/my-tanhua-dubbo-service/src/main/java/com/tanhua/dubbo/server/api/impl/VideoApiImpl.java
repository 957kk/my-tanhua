package com.tanhua.dubbo.server.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.mongodb.client.result.DeleteResult;
import com.tanhua.dubbo.server.api.IdService;
import com.tanhua.dubbo.server.api.VideoApi;
import com.tanhua.dubbo.server.enums.IdType;
import com.tanhua.dubbo.server.pojo.FollowUser;
import com.tanhua.dubbo.server.pojo.Video;
import com.tanhua.dubbo.server.vo.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-16 18:53
 **/
@Service(version = "1.0.0")
@Slf4j
public class VideoApiImpl implements VideoApi {
    @Autowired
    private IdService idService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String QUANZI_VIDEO_RECOMMEND_ = "QUANZI_VIDEO_RECOMMEND_";
    private static final String VIDEO_FOLLOW_USER_KEY_PREFIX = "VIDEO_FOLLOW_USER_";

    @Override
    public String saveVideo(Video video) {
        try {
            if (!ObjectUtil.isAllNotEmpty(video.getVideoUrl(), video.getPicUrl(), video.getUserId())) {
                return null;
            }
            video.setVid(idService.createId(IdType.VIDEO));
            video.setId(ObjectId.get());
            this.mongoTemplate.save(video);
            return video.getId().toHexString();
        } catch (Exception e) {
            log.error("保存video失败，video==" + video, e);
        }
        return null;
    }

    @Override
    public PageInfo<Video> queryVideoList(Long userId, Integer page, Integer pageSize) {
        PageInfo<Video> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        //从redis中获取推荐视频的数据
        String redisKey = QUANZI_VIDEO_RECOMMEND_ + userId;
        String redisData = this.redisTemplate.opsForValue().get(redisKey);
        List<Long> vids = new ArrayList<>();
        int recommendCount = 0;
        if (StrUtil.isNotEmpty(redisData)) {
            //手动分页查询数据
            List<String> vidList = StrUtil.split(redisData, ',');
            //计算分页
            //[0, 10]
            int[] startEnd = PageUtil.transToStartEnd(page - 1, pageSize);
            int startIndex = startEnd[0];
            int endIndex = Math.min(startEnd[1], vidList.size());

            for (int i = startIndex; i < endIndex; i++) {
                vids.add(Convert.toLong(vidList.get(i)));
            }
            recommendCount = vidList.size();
        }

        if (CollUtil.isEmpty(vids)) {
            //没有推荐或前面推荐已经查询完毕，查询系统的视频数据
            //计算前面的推荐视频页数
            int totalPage = PageUtil.totalPage(recommendCount, pageSize);

            PageRequest pageRequest = PageRequest.of(page - totalPage - 1, pageSize, Sort.by(Sort.Order.desc("created")));
            Query query = new Query().with(pageRequest);
            List<Video> videoList = this.mongoTemplate.find(query, Video.class);
            pageInfo.setRecords(videoList);
            return pageInfo;
        }
        //根据vid查询对应的视频数据了
        Query query = Query.query(Criteria.where("vid").in(vids));
        List<Video> videoList = this.mongoTemplate.find(query, Video.class);
        pageInfo.setRecords(videoList);

        return pageInfo;
    }

    @Override
    public Video queryVideoById(String videoId) {
        return this.mongoTemplate.findById(new ObjectId(videoId), Video.class);
    }

    @Override
    public Boolean followUser(Long userId, Long followUserId) {
        if (!ObjectUtil.isAllNotEmpty(userId, followUserId)) {
            return false;
        }

        try {
            //需要将用户的关注列表，保存到redis中，方便后续的查询
            //使用redis的hash结构
            if (this.isFollowUser(userId, followUserId)) {
                return false;
            }
            FollowUser followUser = new FollowUser();
            followUser.setId(ObjectId.get());
            followUser.setUserId(userId);
            followUser.setFollowUserId(followUserId);
            followUser.setCreated(System.currentTimeMillis());

            this.mongoTemplate.save(followUser);
            //保存数据到redis
            String redisKey = this.getVideoFollowUserKey(userId);
           // String hashKey = String.valueOf(followUserId);
            this.redisTemplate.opsForSet().add(redisKey,String.valueOf(followUserId));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean disFollowUser(Long userId, Long followUserId) {
        if (!ObjectUtil.isAllNotEmpty(userId, followUserId)) {
            return false;
        }
        if (!this.isFollowUser(userId, followUserId)) {
            return false;
        }
        //取消关注，删除关注数据即可
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("followUserId").is(followUserId)
        );
        DeleteResult result = this.mongoTemplate.remove(query, FollowUser.class);
        if (result.getDeletedCount() > 0) {
            //同时删除redis中的数据
            String redisKey = this.getVideoFollowUserKey(userId);
           // String hashKey = String.valueOf(followUserId);
            this.redisTemplate.opsForSet().remove(redisKey, String.valueOf(followUserId));
            return true;
        }
        return false;
    }

    @Override
    public Boolean isFollowUser(Long userId, Long followUserId) {
        String redisKey = this.getVideoFollowUserKey(userId);
        //String hashKey = String.valueOf(followUserId);
        return this.redisTemplate.opsForSet().isMember(redisKey,String.valueOf(followUserId));
    }

    private String getVideoFollowUserKey(Long userId) {
        return VIDEO_FOLLOW_USER_KEY_PREFIX + userId;
    }
}

