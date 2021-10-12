package com.tanhua.sso.controller;

import com.tanhua.sso.service.SmsService;
import com.tanhua.sso.vo.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-08 08:48
 **/
@RestController
@RequestMapping("user")
@Slf4j
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("login")
    public ResponseEntity<ResultInfo> sendCheckCode(@RequestBody Map<String, String> param) {
        ResultInfo resultInfo = null;
        try {
            String phone = param.get("phone");
            resultInfo = smsService.sendCheckCode(phone);
        } catch (Exception e) {
           log.error("出错误了，，，",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultInfo);
    }
}

