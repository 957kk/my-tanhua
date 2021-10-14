package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.pojo.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-11 20:48
 **/
@Service
public interface UserInfoService {
    UserInfo queryUserInfoByUserId(Long userId);
    List<UserInfo> queryUserInfoList(QueryWrapper<UserInfo> queryWrapper);

    List<UserInfo> queryUserInfoByUserIdList(List<Object> userIds);
}
