package com.dtnexus.crm.service;

import com.dtnexus.crm.model.User;
import com.dtnexus.crm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TrialAccessService {

    private static final int TRIAL_DAYS = 30;
    private final UserRepository userRepository;

    public TrialAccessService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean hasAccess(String username, String tenant) {
        User user = userRepository.findByUsernameAndTenant(username, tenant).orElse(null);
        if (user == null) return false;
        if ("ASSINANTE_PAGO".equals(user.getAccountStatus())) return true;
        if (!"TRIAL_ATIVO".equals(user.getAccountStatus())) return false;

        LocalDate expirationDate = user.getTrialStartDate().plusDays(TRIAL_DAYS);
        if (!LocalDate.now().isBefore(expirationDate)) {
            user.setAccountStatus("EXPIRADO");
            userRepository.save(user);
            return false;
        }
        return true;
    }

    public long remainingDays(User user) {
        if ("ASSINANTE_PAGO".equals(user.getAccountStatus())) return Long.MAX_VALUE;
        return Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), user.getTrialStartDate().plusDays(TRIAL_DAYS)));
    }
}
