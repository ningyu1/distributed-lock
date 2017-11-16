/*
 * Copyright (c) 2017, Jiuye SCM and/or its affiliates. All rights reserved.
 * FileName: LockCallBack.java
 * Author:   ningyu
 * Date:     2017年1月11日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package cn.tsoft.framework.lock;

/**
 * 回调接口
 * 
 * @author ningyu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface LockCallback<T> {
    /**
     * 获取到锁时回调
     * 
     * @author ningyu
     * @date 2017年2月9日 上午10:07:27
     * 
     * @return
     */
    public T handleObtainLock();

    /**
     * 没有获取到锁时回调
     * 
     * @author ningyu
     * @date 2017年2月9日 上午10:07:36
     * 
     * @return
     * @throws LockCantObtainException
     */
    public T handleNotObtainLock() throws LockCantObtainException;

    /**
     * 获取到锁时，执行业务逻辑出错
     * 
     * @author ningyu
     * @date 2017年2月9日 上午10:08:01
     * 
     * @param e
     * @return
     * @throws LockInsideExecutedException
     */
    public T handleException(LockInsideExecutedException e) throws LockInsideExecutedException;

}
