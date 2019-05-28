package com.sugon.server;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

@Component
@ServerEndpoint("/websocket/{sid}")
public class WebSocketServer {
	
	//当前连接数
	private static int onlineCount=0;
	//concurrent包的线程安全set，用来存放每个客户端对应的MyWebSocket对象
	private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();
	//与某个客户端的连接对话，需要通过它来给客户端发送数据
	private Session session;
	//接收sid
	private String sid="";
	
	/**
	 * 链接建立成功调用的方法
	 * @author lsx
	 * @date 2019年5月28日
	 * @param session
	 * @param sid
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("sid") String sid){
		this.session=session;
		webSocketSet.add(this);
		addOnlineCount();
		System.out.println("开始监听："+sid+"，当前在线人数为"+getOnlineCount());
		this.sid=sid;
		try {
			sendMessage("连接成功");
		} catch (Exception e) {
			System.out.println("webSocket IO异常");
		}
	}
	
	/**
	 * 链接关闭调用的方法
	 * @author lsx
	 * @date 2019年5月28日
	 */
	@OnClose
	public void onClose(){
		webSocketSet.remove(this);
		subOnlineCount();
		System.out.println("有一个链接关闭，当前在线人数为"+getOnlineCount());
	}
	
	/**
	 * 接收到客户端消息调用的方法
	 * @author lsx
	 * @date 2019年5月28日
	 * @param message
	 * @param session
	 */
	@OnMessage
	public void onMessage(String message, Session session){
		System.out.println("收到来自窗口"+sid+"的信息:"+message);
		if(message.indexOf("-")>0){
			String [] messages = message.split("-");
			sendInfo(messages[0], messages[1]);
		}else{
			//群发消息
			for(WebSocketServer item : webSocketSet){
				try {
					item.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 发生错误调用的方法
	 * @author lsx
	 * @date 2019年5月28日
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error){
		System.out.println("发生错误");
		error.printStackTrace();
	}
	
	public static void sendInfo(String message, String sid){
		for(WebSocketServer item : webSocketSet){
			try {
				if(sid.equals(item.sid)){
					item.sendMessage(message);
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * 实现服务端主动推送
	 * @author lsx
	 * @date 2019年5月28日
	 * @param string
	 * @throws IOException 
	 */
	private void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}

	/**
	 * 获取在线人数
	 * @author lsx
	 * @date 2019年5月28日
	 * @return
	 */
	private static synchronized int getOnlineCount() {
		return onlineCount;
	}

	/**
	 * 在线人数增加
	 * @author lsx
	 * @date 2019年5月28日
	 */
	private static synchronized void addOnlineCount() {
		WebSocketServer.onlineCount++;
	}
	
	/**
	 * 在线人数减少
	 * @author lsx
	 * @date 2019年5月28日
	 */
	private static synchronized void subOnlineCount() {
		WebSocketServer.onlineCount--;
	}

}
