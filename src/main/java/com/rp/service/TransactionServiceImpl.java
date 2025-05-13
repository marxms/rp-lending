package com.rp.service;

import com.rp.application.representation.TransactionRepresentation;
import com.rp.application.representation.mapping.TransactionMapper;
import com.rp.repository.TransactionRepository;
import com.rp.repository.WalletRepository;
import com.rp.repository.domain.TransactionType;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;

@Singleton
@RequiredArgsConstructor
public class TransactionServiceImpl {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private static final TransactionMapper TRANSACTION_MAPPER = TransactionMapper.INSTANCE;

    @Transactional
    public TransactionRepresentation postTransaction(TransactionRepresentation transactionRepresentation) {
        var optionalWallet = walletRepository.findByKey(transactionRepresentation.getWalletKey());
        if (optionalWallet.isPresent()) {
            var wallet = optionalWallet.get();
            var transaction = TRANSACTION_MAPPER.fromRepresentationToDomain(transactionRepresentation);
            transaction.setSourceWallet(wallet);
            var result = transactionRepository.save(transaction);
            if (transactionRepresentation.getTransactionType() == TransactionType.DEPOSIT) {
                wallet.setBalance(ofNullable(wallet.getBalance()).orElse(BigDecimal.ZERO).add(transactionRepresentation.getAmount()));
                walletRepository.save(wallet);
            } else {
                wallet.setBalance(wallet.getBalance().subtract(transactionRepresentation.getAmount()));
                walletRepository.save(wallet);
            }
            return TRANSACTION_MAPPER.fromDomainToRepresentation(result);
        }
        throw new IllegalArgumentException("Wallet not found");

    }
}
