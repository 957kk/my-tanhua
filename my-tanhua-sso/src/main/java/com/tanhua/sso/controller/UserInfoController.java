package com.tanhua.sso.controller;

import com.tanhua.sso.service.UserInfoService;
import com.tanhua.sso.service.UserService;
import com.tanhua.sso.vo.PicUploadResult;
import com.tanhua.sso.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @program: my-tanhua
 * @description: userinfo信息
 * @author: xkZhao
 * @Create: 2021-10-10 09:13
 **/
@RestController
@RequestMapping("user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("loginReginfo")
    public ResponseEntity<ResultInfo> saveUserInfo(@RequestBody Map<String, String> param,
                                                   @RequestHeader("Authorization") String token) {
        ResultInfo resultInfo = userInfoService.saveUserInfo(param, token);
        return ResponseEntity.ok(resultInfo);
    }
    /**
     * 完善个人信息-用户头像
     *
     * @return
     */
    @PostMapping("loginReginfo/head")
    public ResponseEntity<ResultInfo> saveUserLogo(@RequestParam("headPhoto") MultipartFile file,
                                               @RequestHeader("Authorization") String token) {
        ResultInfo resultInfo = userInfoService.saveUserLogo(file, token);
        return ResponseEntity.ok(resultInfo);
    }


}

