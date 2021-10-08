package com.tanhua.sso.controller;

import com.tanhua.sso.service.UserService;
import com.tanhua.sso.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("loginVerification")
    public ResponseEntity<ResultInfo> login(@RequestBody Map<String, String> map) {
        ResultInfo resultInfo = userService.login(map);
        return ResponseEntity.status(HttpStatus.OK).body(resultInfo);
    }
}
