package me.daylight.ktzs.configuration.redis;


/**
 * @author Daylight
 * @date 2019/02/15 23:07
 */
public interface RedisMsg {
    /**
     * 接受信息
     * @param message
     */
    void receiveMessage(String message);
}