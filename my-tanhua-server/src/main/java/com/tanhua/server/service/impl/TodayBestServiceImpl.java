package com.tanhua.server.service.impl;


import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.dubbo.server.api.RecommendUserApi;
import com.tanhua.dubbo.server.pojo.RecommendUser;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.dubbo.server.vo.PageResult;
import com.tanhua.dubbo.server.vo.RecommendUserQueryParam;
import com.tanhua.dubbo.server.vo.TodayBest;
import com.tanhua.server.service.TodayBestService;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-11 20:41
 **/
@Service
public class TodayBestServiceImpl implements TodayBestService {

    @Reference(version = "1.0.0")
    private RecommendUserApi recommendUserApi;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoService userInfoService;
    @Value("${tanhua.sso.default.user}")
    private Long defaultUser;
    @Override
    public TodayBest queryTodayBest(String token) {
        User user = userService.queryUserByToken(token);
        if (Objects.isNull(user)) {
            return null;
        }


        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(user.getId());
        if (null == recommendUser) {
            return null;
        }

        TodayBest todayBest = new TodayBest();
        todayBest.setId(recommendUser.getUserId());

        //缘分值
        double score = Math.floor(recommendUser.getScore());//取整,98.2 -> 98
        todayBest.setFateValue(Double.valueOf(score).longValue());

        if (Objects.isNull(todayBest)) {
            //给出默认的推荐用户
            todayBest = new TodayBest();
            todayBest.setId(defaultUser);
            todayBest.setFateValue(80L); //固定值
        }

        System.out.println("abc");
        //补全个人信息
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(todayBest.getId());
        if (null == userInfo) {
            return null;
        }
        todayBest.setAvatar(userInfo.getLogo());
        todayBest.setNickname(userInfo.getNickName());
        todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
        todayBest.setGender(userInfo.getSex().getValue() == 1 ? "man" : "woman");
        todayBest.setAge(userInfo.getAge());

        return todayBest;
    }

    @Override
    public PageResult queryRecommendation(String token, RecommendUserQueryParam queryParam) {
        //校验token是否有效，通过SSO的接口进行校验
        User user = userService.queryUserByToken(token);
        if (null == user) {
            //token非法或已经过期
            return null;
        }

        PageResult pageResult = new PageResult();
        pageResult.setPage(queryParam.getPage());
        pageResult.setPagesize(queryParam.getPagesize());
        PageInfo<RecommendUser> recommendUserPageInfo = recommendUserApi.queryPageInfo(
                user.getId(),
                queryParam.getPage(),
                queryParam.getPagesize());

        List<RecommendUser> records = recommendUserPageInfo.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            //没有查询到推荐的用户列表
            return pageResult;
        }

        //填充个人信息

        //收集推荐用户的id
        Set<Long> userIds = new HashSet<>();
        for (RecommendUser record : records) {
            userIds.add(record.getUserId());
        }

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        //用户id参数
        queryWrapper.in("user_id", userIds);

        if (StringUtils.isNotEmpty(queryParam.getGender())) {
            //需要性别参数查询
//            queryWrapper.eq("sex", StringUtils.equals(queryParam.getGender(), "man") ? 1 : 2);
        }

        if (StringUtils.isNotEmpty(queryParam.getCity())) {
            //需要城市参数查询
//            queryWrapper.like("city", queryParam.getCity());
        }

        if (queryParam.getAge() != null) {
            //设置年龄参数，条件：小于等于
//            queryWrapper.le("age", queryParam.getAge());
        }

        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        if (CollectionUtils.isEmpty(userInfoList)) {
            //没有查询到用户的基本信息
            return pageResult;
        }

        List<TodayBest> todayBests = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            TodayBest todayBest = new TodayBest();

            todayBest.setId(userInfo.getUserId());
            todayBest.setAvatar(userInfo.getLogo());
            todayBest.setNickname(userInfo.getNickName());
            todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
            todayBest.setGender(userInfo.getSex().getValue() == 1 ? "man" : "woman");
            todayBest.setAge(userInfo.getAge());

            //缘分值
            for (RecommendUser record : records) {
                if (record.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    double score = Math.floor(record.getScore());//取整,98.2 -> 98
                    todayBest.setFateValue(Double.valueOf(score).longValue());
                    break;
                }
            }

            todayBests.add(todayBest);
        }

        //按照缘分值进行倒序排序
        Collections.sort(todayBests, (o1, o2) -> new Long(o2.getFateValue() - o1.getFateValue()).intValue());

        pageResult.setItems(todayBests);

        return pageResult;
    }
}

