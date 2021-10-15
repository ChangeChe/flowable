package com.github.changeche.flowable.common.config;

import com.github.changeche.flowable.model.resp.SampleDetailResp;
import org.flowable.ui.idm.properties.FlowableIdmAppProperties;
import org.flowable.ui.idm.servlet.ApiDispatcherServletConfiguration;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Collection;

@Configuration
@EnableConfigurationProperties({FlowableIdmAppProperties.class, FlowableModelerAppProperties.class})
@ComponentScan(basePackages = {
        "org.flowable.ui.idm.conf",
//        "org.flowable.ui.idm.security",
        "org.flowable.ui.idm.service",
        "org.flowable.ui.modeler.repository",
        "org.flowable.ui.modeler.service",
//        "org.flowable.ui.common.filter",
        "org.flowable.ui.common.service",
        "org.flowable.ui.common.repository",
//        "org.flowable.ui.common.security",
        "org.flowable.ui.common.tenant",
        "org.flowable.form"
}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.flowable.ui.idm.conf.ApplicationConfiguration.class)
})
public class ApplicationConfiguration implements BeanPostProcessor {
    @Bean
    public ServletRegistrationBean apiServlet(ApplicationContext applicationContext) {
        AnnotationConfigWebApplicationContext dispatcherServletConfiguration = new AnnotationConfigWebApplicationContext();
        dispatcherServletConfiguration.setParent(applicationContext);
        dispatcherServletConfiguration.register(ApiDispatcherServletConfiguration.class);
        DispatcherServlet servlet = new DispatcherServlet(dispatcherServletConfiguration);
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet, "/api/*");
        registrationBean.setName("Flowable IDM App API Servlet");
        registrationBean.setLoadOnStartup(1);
        registrationBean.setAsyncSupported(true);
        return registrationBean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractSecurityInterceptor) {
            AbstractSecurityInterceptor interceptor = (AbstractSecurityInterceptor) bean;
            AbstractAccessDecisionManager manager = (AbstractAccessDecisionManager) interceptor.getAccessDecisionManager();
            manager.getDecisionVoters().add(new AccessDecisionVoter<Object>() {
                @Override
                public boolean supports(ConfigAttribute attribute) {
                    return true;
                }

                @Override
                public boolean supports(Class<?> clazz) {
                    return clazz == SampleDetailResp.class;
                }

                @Override
                public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
                    return null == authentication || !(authentication.getPrincipal() instanceof SampleDetailResp) ? AccessDecisionVoter.ACCESS_DENIED : AccessDecisionVoter.ACCESS_GRANTED;
                }
            });
        }
        return bean;
    }
}
