package com.bankify.transaction.entity;

import java.time.LocalDateTime;

import com.bankify.transaction.enums.TransactionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transaction_audits")
@Getter
@Setter
public class TransactionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long transactionId;

    @Column(nullable = false)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    private TransactionStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus newStatus;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    private String reason;
}