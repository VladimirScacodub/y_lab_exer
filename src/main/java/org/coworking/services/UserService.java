package org.coworking.services;

import org.coworking.models.User;
import org.coworking.models.enums.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис для работы с User и хранящий пользователей в памяти, где по умолчанию создается пользователь admin
 */
public class UserService {

    /**
     * Хранилище для пользовательских данных
     */
    private static List<User> users = new ArrayList<>();

    /**
     * Генератор идентификаторов для объектов User
     */
    private static int lastId = 1;

    public UserService(){
        registerNewUser("admin", "admin", Role.ADMIN);
    }

    /**
     * Вызращает список всех пользователей
     * @return список User
     */
    public List<User> findAll(){
        return users;
    }

    /**
     * Ищет пользователя по имени
     * @param username - имя пользователя
     * @return Optional, которое может содержать объект User или null
     */
    public Optional<User> getUserByName(String username){
        return users.stream()
                .filter(user -> Objects.equals(user.getName(), username))
                .findAny();
    }

    /**
     * Регистрирует нового пользователя
     * @param username - имя пользователя
     * @param password - пароль пользовтеля
     * @param role - роль в системе
     * @return созданный и добавленный в память User объект
     */
    public User registerNewUser(String username, String password, Role role){
        User newUser = User.builder()
                .id(lastId++)
                .name(username)
                .password(password)
                .role(role)
                .build();
        users.add(newUser);
        return newUser;
    }
}
