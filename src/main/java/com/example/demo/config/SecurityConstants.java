package com.example.demo.config;

/**
 * This class defines constants related to security settings.
 */
public class SecurityConstants {
    // Secret key for JWT (Json Web Token) generation
    public static final String SECRET = "GG";

    // Token expiration time (in milliseconds) - 10 days
    public static final long EXPIRATION_TIME = 864_000_000;

    // Token prefix used in Authorization header
    public static final String TOKEN_PREFIX = "Bearer ";

    // Name of the Authorization header in HTTP requests
    public static final String HEADER_STRING = "Authorization";

    // URL for user registration
    public static final String SIGN_UP_URL = "/api/user/create";
}
