package me.daylight.ktzs.configuration.websocket;

import com.alibaba.fastjson.JSONObject;
import me.daylight.ktzs.model.dto.NoticeDto;
import me.daylight.ktzs.model.dto.WsMsg;
import me.daylight.ktzs.model.entity.Notice;
import me.daylight.ktzs.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daylight
 * @date 2019/03/09 16:42
 */
@Component
@SuppressWarnings({"unchecked", "ConstantConditions"})
@ServerEndpoint("/webSocket/{channel}/{idNumber}")
public class WebSocketServer {
    private static final Logger logger= LoggerFactory.getLogger(WebSocketServer.class);

    private static final String Notice_Read_Table="notice_read_table";
    private static final String Notice_Offline_Table="notice_offline_table";
    public static final String Channel_SignInCount="signInCount";
    public static final String Channel_Notice="notice";

    //保存该服务端连接上的session
    private static Map<String, Session> signInCountSessionMap = new ConcurrentHashMap<>();

    private static Map<String, Session> noticeSessionMap = new ConcurrentHashMap<>();

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private NoticeService noticeService;

    private static WebSocketServer  webSocketServer ;

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        webSocketServer = this;
        webSocketServer.redisTemplate = this.redisTemplate;
        webSocketServer.noticeService=this.noticeService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("channel")String channel,@PathParam("idNumber") String idNumber) throws IOException {
        if (channel.equals(Channel_SignInCount))
            signInCountSessionMap.putIfAbsent(idNumber, session);
        else if (channel.equals(Channel_Notice)) {
            noticeSessionMap.putIfAbsent(idNumber, session);

            if (webSocketServer.redisTemplate.opsForHash().hasKey(Notice_Offline_Table,idNumber)){
                LinkedList<Long> offlineNoticeList=(LinkedList<Long>)webSocketServer.redisTemplate.opsForHash().get(Notice_Offline_Table,idNumber);
                List<Long> readNoticeList=(List<Long>)webSocketServer.redisTemplate.opsForHash().get(Notice_Read_Table,idNumber);
                while (!offlineNoticeList.isEmpty()) {
                    Long noticeId=offlineNoticeList.poll();
                    if (!readNoticeList.contains(noticeId)) {
                        readNoticeList.add(noticeId);
                        NoticeDto noticeDto=webSocketServer.noticeService.findNoticeById(noticeId);
                        session.getBasicRemote().sendText(JSONObject.toJSONString(noticeDto));
                    }
                }
                webSocketServer.redisTemplate.opsForHash().put(Notice_Read_Table,idNumber,readNoticeList);
                webSocketServer.redisTemplate.opsForHash().delete(Notice_Offline_Table,idNumber);
            }
        }
        logger.info(idNumber+"连接"+channel);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session,@PathParam("channel")String channel,@PathParam("idNumber")String idNumber) {
        if (channel.equals(Channel_SignInCount))
            signInCountSessionMap.remove(idNumber);
        else if (channel.equals(Channel_Notice))
            noticeSessionMap.remove(idNumber);
        logger.info(idNumber+"关闭"+channel);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
//        logger.info(message);
        if (message.equals("0x00"))
            session.getBasicRemote().sendText("0x01");
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("error"+error);
    }

    public static synchronized void sendMsg(String channel, WsMsg msg) throws IOException {
        switch (channel){
            case Channel_SignInCount:
                if (signInCountSessionMap.containsKey(msg.getReceiverIdList().get(0)))
                    signInCountSessionMap.get(msg.getReceiverIdList().get(0)).getBasicRemote().sendText(msg.getData().toString());
                break;
            case Channel_Notice:
                Notice notice=(Notice)msg.getData();
                NoticeDto noticeDto=new NoticeDto();
                noticeDto.setId(notice.getId());
                noticeDto.setContent(notice.getContent());
                noticeDto.setCourseName(notice.getCourse().getName());
                noticeDto.setTeacherName(notice.getCourse().getTeacher().getName());
                noticeDto.setTime(notice.getCreateTime().getTime());

                Map readNoticeMap=webSocketServer.redisTemplate.hasKey(Notice_Read_Table)?
                        webSocketServer.redisTemplate.opsForHash().entries(Notice_Read_Table):new HashMap<String,List<Long>>();
                Map offlineNoticeMap=webSocketServer.redisTemplate.hasKey(Notice_Offline_Table)?
                        webSocketServer.redisTemplate.opsForHash().entries(Notice_Offline_Table):new HashMap<String, LinkedList<Notice>>();

                for (String idNumber:msg.getReceiverIdList()) {
                    if (noticeSessionMap.containsKey(idNumber)) {
                        noticeSessionMap.get(idNumber).getBasicRemote().sendText(JSONObject.toJSONString(noticeDto));
                        List<Long> noticeList=readNoticeMap.containsKey(idNumber)?(List<Long>)readNoticeMap.get(idNumber):new ArrayList<>();
                        noticeList.add(notice.getId());
                        readNoticeMap.put(idNumber,noticeList);
                    }
                    else{
                        LinkedList<Long> offlineNotices=offlineNoticeMap.containsKey(idNumber)?(LinkedList<Long>) offlineNoticeMap.get(idNumber):new LinkedList<>();
                        offlineNotices.add(notice.getId());
                        offlineNoticeMap.put(idNumber,offlineNotices);
                    }
                }
                webSocketServer.redisTemplate.opsForHash().putAll(Notice_Read_Table,readNoticeMap);
                webSocketServer.redisTemplate.opsForHash().putAll(Notice_Offline_Table,offlineNoticeMap);
                break;
        }
    }
}
