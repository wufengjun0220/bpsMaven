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
package com.mingtech.application.pool.bank.creditsys.server;

import java.io.IOException;
import java.net.InetSocketAddress;
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

import com.mingtech.application.pool.bank.codec.MessageCodecServerService;
import com.mingtech.application.pool.bank.codec.MessageCodecServerServiceImpl;
import com.mingtech.application.pool.bank.common.AbstractServer;
import com.mingtech.application.pool.bank.creditsys.ICreditHandler;
import com.mingtech.application.pool.bank.message.Constants;
import com.mingtech.application.pool.bank.message.ReturnMessage;
import com.mingtech.framework.common.util.FTPUtil;
import com.mingtech.framework.common.util.HRBConfig;

import edu.emory.mathcs.backport.java.util.concurrent.Executors;

/**
 * <p>
 * ????????????:(C)2003-2010 ?????????????????????????????????????????????
 * </p>
 * 
 * @??????: YeCheng
 * 
 * @?????? ????????????????????????socket?????????
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
	 * ?????????????????????
	 * <p>
	 * ????????????: start|??????:
	 * </p>
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		try {
			// ??????
			IoAcceptorConfig config = new SocketAcceptorConfig();
			config.setThreadModel(ThreadModel.MANUAL);
			DefaultIoFilterChainBuilder chain = config.getFilterChain();
			chain.addLast("threadPool", new ExecutorFilter(Executors
					.newCachedThreadPool()));
			chain.addLast("logger", new LoggingFilter());
			// ??????
			logger.info("??????????????????????????????????????????...");
			acceptor = new SocketAcceptor(4, Executors.newCachedThreadPool());
			acceptor.bind(new InetSocketAddress(port), this, config);
			logger.info("????????????????????????????????????????????????:" + port);
		} catch (IOException e) {
			logger.error("??????????????????????????????????????????!", e);
		}
	}

	/**
	 * ?????????????????????
	 * <p>
	 * ????????????: stop|??????:
	 * </p>
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		acceptor.unbindAll();
		logger.info("????????????????????????????????????????????????:" + getPort());
	}

	public void exceptionCaught(IoSession arg0, Throwable arg1)
			throws Exception {
		logger.info("??????????????????????????????????????????", arg1);
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		// ????????????????????????
		ByteBuffer buffer = (ByteBuffer) session.getAttribute("buffer");
		Integer length = (Integer) session.getAttribute("bodyLength");
		byte[] head = (byte[]) session.getAttribute("head");
		if (buffer == null) {
			buffer = ByteBuffer.allocate(1024);
			buffer.setAutoExpand(true);
			session.setAttribute("buffer", buffer);
		}
		buffer.put((ByteBuffer) message);
		// ???????????????????????????????????????
		if (length == null && buffer.limit() > Constants.MSG_HEAD_LEN) {
			buffer.rewind();
			head = new byte[Constants.MSG_HEAD_LEN];
			buffer.get(head);
			buffer.position(buffer.limit());
			byte[] lenByte = new byte[Constants.MSG_LENGTH_LEN];
			System.arraycopy(head, Constants.MSG_LENGTH_LEN, lenByte, 0,
					lenByte.length);
			length = Integer.valueOf(new String(lenByte));
			session.setAttribute("head", head);
			session.setAttribute("bodyLength", length);
		}
		// ???????????????
		if (length != null
				&& buffer.limit() >= (Constants.MSG_HEAD_LEN + length
						.intValue())) {
			buffer.position(Constants.MSG_HEAD_LEN);
			byte[] body = new byte[length.intValue()];
			buffer.get(body);
			session.removeAttribute("buffer");
			session.removeAttribute("bodyLength");
			session.removeAttribute("head");

			String shead = new String(head);
			String sbody = new String(body);
			String msgStr = shead + sbody;
			logger.info("??????????????????" + msgStr);
			// ?????????????????????????????????ftp?????????
			HRBConfig hc = HRBConfig.getInstance();
			int fileNameIndex = 0;
			if ((fileNameIndex = msgStr.indexOf("FILNAME")) > 0) {
				String mx = null;
				//String nameLast = msgStr.substring(fileNameIndex);
				int index = msgStr.lastIndexOf("</>");
				/*String fileName = msgStr.substring(fileNameIndex+8, index);
				FTPUtil f = new FTPUtil(hc.getCreditServerHostName(), hc
						.getCreditServerUserName(), hc
						.getCreditServerPassWord(), Integer.parseInt(hc
						.getCreditServerHostPort()));
				boolean result = f.downloadFile(hc.getCreditServerUserPath(),
						fileName, fileName);
				if (result) {
					byte[] b = f.getFile(fileName);
					mx = new String(b);
					mx = mx.replaceAll("\"", "");
					logger.info("file receive:" + mx);
					// ?????????????????????????????????????????????????????????
					// xmlString = xmlString + Constants.SPLICT_CODE + mx;
					msgStr = msgStr + Constants.SPLICT_CODE + mx;
					f.disConnectServer();
				}
				byte[] b = f.downloadFile(hc.getCreditServerUserPath(),
						fileName);
				mx = new String(b);
				msgStr = msgStr + Constants.SPLICT_CODE + mx;*/
			}
			// ??????????????????
			Map map = new HashMap();
			map.put(Constants.TX_SUCCESS, Constants.TX_SUCCESS_CODE); // ?????????????????????
			map.put(Constants.TX_FAIL, Constants.TX_FAIL_CODE); // ?????????????????????
			MessageCodecServerService msgCodec = new MessageCodecServerServiceImpl(); // ????????????????????????
			this.setMessageCodecServerService(msgCodec);
			this.setTxResponseCode(map);
			this.setServerName("credit");
			String rpsStr = messageReceived(msgStr);

			String mainBody = "";
			String detailBody = "";
			String[] msg = rpsStr.split(Constants.SPLICT_CODE);
			if (msg != null && msg.length == 1) {
				mainBody = msg[0];
			} else if (msg != null && msg.length == 2) {
				mainBody = msg[0];
				detailBody = msg[1];
			}
			// ???????????????
			/**String fileName = mainBody.substring(
					mainBody.lastIndexOf("FILNAME") + 8).replace("</>", "");
			if (detailBody != null && detailBody.length() > 0) {
				logger.info("FTP ?????????????????????   " + fileName);
				detailBody = detailBody.substring(0, detailBody.length() - 2);
				FTPUtil f = new FTPUtil(hc.getCreditServerHostName(), hc
						.getCreditServerUserName(), hc
						.getCreditServerPassWord(), Integer.parseInt(hc
						.getCreditServerHostPort()));
				f.uploadFile(hc.getCreditServerUserPath(), fileName, detailBody
						.getBytes());
				f.disConnectServer();
			}*/
			// ??????????????????????????????
			byte[] bMessage = mainBody.getBytes();
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
	 * *************************???????????????2014-12-23 YeCheng
	 * ??????**************************************************
	 */
	// ???????????????????????????
	public ReturnMessage txHandleRequest(String code, ReturnMessage request)
			throws Exception {
		return buildResponse(Constants.TX_SUCCESS_CODE, "??????????????????");
	}
	// ????????????
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
