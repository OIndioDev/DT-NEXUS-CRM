package com.dtnexus.crm.repository;

import com.dtnexus.crm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndTenant(String username, String tenant);
}
