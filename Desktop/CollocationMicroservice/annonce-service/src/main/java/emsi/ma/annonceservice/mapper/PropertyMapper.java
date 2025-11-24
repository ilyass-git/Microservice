package emsi.ma.annonceservice.mapper;

import emsi.ma.annonceservice.domain.dto.PropertyDto;
import emsi.ma.annonceservice.domain.entity.Property;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PropertyMapper {
    Property toEntity(PropertyDto propertyDto);
    PropertyDto toDto(Property property);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Property partialUpdate(PropertyDto propertyDto, @MappingTarget Property property);
}

