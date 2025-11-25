package emsi.ma.utilisateurservice.mapper;

import emsi.ma.utilisateurservice.domain.dto.ProfileDto;
import emsi.ma.utilisateurservice.domain.entity.Profile;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProfileMapper {
    Profile toEntity(ProfileDto profileDto);
    ProfileDto toDto(Profile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Profile partialUpdate(ProfileDto profileDto, @MappingTarget Profile profile);
}




