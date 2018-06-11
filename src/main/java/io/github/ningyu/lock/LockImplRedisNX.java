/*
 * Copyright (c) 2017, Jiuye SCM and/or its affiliates. All rights reserved.
 * FileName: LockImplRedisNX.java
 * Author:   ningyu
 * Date:     2017年1月11日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package io.github.ningyu.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.ningyu.redis.client.IRedisClient;

/**
 * 〈一句话功能简述〉分布式并发悲观锁<br>
 * 〈功能详细描述〉
 * 
 * @author ningyu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class LockImplRedisNX implements Lock {
    private static final Logger logger = LoggerFactory.getLogger(LockImplRedisNX.class);
    
    private static final String LOCK_NAMESPACE = "LOCK";

    private IRedisClient redisClient;

    public IRedisClient getRedisClient() {
        return redisClient;
    }

    public void setRedisClient(IRedisClient redisClient) {
        this.redisClient = redisClient;
    }
    
    @Override
    public <T> T lock(String lockForDoTask, long redisKeyExpireSeconds, LockCallback<T> lockCallback)
            throws LockInsideExecutedException, LockCantObtainException {
        return lock(lockForDoTask, LockRetryFrequency.VERY_SLOW, 1, redisKeyExpireSeconds, lockCallback);
    }

    @Override
    public <T> T lock(String lockForDoTask, int timeoutInSecond, long redisKeyExpireSeconds,
            LockCallback<T> lockCallback) throws LockInsideExecutedException, LockCantObtainException {
        return lock(lockForDoTask, LockRetryFrequency.NORMAL, timeoutInSecond, redisKeyExpireSeconds, lockCallback);
    }

    @Override
    public <T> T lock(String key, LockRetryFrequency frequency, int timeoutInSecond, long redisKeyExpireSeconds,
            LockCallback<T> lockCallback) throws LockInsideExecutedException, LockCantObtainException {
        long curentTime = System.currentTimeMillis();
        long expireSecond = curentTime / 1000 + redisKeyExpireSeconds;
        long expireMillisSecond = curentTime + redisKeyExpireSeconds * 1000;

        int retryCount = Float.valueOf(timeoutInSecond * 1000 / frequency.getRetrySpan()).intValue();

        for (int i = 0; i < retryCount; i++) {
            String res = redisClient.set(key, LOCK_NAMESPACE, String.valueOf(expireMillisSecond), "NX", "EX", expireSecond);
            if("OK".equals(res)) {
                logger.debug("obtain the lock: {},  at {} retry", key, i);
                try {
                    return lockCallback.handleObtainLock();
                } catch (Exception e) {
                    LockInsideExecutedException ie = new LockInsideExecutedException(e);
                    return lockCallback.handleException(ie);
                } finally {
                    // logger.info("释放锁{}",key2);
                    redisClient.del(key, LOCK_NAMESPACE);
                }
            } else {
                logger.debug("do not obtain the lock: {},  at {} retry", key, i);
                try {
                    Thread.sleep(frequency.getRetrySpan());
                } catch (InterruptedException e) {
                    logger.error("Interrupte exception", e);
                }
            }
            
            //使用set key value NX EX second 替换 两段式的setnx和expire，减少极端情况下出错概率
//            if (redisClient.setnx(key, LOCK_NAMESPACE, String.valueOf(expireMillisSecond)) == 1) {
//                logger.debug("obtain the lock: {},  at {} retry", key, i);
//                try {
//                    redisClient.expireAt(key, LOCK_NAMESPACE, expireSecond);
//                    return lockCallback.handleObtainLock();
//                } catch (Exception e) {
//                    LockInsideExecutedException ie = new LockInsideExecutedException(e);
//                    return lockCallback.handleException(ie);
//                } finally {
//                    // logger.info("释放锁{}",key2);
//                    redisClient.del(key, LOCK_NAMESPACE);
//                }
//            } else {
//                logger.debug("do not obtain the lock: {},  at {} retry", key, i);
//                try {
//                    Thread.sleep(frequency.getRetrySpan());
//                } catch (InterruptedException e) {
//                    logger.error("Interrupte exception", e);
//                }
//            }
        }

        String expireSpecifiedInString = redisClient.get(key, LOCK_NAMESPACE, null);
        if (expireSpecifiedInString != null) {
            long expireSpecified = Long.valueOf(expireSpecifiedInString);
            if (curentTime > expireSpecified) {
                logger.warn("detect the task lock is expired, key: {}, expireSpecified:{}, currentTime:{}", key,
                        expireSpecified, curentTime);
                redisClient.del(key, LOCK_NAMESPACE);
            }
        }
        T r = lockCallback.handleNotObtainLock();
        return r;
    }

}
