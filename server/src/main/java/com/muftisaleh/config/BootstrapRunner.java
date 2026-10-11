package com.muftisaleh.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.muftisaleh.admin.Admin;
import com.muftisaleh.admin.AdminRepository;
import com.muftisaleh.admin.AdminRole;
import com.muftisaleh.common.audit.AuditActor;
import com.muftisaleh.common.audit.AuditContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Creates the first super admin on an empty database.
 * Only runs when no admins exist — never resets access on a live database.
 */
@Component
@Slf4j
public class BootstrapRunner implements ApplicationRunner {

    private final AdminRepository admins;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties props;
    private final TransactionTemplate transactions;

    BootstrapRunner(AdminRepository admins, PasswordEncoder passwordEncoder,
                    AppProperties props, PlatformTransactionManager transactionManager) {
        this.admins = admins;
        this.passwordEncoder = passwordEncoder;
        this.props = props;
        this.transactions = new TransactionTemplate(transactionManager);
    }

    @Override
    public void run(ApplicationArguments args) {
        AuditContext.runAs(AuditActor.system("bootstrap"),
                () -> transactions.executeWithoutResult(status -> createFirstAdmin()));
    }

    private void createFirstAdmin() {
        AppProperties.Bootstrap config = props.bootstrap();
        if (!config.enabled() || admins.count() > 0) {
            return;
        }
        if (config.password() == null || config.password().isBlank()) {
            log.warn("No admin accounts exist and app.bootstrap.password is not set. "
                    + "Set it and restart to create the first super admin.");
            return;
        }

        Admin admin = new Admin();
        admin.setUsername(config.username());
        admin.setDisplayName(config.displayName());
        admin.setPasswordHash(passwordEncoder.encode(config.password()));
        admin.setRole(AdminRole.SUPER_ADMIN);
        admin.setActive(true);
        admin.setMustChangePassword(true);
        admins.save(admin);

        log.warn("Created the first super admin '{}' from configuration. "
                + "Change this password now, and remove app.bootstrap.password from the environment.",
                config.username());
    }
}
