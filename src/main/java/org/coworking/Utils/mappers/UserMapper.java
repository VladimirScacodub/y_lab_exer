package org.coworking.Utils.mappers;

import org.coworking.annotations.Loggable;
import org.coworking.dtos.UserDTO;
import org.coworking.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Класс Mapper, который переводит DTO в User и обратно
 */
@Loggable
@Mapper
public interface UserMapper {

    /**
     * Экземпляр данного Mapper
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Транформация UserDTO объекта в User
     *
     * @param userDTO DTO, содержащая данные о пользователе
     * @return User объект
     */
    User userDtoToUser(UserDTO userDTO);

    /**
     * Трансформация User объекта в UserDTO
     *
     * @param user объект User, содержащий данные о пользователе
     * @return UserDTO объект
     */
    UserDTO userToUserDto(User user);
}
