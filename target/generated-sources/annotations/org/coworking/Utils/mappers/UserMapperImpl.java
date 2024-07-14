package org.coworking.Utils.mappers;

import javax.annotation.processing.Generated;
import org.coworking.dtos.UserDTO;
import org.coworking.models.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-14T15:35:48+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public User userDtoToUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userDTO.getId() );
        user.name( userDTO.getName() );
        user.password( userDTO.getPassword() );
        user.role( userDTO.getRole() );

        return user.build();
    }

    @Override
    public UserDTO userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( user.getId() );
        userDTO.name( user.getName() );
        userDTO.password( user.getPassword() );
        userDTO.role( user.getRole() );

        return userDTO.build();
    }
}
