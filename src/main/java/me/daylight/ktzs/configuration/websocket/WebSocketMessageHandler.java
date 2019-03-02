package me.daylight.ktzs.configuration.websocket;

import me.daylight.ktzs.configuration.redis.RedisMsg;
import org.springframework.web.socket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daylight
 * @date 2019/02/15 20:41
 */
public class WebSocketMessageHandler implements WebSocketHandler, RedisMsg {

    //保存该服务端连接上的session
    private static Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        String idNumber=(String)session.getAttributes().get("idNumber");
//        if (sessionMap.get(idNumber)==null){
//            sessionMap.put(idNumber,session);
//        }
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus){

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void receiveMessage(String message) {

    }
}
