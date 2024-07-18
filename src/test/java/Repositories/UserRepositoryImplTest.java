package Repositories;

import liquibase.exception.LiquibaseException;
import org.coworking.models.User;
import org.coworking.models.enums.Role;
import org.coworking.repositories.impl.UserRepositoryImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import utils.TestUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.TestUtils.ADMIN_TEST_OBJECT;
import static utils.TestUtils.EXISTENT_NAME;
import static utils.TestUtils.NEW_NAME_STRING;
import static utils.TestUtils.USER_TEST_OBJECT;
import static utils.TestUtils.startTestContainer;
import static utils.TestUtils.stopTestContainers;

@Testcontainers
@DisplayName("Тесты для проверки реализации UserRepository")
public class UserRepositoryImplTest {

    private UserRepositoryImpl userRepository;
    private static Connection connection;

    private static final List<User> EXPECTED_USER_LIST = new ArrayList<>();

    @BeforeAll
    static void setDatabase() throws SQLException, LiquibaseException {
        connection = DriverManager.getConnection(startTestContainer());
        TestUtils.startLiquibase(connection);
        EXPECTED_USER_LIST.add(ADMIN_TEST_OBJECT);
    }

    @AfterAll
    static void setDown() {
        stopTestContainers();
    }

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl(connection);
    }

    @Test
    @DisplayName("Тест на получение всех пользователей из БД")
    void findAllShouldReturnAllUsers() throws SQLException {
        var actualList = userRepository.findAll();

        assertThat(actualList).containsAll(EXPECTED_USER_LIST);
    }

    @Test
    @DisplayName("Тест на сохранение нового пользователя в БД")
    void saveShouldSaveNewUserTest() {
        var actualData = userRepository.save(EXISTENT_NAME, EXISTENT_NAME, Role.USER);

        assertThat(actualData).isEqualTo(USER_TEST_OBJECT);

        EXPECTED_USER_LIST.add(USER_TEST_OBJECT);
    }

    @Test
    @DisplayName("Тест на возврат пустого Optional при запросе на получение несуществующего User")
    void findByIdShouldReturnEmptyOptionalWithNonExistentUsernameTest() throws SQLException {
        var actualUserOptional = userRepository.findByUsername(NEW_NAME_STRING);

        assertThat(actualUserOptional).isEmpty();
    }
}
