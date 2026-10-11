package com.muftisaleh.security;

import java.util.UUID;

/**
 * Who is calling, as proved by the JWT and nothing else.
 */
public record AuthPrincipal(UUID adminId, String displayName) {
}
