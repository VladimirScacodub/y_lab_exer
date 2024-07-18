package Repositories;

import liquibase.exception.LiquibaseException;
import org.coworking.models.Place;
import org.coworking.models.enums.PlaceType;
import org.coworking.repositories.impl.PlaceRepositoryImpl;
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
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.TestUtils.EXISTENT_NAME;
import static utils.TestUtils.NEW_NAME_STRING;
import static utils.TestUtils.TEST_PLACE_NAME_0;
import static utils.TestUtils.TEST_PLACE_NAME_1;
import static utils.TestUtils.TEST_PLACE_NAME_2;
import static utils.TestUtils.startTestContainer;
import static utils.TestUtils.stopTestContainers;

@Testcontainers
@DisplayName("Тесты для проверки реализации PlaceRepository")
class PlaceRepositoryImplTest {

    private PlaceRepositoryImpl placeRepository;

    private static Connection connection;

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
        placeRepository = new PlaceRepositoryImpl(connection);
    }

    @Test
    @DisplayName("Тест на сохранение нового места в БД")
    void saveShouldSaveNewPlaceTest() {
        placeRepository.save(EXISTENT_NAME, PlaceType.WORKPLACE);

        var savedPlace = placeRepository.findByName(EXISTENT_NAME);

        assertThat(savedPlace)
                .isNotEmpty()
                .get()
                .matches(place -> Objects.equals(place.getPlaceName(), EXISTENT_NAME));
    }

    @Test
    @DisplayName("Тест на получение данных о месте в БД")
    void findByNameShouldFindCorrectPlaceByNameTest() {
        var optionalOfExistentPlace = placeRepository.findByName(TEST_PLACE_NAME_1);

        assertThat(optionalOfExistentPlace)
                .isNotEmpty()
                .get()
                .matches(place -> Objects.equals(place.getPlaceName(), TEST_PLACE_NAME_1));
    }


    @Test
    @DisplayName("Тест на обновление данных о месте в БД")
    void updatePlaceShouldUpdateNameAndTypeOfPlaceTest() {
        placeRepository.updatePlace(TEST_PLACE_NAME_0, NEW_NAME_STRING, PlaceType.WORKPLACE);

        var optionalOfUpdatedPlace = placeRepository.findByName(NEW_NAME_STRING);

        assertThat(optionalOfUpdatedPlace)
                .isNotEmpty()
                .get()
                .matches(place -> Objects.equals(place.getPlaceName(), NEW_NAME_STRING));
    }

    @Test
    @DisplayName("Тест на удаление места из БД")
    void removeByIdShouldRemovePlaceTest() {
        placeRepository.removeByName(TEST_PLACE_NAME_2);

        Optional<Place> optionalPlace = placeRepository.findByName(TEST_PLACE_NAME_2);

        assertThat(optionalPlace).isEmpty();
    }

}