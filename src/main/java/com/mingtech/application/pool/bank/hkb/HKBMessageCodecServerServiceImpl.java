/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 */
package com.mingtech.application.pool.bank.hkb;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.codec.MessageCodecServerServiceImpl;
import com.mingtech.application.pool.bank.codec.converter.AbstractTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.BigDecimalTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.DatetimeTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.IntTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.StringTypeConverter;
import com.mingtech.application.pool.bank.codec.converter.VarStringTypeConverter;
import com.mingtech.application.pool.bank.message.Constants;

/**
 * @author xukai
 * 
 *         北京中关村银行服务器端编解码实现
 */
@Service
public class HKBMessageCodecServerServiceImpl extends
		MessageCodecServerServiceImpl implements HKBMessageCodecServerService {
	protected static Map<String, AbstractTypeConverter> typeConverters = new HashMap<String, AbstractTypeConverter>();
	// 解决FastJson组织JSON时报文域首字母转小写问题
	static {
		// 类型转化
		typeConverters.put(Constants.INT_TYPE, new IntTypeConverter());
		typeConverters.put(Constants.BIGDECIMAL_TYPE,
				new BigDecimalTypeConverter());
		typeConverters.put(Constants.DATE_TIME_TYPE,
				new DatetimeTypeConverter());
		typeConverters.put(Constants.STRING_TYPE, new StringTypeConverter());
		typeConverters.put(Constants.VAR_STRING_TYPE,
				new VarStringTypeConverter());
	}
	@Override
	public ReturnMessageNew decodeNetBankMessage(String code, String message)
			throws Exception {
		return HKBMessageUtil.decodeServerMessageNew(code, message);
	}
	
	@Override
	public ReturnMessageNew decodeMessageFile(String code, String splictCode, String fileName)
			throws Exception {
		return HKBMessageUtil.decodeMessageFile(code, splictCode, fileName, false);
	}



	@Override
	public byte[] encodeNetBankMessage(String code, ReturnMessageNew request)
			throws Exception {
		return HKBMessageUtil.encodeServerMessageNew(code, request);
	}
	

	@Override
	public ReturnMessageNew decodeCreditMessage(String code, String message)
			throws Exception {
		return HKBMessageUtil.decodeServerMessageNew(code, message);
	}

	@Override
	public byte[] encodeCreditMessage(String code, ReturnMessageNew request)
			throws Exception {
		return HKBMessageUtil.encodeServerMessageNew(code, request);
	}

	@Override
	public String encodeMessageFile(String code, ReturnMessageNew request)
			throws Exception {
		return HKBMessageUtil.encodeMessageFile(code, request, true);
	}

	@Override
	public String encodeMessageFileClien(String code, ReturnMessageNew request) throws Exception {
		return HKBMessageUtil.encodeMessageFile(code, request, false);
	}

}
