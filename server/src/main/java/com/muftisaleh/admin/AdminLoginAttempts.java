package com.muftisaleh.admin;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.muftisaleh.common.audit.AuditAction;
import com.muftisaleh.common.audit.AuditTrail;
import com.muftisaleh.common.error.ApiException;
import com.muftisaleh.config.AppProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Records failed login attempts and enforces lockout.
 * Uses REQUIRES_NEW so the record commits even when the outer transaction rolls back.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminLoginAttempts {

    static final String SUBJECT = "admin";

    private final AdminRepository admins;
    private final AppProperties props;
    private final AuditTrail trail;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void refused(String username, String reason) {
        log.warn("Admin login refused for '{}': {}", username, reason);
        trail.record(AuditAction.LOGIN_FAILED, SUBJECT, username);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void wrongPassword(Admin admin) {
        int attempts = admin.getFailedAttempts() + 1;
        admin.setFailedAttempts(attempts);

        if (attempts >= props.security().maxFailedLogins()) {
            admin.setLockedUntil(Instant.now().plus(props.security().lockoutDuration()));
            admin.setFailedAttempts(0);
            admins.save(admin);
            trail.record(AuditAction.LOCKOUT, SUBJECT, admin.getId().toString());
            throw ApiException.tooManyRequests(
                    "Too many failed attempts. Try again shortly.",
                    "অনেকবার ভুল হয়েছে। কিছুক্ষণ পর আবার চেষ্টা করুন।");
        }

        admins.save(admin);
        trail.record(AuditAction.LOGIN_FAILED, SUBJECT, admin.getId().toString());
    }
}
