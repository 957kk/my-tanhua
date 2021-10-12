package com.tanhua.server.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.dubbo.server.api.TodayBestService;
import com.tanhua.dubbo.server.vo.PageResult;
import com.tanhua.dubbo.server.vo.RecommendUserQueryParam;
import com.tanhua.dubbo.server.vo.TodayBest;


import com.tanhua.server.utils.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tanhua")
@Slf4j
public class TodayBestController {

    @Reference(version = "1.0.0")
    private TodayBestService todayBestService;

    /**
     * 查询今日佳人
     *
     * @param token
     * @return
     */
    @GetMapping("todayBest")
    public ResponseEntity<TodayBest> queryTodayBest(@RequestHeader("Authorization") String token) {
        try {
            System.out.println(token);
            TodayBest todayBest = todayBestService.queryTodayBest(token);
            if (null != todayBest) {
                return ResponseEntity.ok(todayBest);
            }
        } catch (Exception e) {
            log.error("查询今日佳人出错~ token = " + token, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 查询推荐用户列表
     * @param token
     * @param queryParam
     * @return
     */
    @GetMapping("recommendation")
    @Cache(time = "30")
    public ResponseEntity<PageResult> queryRecommendation(@RequestHeader("Authorization") String token,
                                                          RecommendUserQueryParam queryParam){
        try {
            PageResult pageResult = this.todayBestService.queryRecommendation(token, queryParam);
            if (null != pageResult) {
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            log.error("查询推荐用户列表出错~ token = " + token, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }
}
