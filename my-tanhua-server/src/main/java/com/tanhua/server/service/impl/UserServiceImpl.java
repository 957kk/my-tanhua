package com.tanhua.server.service.impl;

import com.tanhua.common.pojo.User;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-11 20:56
 **/
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${tanhua.sso.url}")
    private String ssoUrl;
    @Override
    public User queryUserByToken(String token) {
        User user = null;
        try {
            String url = ssoUrl + "/user/" + token;
            user = restTemplate.getForObject(url, User.class);
            if(Objects.isNull(user)){
                return null;
            }
        } catch (RestClientException e) {
            log.error("校验token出错，token = " + token, e);
        }
        return user;
    }
}

