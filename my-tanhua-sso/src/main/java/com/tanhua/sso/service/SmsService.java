package com.tanhua.sso.service;

import com.tanhua.sso.vo.ResultInfo;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-08 09:25
 **/
public interface SmsService {
    ResultInfo sendCheckCode(String phone);
}
