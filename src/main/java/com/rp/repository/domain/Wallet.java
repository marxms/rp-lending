package com.rp.repository.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Introspected
@Table(name = "wallet", indexes = {
        @Index(name = "idx_wallet_key", columnList = "key", unique = true),
})
@Data
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String key;
    private String ownerDocument;
    private BigDecimal balance;
    @DateCreated
    private Date createdAt;
    @DateUpdated
    private Date updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sourceWallet")
    private List<Transaction> transactions;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sourceWallet")
    private List<Transfer> outgoingTransfers;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "targetWallet")
    private List<Transfer> incomingTransfers;
}
