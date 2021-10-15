package com.github.changeche.flowable;

import com.github.changeche.flowable.common.config.AppDispatcherServletConfiguration;
import com.github.changeche.flowable.common.config.ApplicationConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;

@Import({
        ApplicationConfiguration.class,
        AppDispatcherServletConfiguration.class
})
@SpringBootApplication
@MapperScan(basePackages = {"com.github.changeche.flowable.biz.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class FlowableApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowableApplication.class, args);
    }

}
