package emsi.ma.contratservice.mapper;

import emsi.ma.contratservice.domain.dto.ContractDto;
import emsi.ma.contratservice.domain.entity.Contract;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContractMapper {
    Contract toEntity(ContractDto contractDto);
    ContractDto toDto(Contract contract);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Contract partialUpdate(ContractDto contractDto, @MappingTarget Contract contract);
}




