package com.tanhua.sso.service;

import com.tanhua.sso.vo.PicUploadResult;
import com.tanhua.sso.vo.ResultInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-10 09:16
 **/
public interface UserInfoService {

    ResultInfo saveUserInfo(Map<String, String> param, String token);

    ResultInfo saveUserLogo(MultipartFile file, String token);
}
