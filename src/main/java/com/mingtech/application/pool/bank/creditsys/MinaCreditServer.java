/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package com.mingtech.application.pool.bank.creditsys;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.common.WriteFuture;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import com.mingtech.application.pool.bank.common.AbstractServer;
import com.mingtech.application.pool.bank.common.RequestHandler;
import com.mingtech.application.pool.bank.hkb.HKBConstants;
import com.mingtech.application.pool.bank.hkb.HKBMessageCodecFactory;
import com.mingtech.application.pool.bank.hkb.Ret;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.ReturnMessage;
import com.mingtech.framework.common.util.StringUtil;

import edu.emory.mathcs.backport.java.util.concurrent.Executors;

/**
 * <p>
 * 版权所有:(C)2003-2010 北京明润华创科技有限责任公司 
 * </p>
 * 
 * @作者: YeCheng
 * 
 * @描述 与信贷系统通讯的socket服务端
 */
public class MinaCreditServer extends AbstractServer implements ICreditHandler,
		IoHandler {

	protected static final Logger logger = Logger
			.getLogger(MinaCreditServer.class);
	private IoAcceptor acceptor = null;
	private Map handlers = new HashMap();
	private int port;

	
	public MinaCreditServer() {
	}

	/**
	 * 启动信贷服务器
	 * <p>
	 * 方法名称: start|描述:
	 * </p>
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		try {
			// 配置
			IoAcceptorConfig config = new SocketAcceptorConfig();
			config.setThreadModel(ThreadModel.MANUAL);
			DefaultIoFilterChainBuilder chain = config.getFilterChain();
			chain.addLast("threadPool", new ExecutorFilter(Executors
					.newCachedThreadPool()));
			chain.addLast("logger", new LoggingFilter());
			// 启动
			logger.info("汉口银行信贷服务器开始启动...");
			acceptor = new SocketAcceptor(4, Executors.newCachedThreadPool());
			acceptor.bind(new InetSocketAddress(port), this, config);
			logger.info("汉口银行信贷服务器开始监听端口:" + port);
		} catch (IOException e) {
			logger.error("汉口银行信贷服务器启动失败!", e);
		}
	}

	/**
	 * 停止信贷服务器
	 * <p>
	 * 方法名称: stop|描述:
	 * </p>
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		acceptor.unbindAll();
		logger.info("汉口银行信贷服务器停止监听端口:" + getPort());
	}

	public void exceptionCaught(IoSession arg0, Throwable arg1)
			throws Exception {
		logger.info("汉口银行信贷服务器发生异常", arg1);
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// 读取消息放入缓存
		ByteBuffer buffer = (ByteBuffer) session.getAttribute("buffer");
		Integer length = (Integer) session.getAttribute("bodyLength");
		byte[] head = (byte[]) session.getAttribute("head");
		if (buffer == null) {
			buffer = ByteBuffer.allocate(1024);
			buffer.setAutoExpand(true);
			session.setAttribute("buffer", buffer);
		}
		buffer.put((ByteBuffer) message);
		// 解析报文头，获取报文体长度
		if(length == null && buffer.limit() > Constants.MSG_LENGTH_LEN){
			buffer.rewind();
			head = new byte[Constants.MSG_LENGTH_LEN];
			buffer.get(head);
			buffer.position(buffer.limit());
			length = Integer.valueOf(new String(head));
			session.setAttribute("head", head);
			session.setAttribute("bodyLength", length);
		}
		// 解析报文体
		if (length != null
				&& buffer.limit() >= (Constants.MSG_LENGTH_LEN + length
						.intValue())) {
			buffer.position(Constants.MSG_LENGTH_LEN);
			byte[] body = new byte[length.intValue()];
			buffer.get(body);
			session.removeAttribute("buffer");
			session.removeAttribute("bodyLength");
			session.removeAttribute("head");

			String shead = new String(head,Constants.ENCODING);
			String sbody = new String(body,Constants.ENCODING);
			String msgStr = shead + sbody;
			logger.info("接收到报文：" + msgStr);
			
			ReturnMessageNew response = null; // 响应对象
			Ret ret = new Ret();
			String txCode = "";// 内部交易码
			ReturnMessageNew request = null; // 请求对象
			try {
				// 1.解码请求报文
				request = HKBMessageCodecFactory.createCreditServer()
						.decodeCreditMessage("",sbody);
				if (request == null) {
					throw new Exception(
							HKBConstants.RESPONSE_FAIL_RET_MSG_PJC002);
				} else {
					txCode = request.getTxCode();
					ret.setRET_CODE(HKBConstants.TX_SUCCESS_CODE);
					ret.setRET_MSG(HKBConstants.RESPONSE_SUCCESS_RET_MSG);
				}
			} catch (Exception e) {
				logger.error("MinaCreditServer解码请求报文失败!", e);
				ret.setRET_CODE(HKBConstants.RESPONSE_FAIL_RET_CODE_PJC002);
				ret.setRET_MSG(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC002 + "["
						+ e.getMessage() + "]");
			}
			if (ret.getRET_CODE()
					.equals(HKBConstants.TX_SUCCESS_CODE)) {
				try {
					// 2.处理请求
					RequestHandler handler = (RequestHandler) this.getHandlers()
							.get(request.getTxCode().toUpperCase()); // 获得交易处理类
					if (handler == null) {
						throw new Exception(
								HKBConstants.RESPONSE_FAIL_RET_MSG_PJC003);
					}
					// 3.业务逻辑处理
					
					logger.info(request.getTxCode()+"接口处理开始...");
					Date starTime = new Date();
					
					response = handler.txHandleRequest(request.getTxCode(), request);
					
					Date endTime = new Date();
					long executeTime = (endTime.getTime() - starTime.getTime());
					logger.info(request.getTxCode()+"接口内部逻辑执行结束，执行时间为 "+executeTime/1000+"秒（"+executeTime+"毫秒)");
					
					
					if (response == null) {
						throw new Exception("业务逻辑处理返回失败!");
					}
				} catch (Exception e) {
					logger.error("MinaCreditServer交易" + txCode + "处理失败!", e);
					ret.setRET_CODE(HKBConstants.RESPONSE_FAIL_RET_CODE_PJC001);
					ret.setRET_MSG(HKBConstants.RESPONSE_FAIL_RET_MSG_PJC001
							+ "[" + e.getMessage() + "]");
				}
			}
			
			// 4.获取响应报文
			String resposeMsg = "";
			byte[] respMsg = null;
			try {
				if (response == null) {
					response = new ReturnMessageNew();
				} else {
					ret = response.getRet();
				}
				// 4.1 创建交易状态、交易返回代码、交易返回信息
				buildResponse(ret, response);
				respMsg = HKBMessageCodecFactory.createCreditServer()
						.encodeCreditMessage(txCode, response);
			} catch (Exception e) {
				logger.error("MinaCreditServer交易" + txCode + "响应失败!", e);
			}
			String bodyLength = String.valueOf(respMsg.length);//如果需要补位，则还需要做补位操作
			String bmsgLength = StringUtil.leftPad(bodyLength, 8, '0');
			resposeMsg = new String(respMsg, Constants.ENCODING);
			resposeMsg = bmsgLength + resposeMsg;
			logger.info("MinaCreditServer Send MSG:" + resposeMsg);
			//将返回结果发给客户端
			byte []bMessage = resposeMsg.getBytes(Constants.ENCODING);
			ByteBuffer rBuffer = ByteBuffer.allocate(bMessage.length);
			rBuffer.put(bMessage);
			rBuffer.flip();
			WriteFuture future = session.write(rBuffer);
			future.join();
		}
	}
	
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
	}

	public void sessionClosed(IoSession arg0) throws Exception {
	}

	public void sessionCreated(IoSession arg0) throws Exception {
	}

	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
	}

	public void sessionOpened(IoSession arg0) throws Exception {
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Map getHandlers() {
		return handlers;
	}

	public void setHandlers(Map handlers) {
		this.handlers = handlers;
	}

	/**
	 * *************************以下代码为2014-12-23 YeCheng
	 * 添加**************************************************
	 */
	// 查询对象，子类使用
	public ReturnMessage txHandleRequest(String code, ReturnMessage request)
			throws Exception {
		return buildResponse(Constants.TX_SUCCESS_CODE, "交易处理成功");
	}
	// 构建响应
	@SuppressWarnings("unchecked")
	protected ReturnMessage buildResponse(String resCode, String resMsg) {
		ReturnMessage response = new ReturnMessage();
		response.getHead().put("RspCode", resCode);
		if (!StringUtils.isEmpty(resMsg)) {
			response.getHead().put("RspMsg", resMsg);
		}
		return response;
	}
}
