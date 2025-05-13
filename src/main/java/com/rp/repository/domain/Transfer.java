package com.rp.repository.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Introspected
@Table(name = "transfer")
@Data
public class Transfer {

    @Id
    @GeneratedValue(generator = "transfer_id_increment", strategy = GenerationType.AUTO)
    private Long id;

    private String transactionID;

    @DateCreated
    private Date createdAt;

    @DateUpdated
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Wallet sourceWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    private Wallet targetWallet;

    private BigDecimal amount;

}
 
