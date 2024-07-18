package org.coworking.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.coworking.models.enums.Role;

/**
 * Клас объектов для передачи данных пользователя
 */
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserDTO {

    /**
     * Идентификатор пользователя
     */
    private int id;

    /**
     * Имя пользователя нужный для авторизации
     */
    @ApiModelProperty(example = "john_doe")
    private String name;

    /**
     * Пароль пользователя нужный для авторизации
     */
    @ApiModelProperty(example = "john_password")
    private String password;

    /**
     * Роль пользователя в системе
     */
    @ApiModelProperty(example = "USER")
    private Role role;

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
