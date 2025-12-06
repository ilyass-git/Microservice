package emsi.ma.utilisateurservice.mapper;

import emsi.ma.utilisateurservice.domain.dto.UserCreateDto;
import emsi.ma.utilisateurservice.domain.dto.UserDto;
import emsi.ma.utilisateurservice.domain.dto.UserResponseDto;
import emsi.ma.utilisateurservice.domain.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserDto userDto);
    UserDto toDto(User user);
    
    User toEntityFromCreate(UserCreateDto userCreateDto);
    UserResponseDto toResponseDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserDto userDto, @MappingTarget User user);
}






