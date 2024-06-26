import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.models.Slot;
import org.coworking.models.enums.PlaceType;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.PlaceService;
import org.coworking.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static utils.TestUtils.EMPTY_STRING;

@DisplayName("Тест сервиса бронирования мест")
public class BookedPlaceServiceTest {

    private PlaceService placeService;

    private UserService userService;

    private BookedPlaceService bookedPlaceService;

    @BeforeEach
    void setUp() {
        placeService = new PlaceService();
        for (int i = 0; i < 5; i++) {
            placeService.createNewPlace("Workplace " + i, PlaceType.WORKPLACE);
            placeService.createNewPlace("Conference hall " + i, PlaceType.CONFERENCE_HALL);
        }
        userService = new UserService();
        bookedPlaceService = new BookedPlaceService(placeService, userService);
        bookedPlaceService.bookPlace(placeService.getAllPlaces().get(4), userService.findAll().get(0), LocalDateTime.now().minusHours(3), LocalDateTime.now().plusHours(1));
        bookedPlaceService.bookPlace(placeService.getAllPlaces().get(2), userService.findAll().get(0), LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        bookedPlaceService.bookPlace(placeService.getAllPlaces().get(0), userService.findAll().get(0), LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(2));

    }

    @Test
    @DisplayName("Тест на получение доступных слотов")
    void getAvailableSlotsShouldReturnCorrectAvailableSlotsTest() {
        final Slot EXPECTED_SLOT = new Slot(LocalDateTime.of(2024, 6, 20, 8, 0), LocalDateTime.of(2024, 6, 20, 20, 0));

        var actualSlots = bookedPlaceService.getAvailableSlots(placeService.getAllPlaces().get(0), LocalDateTime.of(2024, 6, 20, 0, 0));

        assertThat(actualSlots)
                .containsOnly(EXPECTED_SLOT);
    }

    @Test
    @DisplayName("Тест на выброс исключения при поиске бронирования с несуществующем id")
    void findByIdShouldThrowExceptionWithIncorrectId(){
        final int INCORRECT_ID = -1;

        assertThatThrownBy(()->bookedPlaceService.findById(INCORRECT_ID))
                .isInstanceOf(BookedPlaceConflictsException.class);
    }

    @Test
    @DisplayName("Тест на выброс исключения при сортировке по неправильному параметру")
    void getAllBookedPlacesSortedByShouldThrowExceptionWithIncorrectParameter(){
        assertThatThrownBy(()->bookedPlaceService.getAllBookedPlacesSortedBy(EMPTY_STRING))
                .isInstanceOf(BookedPlaceConflictsException.class);
    }
}
