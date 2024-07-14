package org.coworking.repositories;

import org.coworking.models.User;
import org.coworking.models.enums.Role;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с хранилищем пользовательских данных
 */
public interface UserRepository {

    /**
     * Сохранение данных о новом пользователе в хранилище
     * @param username имя пользователя
     * @param password пароль к аккаунту
     * @param role роль пользователя в системе
     * @return объект содержащий все данные о сохраненном пользователе
     */
    User save(String username, String password, Role role);

    /**
     * Получение данных о пользовтеле по его имени из храналища
     * @param username имя пользователя
     * @return Optional объект, оборачивающий пользовательские данные
     */
    Optional<User> findByUsername(String username);

    /**
     * Получение всех пользовательских данных их хранилища
     * @return спиок объектв User
     */
    List<User> findAll();


}
