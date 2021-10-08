package com.tanhua.sso.service;

import com.tanhua.sso.vo.ErrorResult;

import java.util.Map;

public interface UserService {
    ErrorResult login(Map<String, String> map);
}
