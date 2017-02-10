/*
 * Copyright (c) 2017, Tsoft and/or its affiliates. All rights reserved.
 * FileName: Lock.java
 * Author:   ningyu
 * Date:     2017年1月11日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package cn.tsoft.framework.lock;

/**
 *  分布式并发锁<br> 
 *  对给定的可以
 *
 * @author ningyu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface Lock {
  
    /**
     * 
     * 功能描述: 分布式并发锁
     * 〈功能详细描述〉
     *
     * @param key  锁的key, 通过这个key来唯一确认锁，建议
     * @param timeoutInSecond 获取锁的timeout,
     * @param redisKeyExpireSeconds 锁的最大过期时间， 因为是并发锁，为了杜绝因为获得锁而没有释放造成的问题
     * @param lockCallBack 一些了callback方法
     * @return
     * @throws LockInsideExecutedException   在获得锁后，在进行业务操作是发生的异常
     * @throws LockCantObtainException   获取锁的timeout, 仍未获得锁
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public abstract <T> T lock(String key, int timeoutInSecond, long redisKeyExpireSeconds, LockCallback<T> lockCallBack) throws LockInsideExecutedException,  LockCantObtainException;

    /**
     * 
     * 功能描述: 分布式并发锁
     * 〈功能详细描述〉
     *
     * @param key  锁的key, 通过这个key来唯一确认锁，建议
     * @param frequncy 获取锁的frequence,
     * @param timeoutInSecond 获取锁的timeout,
     * @param redisKeyExpireSeconds 锁的最大过期时间， 因为是并发锁，为了杜绝因为获得锁而没有释放造成的问题
     * @param lockCallBack 一些了callback方法
     * @return
     * @throws LockInsideExecutedException   在获得锁后，在进行业务操作是发生的异常
     * @throws LockCantObtainException   获取锁的timeout, 仍未获得锁
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public abstract <T> T lock(String key, LockRetryFrequency frequncy, int timeoutInSecond, long redisKeyExpireSeconds, LockCallback<T> lockCallBack)	throws LockInsideExecutedException, LockCantObtainException;

}