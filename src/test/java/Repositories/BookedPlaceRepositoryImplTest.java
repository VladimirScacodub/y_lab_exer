package Repositories;

import liquibase.exception.LiquibaseException;
import org.coworking.Utils.JDBCUtils;
import org.coworking.models.BookedPlace;
import org.coworking.models.User;
import org.coworking.repositories.BookedPlaceRepositoryImpl;
import org.coworking.repositories.SlotRepository;
import org.coworking.repositories.SlotRepositoryImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.TestUtils.ADMIN_TEST_OBJECT;
import static utils.TestUtils.PLACE_TEST_OBJECT_FOR_BOOKING;
import static utils.TestUtils.startTestContainer;
import static utils.TestUtils.stopTestContainers;

@Testcontainers
@DisplayName("Тесты для проверки реализации BookedPlaceRepository")
class BookedPlaceRepositoryImplTest {

    private BookedPlaceRepositoryImpl bookedPlaceRepository;

    private SlotRepository slotRepository;

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
        slotRepository = new SlotRepositoryImpl(connection);
        bookedPlaceRepository = new BookedPlaceRepositoryImpl(connection, slotRepository);
    }

    @Test
    @DisplayName("Тест на получение всех записей о забронированных местах")
    void finaAllShouldFindAllBookedPlaces(){
        var actualResult = bookedPlaceRepository.findAll();

        assertThat(hasAdminBookedPlaces(actualResult)).isTrue();
    }

    @Test
    @DisplayName("Тест на получение всех записей о забронированных пользователем местах ")
    void findAllByUserShouldFindAllBookedPlacesMadeByUser(){
        var actualResult = bookedPlaceRepository.findAllByUser(ADMIN_TEST_OBJECT);

        assertThat(hasOnlyUserBookedPlaces(actualResult, ADMIN_TEST_OBJECT)).isTrue();
    }

    /**
     * Проверка если среди записей, есть бронирования администратором
     * @param bookedPlaces - все записи о бронированых местах
     * @return true - если есть, иначе false
     */
    private boolean hasAdminBookedPlaces(List<BookedPlace> bookedPlaces){
        return bookedPlaces.stream()
                .anyMatch(bookedPlace -> Objects.equals(bookedPlace.getUser(), ADMIN_TEST_OBJECT));
    }

    /**
     * Проверка если среди записей, есть только бронирования определеннм пользователем
     * @param bookedPlaces - все записи о бронированых местах
     * @param user - пользователь
     * @return true - если есть, иначе false
     */
    private boolean hasOnlyUserBookedPlaces(List<BookedPlace> bookedPlaces, User user){
        return bookedPlaces.stream()
                .allMatch(bookedPlace -> Objects.equals(bookedPlace.getUser(), user));
    }

    @Test
    @DisplayName("Тесть на создание нового бронирования места")
    void saveShouldSaveNewBookedPlaces(){
        int savedId = bookedPlaceRepository.save(PLACE_TEST_OBJECT_FOR_BOOKING, ADMIN_TEST_OBJECT, LocalDateTime.now().minusHours(1), LocalDateTime.now());
        var optionalBookedPlace = bookedPlaceRepository.findById(savedId);

        assertThat(savedId).isGreaterThan(0);
        assertThat(optionalBookedPlace).isPresent();
        assertThat(optionalBookedPlace.get()).matches(BookedPlaceRepositoryImplTest::hasSamePlaceAndUser);
    }

    /**
     * Проверка если бронирования совпадают по попользователю и по месту
     * @param bookedPlace - одно Бронирование
     * @return true - если да, иначе false
     */
    private static boolean hasSamePlaceAndUser(BookedPlace bookedPlace) {
        return Objects.equals(PLACE_TEST_OBJECT_FOR_BOOKING, bookedPlace.getPlace()) && Objects.equals(ADMIN_TEST_OBJECT, bookedPlace.getUser());
    }

    @Test
    @DisplayName("Тесть на удаление записи о бронировании места")
    void removeShouldRemoveBookedPlace(){
        int savedId = bookedPlaceRepository.save(PLACE_TEST_OBJECT_FOR_BOOKING, ADMIN_TEST_OBJECT, LocalDateTime.now().minusHours(1), LocalDateTime.now());

        bookedPlaceRepository.removeById(savedId);

        assertThat(bookedPlaceRepository.findById(savedId)).isEmpty();

    }
}