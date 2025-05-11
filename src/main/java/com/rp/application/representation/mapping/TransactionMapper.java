package com.rp.application.representation.mapping;

import com.rp.application.representation.TransactionRepresentation;
import com.rp.repository.domain.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    TransactionRepresentation fromDomainToRepresentation(Transaction transaction);
    Transaction fromRepresentationToDomain(TransactionRepresentation transactionRepresentation);
}
