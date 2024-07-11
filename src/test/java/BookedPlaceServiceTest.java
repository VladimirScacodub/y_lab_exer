import liquibase.exception.LiquibaseException;
import org.coworking.Utils.JDBCUtils;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.mappers.BookedPlaceMapper;
import org.coworking.Utils.mappers.SlotMapper;
import org.coworking.dtos.AvailableSlotsDTO;
import org.coworking.dtos.SlotDTO;
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
import java.util.List;
import java.util.Objects;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static utils.TestUtils.ADMIN_TEST_OBJECT;
import static utils.TestUtils.EMPTY_STRING;
import static utils.TestUtils.TEST_BOOKED_PLACE_OBJECT;
import static utils.TestUtils.startTestContainer;
import static utils.TestUtils.stopTestContainers;

@DisplayName("Тест сервиса бронирования мест")
@Testcontainers
public class BookedPlaceServiceTest {

    public static final LocalDateTime LOCAL_DATE_TIME = of(2024, 6, 22, 0, 0);
    private PlaceService placeService;

    private BookedPlaceService bookedPlaceService;

    private static Connection connection;

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

        var actualSlots = bookedPlaceService.getAvailableSlots(placeService.getAllPlaces().get(0), LOCAL_DATE_TIME);

        assertThat(actualSlots)
                .containsOnly(EXPECTED_SLOT_DAY_START, EXPECTED_SLOT_DAY_MIDDLE, EXPECTED_SLOT_DAY_END);
    }

    @Test
    @DisplayName("Тест на получение всех доступных слотов")
    void getAvailableSlotsDTOShouldReturnCorrectAvailableSlotsTest(){
        List<AvailableSlotsDTO> slots = bookedPlaceService.getAllAvailableDTOSlots(LOCAL_DATE_TIME);
        assertThat(slots).matches(availableSlotsDTOS -> hasSlot(availableSlotsDTOS, EXPECTED_SLOT_DAY_START) &&
                hasSlot(availableSlotsDTOS, EXPECTED_SLOT_DAY_MIDDLE) &&
                hasSlot(availableSlotsDTOS, EXPECTED_SLOT_DAY_END));
    }

    private boolean hasSlot(List<? extends AvailableSlotsDTO> availableSlotsDTOS, Slot slot) {
        return availableSlotsDTOS.stream().flatMap(availableSlotsDTO -> availableSlotsDTO.getSlotDTOS().stream())
                .anyMatch(slotDTO -> Objects.equals(toSlot(slotDTO), EXPECTED_SLOT_DAY_START));
    }

    private static Slot toSlot(SlotDTO slotDTO) {
        return SlotMapper.INSTANCE.slotDtoToSlot(slotDTO);
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

    @Test
    @DisplayName("Тест на регистрацию нового бронирования")
    void bookPlaceShouldBookPlaceTest() {
        int oldLength = bookedPlaceService.getAllBookedPlaces().size();

        bookedPlaceService.bookPlace(TEST_BOOKED_PLACE_OBJECT, ADMIN_TEST_OBJECT);
        int newSize = bookedPlaceService.getAllBookedPlaces().size();

        assertThat(newSize).isGreaterThan(oldLength);

    }

    @Test
    @DisplayName("Тест на меппинг объектов")
    void bookedMapperShouldCorrectMapDtoTest(){
        var dto = BookedPlaceMapper.INSTANCE.bookedPlaceToBookedPlaceDto(TEST_BOOKED_PLACE_OBJECT);
        var bookedPlace = BookedPlaceMapper.INSTANCE.bookedPlaceDtoToBookedPlace(dto);

        assertThat(TEST_BOOKED_PLACE_OBJECT.getUser()).isEqualTo(bookedPlace.getUser());
        assertThat(TEST_BOOKED_PLACE_OBJECT.getPlace()).isEqualTo(bookedPlace.getPlace());
        assertThat(TEST_BOOKED_PLACE_OBJECT.getSlot()).isEqualTo(bookedPlace.getSlot());
    }

}
