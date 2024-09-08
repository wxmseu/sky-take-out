package com.sky.aspect;

import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component
@Aspect
@Slf4j
public class AdminPermissionCheckAspect {

    @Pointcut("@annotation(com.sky.annotation.AdminPermissionCheck)")
    public void adminPermissionPointcut(){}

    @Before("adminPermissionPointcut()")
    public void adminPermission(JoinPoint joinPoint) {
        Long id = BaseContext.getCurrentId();
        log.info("当前用户id为{}", id);
        if (id != 1) {
            throw new UnauthorizedException("当前用户不是管理员，无权限");
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
