/*
 * Copyright (c) 2017, Tsoft and/or its affiliates. All rights reserved.
 * FileName: Counter.java
 * Author:   ningyu
 * Date:     2017年1月11日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package cn.tsoft.framework.lock.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import cn.tsoft.framework.lock.Lock;
import cn.tsoft.framework.lock.LockCallBack;
import cn.tsoft.framework.lock.LockCantObtainException;
import cn.tsoft.framework.lock.LockInsideExecutedException;
import cn.tsoft.framework.lock.LockRetryFrequncy;
import cn.tsoft.framework.test.BaseJunitTestWithContext;

/**
 * <功能描述>
 * @author ningyu
 * @date 2017年1月18日 下午3:55:54
 */
public class LockTest extends BaseJunitTestWithContext {
	
	private final Logger logger = LoggerFactory.getLogger(LockTest.class);
	
	@Autowired
    Lock lock;
    boolean obtain = false;
    
    @Test
    public void testLockNormal() throws InterruptedException {
        ExecutorService pool=Executors.newFixedThreadPool(10);
        
        Runnable s = new Runnable() {
            @Override
            public void run() {
                String returnValue=lock("Test_key_2",LockRetryFrequncy.VERY_QUICK,10,20,100);
            }
        };
        
        for (int i = 0; i < 10; i++) {
            pool.execute(s);
        }
        pool.shutdown();
        pool.awaitTermination(1000l, TimeUnit.DAYS);
        
    }
    
    private String lock(String key, LockRetryFrequncy frequncy, int timeoutInSecond, long redisKeyExpireSeconds,final long hold) {
        return lock.lock(key, frequncy, timeoutInSecond, redisKeyExpireSeconds, new LockCallBack<String>() {
            @Override
            public String handleException(LockInsideExecutedException e) throws LockInsideExecutedException {
                throw new LockInsideExecutedException(e);
            }

            @Override
            public String handleNotObtainLock() throws LockCantObtainException {
                throw new LockCantObtainException();
            }

            @Override
            public String handleObtainLock() {
                Assert.assertFalse(obtain);
                obtain=true;
                try {
                    Thread.sleep(hold);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.debug("obtain the key");
                obtain=false;
                return "ok";
            }
        });
    }

}


