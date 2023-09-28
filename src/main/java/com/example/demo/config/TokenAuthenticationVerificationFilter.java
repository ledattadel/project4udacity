package com.example.demo.config;

import com.auth0.jwt.JWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

/**
 * This class extends BasicAuthenticationFilter to handle token-based authentication verification.
 */

public class TokenAuthenticationVerificationFilter extends BasicAuthenticationFilter {

    /**
     * Constructor for TokenAuthenticationVerificationFilter.
     *
     * @param authManager The AuthenticationManager to authenticate users.
     */
    public TokenAuthenticationVerificationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    /**
     * Perform internal filtering for token-based authentication.
     *
     * @param req   The HTTP request.
     * @param res   The HTTP response.
     * @param chain The filter chain.
     * @throws IOException      If an I/O error occurs.
     * @throws ServletException If a servlet exception occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    /**
     * Retrieve user authentication information from the token.
     *
     * @param req The HTTP request.
     * @return A UsernamePasswordAuthenticationToken if authentication is successful, or null if not authenticated.
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String token = req.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            String user = JWT.require(HMAC512(SecurityConstants.SECRET.getBytes())).build()
                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getSubject();
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}
