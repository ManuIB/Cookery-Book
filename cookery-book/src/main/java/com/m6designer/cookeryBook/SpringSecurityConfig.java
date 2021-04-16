package com.m6designer.cookeryBook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.m6designer.cookeryBook.auth.handler.LoginSuccessHandler;
import com.m6designer.cookeryBook.models.service.JPpaClientDetailsService;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private LoginSuccessHandler successHandler;

	@Autowired
	private JPpaClientDetailsService clientDetailsService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
			.authorizeRequests()
				.antMatchers("/", "/css/**", "/js/**", "/images/**", "/list", "/locale", "/view/**", "/user/form/**")
					.permitAll().anyRequest().authenticated()
				.and()
				.formLogin()
					.successHandler(successHandler)
					.loginPage("/login")
					// .defaultSuccessUrl("/", true)
					.permitAll()
				.and()
				.logout()
					.permitAll()
				.and()
				.exceptionHandling()
					.accessDeniedPage("/error_403");
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Autowired
	public void configurerGlobal(AuthenticationManagerBuilder builder) throws Exception {

		builder.userDetailsService(clientDetailsService).passwordEncoder(passwordEncoder);
	}
}
