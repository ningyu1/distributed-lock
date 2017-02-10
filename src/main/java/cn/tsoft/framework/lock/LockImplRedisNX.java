/*
 * Copyright (c) 2017, Tsoft and/or its affiliates. All rights reserved.
 * FileName: LockImplRedisNX.java
 * Author:   ningyu
 * Date:     2017年1月11日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package cn.tsoft.framework.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import cn.tsoft.framework.redis.client.RedisClient;



/**
 * 〈一句话功能简述〉分布式并发悲观锁<br> 
 * 〈功能详细描述〉
 *
 * @author ningyu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class LockImplRedisNX implements Lock {
    private static final Logger LOGGER = LoggerFactory.getLogger(LockImplRedisNX.class);
//    private static final long threadSleepMilliseconds=100;
    
    private RedisClient redisClient;

	public RedisClient getRedisClient() {
		return redisClient;
	}

	public void setRedisClient(RedisClient redisClient) {
		this.redisClient = redisClient;
	}

	@Override
	public <T> T lock(String lockForDoTask, int timeoutInSecond, long redisKeyExpireSeconds, LockCallback<T> lockCallBack)	throws LockInsideExecutedException, LockCantObtainException {
		return lock(lockForDoTask,LockRetryFrequency.NORMAL,timeoutInSecond, redisKeyExpireSeconds,lockCallBack);
	}



    /* (non-Javadoc)
     * @see com.saic.ebiz.scoreapp.common.util.lock.Lock#lock(java.lang.String, int, long, com.saic.ebiz.scoreapp.common.util.lock.LockCallBack)
     */
    @Override
    public <T> T lock(String key, LockRetryFrequency frequncy, int timeoutInSecond,long redisKeyExpireSeconds, LockCallback<T> lockCallBack) throws LockInsideExecutedException,LockCantObtainException{
        String lockKey=generateKey(key);
        long curentTime = System.currentTimeMillis();
        long expireSecond = curentTime / 1000 + redisKeyExpireSeconds;
        long expireMillisSecond = curentTime + redisKeyExpireSeconds * 1000;
        
        int retryCount= Float.valueOf(timeoutInSecond*1000/frequncy.getRetrySpan()).intValue();
        
        for (int i = 0; i < retryCount; i++) {
            if (redisClient.setnx(lockKey, String.valueOf(expireMillisSecond)) == 1) {
                LOGGER.debug("obtain the lock: {},  at {} retry",lockKey,i);
                try {
                    redisClient.expireAt(lockKey, expireSecond);
                    return lockCallBack.handleObtainLock();
                } catch (Exception e) {
                    LockInsideExecutedException ie=new LockInsideExecutedException(e);
                    return lockCallBack.handleException(ie);
                } finally {
                    // logger.info("释放锁{}",key2);
                    redisClient.del(lockKey);
//                    redisClient.remove(lockForDoTask);
                }
            } else {
                LOGGER.debug("do not obtain the lock: {},  at {} retry",lockKey,i);
                try {
                    Thread.sleep(frequncy.getRetrySpan());
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupte exception", e);
                }
            }
        }
        
        String expireSpecifiedInString = redisClient.get(lockKey);
        if (expireSpecifiedInString != null) {
            long expireSpecified = Long.valueOf(expireSpecifiedInString);
            if (curentTime > expireSpecified) {
                LOGGER.warn("detect the task lock is expired, key: {}, expireSpecified:{}, currentTime:{}",
                        lockKey, expireSpecified, curentTime);
                redisClient.del(lockKey);
            }
        }
        T r =lockCallBack.handleNotObtainLock();
        return r;
    }
    
    private String generateKey(String key){
        return "LOCK_"+key;
    }

    
}
