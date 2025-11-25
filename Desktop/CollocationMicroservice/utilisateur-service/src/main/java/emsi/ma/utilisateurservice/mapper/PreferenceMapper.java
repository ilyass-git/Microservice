package emsi.ma.utilisateurservice.mapper;

import emsi.ma.utilisateurservice.domain.dto.PreferenceDto;
import emsi.ma.utilisateurservice.domain.entity.Preference;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PreferenceMapper {
    Preference toEntity(PreferenceDto preferenceDto);
    PreferenceDto toDto(Preference preference);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Preference partialUpdate(PreferenceDto preferenceDto, @MappingTarget Preference preference);
}




