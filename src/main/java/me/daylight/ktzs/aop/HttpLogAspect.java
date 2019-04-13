package me.daylight.ktzs.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import me.daylight.ktzs.authority.SessionUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author Daylight
 * @date 2019/01/30 01:01
 */
//@Aspect
//@Component
public class HttpLogAspect {
    private Logger logger= LoggerFactory.getLogger(getClass());

    @Pointcut("execution(public * me.daylight.ktzs.controller..*.*(..))")
    public void webLog(){}

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        if (new AntPathMatcher().match("/file/**",request.getRequestURI()))
            return;

        logger.info("-------------------------------------------------------------------------------------");

        logger.info("-->URL:" + request.getRequestURI());
        logger.info("-->HTTP_METHOD:" + request.getMethod());
        logger.info("-->IP:" + request.getRemoteAddr());
        logger.info("-->SESSION:" + request.getSession().getId());
        logger.info("-->ID_NUMBER:" + (SessionUtil.getInstance().getIdNumber()));
        logger.info("-->CLASS_METHOD:" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("-->ARGS:" + JSONArray.toJSONString(joinPoint.getArgs()));

        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paramName = enu.nextElement();
            logger.info("--->" + paramName + ":" + request.getParameter(paramName));
        }
    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        // 处理完请求，返回内容
        logger.info("-->RESPONSE:"+JSON.toJSONString(ret));
        logger.info("-------------------------------------------------------------------------------------");
    }
}
