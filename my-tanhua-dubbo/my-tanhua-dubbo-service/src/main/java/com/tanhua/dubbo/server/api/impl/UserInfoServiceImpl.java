package com.tanhua.dubbo.server.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.mapper.UserInfoMapper;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.dubbo.server.api.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo queryUserInfoByUserId(Long userId) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return userInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public List<UserInfo> queryUserInfoList(QueryWrapper<UserInfo> queryWrapper) {
        return userInfoMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserInfo> queryUserInfoByUserIdList(List<Object> userIds) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userIds);
        return userInfoMapper.selectList(queryWrapper);
    }
}
