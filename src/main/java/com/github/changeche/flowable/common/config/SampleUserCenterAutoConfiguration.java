package com.github.changeche.flowable.common.config;

import com.github.changeche.flowable.service.impl.SampleIdentityServiceImpl;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.spring.SpringIdmEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.spring.boot.FlowableSecurityAutoConfiguration;
import org.flowable.spring.boot.ProcessEngineServicesAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import org.flowable.spring.security.FlowableAuthenticationProvider;
import org.flowable.ui.common.properties.FlowableCommonAppProperties;
import org.flowable.ui.common.security.CustomPersistentRememberMeServices;
import org.flowable.ui.common.security.FlowableUiSecurityAutoConfiguration;
import org.flowable.ui.common.security.PersistentTokenService;
import org.flowable.ui.idm.conf.IdmSecurityConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;

/**
 * @author Chenjing
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore({
        FlowableUiSecurityAutoConfiguration.class,
        FlowableSecurityAutoConfiguration.class,
        IdmEngineServicesAutoConfiguration.class,
        ProcessEngineServicesAutoConfiguration.class,
        IdmSecurityConfiguration.class
})
public class SampleUserCenterAutoConfiguration implements ApplicationContextAware {

    ApplicationContext applicationContext;

    public SampleUserCenterAutoConfiguration() {

    }

    @Bean
    public EngineConfigurationConfigurer<SpringIdmEngineConfiguration> xmwIdmEngineConfigurer() {
        return idmEngineConfiguration -> idmEngineConfiguration
                .setIdmIdentityService(new SampleIdentityServiceImpl(applicationContext, idmEngineConfiguration));
    }

    @Bean
//    @ConditionalOnMissingBean(AuthenticationProvider.class)
    public FlowableAuthenticationProvider flowableAuthenticationProvider(IdmIdentityService idmIdentitySerivce,
                                                                         @Qualifier("sampleUserDetailsServiceImpl") UserDetailsService userDetailsService) {
        return new FlowableAuthenticationProvider(idmIdentitySerivce, userDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean
    public RememberMeServices flowableUiRememberMeService(FlowableCommonAppProperties properties, @Qualifier("sampleUserDetailsServiceImpl") UserDetailsService userDetailsService,
                                                          PersistentTokenService persistentTokenService) {
        return new CustomPersistentRememberMeServices(properties, userDetailsService, persistentTokenService);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
