package com.zwj.xfyun.service.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author zhouwenjie
 * @Description 讯飞星火沟通
 * @Date 2023/10/31 14:23
 **/

@Component
@ServerEndpoint("/xin-huo/socket")
public class XfyunContactSocket {

    private static final Logger logger = LoggerFactory.getLogger(XfyunContactSocket.class);
    private Session session;

    private static CopyOnWriteArraySet<XfyunContactSocket> webSocketSet = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);
        logger.info("webSocket消息，有新的连接");
    }

    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        logger.info("webSocket消息，连接断开");
    }

    @OnMessage
    public void onMessage(String message){
        logger.info("webSocket消息，收到客户端发来的消息",message);
    }

    public void sendMessage(String  message){
        for (XfyunContactSocket webSocket: webSocketSet){
            logger.info("webSocket消息，广播消息",message);
            try {
                webSocket.session.getBasicRemote().sendText(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
