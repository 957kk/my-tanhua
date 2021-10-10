package com.tanhua.sso.service;

import com.tanhua.sso.pojo.User;
import com.tanhua.sso.vo.PicUploadResult;
import com.tanhua.sso.vo.ResultInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    ResultInfo login(Map<String, String> map);

    PicUploadResult picUpload(MultipartFile multipartFile);

    User queryUserByToken(String token);
}
