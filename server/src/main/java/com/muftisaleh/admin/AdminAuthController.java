package com.muftisaleh.admin;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.muftisaleh.admin.AdminDtos.AdminDto;
import com.muftisaleh.admin.AdminDtos.ChangePasswordRequest;
import com.muftisaleh.admin.AdminDtos.LoginRequest;
import com.muftisaleh.admin.AdminDtos.LoginResponse;
import com.muftisaleh.common.error.ApiException;
import com.muftisaleh.security.AuthPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Auth", description = "Admin authentication")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService authService;
    private final AdminRepository admins;

    @PostMapping("/auth/login")
    @SecurityRequirements
    @Operation(summary = "Admin login — username + password → JWT")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/admin/auth/me")
    @Operation(summary = "Get the current admin's profile")
    public AdminDto me(@AuthenticationPrincipal AuthPrincipal principal) {
        Admin admin = admins.findById(principal.adminId())
                .orElseThrow(() -> ApiException.notFound("Admin not found.", "অ্যাডমিন পাওয়া যায়নি।"));
        return AdminAuthService.toDto(admin);
    }

    @PostMapping("/admin/auth/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Change the current admin's password")
    public void changePassword(@AuthenticationPrincipal AuthPrincipal principal,
                               @Valid @RequestBody ChangePasswordRequest request) {
        Admin admin = admins.findById(principal.adminId())
                .orElseThrow(() -> ApiException.notFound("Admin not found.", "অ্যাডমিন পাওয়া যায়নি।"));
        authService.changePassword(admin, request.currentPassword(), request.newPassword());
    }

    @PostMapping("/auth/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "End the session — tokens are stateless; this is the client discarding them")
    public void logout() {
        // stateless — client discards token
    }
}
