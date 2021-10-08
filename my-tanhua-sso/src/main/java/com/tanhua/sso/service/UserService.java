package com.tanhua.sso.service;

import com.tanhua.sso.vo.ResultInfo;

import java.util.Map;

public interface UserService {
    ResultInfo login(Map<String, String> map);
}
