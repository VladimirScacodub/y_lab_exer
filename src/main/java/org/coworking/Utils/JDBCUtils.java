package org.coworking.Utils;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.coworking.annotations.Loggable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static java.nio.file.Files.exists;
import static java.util.Objects.nonNull;

/**
 * Класс содержащий полезные методы для работы с JDBC связью с БД
 */
@Loggable
public class JDBCUtils {

    private static Connection connection;

    /**
     * Получение Connection объекта, который связан с БД
     *
     * @return Connection
     * @throws SQLException если со связью с БД возникли пробемы
     */
    public static Connection getConnection() throws SQLException {
        if (nonNull(connection))
            return connection;
        Properties properties = getProperties();
        final String db_url = properties.getProperty("url");
        final String db_user = properties.getProperty("user");
        final String db_password = properties.getProperty("password");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        connection = DriverManager.getConnection(db_url, db_user, db_password);
        connection.setAutoCommit(false);
        return connection;
    }

    /**
     * Вызов rollback метода, который отменяет изменения текущей транзакции
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

    /**
     * Подготовка и запуск всех Liquibase скриптов
     *
     * @param connection Connection объект связанный с БД
     * @throws SQLException       - если есть проблемы с БД
     * @throws LiquibaseException если есть проблемы с liquibase скриптами
     */
    public static void startLiquibase(Connection connection) throws SQLException, LiquibaseException {
        var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

        String liquibaseSchemaName = getProperties().getProperty("liquibaseSchemaName");
        createSchemaForLiquibaseLogs(connection, liquibaseSchemaName);
        database.setLiquibaseSchemaName(liquibaseSchemaName);
        Liquibase liquibase = new Liquibase(getProperties().getProperty("changeLogFile"), new ClassLoaderResourceAccessor(), database);

        liquibase.update();
    }

    /**
     * Создание отдельной схемы в БД для служебных таблиц liquibase
     *
     * @param connection          Connection объект связанный с БД
     * @param liquibaseSchemaName имя новой liquibase схемы
     * @throws SQLException в случае если есть проблемы с БД
     */
    private static void createSchemaForLiquibaseLogs(Connection connection, String liquibaseSchemaName) throws SQLException {
        Statement statement = connection.createStatement();
        String schemaCreationQuery = "CREATE SCHEMA IF NOT EXISTS " + liquibaseSchemaName;
        statement.executeUpdate(schemaCreationQuery);
        connection.commit();
    }

    /**
     * Получения объекта Properties через которого можно обращаться к конфигурация из .properties файла
     *
     * @return объект Properties
     */
    private static Properties getProperties() {
        Properties properties = new Properties();
        String path = "src/main/resources/liquibase.properties";
        if (!exists(Path.of(path))){
            String currentPath = new File("").getAbsolutePath();
            String parentPath = Paths.get(currentPath).getParent().toAbsolutePath().toString();
            path = parentPath + "/webapps/coworking-service/WEB-INF/classes/liquibase.properties";
        }

        try (InputStream inputStream = new FileInputStream(path)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println(new File("").getAbsolutePath());
            System.out.println(e.getMessage());
        }
        return properties;
    }

}
