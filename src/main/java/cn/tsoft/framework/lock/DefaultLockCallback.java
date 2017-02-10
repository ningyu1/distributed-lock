/*
 * Copyright (c) 2017, Tsoft and/or its affiliates. All rights reserved.
 * FileName: DefaultLocKCallBack.java
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

/**
 *  默认的一个callback类<br> 
 * 〈功能详细描述〉
 *
 * @author ningyu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class DefaultLockCallback<T> implements LockCallback<T>{
    private final Logger logger = LoggerFactory.getLogger(DefaultLockCallback.class);
    private T returnValueForhandleNotObtainLock;
    private T returnValueForhandleException;
    

    /**
     * @param returnValueForhandleNotObtainLock 没有获取到锁时，返回值
     * @param returnValueForhandleException     获取到锁后内部执行报错时，返回值
     */
    public DefaultLockCallback(T returnValueForhandleNotObtainLock, T returnValueForhandleException) {
        super();
        this.returnValueForhandleNotObtainLock = returnValueForhandleNotObtainLock;
        this.returnValueForhandleException = returnValueForhandleException;
    }

    @Override
    public T handleNotObtainLock()  {
        logger.error("LockCantObtainException");
        return returnValueForhandleNotObtainLock;
    }

    @Override
    public T handleException(LockInsideExecutedException e)  {
        logger.error("LockInsideExecutedException", e);
        return returnValueForhandleException;
    }

}
