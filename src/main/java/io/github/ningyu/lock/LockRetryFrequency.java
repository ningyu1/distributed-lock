/*
 * Copyright (c) 2017, Jiuye SCM and/or its affiliates. All rights reserved.
 * FileName: LockRetryFrequncy.java
 * Author:   ningyu
 * Date:     2017年1月11日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package io.github.ningyu.lock;

/**
 * 锁重试获取频率策略
 * 
 * @author ningyu
 * 
 */
public enum LockRetryFrequency {

    VERY_QUICK(10), QUICK(50), NORMAL(100), SLOW(500), VERY_SLOW(1000);

    private int retrySpan = 100;

    /**
     * 
     * @param rf
     *            重试间隔 ， 毫秒
     */
    private LockRetryFrequency(int rf) {
        retrySpan = rf;
    }

    public int getRetrySpan() {
        return retrySpan;
    }

}
