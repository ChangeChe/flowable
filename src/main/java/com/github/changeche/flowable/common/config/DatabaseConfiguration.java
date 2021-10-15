package com.github.changeche.flowable.common.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.github.changeche.flowable.model.bean.DynamicDataSource;
import com.github.changeche.flowable.model.enums.DBTypeEnum;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class DatabaseConfiguration {
    protected static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_DE_";

    /**
     * flowable数据源
     */
    @Bean
    @ConfigurationProperties("spring.datasource.dynamic.datasource.flowable")
    @Primary
    public DataSource flowableDataSource(){
        log.info("加载主数据源flowable DataSource.");
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 在线数据库
     */
    @Bean
    @ConfigurationProperties("spring.datasource.dynamic.datasource.biz")
    public DataSource bizDataSource(){
        log.info("加载从数据源业务 DataSource.");
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 动态数据源
     */
    @Bean
    public DataSource myRoutingDataSource(@Qualifier("flowableDataSource") DataSource flowableDataSource,
                                          @Qualifier("bizDataSource") DataSource bizDataSource) {
        log.info("加载[flowableDataSource-bizDataSource]设置为动态数据源DynamicDataSource.");
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        // 可放置多个业务数据源
        targetDataSources.put(DBTypeEnum.FLOWABLE, flowableDataSource);
        targetDataSources.put(DBTypeEnum.BIZ, bizDataSource);

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(flowableDataSource);
        dynamicDataSource.setTargetDataSources(targetDataSources);

        return dynamicDataSource;
    }

    @Bean
    public Liquibase liquibase(@Qualifier("flowableDataSource") DataSource dataSource) {
        log.info("Configuring Liquibase");

        Liquibase liquibase = null;
        try {
            DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            database.setDatabaseChangeLogTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogTableName());
            database.setDatabaseChangeLogLockTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogLockTableName());

            liquibase = new Liquibase("META-INF/liquibase/flowable-modeler-app-db-changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("flowable");
            return liquibase;
        } catch (Exception e) {
            throw new InternalServerErrorException("Error creating liquibase database", e);
        } finally {
            closeDatabase(liquibase);
        }
    }

    private void closeDatabase(Liquibase liquibase) {
        if (liquibase != null) {
            Database database = liquibase.getDatabase();
            if (database != null) {
                try {
                    database.close();
                } catch (DatabaseException e) {
                    log.warn("Error closing database", e);
                }
            }
        }
    }
}
