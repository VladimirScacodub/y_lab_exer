package Repositories;

import liquibase.exception.LiquibaseException;
import org.assertj.core.api.Assertions;
import org.coworking.repositories.impl.UserActionAuditRepositoryImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.time.LocalDateTime.now;
import static utils.TestUtils.ADMIN_TEST_OBJECT;
import static utils.TestUtils.EMPTY_STRING;
import static utils.TestUtils.startTestContainer;
import static utils.TestUtils.stopTestContainers;

@DisplayName("Тест репозитория UserActionAuditRepositoryImpl")
class UserActionAuditRepositoryImplTest {
    private static Connection connection;

    private UserActionAuditRepositoryImpl userActionAuditRepository;

    @BeforeAll
    static void setDatabase() throws SQLException, LiquibaseException {
        connection = DriverManager.getConnection(startTestContainer());
        TestUtils.startLiquibase(connection);
    }

    @AfterAll
    static void setDown() {
        stopTestContainers();
    }

    @BeforeEach
    void setUp() {
        userActionAuditRepository = new UserActionAuditRepositoryImpl(connection);
    }

    @Test
    @DisplayName("Тест на запись аудит в БД")
    void saveShouldMakeAuditNotesToDBTest() throws SQLException {
        int oldNumberOfNotes = getNumberOfLogs();

        userActionAuditRepository.save(ADMIN_TEST_OBJECT, EMPTY_STRING, now());
        int newNumberOfNotes = getNumberOfLogs();

        Assertions.assertThat(newNumberOfNotes).isGreaterThan(oldNumberOfNotes);
    }
    private int getNumberOfLogs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM coworking_schema.user_action_audit";
        ResultSet resultSet = connection.createStatement().executeQuery(sql);
        resultSet.next();
        return resultSet.getInt(1);
    }

}