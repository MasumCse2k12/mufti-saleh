package com.muftisaleh.admin;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.muftisaleh.admin.AdminDtos.AdminDto;
import com.muftisaleh.admin.AdminDtos.LoginRequest;
import com.muftisaleh.admin.AdminDtos.LoginResponse;
import com.muftisaleh.common.audit.ActorKind;
import com.muftisaleh.common.audit.AuditAction;
import com.muftisaleh.common.audit.AuditContext;
import com.muftisaleh.common.audit.AuditTrail;
import com.muftisaleh.common.error.ApiException;
import com.muftisaleh.config.AppProperties;
import com.muftisaleh.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAuthService {

    private final AdminRepository admins;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;
    private final AppProperties props;
    private final AdminLoginAttempts attempts;
    private final AuditTrail trail;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = request.username().strip();
        Admin admin = admins.findByUsernameIgnoringCase(username).orElse(null);

        if (admin == null) {
            attempts.refused(username, "no such account");
            throw wrongCredentials();
        }
        if (admin.isLocked()) {
            attempts.refused(username, "locked out");
            throw ApiException.tooManyRequests(
                    "Too many failed attempts. Try again shortly.",
                    "অনেকবার ভুল হয়েছে। কিছুক্ষণ পর আবার চেষ্টা করুন।");
        }
        if (!admin.isActive()) {
            attempts.refused(username, "account disabled");
            throw wrongCredentials();
        }
        if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            attempts.wrongPassword(admin);
            throw wrongCredentials();
        }

        admin.setFailedAttempts(0);
        admin.setLockedUntil(null);
        admin.setLastLoginAt(Instant.now());

        // Re-bind the actor now that we know who it is, so the LOGIN row is attributed
        var actor = AuditContext.current().identified(admin.getId(), ActorKind.ADMIN, admin.getUsername());
        AuditContext.runAs(actor, () -> {
            admins.saveAndFlush(admin);
            trail.record(AuditAction.LOGIN, AdminLoginAttempts.SUBJECT, admin.getId().toString());
        });

        log.info("Admin {} signed in", admin.getUsername());
        String token = jwt.issueAdminAccess(admin.getId(), admin.getDisplayName());
        return new LoginResponse(token, props.jwt().adminAccessTtl().toSeconds(), toDto(admin));
    }

    @Transactional
    public void changePassword(Admin admin, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, admin.getPasswordHash())) {
            throw ApiException.unauthorized("Current password is wrong.", "বর্তমান পাসওয়ার্ড ভুল।");
        }
        admin.setPasswordHash(passwordEncoder.encode(newPassword));
        admin.setMustChangePassword(false);
        admins.save(admin);
        trail.record(AuditAction.PASSWORD_SET, AdminLoginAttempts.SUBJECT, admin.getId().toString());
    }

    public static AdminDto toDto(Admin admin) {
        return new AdminDto(
                admin.getId(),
                admin.getUsername(),
                admin.getDisplayName(),
                admin.getRole(),
                admin.isActive(),
                admin.isMustChangePassword(),
                admin.getLastLoginAt());
    }

    private static ApiException wrongCredentials() {
        return ApiException.unauthorized("Wrong username or password.", "ব্যবহারকারীর নাম বা পাসওয়ার্ড ভুল।");
    }
}
