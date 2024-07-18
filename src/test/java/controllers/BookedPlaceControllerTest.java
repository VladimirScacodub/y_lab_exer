package controllers;

import org.coworking.controllers.BookedPlaceController;
import org.coworking.controllers.ExceptionHandlerController;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.validators.BookedPlaceValidator;
import org.coworking.services.validators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import utils.TestUtils;

import static java.lang.String.valueOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.TestUtils.TEST_BOOKED_PLACE_JSON;

@DisplayName("Тест Controller для бронирования")
public class BookedPlaceControllerTest {

    public static final String BOOK_PLACE_ENDPOINT_URL = "/book-place";
    public static final String DELETE_BOOKED_PLACE_ENDPOINT_URL = "/delete-booked-place";
    public static final String GET_AVAILABLE_SLOTS_ENDPOINT_URL = "/get-available-slots";
    public static final String GET_ALL_BOOKING_ENPOINT_URL = "/get-all-booking";
    public static final String GET_CURRENT_USER_BOOKING_ENDPOINT_URL = "/get-current-user-booked-place";
    @Mock
    private UserValidator userValidator;

    @Mock
    private BookedPlaceService bookedPlaceService;

    @Mock
    private BookedPlaceValidator bookedPlaceValidator;

    @InjectMocks
    private BookedPlaceController placeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(placeController)
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
    }

    @Test
    @DisplayName("Тест на вызов создания бронирования")
    void bookPlaceShouldCallBookingCreationTest() throws Exception {
        mockMvc.perform(post(BOOK_PLACE_ENDPOINT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEST_BOOKED_PLACE_JSON))
                .andExpect(status().isOk());

        verify(bookedPlaceValidator).validateExistingBookedDtoFields(any());
        verify(bookedPlaceValidator,times(2)).validateDateTimeFormat(anyString());
        verify(bookedPlaceValidator).validateBookingPlace(any());
        verify(bookedPlaceService).bookPlace(any(),any());
    }

    @Test
    @DisplayName("Тест на вызов удаления бронирования")
    void deleteBookedPlaceShouldCallBookingDeletingTest() throws Exception {
        final int CANCEL_BOOKING_ID = 1;

        mockMvc.perform(delete(DELETE_BOOKED_PLACE_ENDPOINT_URL).param("id", valueOf(CANCEL_BOOKING_ID)))
                .andExpect(status().isOk());

        verify(bookedPlaceValidator).validateCancelingBookedPlaceByNonAdmin(eq(CANCEL_BOOKING_ID), any());
        verify(bookedPlaceService).cancelBooking(CANCEL_BOOKING_ID);
    }

    @Test
    @DisplayName("Тест на вызов получения всех доступных слотов по дате")
    void getAvailableBookedPlacesShouldCallListOfAvailablePlacesTest() throws Exception {
        final String TEST_DATE_PARAM = "2024-06-22";
        mockMvc.perform(get(GET_AVAILABLE_SLOTS_ENDPOINT_URL).param("date", TEST_DATE_PARAM))
                .andExpect(status().isOk());

        verify(bookedPlaceService).getAllAvailableDTOSlots(any());
    }

    @Test
    @DisplayName("Тест на вызов получения всех доступных слотов по неправильной дате")
    void getAvailableBookedPlacesShouldNotCallListByIncorrectDataTest() throws Exception {
        final String TEST_DATE_PARAM = "2024-22-01";
        mockMvc.perform(get(GET_AVAILABLE_SLOTS_ENDPOINT_URL).param("date", TEST_DATE_PARAM))
                .andExpect(status().isBadRequest());

        verify(bookedPlaceService, times(0)).getAllAvailableDTOSlots(any());
    }

    @Test
    @DisplayName("Тест на вызов получения всей информации о бронировании с сортировкой")
    void getAllBookingShouldCallSortedListOfAllBookingTest() throws Exception {
        final String TEST_DATE_PARAM = "1";
        mockMvc.perform(get(GET_ALL_BOOKING_ENPOINT_URL).param("indexOfField", TEST_DATE_PARAM))
                .andExpect(status().isOk());

        verify(bookedPlaceService).getAllBookedPlacesSortedBy(TEST_DATE_PARAM);
    }

    @Test
    @DisplayName("Тест на вызов получения всей информации о бронировании без сортировки")
    void getAllBookingShouldCallListOfAllBookingTest() throws Exception {
        mockMvc.perform(get(GET_ALL_BOOKING_ENPOINT_URL))
                .andExpect(status().isOk());

        verify(bookedPlaceService).getAllBookedPlaces();
    }
    @Test
    @DisplayName("Тест на вызов получения всей информации о бронировании текущего пользователя")
    void getCurrentUserBookingShouldCallListOfBookingByUserTest() throws Exception {
        mockMvc.perform(get(GET_CURRENT_USER_BOOKING_ENDPOINT_URL))
                .andExpect(status().isOk());

        verify(bookedPlaceService).getAllBookedPlacesByUser(any());
    }
}
