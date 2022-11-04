package com.mingtech.application.pool.bank.message;


import java.nio.charset.Charset;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;

public class TextEncoder implements ProtocolEncoder {
	 Charset charset = Charset.forName(Constants.ENCODING);
	   
	    
	    public void dispose(IoSession session) throws Exception {
	        // TODO Auto-generated method stub
	       
	    }

	   
	    public void encode(IoSession session, Object message, ProtocolEncoderOutput output)
	            throws Exception {
	       System.out.print("----------------------------开始-----------------------------");
	        ByteBuffer buf = ByteBuffer.allocate(2048).setAutoExpand(true);
	       
	        buf.putString(message.toString(), charset.newEncoder());
	       
	        buf.putString(LineDelimiter.DEFAULT.getValue(), charset.newEncoder());
	        buf.flip();
	        System.out.print("----------------------------结束-----------------------------");
	        output.write(buf);
	       
	    }

	}



