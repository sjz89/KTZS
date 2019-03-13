package me.daylight.ktzs.configuration.redis;

import me.daylight.ktzs.configuration.websocket.WebSocketServer;
import me.daylight.ktzs.model.dto.WsMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Daylight
 * @date 2019/03/10 22:39
 */
@Component
public class NoticePushListener implements MessageListener {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        try {
            WsMsg msg=(WsMsg) redisTemplate.getValueSerializer().deserialize(message.getBody());
            WebSocketServer.sendMsg(WebSocketServer.Channel_Notice,msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
