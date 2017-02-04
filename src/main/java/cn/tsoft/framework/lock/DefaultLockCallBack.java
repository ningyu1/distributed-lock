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
public abstract class DefaultLockCallBack<T> implements LockCallBack<T>{
    private final Logger logger = LoggerFactory.getLogger(DefaultLockCallBack.class);
    private T returnValueForhandleNotObtainLock;
    private T returnValueForhandleException;
    

    /**
     * @param returnValueForhandleNotObtainLock 没有获取到锁时，返回值
     * @param returnValueForhandleException     获取到锁后内部执行报错时，返回值
     */
    public DefaultLockCallBack(T returnValueForhandleNotObtainLock, T returnValueForhandleException) {
        super();
        this.returnValueForhandleNotObtainLock = returnValueForhandleNotObtainLock;
        this.returnValueForhandleException = returnValueForhandleException;
    }

    /* (non-Javadoc)
     * @see com.saic.ebiz.mms.common.util.lock.LockCallBack#handleNotObtainLock()
     */
    @Override
    public T handleNotObtainLock()  {
        logger.error("LockCantObtainException");
        return returnValueForhandleNotObtainLock;
    }

    /* (non-Javadoc)
     * @see com.saic.ebiz.mms.common.util.lock.LockCallBack#handleException(com.saic.ebiz.mms.common.util.lock.LockInsideExecutedException)
     */
    @Override
    public T handleException(LockInsideExecutedException e)  {
        logger.error("LockInsideExecutedException", e);
        return returnValueForhandleException;
    }

}
