package com.tanhua.sso.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.enums.SexEnum;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.common.mapper.UserInfoMapper;
import com.tanhua.sso.service.UserInfoService;
import com.tanhua.sso.service.UserService;
import com.tanhua.sso.utils.ArcsoftFaceEngineUtils;
import com.tanhua.sso.utils.PicUploadUtils;
import com.tanhua.sso.vo.PicUploadResult;
import com.tanhua.sso.vo.ResultInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-10 09:17
 **/
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public ResultInfo saveUserInfo(Map<String, String> param, String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return ResultInfo.builder().code("00000").message("token信息为空").build();
            }
            User user = userService.queryUserByToken(token);
            if (Objects.isNull(user)) {
                return ResultInfo.builder().code("00001").message("token信息无效").build();
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(user.getId());
            userInfo.setSex(StringUtils.equalsIgnoreCase(param.get("gender"), "man") ? SexEnum.MAN : SexEnum.WOMAN);
            userInfo.setNickName(param.get("nickname"));
            userInfo.setBirthday(param.get("birthday"));
            userInfo.setCity(param.get("city"));

            UserInfo userInfo1 = userInfoMapper.selectById(user.getId());
            if (Objects.isNull(userInfo1)) {
                userInfoMapper.insert(userInfo);
            } else {
                QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
                wrapper.eq("user_id", userInfo.getUserId());
                userInfoMapper.update(userInfo, wrapper);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultInfo.builder().code("00004").message("保存信息失败").build();
        }
        return ResultInfo.builder().code("00005").message("保存信息成功").build();
    }

    @Override
    public ResultInfo saveUserLogo(MultipartFile file, String token) {
        if (StringUtils.isEmpty(token)) {
            return ResultInfo.builder().code("00000").message("token信息为空").build();
        }
        User user = userService.queryUserByToken(token);
        if (Objects.isNull(user)) {
            return ResultInfo.builder().code("00001").message("token信息无效").build();
        }
        try {
            boolean b = ArcsoftFaceEngineUtils.checkIsPortrait(file.getBytes());
            if (!b) {
                return ResultInfo.builder().code("00006").message("照片非人相").build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


          /*  //图片上传到阿里云OSS
            PicUploadResult result = PicUploadUtils.picUpload(file);
            String picName = result.getName();
            if (StringUtils.isEmpty(picName)) {
                //上传失败
                return ResultInfo.builder().code("00006").message("照片上传失败").build();
            }*/
        try {
            UserInfo userInfo1 = userInfoMapper.selectById(user.getId());
            if (Objects.isNull(userInfo1)) {
                userInfoMapper.insert(UserInfo.builder().userId(user.getId()).logo("abc12345").build());
            } else {
                QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
                wrapper.eq("user_id", userInfo1.getUserId());
                userInfoMapper.update(UserInfo.builder().userId(user.getId()).logo("abc12345").build(), wrapper);
            }
        } catch (Exception e) {
            return ResultInfo.builder().code("00007").message("照片路径保存失败").build();
        }
        return ResultInfo.builder().code("00008").message("照片上传成功").build();
    }
}

