package com.wuhen.springwork.aspect;

import com.wuhen.springwork.anno.LogRecord;
import com.wuhen.springwork.base.Response;
import com.wuhen.springwork.enums.ErrorCodeEnum;
import com.wuhen.springwork.utils.JSONUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author chaoshunh
 * @create 2022/8/3
 */
@Aspect
@Component
@Order(1)
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    /**
     * 有这个注解的方法都被视为切点
     */
    @Pointcut("@annotation(com.wuhen.springwork.anno.LogRecord)")
    public void advice(){}

    @Around("advice()")//环绕增强，前后都会执行
    public Object around(ProceedingJoinPoint joinPoint) {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        LogRecord logRecord = method.getDeclaredAnnotation(LogRecord.class);
        String tag = logRecord.tag();
        Object proceed = null;
        try {
            proceed = joinPoint.proceed();
            logger.info("方法tag为：[{}]，参数为：{} 返回值为：{}",tag,JSONUtils.toJson(args),JSONUtils.toJson(proceed));
            logger.info("方法tag为：[{}],执行时间{}ms",tag,System.currentTimeMillis() - start);
        } catch (Throwable throwable) {
            logger.error("方法tag为：[{}]，参数为：{} 出现异常,异常信息为：",tag,JSONUtils.toJson(args),throwable);
            return Response.error(ErrorCodeEnum.EXCEPTION);
        }
        return proceed;
    }
}
