package com.example.demo.config;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.User;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

/**
 * This class extends UsernamePasswordAuthenticationFilter to handle JWT authentication.
 */
@RequiredArgsConstructor
public class TokenBasedAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;



    /**
     * Attempt user authentication.
     *
     * @param req The HTTP request.
     * @param res The HTTP response.
     * @return Authentication result.
     * @throws AuthenticationException If authentication fails.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            // Read user credentials from the request and attempt authentication
            com.example.demo.model.persistence.User credentials = new ObjectMapper()
                    .readValue(req.getInputStream(), com.example.demo.model.persistence.User.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getUsername(),
                            credentials.getPassword(),
                            new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handle successful user authentication.
     *
     * @param req    The HTTP request.
     * @param res    The HTTP response.
     * @param chain  The filter chain.
     * @param auth   The user's authentication details.
     * @throws IOException      If an I/O error occurs.
     * @throws ServletException If a servlet exception occurs.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        // Create and add a JWT token to the response header
        String token = JWT.create()
                .withSubject(((User) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    }

    /**
     * Handle unsuccessful user authentication.
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param failed The authentication exception that occurred.
     * @throws IOException If an I/O error occurs.
     * @throws ServletException If a servlet exception occurs.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
