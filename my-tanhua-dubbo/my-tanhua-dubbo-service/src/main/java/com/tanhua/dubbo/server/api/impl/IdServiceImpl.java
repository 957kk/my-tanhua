package com.tanhua.dubbo.server.api.impl;

import com.tanhua.dubbo.server.api.IdService;
import com.tanhua.dubbo.server.enums.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IdServiceImpl implements IdService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 实现逻辑：借助于Redis中的自增长特性，实现全局的id自增长(唯一)
     *
     * @return
     */
    @Override
    public Long createId(IdType idType){
        String key = "TANHUA_ID_" + idType.toString();
        return this.redisTemplate.opsForValue().increment(key);
    }

}
