import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.models.enums.PlaceType;
import org.coworking.services.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static utils.TestUtils.EMPTY_STRING;
import static utils.TestUtils.TEST_PLACE_NAME;

@DisplayName("Тест сервиса для рабочих мест")
public class PlaceServiceTest {

    PlaceService placeService;

    @BeforeEach
    void setUp() {
        placeService = new PlaceService();
        for (int i = 0; i < 5; i++) {
            placeService.createNewPlace("Workplace " + i, PlaceType.WORKPLACE);
            placeService.createNewPlace("Conference hall " + i, PlaceType.CONFERENCE_HALL);
        }
    }

    @Test
    @DisplayName("findByName должен вернуть нужное рабочее место")
    void findByNameShouldReturnPlaceTest() {
        assertThat(placeService.findByName(TEST_PLACE_NAME))
                .get()
                .matches(place -> Objects.equals(place.getPlaceName(), TEST_PLACE_NAME));
    }

    @Test
    @DisplayName("removePlace недолжен работать с несуществующими именами рабочих мест")
    void removePlaceShouldThrowExceptionWithNonExistentNameTest() {
        assertThatThrownBy(() -> placeService.removePlace(EMPTY_STRING))
                .isInstanceOf(PlaceNamingException.class);
    }
}
