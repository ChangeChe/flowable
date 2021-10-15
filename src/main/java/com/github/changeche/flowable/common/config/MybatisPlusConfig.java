package com.github.changeche.flowable.common.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.changeche.flowable.common.interceptor.DbSelectorInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @Author: DengDaoCheng
 * @Date: 2020/6/30 18:09
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(MybatisPlusProperties.class)
public class MybatisPlusConfig implements MetaObjectHandler {
    private GlobalConfig globalConfig = GlobalConfigUtils.defaults();

    /**
     * 配置mybatis-plus的SqlSessionFactory
     */
    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(@Qualifier("myRoutingDataSource") DataSource myRoutingDataSource) throws Exception {
        log.info("自定义配置mybatis-plus的SqlSessionFactory.");

        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
        mybatisPlus.setDataSource(myRoutingDataSource);

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        ///自定义配置
        mybatisPlus.setConfiguration(configuration);

        //// 设置mapper.xml文件的路径
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resource = resolver.getResources("classpath:mapper/biz/*.xml");
        mybatisPlus.setMapperLocations(resource);
        //添加插件到SqlSessionFactory才能生效
        mybatisPlus.setPlugins(paginationInterceptor(), new DbSelectorInterceptor());
        globalConfig.setMetaObjectHandler(this);
        mybatisPlus.setGlobalConfig(globalConfig);
        return mybatisPlus;
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setLimit(500);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        String[] fieldNameList = {"createdTime", "createdAt", "updatedTime", "updatedAt", "updateAt"};
        fillDateField(metaObject, fieldNameList);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String[] fieldNameList = {"updatedTime", "updatedAt", "updateAt"};
        fillDateField(metaObject, fieldNameList);
    }

    private void fillDateField(MetaObject metaObject, String[] fieldNameList) {
        Long longTime = System.currentTimeMillis();
        Integer intTime = (int) (longTime/1000);
        for (String fieldName : fieldNameList) {
            if (metaObject.hasGetter(fieldName)) {
                if (metaObject.getGetterType(fieldName).isAssignableFrom(Integer.class)) {
                    this.setFieldValByName(fieldName, intTime, metaObject);
                } else if (metaObject.getGetterType(fieldName).isAssignableFrom(Long.class)) {
                    this.setFieldValByName(fieldName, longTime, metaObject);
                }
            }
        }
    }
}
