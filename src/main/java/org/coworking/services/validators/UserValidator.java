package org.coworking.services.validators;

import lombok.AllArgsConstructor;
import org.coworking.Utils.exceptions.UserAuthorisationException;
import org.coworking.Utils.exceptions.UserRegistrationException;
import org.coworking.models.User;
import org.coworking.services.UserService;

import java.util.List;
import java.util.Objects;

/**
 * Класс используется для валидации пользовательских данных
 */
@AllArgsConstructor
public class UserValidator {

    /**
     * Зависимость используемая для получения пользовательских данных
     */
    UserService userService;

    /**
     * Валидирует данные для регистрацию нового пользователя
     *
     * @param username имя пользователя
     * @param password пароль
     * @throws UserRegistrationException в случае если данные для регистрации были введены неправильно
     */
    public void validateUserRegistration(String username, String password) throws UserRegistrationException {

        if (username.trim().isEmpty()) {
            throw new UserRegistrationException("Имя пользователя должно содержать символы!");
        }
        if (isUsernameAlreadyExists(username)) {
            throw new UserRegistrationException("Пользователь с таким именем уже существует!");
        }
        if (password.trim().isEmpty()) {
            throw new UserRegistrationException("Пароль должен содержать символы!");
        }
    }

    /**
     * Прозводит авторизацию пользователя, используя имя и пароль
     *
     * @param username Имя пользователя
     * @param password Пароль
     * @return User объект авторизированного пользователя
     * @throws UserAuthorisationException в случае если пользоватедя нет или он ввел неправильный пароль
     */
    public User getValidatedAuthorisedUser(String username, String password) throws UserAuthorisationException {
        User user = userService.getUserByName(username)
                .orElseThrow(() -> new UserAuthorisationException("Пользователя с таким именем не сущестует"));
        if (!Objects.equals(user.getPassword(), password)) {
            throw new UserAuthorisationException("Был введен неправильный пароль!");
        }
        return user;
    }

    /**
     * Проверка на существование пользователя в в памяти
     *
     * @param username Имя пользователя
     * @return true если существует, иначе false
     */
    private boolean isUsernameAlreadyExists(String username) {
        List<User> users = userService.findAll();
        return users.stream().anyMatch(user -> Objects.equals(user.getName(), username));
    }

}
