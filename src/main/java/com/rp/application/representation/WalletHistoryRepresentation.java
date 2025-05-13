package com.rp.application.representation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletHistoryRepresentation {
    private List<WalletRepresentation> history;
}
