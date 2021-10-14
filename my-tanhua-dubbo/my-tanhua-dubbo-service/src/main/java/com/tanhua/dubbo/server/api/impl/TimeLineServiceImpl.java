package com.tanhua.dubbo.server.api.impl;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.server.api.TimeLineService;
import com.tanhua.dubbo.server.pojo.TimeLine;
import com.tanhua.dubbo.server.pojo.Users;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class TimeLineServiceImpl implements TimeLineService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Async //异步执行，原理：底层开一个线程去执行该方法
    public CompletableFuture<String> saveTimeLine(Long userId, ObjectId publishId) {
        //写入好友的时间线表

        try {
            //查询好友列表
            Query query = Query.query(Criteria.where("userId").is(userId));
            List<Users> usersList = this.mongoTemplate.find(query, Users.class);
            if (CollUtil.isEmpty(usersList)) {
                //返回成功
                return CompletableFuture.completedFuture("ok");
            }

            //依次写入到好友的时间线表中
            for (Users users : usersList) {
                TimeLine timeLine = new TimeLine();
                timeLine.setId(ObjectId.get());
                timeLine.setDate(System.currentTimeMillis());
                timeLine.setPublishId(publishId);
                timeLine.setUserId(userId);

                //写入数据
                this.mongoTemplate.save(timeLine, "quanzi_time_line_" + users.getFriendId());
            }
        } catch (Exception e) {
            log.error("写入好友时间线表失败~ userId = " + userId + ", publishId = " + publishId, e);
            //TODO 事务回滚问题
            return CompletableFuture.completedFuture("error");
        }

        return CompletableFuture.completedFuture("ok");
    }

}
