package com.rp.service;

import com.rp.application.representation.WalletHistoryRepresentation;
import com.rp.application.representation.WalletRepresentation;
import com.rp.application.representation.mapping.WalletMapper;
import com.rp.repository.WalletRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class WalletServiceImpl {

    private final WalletRepository walletRepository;
    private static final WalletMapper MAPPER = WalletMapper.INSTANCE;


    public Optional<WalletRepresentation> getWalletByKey(String walletKey) {
        return walletRepository.findByKey(walletKey)
                .map(MAPPER::fromDomainToRepresentation);
    }

    public WalletRepresentation save(WalletRepresentation walletRepresentation) {
        var wallet = MAPPER.fromRepresentationToDomain(walletRepresentation);
        wallet.setKey(UUID.randomUUID().toString());
        return MAPPER.fromDomainToRepresentation(walletRepository.save(wallet));
    }

    public WalletHistoryRepresentation getWalletHistory(String walletKey, int page, int size) {
        WalletHistoryRepresentation walletHistoryRepresentation = new WalletHistoryRepresentation();
        var queryResult = walletRepository.getWalletHistory(walletKey, page, size);
        walletHistoryRepresentation.setHistory(queryResult.stream()
                .map(MAPPER::fromDomainToRepresentation)
                .toList());
        return walletHistoryRepresentation;
    }
}
