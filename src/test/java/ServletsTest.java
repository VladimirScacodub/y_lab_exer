import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.coworking.Utils.ObjectMapperUtil;
import org.coworking.Utils.ServletUtils;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.exceptions.ForbiddenAccessException;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.Utils.exceptions.UserAuthorisationException;
import org.coworking.Utils.exceptions.UserRegistrationException;
import org.coworking.repositories.SlotRepository;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.PlaceService;
import org.coworking.services.UserService;
import org.coworking.services.validators.BookedPlaceValidator;
import org.coworking.services.validators.PlaceValidator;
import org.coworking.services.validators.UserValidator;
import org.coworking.servlets.booking.BookPlaceServlet;
import org.coworking.servlets.booking.DeleteBookedPlaceServlet;
import org.coworking.servlets.booking.GetAllAvailableSlotsServlet;
import org.coworking.servlets.booking.GetAllBookedPlaceServlet;
import org.coworking.servlets.booking.GetAllUserBookedPlaceServlet;
import org.coworking.servlets.place.DeletePlaceServlet;
import org.coworking.servlets.place.GelAllPlaceServlet;
import org.coworking.servlets.place.SaveNewPlaceServlet;
import org.coworking.servlets.place.UpdatePlaceServlet;
import org.coworking.servlets.user.AuthoriseUserServlet;
import org.coworking.servlets.user.RegisterUserServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import utils.TestUtils;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static utils.TestUtils.ADMIN_TEST_OBJECT;
import static utils.TestUtils.SERVLET_INPUT_STREAM;
import static utils.TestUtils.SERVLET_OUTPUT_STREAM;
import static utils.TestUtils.TEST_ADMIN_DTO;
import static utils.TestUtils.TEST_BOOKED_PLACE_DTO;
import static utils.TestUtils.TEST_PLACE_DTO;
import static utils.TestUtils.TEST_PLACE_NAME_0;
import static utils.TestUtils.USER_TEST_OBJECT;

@DisplayName("Тест всех сервлетов")
public class ServletsTest {

    public static final String TEST_DATE = "2024-08-22";
    @Mock
    private UserValidator userValidator;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthoriseUserServlet authoriseUserServlet;

    @InjectMocks
    private RegisterUserServlet registerUserServlet;

    @InjectMocks
    private UpdatePlaceServlet updatePlaceServlet;

    @InjectMocks
    private SaveNewPlaceServlet saveNewPlaceServlet;

    @InjectMocks
    private GelAllPlaceServlet gelAllPlaceServlet;

    @InjectMocks
    private DeletePlaceServlet deletePlaceServlet;

    @InjectMocks
    private GetAllUserBookedPlaceServlet getAllUserBookedPlaceServlet;

    @InjectMocks
    private GetAllBookedPlaceServlet getAllBookedPlaceServlet;

    @InjectMocks
    private GetAllAvailableSlotsServlet getAllAvailableSlotsServlet;

    @InjectMocks
    private DeleteBookedPlaceServlet deleteBookedPlaceServlet;

    @InjectMocks
    private BookPlaceServlet bookPlaceServlet;

    @Mock
    private BookedPlaceService bookedPlaceService;

    @Mock
    private BookedPlaceValidator bookedPlaceValidator;

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private PlaceService placeService;

    @Mock
    private PlaceValidator placeValidator;

    @Mock
    private ObjectMapperUtil objectMapperUtil;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(request.getInputStream()).thenReturn(SERVLET_INPUT_STREAM);
        when(response.getOutputStream()).thenReturn(SERVLET_OUTPUT_STREAM);

    }

    @Test
    @DisplayName("Тест на вызов авторизации пользователя")
    void authoriseUserServletShouldCallAuthorisationUserTest() throws ServletException, IOException, UserAuthorisationException {
        Mockito.doReturn(TestUtils.ADMIN_TEST_OBJECT).when(userValidator).getValidatedAuthorisedUser(any());

        authoriseUserServlet.doPost(request, response);

        verify(response).setStatus(SC_OK);
    }

    @Test
    @DisplayName("Тест на вызов неудачной авторизации пользователя")
    void authoriseUserServletShouldShouldReturnBadRequestTest() throws ServletException, IOException, UserAuthorisationException {
        doThrow(new UserAuthorisationException("")).when(userValidator).getValidatedAuthorisedUser(any());

        authoriseUserServlet.doPost(request, response);

        verify(response).setStatus(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на вызов успешной регистрации пользователя")
    void registerUserServletShouldReturnStatusOkTest() throws ServletException, IOException {
        when(objectMapperUtil.getDto(any(), any())).thenReturn(TEST_ADMIN_DTO);

        registerUserServlet.doPost(request, response);

        verify(userService).registerNewUser(any());
        verify(response).setStatus(SC_OK);
    }

    @Test
    @DisplayName("Тест на вызов успешной регистрации пользователя")
    void registerUserServletShouldReturnStatusBadRequestTest() throws ServletException, IOException, UserRegistrationException {
        doThrow(new UserRegistrationException("")).when(userValidator).validateUserRegistration(any());

        registerUserServlet.doPost(request, response);

        verify(response).setStatus(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на вызов обновления места")
    void updatePlaceServletShouldCallPlaceUpdatingTest() throws ServletException, IOException, PlaceNamingException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);
        when(objectMapperUtil.getDto(any(), any())).thenReturn(TEST_PLACE_DTO);

        updatePlaceServlet.doPut(request, response);

        verify(response).setStatus(SC_OK);
        verify(placeService).updatePlace(any(),any());
    }
    @Test
    @DisplayName("Тест на вызов неудачного обновления места")
    void updatePlaceServletShouldReturnBadRequestTest() throws ServletException, IOException, PlaceNamingException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);
        when(objectMapperUtil.getDto(any(), any())).thenReturn(TEST_PLACE_DTO);
        doThrow(new PlaceNamingException("")).when(placeValidator).validatePlaceUpdating(any(), eq(TEST_PLACE_DTO));

        updatePlaceServlet.doPut(request, response);

        verify(response).setStatus(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на вызов создания места")
    void createPlaceServletShouldCallPlaceCreationTest() throws ServletException, IOException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);
        when(objectMapperUtil.getDto(any(), any())).thenReturn(TEST_PLACE_DTO);

        saveNewPlaceServlet.doPost(request, response);

        verify(response).setStatus(SC_OK);
        verify(placeService).createNewPlace(any());
    }

    @Test
    @DisplayName("Тест на вызов неудачного создания места")
    void createPlaceServletShouldReturnBadRequestTest() throws ServletException, IOException, PlaceNamingException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);
        when(objectMapperUtil.getDto(any(), any())).thenReturn(TEST_PLACE_DTO);
        doThrow(new PlaceNamingException("")).when(placeValidator).validateExistedPlaceName(TEST_PLACE_DTO);

        saveNewPlaceServlet.doPost(request, response);

        verify(response).setStatus(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на вызов получения всех мест")
    void createPlaceServletShouldCallListOfAllPlacesTest() throws ServletException, IOException, PlaceNamingException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);

        gelAllPlaceServlet.doGet(request, response);

        verify(response).setStatus(SC_OK);
        verify(placeService).getAllPlaces();
    }

    @Test
    @DisplayName("Тест на вызов удаления места")
    void deletePlaceServletShouldCallPlaceDeletingTest() throws ServletException, IOException, PlaceNamingException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);

        deletePlaceServlet.doDelete(request,response);

        verify(response).setStatus(SC_OK);
        verify(placeService).removePlace(any());
    }

    @Test
    @DisplayName("Тест на вызов списка всех бронирований")
    void getAllBookedPlaceServletShouldReturnStatusOkTest() throws ServletException, IOException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);

        getAllBookedPlaceServlet.doGet(request,response);

        verify(response).setStatus(SC_OK);
        verify(bookedPlaceService).getAllBookedPlaces();
    }

    @Test
    @DisplayName("Тест на вызов списка всех бронирований пользователя")
    void getAllUserBookedPlaceServletShouldReturnStatusOkTest() throws ServletException, IOException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);

        getAllUserBookedPlaceServlet.doGet(request,response);

        verify(response).setStatus(SC_OK);
        verify(bookedPlaceService).getAllBookedPlacesByUser(ADMIN_TEST_OBJECT);
    }

    @Test
    @DisplayName("Тест на вызов доступных слотов")
    void getAllAvailableSlotsServletShouldReturnStatusOkTest() throws ServletException, IOException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);
        when(request.getParameter(any())).thenReturn(TEST_DATE);

        getAllAvailableSlotsServlet.doGet(request,response);

        verify(response).setStatus(SC_OK);
        verify(bookedPlaceService).getAllAvailableDTOSlots(any());
    }

    @Test
    @DisplayName("Тест на вызов доступных слотов неавторизированным пользователем")
    void getAllAvailableSlotsServletShouldReturnUnauthorisedStatusTest() throws ServletException, IOException {
        ServletUtils.setCurrentUser(null);
        when(request.getParameter(any())).thenReturn(TEST_DATE);

        getAllAvailableSlotsServlet.doGet(request,response);

        verify(response).setStatus(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Тест на вызов доступных слотов с неправильной датой")
    void getAllAvailableSlotsServletShouldReturnBadRequestTest() throws ServletException, IOException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);
        when(request.getParameter(any())).thenReturn(TEST_PLACE_NAME_0);

        getAllAvailableSlotsServlet.doGet(request,response);

        verify(response).setStatus(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест со вызов создания бронирования")
    void bookPlaceServletShouldCallBookServiceTest() throws ServletException, IOException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);
        when(objectMapperUtil.getDto(any(), any())).thenReturn(TEST_BOOKED_PLACE_DTO);

        bookPlaceServlet.doPost(request,response);

        verify(response).setStatus(SC_OK);
        verify(bookedPlaceService).bookPlace(any(),any());

    }

    @Test
    @DisplayName("Тест на вызов отмены бронирования")
    void deleteBookedPlaceServletShouldCallCancelingTest() throws ServletException, IOException, BookedPlaceConflictsException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);
        when(request.getParameter(any())).thenReturn("1");

        deleteBookedPlaceServlet.doDelete(request,response);

        verify(response).setStatus(SC_OK);
        verify(bookedPlaceService).cancelBooking(1);
    }

    @Test
    @DisplayName("Тест на вызов отмены неуказанного бронирования")
    void deleteBookedPlaceServletShouldReturnBadRequestWithBadIdTest() throws ServletException, IOException, BookedPlaceConflictsException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);
        when(request.getParameter(any())).thenReturn(null);

        deleteBookedPlaceServlet.doDelete(request,response);

        verify(response).setStatus(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на вызов отмены бронирования не авторизированным пользователем")
    void deleteBookedPlaceServletShouldReturnBadRequestWithNoUserTest() throws ServletException, IOException, BookedPlaceConflictsException {
        ServletUtils.setCurrentUser(null);
        when(request.getParameter(any())).thenReturn("1");

        deleteBookedPlaceServlet.doDelete(request,response);

        verify(response).setStatus(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Тест на вызов отмены бронирования с неправильным id")
    void deleteBookedPlaceServletShouldReturnBadRequestWithIncorrectDataTest() throws ServletException, IOException, BookedPlaceConflictsException {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);
        when(request.getParameter(any())).thenReturn("1fd");

        deleteBookedPlaceServlet.doDelete(request,response);

        verify(response).setStatus(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Тест на вызов иключения при отсутствии авторизации")
    void checkAuthorisationShouldThrowExceptionWhenUserUnauthorisedTest() throws RequiredAuthorisationException {
        ServletUtils.setCurrentUser(null);

        Assertions.assertThatThrownBy(ServletUtils::checkAuthorisation)
                .isInstanceOf(RequiredAuthorisationException.class);
    }

    @Test
    @DisplayName("Тест на вызов иключения при отсутствии прав администратора")
    void checkAdminAuthoritiesOfCurrentUserShouldThrowExceptionWhenUserIsNotAdminTest() throws RequiredAuthorisationException {
        ServletUtils.setCurrentUser(USER_TEST_OBJECT);

        Assertions.assertThatThrownBy(ServletUtils::checkAdminAuthoritiesOfCurrentUser)
                .isInstanceOf(ForbiddenAccessException.class);
    }
}
