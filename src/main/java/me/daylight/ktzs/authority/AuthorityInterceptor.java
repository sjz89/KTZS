package me.daylight.ktzs.authority;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.utils.RetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author Daylight
 * @date 2019/01/27 16:17
 */
public class AuthorityInterceptor implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    private static Logger logger= LoggerFactory.getLogger(AuthorityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl=request.getRequestURI();

        AntPathMatcher pathMatcher=new AntPathMatcher();
        //检查url是否为静态资源文件
        if (pathMatcher.match("/static/**",requestUrl))
            return true;

        String ipAddress=request.getHeader("X-Real-IP");
        if (ipAddress==null)
            ipAddress=request.getRemoteAddr();
        logger.info("-------------------------------------------------------------------------------------");
        logger.info("-->URL:" + request.getRequestURI());
        logger.info("-->HTTP_METHOD:" + request.getMethod());
        logger.info("-->IP:" + ipAddress);
        logger.info("-->SERVER_PORT:" + request.getServerPort());
        logger.info("-->USER:" + (SessionUtil.getInstance().getIdNumber()));
        logger.info("-->SESSION:" + request.getSession().getId());
        logger.info("-------------------------------------------------------------------------------------");

        HandlerMethod handlerMethod=null;
        if (handler instanceof HandlerMethod)
            handlerMethod=(HandlerMethod) handler;

        //检查是否有Unlimited注解
        if (!Objects.isNull(handlerMethod)&&(handlerMethod.hasMethodAnnotation(Unlimited.class)
                ||handlerMethod.getMethod().getDeclaringClass().getDeclaredAnnotation(Unlimited.class)!=null)) {

            //防止重复登陆
            if (SessionUtil.getInstance().isUserLogin()&&pathMatcher.match("/login", requestUrl)) {
                response.sendRedirect("/redirect");
                return false;
            }

            return true;
        }

        //检查是否登录
        if (SessionUtil.getInstance().isUserLogin()){
            //首页
            if (pathMatcher.match("/",requestUrl)||pathMatcher.match("/home",requestUrl)||pathMatcher.match("/redirect",requestUrl))
                return true;

            //检查权限
            if (SessionUtil.getInstance().checkAuth(requestUrl))
                return true;

            //权限不足
            if (!Objects.isNull(handlerMethod)) {
                if (( handlerMethod.getMethod().getReturnType().equals(BaseResponse.class)) || SessionUtil.getInstance().isMobile())
                    response.getWriter().write(objectMapper.writeValueAsString(RetResponse.unauthorized()));
                else
                    response.sendRedirect("/401");
            }
            return false;
        }

        //未登录
        if (!Objects.isNull(handlerMethod)){
            if ((handlerMethod.getMethod().getReturnType().equals(BaseResponse.class)) || SessionUtil.getInstance().isMobile())
                response.getWriter().write(objectMapper.writeValueAsString(RetResponse.error("need to login")));
            else
                response.sendRedirect("/login");
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }
}
