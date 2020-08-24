/**
 * ================================================================
 * Copyright (c) 2017-2020 Maiereni Software and Consulting Inc
 * ================================================================
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maiereni.webservices.utils.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Map;

/**
 * Web security configuration adapter
 * @author Petre Maierean
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    public WebSecurityConfiguration() {
        super(true);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        registerAuthenticationProvider(http);
        final CsrfConfigurer<HttpSecurity> configCsrf = http.csrf();
        registerCorsRepositories(configCsrf);
        registerIgnoringRequestMatchers(configCsrf);
        configCsrf.and()
                .addFilter(new WebAsyncManagerIntegrationFilter())
                .exceptionHandling().and()
                .headers().and()
                .sessionManagement().and()
                .securityContext().and()
                .requestCache().and()
                .anonymous().and()
                .servletApi().and()
                .apply(new DefaultLoginPageConfigurer<>()).and()
                .logout();
        // @formatter:on
        ClassLoader classLoader = getApplicationContext().getClassLoader();
        List<AbstractHttpConfigurer> defaultHttpConfigurers =
                SpringFactoriesLoader.loadFactories(AbstractHttpConfigurer.class, classLoader);

        for (AbstractHttpConfigurer configurer : defaultHttpConfigurers) {
            http.apply(configurer);
        }
    }

    private void registerCorsRepositories(final CsrfConfigurer<HttpSecurity> configCsrf) {
        Map<String, CsrfTokenRepository> repos = getApplicationContext().getBeansOfType(CsrfTokenRepository.class);
        repos.forEach( (s, r) -> {
            configCsrf.csrfTokenRepository(r);
        });
    }

    private void registerIgnoringRequestMatchers(final CsrfConfigurer<HttpSecurity> configCsrf) {
        Map<String, RequestMatcher> matchers = getApplicationContext().getBeansOfType(RequestMatcher.class);
        matchers.forEach( (s, m) -> {
            configCsrf.ignoringRequestMatchers(m);
        });
    }

    private void registerAuthenticationProvider(final HttpSecurity http) {
        Map<String, AuthenticationProvider> authenticationProviders = getApplicationContext().getBeansOfType(AuthenticationProvider.class);
        authenticationProviders.forEach( (s, ap) -> {
            http.authenticationProvider(ap);
        });
    }
}
