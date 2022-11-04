package com.mingtech.application.redis;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.mingtech.application.utils.ErrorCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *Reids客户端访问工具类 
 *@author r2
 */
public class RedisUtils {
    /**
     * log日志
     */
	private static final Logger logger = Logger.getLogger(RedisUtils.class);

    private RedisTemplate redisTemplate;

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }
    
    /**
     * 删除对应的value
     *
     * @param key
     */
    public void removeAllkeys() {
    	Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }
    
    /**
     * 计数器自增
     * @param key
     */
    public int increment(final String key){
    	 Long increment = redisTemplate.opsForValue().increment(key, 1);
    	 return increment.intValue();
    }
    
    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间：秒
     * @return 操作是否成功
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_MSG_998,e);
            return false;
        }
    }
 
    /**
     * 根据key获取过期时间
     *
     * @param key 键
     * @return 时间（秒），返回0代表永久有效
     */
    public long getExpireTime(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 判断缓存中是否有对应的value
     *
     * @param redisKey redis的Key
     * @param hashKey  HashMap的Key
     */
    public boolean existsForHash(String redisKey, final Object hashKey) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        return hashOperations.hasKey(redisKey, hashKey);
    }
    
    /**
     * String类型 读取缓存
     *
     * @param key
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * String类型写入缓存
     *
     * @param key
     * @param value
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        ValueOperations<Serializable, String> operations = redisTemplate.opsForValue();
        operations.set(key, value);
        result = true;
        return result;
    }

    /**
     * String类型写入缓存,并设置有效时间
     * @param key
     * @param value
     * @param expireTime 秒
     */
    public boolean set(final String key, String value, long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, String> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            this.expire(key, expireTime);
            result = true;
        } catch (Exception e) {
            logger.error("Redis存储数据失败", e);
            result = false;
        }
        return result;
    }
    
   
    
    /**
     * Hash Del 删除HashMap中指定项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hdel(String key, String item) {
        return redisTemplate.opsForHash().delete(key, item);
    }
    
    /**
     * HashGet 获取HashMap中指定项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }
 
    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
  
    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
        	logger.error(Thread.currentThread().getName()+"-将缓存MAP放入redis缓存异常:",e);
            return false;
        }
    }
    
    
 
    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_MSG_998,e);
            return false;
        }
    }
    
    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_MSG_998,e);
            return null;
        }
    }
 
    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_MSG_998,e);
            return 0;
        }
    }
 
    /**
     * 通过索引 获取指定list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_MSG_998,e);
            return null;
        }
    }
 
 
    /**
     * 将对象添加到list缓存中
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
        	logger.error(Thread.currentThread().getName()+"-将报文放入redis队列异常:",e);
            return false;
        }
    }
 
    /**
     * 将对象添加到list缓存，并设定有效时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_MSG_998,e);
            return false;
        }
    }
 
    /**
     * 批量添加对象到list缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().leftPushAll(key, value);
            return true;
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_MSG_998,e);
            return false;
        }
    }
 
    /**
     * 批量添加对象到list缓存，并设定有效时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().leftPushAll(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_MSG_998,e);
            return false;
        }
    }
    
    /**
     * 通过阻塞方式获取list最右边第一个元素，如果超时则退出
     *
     * @param key   键
     * @param time  超时时间(秒)
     * @return
     */
    public Object lbrPop(String key, long time) {
            return redisTemplate.opsForList().rightPop(key, time, TimeUnit.SECONDS);
    }
 
    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_MSG_998,e);
            return false;
        }
    }
 
    /**
        * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            logger.error(ErrorCode.ERR_MSG_998,e);
            return 0;
        }
    }
    
    /**
	 *Redis同步锁
	  *给报价单新增、编辑、提交审批、记账、报文发送等业务增加redis同步锁
	 *@param key 原业务表主键
	 *@param expireTime 锁自动失效时间（单位：秒）
	 *@return true加锁成功 false加锁失败-说明该业务已经被其他人使用 
	 */
    public boolean getLock(String key, String value, int expireTime) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return false;
        }
        try {
        	boolean res = redisTemplate.opsForValue().setIfAbsent(key, value);
        	if(!res){
        		return false;
        	}
        	if (expireTime > 0) {
        		expire(key, expireTime);
        	}else {
        		expire(key, 1200);//默认20分钟失效
        	}
        	return true;
        } catch (Exception ex) {
            logger.warn(ErrorCode.ERR_CALL_REDIS, ex);
            return false;
        }
    }

    public void unLock(String key) {
        remove(key);
    }

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}
	
	
	
}

