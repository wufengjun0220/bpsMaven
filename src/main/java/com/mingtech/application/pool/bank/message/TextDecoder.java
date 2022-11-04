package com.mingtech.application.pool.bank.message;


import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class TextDecoder implements ProtocolDecoder{

    //Charset charset = Charset.forName("GBK");
    ByteBuffer buf = ByteBuffer.allocate(1024).setAutoExpand(true);
   
   
    public void decode(IoSession session, ByteBuffer in, ProtocolDecoderOutput output)
            throws Exception {
        /*
        while(in.hasRemaining()){
            byte b = in.get();
            if(b == '\n'){
                buf.flip();
                byte[] bytes = new byte[buf.limit()];
                buf.get(bytes);
                String message = new String(bytes,charset);
                buf = ByteBuffer.allocate(1024).setAutoExpand(true);
               
                output.write(message);
            }else{
                buf.put(b);
            }
        }*/
    	
    	ByteBuffer buf = ByteBuffer.allocate(100).setAutoExpand(true);     
        
        int n = in.limit();   
        StringBuffer sb = new StringBuffer(512);   
        byte [] sizeBytes = null;   
        if(in.hasRemaining()){     
           sizeBytes = new byte[n];   
            in.get(sizeBytes);   
            //buf.flip();     
            //sb.append(new String(sizeBytes, Constants.ENCODING));    
        }     
        //output.write(sb.toString());     
        output.write(sizeBytes);  
   }     


    
    public void dispose(IoSession arg0) throws Exception {
        // TODO Auto-generated method stub
       
    }

    
    public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
            throws Exception {
        // TODO Auto-generated method stub
       
    }

}


