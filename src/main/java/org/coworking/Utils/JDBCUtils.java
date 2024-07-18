package org.coworking.Utils;

import org.coworking.annotations.Loggable;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс содержащий полезные методы для работы с JDBC связью с БД
 */
@Loggable
public class JDBCUtils {

    /**
     * Оборачивание вызова rollback метода, который отменяет изменения текущей транзакции,
     * для обработки исключения, если оно возникает
     *
     * @param connection Connection объект связанный с БД
     */
    public static void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
