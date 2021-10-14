package com.tanhua.server.service;

import com.tanhua.common.pojo.UserInfo;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.vo.PageResult;
import com.tanhua.dubbo.server.vo.QuanZiVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-13 20:04
 **/
public interface QuanZiService {
    PageResult queryPublishList(Integer page, Integer pageSize);
   void fillUserInfoToQuanZiVo(UserInfo userInfo, QuanZiVo quanZiVo);
    List<QuanZiVo> fillQuanZiVo(List<Publish> records);
    String savePublish(String textContent,
                       String location,
                       String latitude,
                       String longitude,
                       MultipartFile[] multipartFile);
    PageResult queryRecommendPublishList(Integer page, Integer pageSize);

}
