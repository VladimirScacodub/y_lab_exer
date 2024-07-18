package org.coworking.repositories.impl;

import lombok.AllArgsConstructor;
import org.coworking.models.User;
import org.coworking.repositories.UserActionAuditRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static java.sql.Timestamp.valueOf;
import static org.coworking.Utils.JDBCUtils.rollback;

/**
 * Реализация UserActionAuditRepositoryImpl хранящая записи в БД
 */
@AllArgsConstructor
@Repository
public class UserActionAuditRepositoryImpl implements UserActionAuditRepository {

    /**
     * Связь с БД
     */
    private Connection connection;

    /**
     * Созранение записи о действиях пользователя в БД
     * @param user пользователя соверщивщий действия
     * @param actionDescription описание действия
     * @param actionDateTime дата и время действия
     */
    @Override
    public void save(User user, String actionDescription, LocalDateTime actionDateTime) {
        try {
            String preparedSql = "INSERT INTO coworking_schema.user_action_audit (user_id, action_description, datetime) VALUES (?,?,?)";
            var statement = connection.prepareStatement(preparedSql);
            statement.setInt(1, user.getId());
            statement.setString(2, actionDescription);
            statement.setTimestamp(3, valueOf(actionDateTime));
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
