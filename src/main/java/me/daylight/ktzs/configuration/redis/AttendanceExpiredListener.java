package me.daylight.ktzs.configuration.redis;

import me.daylight.ktzs.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author daylight
 * @date 2019/03/04 22:56
 */
@Component
public class AttendanceExpiredListener implements MessageListener {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private AttendanceService attendanceService;

    //订阅redis过期事件，在签到结束后自动将记录存入数据库
    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(Message message, byte[] bytes) {
        if (!message.toString().startsWith("qd_"))
            return;
        String uniqueId=message.toString().split("_")[1];
        //将redis中的签到记录存放到数据库中
        Map attendanceMap=redisTemplate.opsForHash().entries(uniqueId+"_list");
        attendanceService.saveAll(attendanceMap.values());

        redisTemplate.delete(uniqueId+"_list");
        redisTemplate.delete(uniqueId+"_position");
    }
}
