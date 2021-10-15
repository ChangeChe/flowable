package com.github.changeche.flowable.model.bean;

import com.github.changeche.flowable.model.enums.DBTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据源却换
 * @Author: DengDaoCheng
 * @Date: 2020/9/1 11:25
 */
@Slf4j
public class DbContextHolder {
    private static final ThreadLocal<DBTypeEnum> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void set(DBTypeEnum dbType) {
        log.debug("切换到{}", dbType.name());
        CONTEXT_HOLDER.set(dbType);
    }

    public static DBTypeEnum get() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除上下文数据
     */
    static void clearDbType() {
        CONTEXT_HOLDER.remove();
    }

}
