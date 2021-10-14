package com.tanhua.sso.controller;

import com.tanhua.common.pojo.User;
import com.tanhua.sso.service.UserService;
import com.tanhua.sso.vo.PicUploadResult;
import com.tanhua.sso.vo.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("loginVerification")
    public ResponseEntity<ResultInfo> login(@RequestBody Map<String, String> map) {
        ResultInfo resultInfo = null;
        try {
            resultInfo = userService.login(map);
        } catch (Exception e) {
            log.error("出错误了，，，map=" + map, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultInfo);
    }

    @PostMapping("pic/upload")
    public ResponseEntity<PicUploadResult> picUpload(@RequestParam("file") MultipartFile multipartFile) {
        PicUploadResult picUploadResult = null;
        try {
            picUploadResult = userService.picUpload(multipartFile);
        } catch (Exception e) {
            log.error("出错误了，，，file" + multipartFile, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(picUploadResult);
    }

    /**
     * 校验token，根据token查询用户数据
     *
     * @param token token信息
     * @return user对象
     */
    @GetMapping("{token}")
    public User queryUserByToken(@PathVariable("token") String token) {

        return userService.queryUserByToken(token);
    }

}
