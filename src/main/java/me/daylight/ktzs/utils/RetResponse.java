package me.daylight.ktzs.utils;

import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daylight
 * @date 2018/12/30 16:34
 */
public class RetResponse {
    private RetResponse(){}

    public static BaseResponse success(){
        return new BaseResponse();
    }

    public static BaseResponse success(Object object){
        BaseResponse response=new BaseResponse();
        response.setData(object);
        return response;
    }

    public static BaseResponse error(){
        BaseResponse response=new BaseResponse();
        response.setCode(400);
        response.setMsg("error");
        return response;
    }

    public static BaseResponse error(String msg){
        BaseResponse response=new BaseResponse();
        response.setCode(400);
        response.setMsg(msg);
        return response;
    }

    public static BaseResponse unauthorized(){
        BaseResponse response=new BaseResponse();
        response.setCode(401);
        response.setMsg("UNAUTHORIZED");
        return response;
    }

    public static Map<String,Object> transformUser(User user){
        Map<String,Object> map=new HashMap<>();
        map.put("id",user.getId());
        map.put("idNumber",user.getIdNumber());
        map.put("name",user.getName());
        map.put("phone",user.getPhone());
        map.put("role",user.getRole().getName());
        return map;
    }

    public static Map<String,Object> pageResult(Page page){
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("list",page.getContent());
        resultMap.put("count",page.getTotalElements());
        return resultMap;
    }
}
