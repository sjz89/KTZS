package me.daylight.ktzs.configuration;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.AuthorityInterceptor;
import me.daylight.ktzs.model.dto.Api;
import me.daylight.ktzs.model.enums.RoleList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author Daylight
 * @date 2018/12/30 16:56
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private WebApplicationContext applicationContext;

    @Bean
    public AuthorityInterceptor authorityInterceptor(){
        return new AuthorityInterceptor();
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> containerCustomizer() {

        return container -> {
            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error");

            container.addErrorPages(error401Page, error404Page, error500Page);
        };
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorityInterceptor());
    }

    @Bean
    public List<Api> apiDoc(){
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        List<Api> list=new ArrayList<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            Api api=new Api();
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            ApiDoc annotation=method.getMethodAnnotation(ApiDoc.class);
            if (annotation==null)
                annotation=method.getMethod().getDeclaringClass().getDeclaredAnnotation(ApiDoc.class);
            if (Objects.isNull(annotation))
                continue;
            PatternsRequestCondition p = info.getPatternsCondition();
            for (String url : p.getPatterns()) {
                api.setUrl(url);
            }
            api.setDescription(annotation.description());
            List<String> roleList=new ArrayList<>();
            for (RoleList role:annotation.role())
                roleList.add(role.name());
            api.setRole(roleList);
            Parameter[] parameters = method.getMethod().getParameters();
            StringBuilder params= new StringBuilder();
            for (Parameter parameter:parameters){
                String paramName=parameter.getName();
                //排除request、model、response的入参
                if (paramName.equals("request")||paramName.equals("model")||paramName.equals("response"))
                    continue;
                String type=parameter.getType().getName();
                type=type.substring(type.lastIndexOf(".")+1);
                params.append(type).append(" ").append(parameter.getName()).append(",");
            }
            api.setParams(params.toString().length()==0?"":params.deleteCharAt(params.lastIndexOf(",")).toString());
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                api.setType(requestMethod.toString());
            }

            list.add(api);
        }
        return list;
    }
}
