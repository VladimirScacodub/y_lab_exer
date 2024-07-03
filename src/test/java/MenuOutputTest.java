import org.coworking.services.validators.BookedPlaceValidator;
import org.coworking.services.validators.PlaceValidator;
import org.coworking.services.validators.UserValidator;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.Utils.exceptions.UserAuthorisationException;
import org.coworking.services.BookedPlaceService;
import org.coworking.outputs.MenuOutput;
import org.coworking.services.PlaceService;
import org.coworking.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Stubber;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static utils.TestUtils.ADMIN_LOGIN;
import static utils.TestUtils.ADMIN_PASSWORD;
import static utils.TestUtils.ADMIN_TEST_OBJECT;
import static utils.TestUtils.NEW_NAME_STRING;
import static utils.TestUtils.PLACE_TEST_OBJECT;
import static utils.TestUtils.TEST_BOOKED_PLACE_LIST;
import static utils.TestUtils.TEST_DATE;
import static utils.TestUtils.TEST_PLACE_NAME_0;
import static utils.TestUtils.TEST_PLACE_TYPE;
import static utils.TestUtils.TEST_PLACE_TYPE_STRING;

@DisplayName("Тестированеи меню вывода сообщений")
public class MenuOutputTest {

    @Mock
    UserService userService;

    @Mock
    UserValidator userValidator;

    @Mock
    PlaceService placeService;

    @Mock
    PlaceValidator placeValidator;

    @Mock
    BookedPlaceService bookedPlaceService;

    @Mock
    BookedPlaceValidator bookedPlaceValidator;

    @InjectMocks
    MenuOutput menuOutput;

    @BeforeEach
    void setUp() throws UserAuthorisationException {
        menuOutput = spy(new MenuOutput(userValidator, userService, placeService, placeValidator, bookedPlaceService, bookedPlaceValidator, null));
        MockitoAnnotations.openMocks(this);
        when(userValidator.getValidatedAuthorisedUser(ADMIN_LOGIN, ADMIN_PASSWORD))
                .thenReturn(ADMIN_TEST_OBJECT);
    }

    @Test
    @Timeout(value = 2, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    @DisplayName("Тест на вызов авторизации")
    void performMenuShouldAuthoriseUserTest() throws UserAuthorisationException {
        mockAuthorisation()
                .doReturn("0")
                .when(menuOutput).inputLine();

        menuOutput.performMenu();

        verify(menuOutput, times(5)).inputLine();
        verify(userValidator).getValidatedAuthorisedUser(matches(ADMIN_LOGIN), matches(ADMIN_PASSWORD));
    }

    @Test
    @Timeout(value = 2, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    @DisplayName("Тест на вызов бронирования рабочего места")
    void performMenuShouldBookPlaceTest() {
        when(placeService.findByName(TEST_PLACE_NAME_0)).thenReturn(Optional.of(PLACE_TEST_OBJECT));
        mockAuthorisation()
                .doReturn("2")
                .doReturn(TEST_DATE)
                .doReturn("1")
                .doReturn(TEST_PLACE_NAME_0)
                .doReturn(TEST_DATE + " 12:00")
                .doReturn(TEST_DATE + " 15:00")
                .doReturn("0")
                .when(menuOutput).inputLine();

        menuOutput.performMenu();

        verify(menuOutput, times(12)).inputLine();
        verify(bookedPlaceService).bookPlace(any(), any(), any(), any());
    }

    @Test
    @Timeout(value = 2, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    @DisplayName("Тест на вызов регистрации и авторизации")
    void performMenuShouldRegisterUserTest() {
        doReturn("2")
                .doReturn(NEW_NAME_STRING)
                .doReturn(NEW_NAME_STRING)
                .doReturn("1")
                .doReturn(NEW_NAME_STRING)
                .doReturn(NEW_NAME_STRING)
                .doReturn("0")
                .when(menuOutput).inputLine();

        menuOutput.performMenu();

        verify(menuOutput, times(7)).inputLine();
        verify(userService).registerNewUser(matches(NEW_NAME_STRING), matches(NEW_NAME_STRING), any());
    }

    @Test
    @Timeout(value = 2, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    @DisplayName("Тест на вызов отмены бронирования")
    void performMenuShouldCallBookingCancelingTest() throws BookedPlaceConflictsException {
        when(bookedPlaceService.getAllBookedPlacesByUser(ADMIN_TEST_OBJECT))
                .thenReturn(TEST_BOOKED_PLACE_LIST);
        mockAuthorisation()
                .doReturn("3")
                .doReturn("1")
                .doReturn("0")
                .when(menuOutput).inputLine();

        menuOutput.performMenu();

        verify(bookedPlaceService).cancelBooking(anyInt());
    }

    @Test
    @Timeout(value = 2, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    @DisplayName("Тест на вызов создания нового рабочего места админом")
    void performMenuShouldCallPlaceCreationTest() {
        mockAuthorisation()
                .doReturn("5")
                .doReturn(TEST_PLACE_NAME_0)
                .doReturn(TEST_PLACE_TYPE_STRING)
                .doReturn("0")
                .when(menuOutput).inputLine();

        menuOutput.performMenu();

        verify(placeService).createNewPlace(TEST_PLACE_NAME_0, TEST_PLACE_TYPE);
    }

    /**
     * Устанавилвает возвращаемые значения, нужные для авторизации
     * @return Возвращает Stubber, который используется для авторизации
     */
    private static Stubber mockAuthorisation() {
        return doReturn("1")
                .doReturn(ADMIN_LOGIN)
                .doReturn(ADMIN_PASSWORD);
    }

    @Test
    @Timeout(value = 2, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    @DisplayName("Тест на вызов удаления рабочего места админом")
    void performMenuShouldCallPlaceRemovalTest() throws PlaceNamingException {
        mockAuthorisation()
                .doReturn("6")
                .doReturn(TEST_PLACE_NAME_0)
                .doReturn("1")
                .doReturn("0")
                .when(menuOutput).inputLine();

        menuOutput.performMenu();

        verify(bookedPlaceService).cancelBookingWithRemovingPlace(TEST_PLACE_NAME_0);
    }

    @Test
    @Timeout(value = 2, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    @DisplayName("Тест на вызов обновления рабочего места админом")
    void performMenuShouldCallPlaceUpdatingTest() throws PlaceNamingException {
        mockAuthorisation()
                .doReturn("7")
                .doReturn(TEST_PLACE_NAME_0)
                .doReturn(NEW_NAME_STRING)
                .doReturn(TEST_PLACE_TYPE_STRING)
                .doReturn("0")
                .when(menuOutput).inputLine();

        menuOutput.performMenu();

        verify(placeService).updatePlace(TEST_PLACE_NAME_0, NEW_NAME_STRING, TEST_PLACE_TYPE);
    }

    @Test
    @Timeout(value = 2, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    @DisplayName("Тест на вызов демонстрации всех бронирований")
    void performMenuShouldShowBookingListTest() throws BookedPlaceConflictsException {
        mockAuthorisation()
                .doReturn("4")
                .doReturn("1")
                .doReturn("0")
                .when(menuOutput).inputLine();

        menuOutput.performMenu();

        verify(bookedPlaceService).getAllBookedPlacesSortedBy(any());
    }

}