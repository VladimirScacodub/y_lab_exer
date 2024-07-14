package org.coworking.services;

import lombok.AllArgsConstructor;
import org.coworking.annotations.Loggable;
import org.coworking.models.User;
import org.coworking.models.enums.Role;
import org.coworking.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с User и работающий с репозиторием пользователей, где по умолчанию создается пользователь admin
 */
@Loggable
@Service
@AllArgsConstructor
public class UserService {

    /**
     * Репозиторий для пользовательских данных
     */
    private UserRepository userRepository;

    /**
     * Вызращает список всех пользователей
     *
     * @return список User
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Ищет пользователя по имени
     *
     * @param username имя пользователя
     * @return Optional, которое может содержать объект User или null
     */
    public Optional<User> getUserByName(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Регистрирует нового пользователя
     *
     * @param username имя пользователя
     * @param password пароль пользовтеля
     * @param role     роль в системе
     * @return созданный и добавленный в память User объект
     */
    public User registerNewUser(String username, String password, Role role) {
        return userRepository.save(username, password, role);
    }

    /**
     * Регистрирует нового пользователя
     * @param user объект пользователя содержащий данные
     * @return созданный и добавленный в память User объект
     */
    public User registerNewUser(User user){
        return registerNewUser(user.getName(), user.getPassword(), user.getRole());
    }
}
