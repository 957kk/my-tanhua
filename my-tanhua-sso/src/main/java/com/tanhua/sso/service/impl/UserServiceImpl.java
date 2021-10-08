package com.tanhua.sso.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.sso.mapper.UserMapper;
import com.tanhua.sso.pojo.User;
import com.tanhua.sso.service.UserService;
import com.tanhua.sso.vo.ErrorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public ErrorResult login(Map<String, String> map) {
        String phone = map.get("phone");
        String code = map.get("code");
        String redisKey = "CHECK_CODE_" + phone;
        boolean isNew = false;
        if(!redisTemplate.hasKey(redisKey)){
            return ErrorResult.builder().errCode("100000").errMessage("验证码错误").build();
        }
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("mobile",phone);
        User user = userMapper.selectOne(wrapper);
        if(StringUtils.isEmpty(user)){
            User user1 = new User();
            user1.setMobile(phone);
            user1.setPassword(null);
            userMapper.insert(user1);
            isNew=true;
        }


        return ErrorResult.builder().errCode("100001").errMessage("ssss").build();
    }
}
