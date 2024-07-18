package org.coworking.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.coworking.Utils.exceptions.UserRegistrationException;
import org.coworking.Utils.mappers.UserMapper;
import org.coworking.annotations.Loggable;
import org.coworking.dtos.MessageDTO;
import org.coworking.dtos.UserDTO;
import org.coworking.models.User;
import org.coworking.services.UserService;
import org.coworking.services.validators.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Класс контроллер, который отвечает на запросы связанные с пользователями
 */
@RestController
@RequiredArgsConstructor
@Api(value = "Пользователи", description = "Операция связанная с пользователями")
@Loggable
public class UserController {

    /**
     * Сервис для работы с пользователем
     */
    private final UserService userService;

    /**
     * Сервис для валидации пользовательских данных
     */
    private final UserValidator userValidator;


    /**
     * Метод POST выполняющий регистрацию пользователя
     *
     * @param userDTO даныые для регистрации новго пользователя
     * @return ResponseEntity, содержащий HTTP ответ
     * @throws UserRegistrationException в случае если пользователь введет неправильные данные для регистрации
     */
    @ApiOperation(value = "Регистрация нового пользователя", notes = "Метод POST выполняющий регистрацию пользователя")
    @PostMapping("/register-user")
    public ResponseEntity<MessageDTO> registerUser(@RequestBody UserDTO userDTO) throws UserRegistrationException {
        userValidator.validateUserRegistration(userDTO);
        User user = UserMapper.INSTANCE.userDtoToUser(userDTO);
        userService.registerNewUser(user);
        final String message = "Пользователь успешно зарегистрировался";
        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.add("name", user.getName()))
                .body(new MessageDTO(message));
    }

}
