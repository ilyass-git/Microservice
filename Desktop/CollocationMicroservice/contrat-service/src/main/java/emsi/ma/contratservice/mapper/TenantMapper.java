package emsi.ma.contratservice.mapper;

import emsi.ma.contratservice.domain.dto.TenantDto;
import emsi.ma.contratservice.domain.entity.Tenant;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TenantMapper {
    Tenant toEntity(TenantDto tenantDto);
    TenantDto toDto(Tenant tenant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tenant partialUpdate(TenantDto tenantDto, @MappingTarget Tenant tenant);
}






