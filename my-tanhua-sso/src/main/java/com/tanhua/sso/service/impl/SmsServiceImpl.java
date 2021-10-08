package com.tanhua.sso.service.impl;

import com.tanhua.sso.service.SmsService;
import com.tanhua.sso.vo.ResultInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-08 09:25
 **/
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public ResultInfo sendCheckCode(String phone) {
        boolean matches = phone.matches("^1[0-9]{10}$");
        if(!matches){
            return ResultInfo.builder().code("000003").message("手机号不合法").build();
        }
        String redisKey = "CHECK_CODE_" + phone;

        if (redisTemplate.hasKey(redisKey)) {
            String msg = "上一次发送的验证码还未失效！";
            return ResultInfo.builder().code("000001").message(msg).build();
        }
        String code = "123456";

        if(StringUtils.isEmpty(code)){
            String msg = "发送短信验证码失败！";
            return ResultInfo.builder().code("000000").message(msg).build();
        }
        //短信发送成功，将验证码保存到redis中，有效期为5分钟
        this.redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(5));

        String msg = "发送成功！";
        return ResultInfo.builder().code("000002").message(msg).build();
    }
}

