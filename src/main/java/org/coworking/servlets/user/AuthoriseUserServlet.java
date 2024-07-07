package org.coworking.servlets.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liquibase.exception.LiquibaseException;
import org.coworking.Utils.ObjectMapperUtil;
import org.coworking.Utils.ServletUtils;
import org.coworking.Utils.exceptions.UserAuthorisationException;
import org.coworking.annotations.Loggable;
import org.coworking.annotations.LoginAudit;
import org.coworking.dtos.UserDTO;
import org.coworking.models.User;
import org.coworking.services.validators.UserValidator;

import java.io.IOException;
import java.sql.SQLException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.coworking.Utils.ServletUtils.setMessage;

/**
 * Сервлет выполняющий авторизацию пользователя
 */
@Loggable
@LoginAudit
public class AuthoriseUserServlet extends HttpServlet {

    /**
     * Сервис для работы с пользователем
     */
    private final UserValidator userValidator;

    /**
     * Объект позволяеющий трансвормировать JSON в объекты и наоборот
     */
    private final ObjectMapperUtil objectMapperUtil;

    public AuthoriseUserServlet(UserValidator userValidator, ObjectMapperUtil objectMapperUtil) throws SQLException, LiquibaseException {
        this.userValidator = userValidator;
        this.objectMapperUtil = objectMapperUtil;
    }

    /**
     * Метод POST, выполняющий авторизацию пользователя
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
            User currentUser = userValidator.getValidatedAuthorisedUser(userDTO);
            ServletUtils.setCurrentUser(currentUser);
            final String message = "Авторизация выполнена успешно";
            setMessage(resp, message, SC_OK);
            System.out.println(ServletUtils.getCurrentUser());
        } catch (UserAuthorisationException e) {
            setMessage(resp,e.getMessage(), SC_BAD_REQUEST);
        }

    }
}
