package com.mingtech.application.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.mingtech.application.utils.ConstantFields;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTTokenUtil {

    private static String tokenTimeDefault = "20";

    private static JWTTokenUtil jwtTokenUtil;
    private String tokenTime;
    private static final Logger logger = Logger.getLogger(JWTTokenUtil.class);
    public static JWTTokenUtil getInstance(){
        if(jwtTokenUtil == null){
//        	String tokenTime = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.SYS_SESSION_TIMEOUT);
            jwtTokenUtil = new JWTTokenUtil();
            jwtTokenUtil.setTokenTime(tokenTimeDefault);
        }
        return jwtTokenUtil;
    }

    public String getToken(String memberCode,String userName,String password){
        if(StringUtils.isBlank(tokenTime)){
            tokenTime = tokenTimeDefault;
        }
        int time = Integer.parseInt(tokenTime);
        Map claims= new HashMap();
        claims.put("sub",memberCode+"_"+userName);
        claims.put("created",new Date());
        String token = Jwts.builder()
                .setSubject(memberCode+"_"+userName)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * time * 1000))
                .signWith(SignatureAlgorithm.HS512, password)
                .compact();
        return token;
    }
    
    /**
     * @Desc 根据渠道号获取Token
     * @param channelNo 渠道号
     * @param memberCode 法人编码
     * @param userName 用户名称
     * @param password 密码
     * @return
     */
    public String getToken(String channelNo,String memberCode,String userName,String password){
        if(StringUtils.isBlank(tokenTime)){
            tokenTime = tokenTimeDefault;
        }
        int time = Integer.parseInt(tokenTime);
        Map claims= new HashMap();
        claims.put("sub",channelNo+ConstantFields.VAR_JOIN_BOTTOM_LINE+memberCode+ConstantFields.VAR_JOIN_BOTTOM_LINE+userName);
        claims.put("created",new Date());
        return Jwts.builder()
                .setSubject(channelNo+ConstantFields.VAR_JOIN_BOTTOM_LINE+memberCode+ConstantFields.VAR_JOIN_BOTTOM_LINE+userName)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * time * 1000))
                .signWith(SignatureAlgorithm.HS512, password)
                .compact();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String token = JWTTokenUtil.getInstance().getToken("901909","xuqiang","2222");


        //解析负载部分
        String json = new String(TextCodec.BASE64URL.decode(token.split("\\.")[1]),  "UTF-8");
        JSONObject body = JSON.parseObject(json);
        Object sub = body.get("sub");
    }

	public void setTokenTime(String tokenTime) {
		this.tokenTime = tokenTime;
	}
}
