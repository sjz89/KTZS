package me.daylight.ktzs.authority;

import me.daylight.ktzs.model.entity.Permission;
import me.daylight.ktzs.model.entity.User;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Daylight
 * @date 2019/01/30 20:48
 */
public class SessionUtil {

    private static SessionUtil instance;

    private SessionUtil(){
    }

    public static SessionUtil getInstance(){
        synchronized (SessionUtil.class){
            if (Objects.isNull(instance))
                instance=new SessionUtil();
        }
        return instance;
    }

    private HttpServletRequest getHttpRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    private HttpSession getHttpSession() {
        return getHttpRequest().getSession();
    }

    public void setSessionMap(User user, Permission[] permissions){
        //解决JPA懒加载可能导致的permission无法获取的问题
        user.getRole().setPermissions(Arrays.asList(permissions));
        //不缓存密码
        user.setPassword(null);
        getHttpSession().setAttribute("user",user);
    }

    public boolean checkAuth(String url){
        if (!isUserLogin())
            return false;
        AntPathMatcher pathMatcher=new AntPathMatcher();
        for (Permission permission:getPermissions()){
            if (pathMatcher.match(permission.getPath(),url))
                return true;
        }
        return false;
    }

    public String getIdNumber(){
        if (isUserLogin())
            return ((User)getHttpSession().getAttribute("user")).getIdNumber();
        return "HAVEN'T LOGIN";
    }

    public User getUser(){
        return (User)getHttpSession().getAttribute("user");
    }

    private Permission[] getPermissions(){
        return ((User)getHttpSession().getAttribute("user")).getRole().getPermissions().toArray(new Permission[0]);
    }

    public boolean isUserLogin(){
        return !Objects.isNull(getHttpSession().getAttribute("user"));
    }

    public boolean isMobile(){
        return getHttpRequest().getHeader("agent")!=null&&getHttpRequest().getHeader("agent").equals("Android");
    }

    public void setMobileSessionTimeout(){
        if (isMobile())
            getHttpSession().setMaxInactiveInterval(60*60);
    }

    public void logout(){
        getHttpSession().invalidate();
    }
}
