package emsi.ma.annonceservice.mapper;

import emsi.ma.annonceservice.domain.dto.RoomDto;
import emsi.ma.annonceservice.domain.entity.Room;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {
    Room toEntity(RoomDto roomDto);
    RoomDto toDto(Room room);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(RoomDto roomDto, @MappingTarget Room room);
}




