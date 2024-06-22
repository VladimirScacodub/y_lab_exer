import org.assertj.core.api.Assertions;
import org.coworking.services.validators.PlaceValidator;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.services.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static utils.TestUtils.EXISTENT_NAME;
import static utils.TestUtils.NEW_NAME_STRING;
import static utils.TestUtils.PLACE_TEST_OBJECT;
import static utils.TestUtils.TEST_LIST_OF_PLACE;

@DisplayName("Тест Валидатра для Place service")
public class PlaceValidatorTest {

    @Mock
    PlaceService placeService;

    @InjectMocks
    PlaceValidator placeValidator;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Тест на выброс иключения на попытку создания нового Place с уже существующим именем")
    void placeValidatorShouldThrowExceptionWhenUserPlaceExistsTest(){
        Mockito.when(placeService.findByName(EXISTENT_NAME))
                .thenReturn(Optional.of(PLACE_TEST_OBJECT));

        Assertions.assertThatThrownBy(()->placeValidator.validateExistedPlaceName(EXISTENT_NAME))
                .isInstanceOf(PlaceNamingException.class);
    }

    @Test
    @DisplayName("Тест на выброс исключений на попытку изменения несуществующего Place")
    void placeValidatorShouldThrowExceptionWhenPlaceDoesNotExistTest(){
        Mockito.when(placeService.findByName(EXISTENT_NAME))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(()->placeValidator.validatePlaceUpdating(EXISTENT_NAME, NEW_NAME_STRING))
                .isInstanceOf(PlaceNamingException.class);
    }

    @Test
    @DisplayName("Тест на выброс исключений на попытку изменения имени Place на уже занятое")
    void placeValidatorShouldThrowExceptionWhenNewPlaceNameAlreadyExistTest(){
        Mockito.when(placeService.findByName(EXISTENT_NAME))
                .thenReturn(Optional.of(PLACE_TEST_OBJECT));
        Mockito.when(placeService.getAllPlaces()).thenReturn(TEST_LIST_OF_PLACE);

        Assertions.assertThatThrownBy(()->placeValidator.validatePlaceUpdating(EXISTENT_NAME, NEW_NAME_STRING))
                .isInstanceOf(PlaceNamingException.class);
    }
}
