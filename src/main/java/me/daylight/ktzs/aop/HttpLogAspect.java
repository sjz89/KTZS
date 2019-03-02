package me.daylight.ktzs.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.model.dto.HttpRecord;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Daylight
 * @date 2019/01/30 01:01
 */
@Aspect
@Component
public class HttpLogAspect {
    private Logger logger= LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private HttpRecord httpRecord;

    @Pointcut("execution(public * me.daylight.ktzs.controller..*.*(..))")
    public void webLog(){}

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        httpRecord =new HttpRecord();
        logger.info("-------------------------------------------------------------------------------------");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

        logger.info("-->URL:" + request.getRequestURI());
        logger.info("-->HTTP_METHOD:" + request.getMethod());
        logger.info("-->IP:" + request.getRemoteAddr());
        logger.info("-->SESSION:" + request.getSession().getId());
        logger.info("-->ID_NUMBER:" + (SessionUtil.getInstance().getIdNumber()));
        logger.info("-->CLASS_METHOD:" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("-->ARGS:" + JSONArray.toJSONString(joinPoint.getArgs()));

        Map<String,String> params=new HashMap<>();
        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paramName = enu.nextElement();
            params.put(paramName.substring(paramName.lastIndexOf(".")+1),request.getParameter(paramName));
            logger.info("--->" + paramName + ":" + request.getParameter(paramName));
        }

        httpRecord.setURL(request.getRequestURI());
        httpRecord.setMETHOD(request.getMethod());
        httpRecord.setIP(request.getRemoteAddr());
        httpRecord.setSESSION(request.getSession().getId());
        httpRecord.setUSER(SessionUtil.getInstance().getIdNumber());
        httpRecord.setCLASS_METHOD(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        httpRecord.setARGS(JSONArray.toJSONString(joinPoint.getArgs()));
        httpRecord.setPARAMS(JSON.toJSONString(params));

    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        // 处理完请求，返回内容
        httpRecord.setRESPONSE(JSON.toJSONString(ret));
//        redisTemplate.opsForList().leftPush("httpRecord",httpRecord);

        logger.info("-->RESPONSE:"+JSON.toJSONString(ret));
        logger.info("-------------------------------------------------------------------------------------");
    }
}
