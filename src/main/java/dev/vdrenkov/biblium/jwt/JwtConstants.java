package dev.vdrenkov.biblium.jwt;

public final class JwtConstants {
    public static final long JWT_TOKEN_VALIDITY = 60L * 60L;

    public static final String JWT_COOKIE_NAME = "BIBLIUM_AUTH";

    private JwtConstants() {
        throw new IllegalStateException("Utility class. Do not instantiate!");
    }
}
