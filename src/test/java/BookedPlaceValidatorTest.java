import org.coworking.models.enums.PlaceType;
import org.coworking.services.validators.BookedPlaceValidator;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.PlaceService;
import org.coworking.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Тест Валидатора бронированых мест")
public class BookedPlaceValidatorTest {

    PlaceService placeService;

    UserService userService;

    BookedPlaceService bookedPlaceService;

    BookedPlaceValidator bookedPlaceValidator;

    @BeforeEach
    public void fillData() {
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
        bookedPlaceValidator = new BookedPlaceValidator(bookedPlaceService);
    }

    @Test
    @DisplayName("Тест на обнаружение конфликов бронирования")
    void validateBookingPlaceShouldDetectBookingConflictsTest() {
        assertThatThrownBy(() -> bookedPlaceValidator.validateBookingPlace(placeService.getAllPlaces().get(0), LocalDateTime.MAX, LocalDateTime.MIN))
                .isInstanceOf(BookedPlaceConflictsException.class);
        assertThatThrownBy(() -> bookedPlaceValidator.validateBookingPlace(placeService.getAllPlaces().get(0), LocalDateTime.MIN, LocalDateTime.MAX))
                .isInstanceOf(BookedPlaceConflictsException.class);
        assertThatThrownBy(() -> bookedPlaceValidator.validateBookingPlace(placeService.getAllPlaces().get(0), LocalDateTime.now().minusHours(1), LocalDateTime.now()))
                .isInstanceOf(BookedPlaceConflictsException.class);
        assertThatThrownBy(() -> bookedPlaceValidator.validateBookingPlace(placeService.getAllPlaces().get(0), LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(BookedPlaceConflictsException.class);
    }
}
