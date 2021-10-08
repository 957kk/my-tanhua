package com.tanhua.sso.utils;

import com.aliyun.oss.OSSClient;
import com.tanhua.sso.vo.PicUploadResult;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @program: my-tanhua
 * @description: 阿里云文件上传工具类
 * @author: xkZhao
 * @Create: 2021-10-08 09:25
 **/

public class PicUploadUtils {
    private static String endpoint;
    private static String accessKeyId;
    private static String accessKeySecret;
    private static String bucketName;
    private static String urlPrefix;
    /**
     * 允许上传的格式
     */

    private PicUploadUtils(){}
    private static final String[] IMAGE_TYPE = new String[]{".bmp", ".jpg",
            ".jpeg", ".gif", ".png"};
    public static PicUploadResult picUpload(MultipartFile uploadFile) {
        try {
            InputStream inputStream = PicUploadUtils.class.getClassLoader().getResourceAsStream("aliyun.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            endpoint = properties.getProperty("aliyun.endpoint");
            accessKeyId = properties.getProperty("aliyun.accessKeyId");
            accessKeySecret = properties.getProperty("aliyun.accessKeySecret");
            bucketName = properties.getProperty("aliyun.bucketName");
            urlPrefix = properties.getProperty("aliyun.urlPrefix");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null == uploadFile) {
            return PicUploadResult.builder().status("error").code("300004").response("文件为空").build();
        }
        String originalFilename = uploadFile.getOriginalFilename();
        long size = uploadFile.getSize();
        if (StringUtils.isEmpty(originalFilename) || size <= 0) {
            return PicUploadResult.builder().status("error").code("300002").response("图片为空").build();
        }
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(), type)) {
                isLegal = true;
                break;
            }
        }
        if (!isLegal) {
            return PicUploadResult.builder().status("error").code("300000").response("上传图片格式不支持").build();
        }
        long max = 1048576L;
        if (size > max) {
            return PicUploadResult.builder().status("error").code("300005").response("文件为空").build();
        }

        String filePath = getFilePath(originalFilename);

        // 上传到阿里云
        try {
            // 目录结构：images/2018/12/29/xxxx.jpg
            ossClient.putObject(bucketName, filePath, new
                    ByteArrayInputStream(uploadFile.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            //上传失败
            return PicUploadResult.builder().status("error").code("300001").response("上传失败").build();
        }
        return PicUploadResult.builder()
                .name(urlPrefix+filePath)
                .uid(String.valueOf(System.currentTimeMillis()))
                .status("success")
                .code("300006")
                .response("上传成功").build();
    }

    /**
     * 获取阿里云oss路径
     * @param sourceFileName
     * @return
     */
    private static String getFilePath(String sourceFileName) {
        DateTime dateTime = new DateTime();
        return "images/" + dateTime.toString("yyyy")
                + "/" + dateTime.toString("MM") + "/"
                + dateTime.toString("dd") + "/" + System.currentTimeMillis() +
                RandomUtils.nextInt(100, 9999) + "." +
                StringUtils.substringAfterLast(sourceFileName, ".");
    }

}