package emsi.ma.annonceservice.mapper;

import emsi.ma.annonceservice.domain.dto.AdDto;
import emsi.ma.annonceservice.domain.entity.Ad;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdMapper {
    Ad toEntity(AdDto adDto);
    AdDto toDto(Ad ad);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Ad partialUpdate(AdDto adDto, @MappingTarget Ad ad);
}

