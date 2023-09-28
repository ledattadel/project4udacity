package com.example.demo.config;

import com.example.demo.service.UserDetailsServiceImplements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * This class configures security settings for the application using Spring Security.
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	UserDetailsServiceImplements userDetailsService;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * Configure HTTP security settings.
	 *
	 * @param http The HttpSecurity object to configure.
	 * @throws Exception If configuration fails.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests()
				.antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll().anyRequest().authenticated()
				.and().addFilter(new TokenBasedAuthenticationFilter(authenticationManager()))
				.addFilter(new TokenAuthenticationVerificationFilter(authenticationManager())).sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	/**
	 * Configure authentication manager bean.
	 *
	 * @return An AuthenticationManager bean.
	 * @throws Exception If configuration fails.
	 */
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * Configure authentication manager builder.
	 *
	 * @param auth The AuthenticationManagerBuilder object to configure.
	 * @throws Exception If configuration fails.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.parentAuthenticationManager(authenticationManagerBean()).userDetailsService(userDetailsService)
				.passwordEncoder(bCryptPasswordEncoder);
	}
}
