package com.brookmonte.friday.FridayPolymer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.brookmonte.friday.FridayPolymer.domain.admin.FridayAuthenticationProvider;

/**
 * WebSecurityConfig
 * 
 * Configure application security
 * 
 * @author Pete
 *
 */
@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    FridayAuthenticationProvider authProvider;

    /**
     * configure
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.headers().disable()
        .headers()
        .frameOptions().cacheControl().httpStrictTransportSecurity().xssProtection();                  
        
        http.authorizeRequests()               
            .antMatchers("/, /home")
                .permitAll()
                .anyRequest()
                .authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
            .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .permitAll()
                .invalidateHttpSession(true)
                .deleteCookies("JESSIONID")
            .and()
                .csrf();
    }

    /**
     * configure
     */
    @Override
    public void configure(WebSecurity web) throws Exception
    {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**", "/styles/**", "/elements/**"); // #3
    }

    /**
     * configureGlobal
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {        
        auth.authenticationProvider(authProvider);
    }
}