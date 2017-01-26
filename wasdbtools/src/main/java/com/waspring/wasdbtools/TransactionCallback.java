package com.waspring.wasdbtools;

/**
 * 事务回调事件
 * @author felly
 *
 */
public interface TransactionCallback {
    public Object doTransactionEvent() throws Exception;  
}
