package com.tanhua.dubbo.server.api;


import com.tanhua.dubbo.server.vo.PageResult;
import com.tanhua.dubbo.server.vo.RecommendUserQueryParam;
import com.tanhua.dubbo.server.vo.TodayBest;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-11 20:36
 **/
public interface TodayBestService {
    TodayBest queryTodayBest(String token);

    PageResult queryRecommendation(String token, RecommendUserQueryParam queryParam);
}

