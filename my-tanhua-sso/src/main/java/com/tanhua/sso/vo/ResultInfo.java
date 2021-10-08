package com.tanhua.sso.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultInfo {
    private String code;
    private String message;
    private Object object;
}
