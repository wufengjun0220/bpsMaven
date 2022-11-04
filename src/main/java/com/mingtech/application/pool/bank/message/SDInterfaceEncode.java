package com.mingtech.application.pool.bank.message;


import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class SDInterfaceEncode implements  ProtocolCodecFactory{
	
	
	 public ProtocolDecoder getDecoder() throws Exception {
	        return new TextDecoder();
	    }

	    
	    public ProtocolEncoder getEncoder() throws Exception {
	        return new TextEncoder();
	    }



	
}
