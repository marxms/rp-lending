package com.rp.repository.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Introspected
@Table(name = "wallet", indexes = {
        @Index(name = "idx_wallet_key", columnList = "key", unique = true),
})
@Audited
@AuditTable("wallet_hst")
@Data
public class Wallet {
    @Id
    @GeneratedValue(generator = "wallet_id_increment", strategy = GenerationType.AUTO)
    private Long id;
    private String key;
    private String ownerDocument;
    private BigDecimal balance;
    @DateCreated
    private Date createdAt;
    @DateUpdated
    private Date updatedAt;
    @NotAudited
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sourceWallet")
    private List<Transaction> transactions;
    @NotAudited
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sourceWallet")
    private List<Transfer> outgoingTransfers;
    @NotAudited
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "targetWallet")
    private List<Transfer> incomingTransfers;

}
