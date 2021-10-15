package com.github.changeche.flowable.common.interceptor;

import com.github.changeche.flowable.common.annotation.DBType;
import com.github.changeche.flowable.model.bean.DbContextHolder;
import com.github.changeche.flowable.model.enums.DBTypeEnum;
import liquibase.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chenjing
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {
                MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = {
                MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class }),
        @Signature(type = Executor.class, method = "close", args = {boolean.class})
})
public class DbSelectorInterceptor implements Interceptor {

    private static final Map<String, DBTypeEnum> CACHE_MAP = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        String closeMethodName = "close";
        DBTypeEnum databaseType = null;
        if(!closeMethodName.equals(methodName)) {
            Object[] objects = invocation.getArgs();
            MappedStatement ms = (MappedStatement) objects[0];
            if((databaseType = CACHE_MAP.get(ms.getId())) == null) {
                // statementId的组成为xxx.xxx.xxMapper.funcName
                String statementId = ms.getId();
                String[] parts = statementId.split("\\.");
                String[] strings = Arrays.copyOf(parts, parts.length - 1);
                String className = StringUtil.join(strings, ".");
                Class<?> aClass = Class.forName(className);
                DBType annotation = aClass.getAnnotation(DBType.class);
                if (null == annotation) {
                    databaseType = DBTypeEnum.FLOWABLE;
                } else {
                    databaseType = annotation.value();
                }
                log.debug("设置方法[{}] use [{}] Strategy, SqlCommandType [{}]..", ms.getId(), databaseType.name(), ms.getSqlCommandType().name());
                CACHE_MAP.put(ms.getId(), databaseType);
            }

        } else {
            log.debug("close方法 重置为 [{}] Strategy", DBTypeEnum.FLOWABLE.name());
            databaseType = DBTypeEnum.FLOWABLE;
        }
        DbContextHolder.set(databaseType);

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
