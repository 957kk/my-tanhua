package com.tanhua.dubbo.server.api;

import org.bson.types.ObjectId;

import java.util.concurrent.CompletableFuture;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-13 21:21
 **/
public interface TimeLineService {
    CompletableFuture<String> saveTimeLine(Long userId, ObjectId publishId);
}
