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
package io.github.ningyu.lock.test;

import io.github.ningyu.lock.DefaultLockCallback;
import io.github.ningyu.lock.Lock;
import io.github.ningyu.lock.LockCallback;
import io.github.ningyu.lock.LockCantObtainException;
import io.github.ningyu.lock.LockInsideExecutedException;
import io.github.ningyu.lock.LockRetryFrequency;
import io.github.ningyu.test.BaseJunitTestWithContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

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
    public void testLockNormal1() throws InterruptedException {
        ExecutorService pool=Executors.newFixedThreadPool(10);
        
        Runnable s = new Runnable() {
            @Override
            public void run() {
                String returnValue=lock1("Test_key_2",LockRetryFrequency.VERY_QUICK,30,20,1000);
                logger.info("lock result:{}", returnValue);
            }
        };
        
        for (int i = 0; i < 10; i++) {
            pool.execute(s);
        }
        pool.shutdown();
        pool.awaitTermination(1000l, TimeUnit.DAYS);
        
    }
    @Test
    public void testLockNormal2() throws InterruptedException {
    	ExecutorService pool=Executors.newFixedThreadPool(10);
    	
    	Runnable s = new Runnable() {
    		@Override
    		public void run() {
    			String returnValue=lock2("Test_key_2",LockRetryFrequency.VERY_QUICK,1,20,1000);
    			logger.info("lock result:{}", returnValue);
    		}
    	};
    	
    	for (int i = 0; i < 10; i++) {
    		pool.execute(s);
    	}
    	pool.shutdown();
    	pool.awaitTermination(1000l, TimeUnit.DAYS);
    	
    }
    
    private String lock1(final String key, LockRetryFrequency frequncy, int timeoutInSecond, long redisKeyExpireSeconds,final long hold) {
      
    	return lock.lock(key, frequncy, timeoutInSecond, redisKeyExpireSeconds, new LockCallback<String>() {
          @Override
          public String handleException(LockInsideExecutedException e) throws LockInsideExecutedException {
//              throw new LockInsideExecutedException(e);
              logger.error("获取到锁，内部执行报错");
              return "Exception";
              
          }

          @Override
          public String handleNotObtainLock() throws LockCantObtainException {
        	  logger.error("没有获取到锁");
//              throw new LockCantObtainException();
        	  return "NotObtainLock";
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
              logger.info("获取到锁，obtain the key");
              obtain=false;
              return "ok";
          }
      });
      
  }
    
    private String lock2(final String key, LockRetryFrequency frequncy, int timeoutInSecond, long redisKeyExpireSeconds,final long hold) {
        
        return lock.lock(key, frequncy, timeoutInSecond, redisKeyExpireSeconds, new DefaultLockCallback<String>("NotObtainLock", "Exception") {

            @Override
            public String handleObtainLock() {
                Assert.assertFalse(obtain);
                obtain=true;
                try {
                    Thread.sleep(hold);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("obtain the key:{}", key);
                obtain=false;
                return "ok";
            }
        });
    }

}


