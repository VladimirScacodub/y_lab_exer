package org.coworking.servlets.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.coworking.Utils.ObjectMapperUtil;
import org.coworking.Utils.mappers.UserMapper;
import org.coworking.Utils.exceptions.UserRegistrationException;
import org.coworking.annotations.Loggable;
import org.coworking.annotations.RegisterAudit;
import org.coworking.dtos.UserDTO;
import org.coworking.models.User;
import org.coworking.services.UserService;
import org.coworking.services.validators.UserValidator;

import java.io.IOException;
import java.sql.SQLException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.coworking.Utils.ServletUtils.setMessage;

/**
 * Сервлет выполняющий регистрацию пользователя
 */
@Loggable
@RegisterAudit
public class RegisterUserServlet extends HttpServlet {

    /**
     * Сервис для работы с пользователем
     */
    private final UserService userService;

    /**
     * Сервис для валидации пользовательских данных
     */
    private final UserValidator userValidator;

    /**
     * Объект позволяеющий трансвормировать JSON в объекты и наоборот
     */
    private final ObjectMapperUtil objectMapperUtil;

    public RegisterUserServlet(UserService userService, UserValidator userValidator, ObjectMapperUtil objectMapperUtil) throws SQLException, LiquibaseException {
        this.userService = userService;
        this.userValidator = userValidator;
        this.objectMapperUtil = objectMapperUtil;
    }

    /**
     * Метод POST, регистрирующий пользователя
     *
     * @param req HTTP запрос
     * @param resp HTTP ответ
     * @throws ServletException если возникает броблемы с сервлетом
     * @throws IOException если возникает  проблемы с потоками I/O
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        UserDTO userDTO = objectMapperUtil.getDto(req, UserDTO.class);
        try {
            userValidator.validateUserRegistration(userDTO);
            User user = UserMapper.INSTANCE.userDtoToUser(userDTO);
            userService.registerNewUser(user);
            final String message = "Пользователь успешно зарегестрировался";
            resp.setHeader("username", user.getName());
            setMessage(resp, message, SC_OK);
        } catch (UserRegistrationException e) {
            setMessage(resp, e.getMessage(), SC_BAD_REQUEST);
        }
    }
}
