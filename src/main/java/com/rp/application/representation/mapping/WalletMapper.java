package com.rp.application.representation.mapping;

import com.rp.application.representation.WalletRepresentation;
import com.rp.repository.domain.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WalletMapper {
    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);
    WalletRepresentation fromDomainToRepresentation(Wallet wallet);
    Wallet fromRepresentationToDomain(WalletRepresentation walletRepresentation);
}
