package az.gdg.msauth.mapper;

import az.gdg.msauth.model.dto.UserDetail;
import az.gdg.msauth.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDetail entityToDto(UserEntity userEntity);

    List<UserDetail> entityToDtoList(List<UserEntity> userEntity);

}
