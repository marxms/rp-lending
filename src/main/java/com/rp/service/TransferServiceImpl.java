package com.rp.service;

import com.rp.application.representation.TransferRepresentation;
import com.rp.application.representation.mapping.TransferMapper;
import com.rp.repository.TransferRepository;
import com.rp.repository.WalletRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;

@Singleton
@RequiredArgsConstructor
public class TransferServiceImpl {

    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;
    private static final TransferMapper TRANSFER_MAPPER = TransferMapper.INSTANCE;

    @Transactional
    public TransferRepresentation postTransfer(TransferRepresentation transferRepresentation) {
        var optionalSourceWallet = walletRepository.findByKey(transferRepresentation.getSourceWalletKey());
        var optionalDestinationWallet = walletRepository.findByKey(transferRepresentation.getDestinationWalletKey());
        if (optionalSourceWallet.isPresent() && optionalDestinationWallet.isPresent()) {
            var sourceWallet = optionalSourceWallet.get();
            var destinationWallet = optionalDestinationWallet.get();
            if (sourceWallet.getBalance().compareTo(transferRepresentation.getAmount()) < 0) {
                throw new IllegalArgumentException("Insufficient funds in source wallet");
            }
            var transfer = TRANSFER_MAPPER.fromRepresentationToDomain(transferRepresentation);
            transfer.setSourceWallet(sourceWallet);
            transfer.setTargetWallet(destinationWallet);
            var result = transferRepository.save(transfer);
            sourceWallet.setBalance(ofNullable(sourceWallet.getBalance()).orElse(BigDecimal.ZERO).subtract(transferRepresentation.getAmount()));
            destinationWallet.setBalance(ofNullable(destinationWallet.getBalance()).orElse(BigDecimal.ZERO).add(transferRepresentation.getAmount()));
            walletRepository.save(sourceWallet);
            walletRepository.save(destinationWallet);
            return TRANSFER_MAPPER.fromDomainToRepresentation(result);
        }
        throw new IllegalArgumentException("Wallet not found");
    }

}
