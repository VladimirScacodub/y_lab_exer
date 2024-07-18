package org.coworking.repositories.impl;

import lombok.AllArgsConstructor;
import org.coworking.Utils.mappers.ResultSetMapper;
import org.coworking.models.User;
import org.coworking.models.enums.Role;
import org.coworking.repositories.UserRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.coworking.Utils.JDBCUtils.rollback;

/**
 * Реализация UserRepository хранящая пользовательские данные в БД
 */
@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    /**
     * Связь с БД, через которую будет происходить CRUD операции над данными о слотах
     */
    private Connection connection;

    /**
     * Сохранение данных о новом пользователе в БД
     * @param username имя пользователя
     * @param password пароль к аккаунту
     * @param role роль пользователя в системе
     * @return объект содержащий все данные о сохраненном пользователе
     */
    @Override
    public User save(String username, String password, Role role) {
        User savedUser = null;
        try {
            saveUser(username, password, role);
            savedUser = findByUsername(username)
                    .orElseThrow(()-> new SQLException("Saved User not found"));
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            rollback(connection);
        }
        return savedUser;
    }

    /**
     * Выолнение insert запроса для добавление пользовательских данных в БД
     * @param username имя пользователя
     * @param password пароль к аккаунту
     * @param role роль пользователя в системе
     * @throws SQLException если в ходе insert запроса возникла ошибка
     */
    private void saveUser(String username, String password, Role role) throws SQLException {
        String preparedSql = "INSERT INTO coworking_schema.users (name, password, role) " +
                "VALUES (?,?,?);";
        var insertStatement = connection.prepareStatement(preparedSql);
        insertStatement.setString(1, username);
        insertStatement.setString(2, password);
        insertStatement.setString(3, role.name());
        insertStatement.executeUpdate();
    }

    /**
     * Получение данных о пользовтеле по его имени из БД
     * @param username имя пользователя
     * @return Optional объект, оборачивающий пользовательские данные
     */
    @Override
    public Optional<User> findByUsername(String username) {
        String selectQuery = "SELECT * FROM coworking_schema.users WHERE name = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(selectQuery);
            statement.setString(1,username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.ofNullable(ResultSetMapper.mapUserRow(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    /**
     * Получение всех пользовательских данных их БД
     * @return спиок объектв User
     */
    @Override
    public List<User> findAll() {
        String selectQuery = "SELECT * FROM coworking_schema.users";
        Statement statement;
        List<User> userList = new ArrayList<>();
        try {
            statement = connection.createStatement();
            var resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()){
                userList.add(ResultSetMapper.mapUserRow(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }
}
