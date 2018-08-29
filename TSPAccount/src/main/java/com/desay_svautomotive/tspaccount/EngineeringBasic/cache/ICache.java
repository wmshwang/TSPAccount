package com.desay_svautomotive.tspaccount.EngineeringBasic.cache;

/**
 * @author 王漫生
 * @date 2018-7-10
 * @project：个人中心
 */

public interface ICache {
    void put(String key, Object value);

    Object get(String key);

    void remove(String key);

    boolean contains(String key);

    void clear();

}
