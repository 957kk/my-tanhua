package com.tanhua.sso.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.mapper.UserMapper;
import com.tanhua.common.pojo.User;
import com.tanhua.sso.service.UserService;
import com.tanhua.sso.utils.PicUploadUtils;
import com.tanhua.sso.utils.TokenUtils;
import com.tanhua.sso.vo.PicUploadResult;
import com.tanhua.sso.vo.ResultInfo;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
            return ResultInfo.builder().code("100002").message("验证码已失效").build();
        }

        String redisData = this.redisTemplate.opsForValue().get(redisKey);
        System.out.println(redisData);
        if (!code.equals(redisData)) {
            return ResultInfo.builder().code("100000").message("验证码错误").build();
        }
        redisTemplate.delete(redisKey);
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
        String token = TokenUtils.buildToken(secret, claims);
        try {
            //发送用户登录成功的消息
            Map<String, Object> msg = new HashMap<>();
            msg.put("id", user.getId());
            msg.put("date", System.currentTimeMillis());

            this.rocketMQTemplate.convertAndSend("tanhua-sso-login", msg);
        } catch (MessagingException e) {
            log.error("发送消息失败！", e);
        }

        HashMap<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("isNew", isNew);
        return ResultInfo.builder().code("100001").message("登陆成功").object(tokenMap).build();
    }

    @Override
    public PicUploadResult picUpload(MultipartFile multipartFile) {
        return PicUploadUtils.picUpload(multipartFile);
    }

    @Override
    public User queryUserByToken(String token) {

        try {
            Map<String, Object> body = TokenUtils.parseToken(secret, token);
            User user = new User();
            user.setId(Long.valueOf(body.get("id").toString()));
            String redisKey = "TANHUA_USER_MOBILE_" + user.getId();
            //需要返回user对象中的mobile，需要查询数据库获取到mobile数据
            //如果每次都查询数据库，必然会导致性能问题，需要对用户的手机号进行缓存操作
            //数据缓存时，需要设置过期时间，过期时间要与token的时间一致
            //如果用户修改了手机号，需要同步修改redis中的数据
            if (redisTemplate.hasKey(redisKey)) {
                String mobile = redisTemplate.opsForValue().get(redisKey);
                user.setMobile(mobile);
            } else {
                User u = userMapper.selectById(user.getId());
                long timeout = Long.parseLong(body.get("exp").toString()) * 1000 - System.currentTimeMillis();
                this.redisTemplate.opsForValue().set(redisKey, u.getMobile(), timeout, TimeUnit.MILLISECONDS);
            }
            return user;
        } catch (ExpiredJwtException e) {
            log.info("token已经过期！ token = " + token,e);
        } catch (Exception e) {
            log.error("token不合法！ token = "+ token, e);
        }
        return null;
    }

}
