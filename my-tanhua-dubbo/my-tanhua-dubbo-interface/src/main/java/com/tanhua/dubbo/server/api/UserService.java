package com.tanhua.dubbo.server.api;

import com.tanhua.common.pojo.User;
import org.springframework.stereotype.Service;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-11 20:48
 **/
@Service
public interface UserService {
    User queryUserByToken(String token);
}
