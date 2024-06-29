import liquibase.exception.LiquibaseException;
import org.coworking.Utils.JDBCUtils;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.models.Slot;
import org.coworking.models.enums.PlaceType;
import org.coworking.repositories.BookedPlaceRepository;
import org.coworking.repositories.BookedPlaceRepositoryImpl;
import org.coworking.repositories.PlaceRepositoryImpl;
import org.coworking.repositories.SlotRepository;
import org.coworking.repositories.SlotRepositoryImpl;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.PlaceService;
import org.coworking.services.UserService;
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

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static utils.TestUtils.EMPTY_STRING;
import static utils.TestUtils.startTestContainer;
import static utils.TestUtils.stopTestContainers;

@DisplayName("Тест сервиса бронирования мест")
@Testcontainers
public class BookedPlaceServiceTest {

    private PlaceService placeService;

    private BookedPlaceService bookedPlaceService;

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
        SlotRepository slotRepository = new SlotRepositoryImpl(connection);
        BookedPlaceRepository bookedPlaceRepository = new BookedPlaceRepositoryImpl(connection, slotRepository);
        bookedPlaceService = new BookedPlaceService(placeService, bookedPlaceRepository);
    }

    @Test
    @DisplayName("Тест на получение доступных слотов")
    void getAvailableSlotsShouldReturnCorrectAvailableSlotsTest() {

        final Slot EXPECTED_SLOT_DAY_START = Slot.builder()
                .start(of(2024, 6, 22, 8, 0))
                .end(of(2024, 6, 22, 11, 30))
                .build();
        final Slot EXPECTED_SLOT_DAY_MIDDLE = Slot.builder()
                .start(of(2024, 6, 22, 14, 30))
                .end(of(2024, 6, 22, 16, 30))
                .build();
        final Slot EXPECTED_SLOT_DAY_END = Slot.builder()
                .start(of(2024, 6, 22, 19, 30))
                .end(of(2024, 6, 22, 20, 0))
                .build();

        var actualSlots = bookedPlaceService.getAvailableSlots(placeService.getAllPlaces().get(0), of(2024, 6, 22, 0, 0));

        assertThat(actualSlots)
                .containsOnly(EXPECTED_SLOT_DAY_START, EXPECTED_SLOT_DAY_MIDDLE, EXPECTED_SLOT_DAY_END);
    }

    @Test
    @DisplayName("Тест на выброс исключения при поиске бронирования с несуществующем id")
    void findByIdShouldThrowExceptionWithIncorrectId() {
        final int INCORRECT_ID = -1;

        assertThatThrownBy(() -> bookedPlaceService.findById(INCORRECT_ID))
                .isInstanceOf(BookedPlaceConflictsException.class);
    }

    @Test
    @DisplayName("Тест на выброс исключения при сортировке по неправильному параметру")
    void getAllBookedPlacesSortedByShouldThrowExceptionWithIncorrectParameter() {
        assertThatThrownBy(() -> bookedPlaceService.getAllBookedPlacesSortedBy(EMPTY_STRING))
                .isInstanceOf(BookedPlaceConflictsException.class);
    }
}
