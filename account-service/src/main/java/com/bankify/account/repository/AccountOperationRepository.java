package com.bankify.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankify.account.entity.AccountOperation;

public interface AccountOperationRepository
        extends JpaRepository<AccountOperation, Long> {

    Optional<AccountOperation> findByOperationKey(String operationKey);

    boolean existsByOperationKey(String operationKey);
}