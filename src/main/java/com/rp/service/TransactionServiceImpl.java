package com.rp.service;

import com.rp.application.representation.TransactionRepresentation;
import com.rp.application.representation.mapping.TransactionMapper;
import com.rp.repository.TransactionRepository;
import com.rp.repository.WalletRepository;
import com.rp.repository.domain.Transaction;
import com.rp.repository.domain.TransactionType;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Singleton
@RequiredArgsConstructor
public class TransactionServiceImpl {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private static final TransactionMapper TRANSACTION_MAPPER = TransactionMapper.INSTANCE;

    // Example method to create an order
    public TransactionRepresentation postTransaction(TransactionRepresentation transactionRepresentation) {
        Transaction result = null;
        var optionalWallet = walletRepository.findByKey(transactionRepresentation.getWalletKey());
        if (optionalWallet.isPresent()) {
            var wallet = optionalWallet.get();
            var transaction = TRANSACTION_MAPPER.fromRepresentationToDomain(transactionRepresentation);
            transaction.setSourceWallet(wallet);
            result = transactionRepository.save(transaction);
            if (transactionRepresentation.getTransactionType() == TransactionType.DEPOSIT) {
                walletRepository.updateBalanceByKey(wallet.getKey(), ofNullable(wallet.getBalance()).orElse(BigDecimal.ZERO).add(transactionRepresentation.getAmount()));
            } else {
                walletRepository.updateBalanceByKey(wallet.getKey(), ofNullable(wallet.getBalance()).orElse(BigDecimal.ZERO).subtract(transactionRepresentation.getAmount()));
            }
            return TRANSACTION_MAPPER.fromDomainToRepresentation(result);
        }
        throw new RuntimeException("Wallet not found");

    }

    public TransactionRepresentation getTransactionById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return transaction.map(TRANSACTION_MAPPER::fromDomainToRepresentation).orElse(null);
    }
}
