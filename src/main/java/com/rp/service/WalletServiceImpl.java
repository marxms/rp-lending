package com.rp.service;

import com.rp.application.representation.WalletHistoryRepresentation;
import com.rp.application.representation.WalletRepresentation;
import com.rp.application.representation.mapping.WalletMapper;
import com.rp.repository.WalletRepository;
import com.rp.repository.domain.Wallet;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class WalletServiceImpl {

    private final WalletRepository walletRepository;
    private static final WalletMapper MAPPER = WalletMapper.INSTANCE;
    @PersistenceContext
    private final EntityManager entityManager;

    public Optional<WalletRepresentation> getWalletByKey(String walletKey) {
        return walletRepository.findByKey(walletKey)
                .map(MAPPER::fromDomainToRepresentation);
    }

    public WalletRepresentation save(WalletRepresentation walletRepresentation) {
        var wallet = MAPPER.fromRepresentationToDomain(walletRepresentation);
        wallet.setKey(UUID.randomUUID().toString());
        return MAPPER.fromDomainToRepresentation(walletRepository.save(wallet));
    }

    @SuppressWarnings("unchecked")
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public WalletHistoryRepresentation getWalletHistory(String walletKey, int page, int size) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        WalletHistoryRepresentation walletHistoryRepresentation = new WalletHistoryRepresentation();
        List<Wallet> queryResult = auditReader.createQuery()
                .forRevisionsOfEntity(Wallet.class, true, true)
                .add(AuditEntity.property("key").eq(walletKey))
                .addOrder(AuditEntity.revisionNumber().desc())
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        walletHistoryRepresentation.setHistory(queryResult.stream()
                .map(MAPPER::fromDomainToRepresentation)
                .toList());
        return walletHistoryRepresentation;
    }
}
