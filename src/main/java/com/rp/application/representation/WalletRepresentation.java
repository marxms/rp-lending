package com.rp.application.representation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletRepresentation {
    private String key;
    private String ownerDocument;
    private BigDecimal balance;
    private Date createdAt;
    private Date updatedAt;
}
