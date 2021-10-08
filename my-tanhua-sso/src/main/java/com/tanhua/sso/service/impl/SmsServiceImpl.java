package com.tanhua.sso.service.impl;

import com.tanhua.sso.service.SmsService;
import com.tanhua.sso.utils.UUIDUtils;
import com.tanhua.sso.vo.ErrorResult;
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
    public ErrorResult sendCheckCode(String phone) {
       /* Random random = new Random();
        int i = random.nextInt(25);
        String checkCode = UUIDUtils.getUUID32().substring(i, i + 6);
        System.out.println(checkCode);*/
        boolean matches = phone.matches("^1[0-9]{10}$");
        if(!matches){
            return ErrorResult.builder().errCode("000003").errMessage("手机号不合法").build();
        }
        String redisKey = "CHECK_CODE_" + phone;

        if (redisTemplate.hasKey(redisKey)) {
            String msg = "上一次发送的验证码还未失效！";
            return ErrorResult.builder().errCode("000001").errMessage(msg).build();
        }
        String code = "123456";

        if(StringUtils.isEmpty(code)){
            String msg = "发送短信验证码失败！";
            return ErrorResult.builder().errCode("000000").errMessage(msg).build();
        }
        //短信发送成功，将验证码保存到redis中，有效期为5分钟
        this.redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(5));

        return null;
    }
}

