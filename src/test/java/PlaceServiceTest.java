import liquibase.exception.LiquibaseException;
import org.coworking.Utils.JDBCUtils;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.repositories.PlaceRepositoryImpl;
import org.coworking.services.PlaceService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static utils.TestUtils.EMPTY_STRING;
import static utils.TestUtils.TEST_PLACE_NAME_0;
import static utils.TestUtils.startTestContainer;
import static utils.TestUtils.stopTestContainers;

@DisplayName("Тест сервиса для рабочих мест")
public class PlaceServiceTest {

    PlaceService placeService;

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
        placeService = new PlaceService(new PlaceRepositoryImpl(connection));

    }

    @Test
    @DisplayName("findByName должен вернуть нужное рабочее место")
    void findByNameShouldReturnPlaceTest() {
        assertThat(placeService.findByName(TEST_PLACE_NAME_0))
                .get()
                .matches(place -> Objects.equals(place.getPlaceName(), TEST_PLACE_NAME_0));
    }

    @Test
    @DisplayName("removePlace недолжен работать с несуществующими именами рабочих мест")
    void removePlaceShouldThrowExceptionWithNonExistentNameTest() {
        assertThatThrownBy(() -> placeService.removePlace(EMPTY_STRING))
                .isInstanceOf(PlaceNamingException.class);
    }
}
