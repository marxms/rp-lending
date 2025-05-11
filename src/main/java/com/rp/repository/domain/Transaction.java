package com.rp.repository.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Introspected
@Entity
@Table(name = "transaction")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @DateCreated
    private String createdAt;
    @DateUpdated
    private String updatedAt;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private BigDecimal amount;
    @ManyToOne(fetch = FetchType.LAZY)
    private Wallet sourceWallet;

}
