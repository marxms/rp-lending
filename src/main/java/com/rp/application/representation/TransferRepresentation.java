package com.rp.application.representation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRepresentation {

    private String sourceWalletKey;
    private String destinationWalletKey;
    private BigDecimal amount;

}
