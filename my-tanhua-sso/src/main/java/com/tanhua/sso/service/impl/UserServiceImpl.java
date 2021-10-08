package com.tanhua.sso.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.sso.mapper.UserMapper;
import com.tanhua.sso.pojo.User;
import com.tanhua.sso.service.UserService;
import com.tanhua.sso.utils.PicUploadUtils;
import com.tanhua.sso.vo.PicUploadResult;
import com.tanhua.sso.vo.ResultInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public ResultInfo login(Map<String, String> map) {
        String phone = map.get("phone");
        String code = map.get("code");
        String redisKey = "CHECK_CODE_" + phone;
        boolean isNew = false;
        if (!redisTemplate.hasKey(redisKey)) {
            return ResultInfo.builder().code("100000").message("验证码错误").build();
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", phone);
        User user = userMapper.selectOne(wrapper);
        if (StringUtils.isEmpty(user)) {
            User user1 = new User();
            user1.setMobile(phone);
            user1.setPassword(null);
            userMapper.insert(user1);
            isNew = true;
        }


        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("id", user.getId());
        // 生成token
        String token = Jwts.builder()
                //payload，存放数据的位置，不能放置敏感数据，如：密码等
                .setClaims(claims)
                //设置加密方法和加密盐
                .signWith(SignatureAlgorithm.HS256, secret)
                //设置过期时间，12小时后过期
                .setExpiration(new DateTime().plusHours(12).toDate())
                .compact();

        try {
            //发送用户登录成功的消息
            Map<String,Object> msg = new HashMap<>();
            msg.put("id", user.getId());
            msg.put("date", System.currentTimeMillis());

            this.rocketMQTemplate.convertAndSend("tanhua-sso-login", msg);
        } catch (MessagingException e) {
            log.error("发送消息失败！", e);
        }

        HashMap<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token",token);
        tokenMap.put("isNew",isNew);
        return ResultInfo.builder().code("100001").message("登陆成功").object(tokenMap).build();
    }

    @Override
    public PicUploadResult picUpload(MultipartFile multipartFile) {
        return PicUploadUtils.picUpload(multipartFile);
    }
}
