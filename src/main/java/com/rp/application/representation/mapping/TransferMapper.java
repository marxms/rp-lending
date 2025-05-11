package com.rp.application.representation.mapping;

import com.rp.application.representation.TransferRepresentation;
import com.rp.repository.domain.Transfer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransferMapper {
    TransferMapper INSTANCE = Mappers.getMapper(TransferMapper.class);

    TransferRepresentation fromDomainToRepresentation(Transfer transfer);

    Transfer fromRepresentationToDomain(TransferRepresentation transferRepresentation);
}
