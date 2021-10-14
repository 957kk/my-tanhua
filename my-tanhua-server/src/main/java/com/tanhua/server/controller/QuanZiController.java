package com.tanhua.server.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.dubbo.server.api.QuanZiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: my-tanhua
 * @description: 朋友圈控制器
 * @author: xkZhao
 * @Create: 2021-10-13 19:58
 **/
@RestController
@RequestMapping("movements")
public class QuanZiController {

    @Reference(version = "1.0.0")
    private QuanZiService quanZiService;


}

