package com.tanhua.sso.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PicUploadResult {

    /**
     * 唯一标识id
     */
    private String uid;
    /**
     * 文件名(文件oss存储路径)
     */
    private String name;
    /**
     * 状态有：uploading done error removed
     */
    private String status;
    /**
     * 状态码
     */
    private String code;
    /**
     * 服务端响应内容，如：'{"status": "success"}'
     */
    private String response;

}
