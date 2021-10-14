package com.tanhua.server.interceptor;

import cn.hutool.core.util.StrUtil;
import com.tanhua.common.pojo.User;
import com.tanhua.common.utils.NoAuthorization;
import com.tanhua.common.utils.UserThreadLocal;

import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserTokenInterceptor implements HandlerInterceptor {

   @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //校验handler是否是HandlerMethod
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //判断是否包含@NoAuthorization注解，如果包含，直接放行
        if (((HandlerMethod) handler).hasMethodAnnotation(NoAuthorization.class)) {
            return true;
        }

        //从请求头中获取token
        String token = request.getHeader("Authorization");
        if(StrUtil.isNotEmpty(token)){
            User user = this.userService.queryUserByToken(token);
            if(user != null){
                //token有效
                //将User对象放入到ThreadLocal中
                UserThreadLocal.set(user);
                return true;
            }
        }

        //token无效，响应状态为401
        response.setStatus(401); //无权限

        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //从ThreadLocal中移除User对象
        UserThreadLocal.remove();
    }
}
