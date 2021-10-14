package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.enums.IdType;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-13 21:20
 **/
public interface IdService {

    Long createId(IdType idType);
}
