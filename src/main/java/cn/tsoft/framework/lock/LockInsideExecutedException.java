/*
 * Copyright (c) 2017, Tsoft and/or its affiliates. All rights reserved.
 * FileName: LockInsideExecutedException.java
 * Author:   ningyu
 * Date:     2017年1月11日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package cn.tsoft.framework.lock;

/**
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author ningyu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class LockInsideExecutedException extends RuntimeException{

    /**
     */
    private static final long serialVersionUID = 1L;

    public LockInsideExecutedException() {
        super();
        // TODO Auto-generated constructor stub
    }
    public LockInsideExecutedException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
    public LockInsideExecutedException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    
}
