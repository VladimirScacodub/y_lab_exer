import liquibase.exception.LiquibaseException;
import org.assertj.core.api.Assertions;
import org.coworking.Utils.JDBCUtils;
import org.coworking.repositories.UserRepositoryImpl;
import org.coworking.services.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static utils.TestUtils.USER_TEST_OBJECT;
import static utils.TestUtils.startTestContainer;
import static utils.TestUtils.stopTestContainers;

@DisplayName("Тест для UserService")
public class UserServiceTest {

    private UserService userService;

    private static Connection connection;

    @BeforeAll
    static void setDatabase() throws SQLException, LiquibaseException {
        connection = DriverManager.getConnection(startTestContainer());
        JDBCUtils.startLiquibase(connection);
    }

    @AfterAll
    static void setDown() {
        stopTestContainers();
    }


    @BeforeEach
    void setUp() {
        userService = new UserService( new UserRepositoryImpl(connection));
    }

    @Test
    @DisplayName("Тест создания нового пользователя")
    void saveShouldSaveNewUser(){
        int oldUserCount = userService.findAll().size();

        userService.registerNewUser(USER_TEST_OBJECT);
        int newUserCount = userService.findAll().size();

        Assertions.assertThat(newUserCount).isGreaterThan(oldUserCount);
    }

}
