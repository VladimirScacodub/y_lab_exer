package controllers;


import org.coworking.controllers.ExceptionHandlerController;
import org.coworking.controllers.PlaceController;
import org.coworking.services.PlaceService;
import org.coworking.services.validators.PlaceValidator;
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
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import utils.TestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.TestUtils.TEST_PLACE_JSON;
import static utils.TestUtils.TEST_PLACE_NAME_0;
import static utils.TestUtils.TEST_PLACE_NAME_1;

@DisplayName("Тест Controller для мест")
public class PlaceControllerTest {

    public static final String DELETE_PLACE_ENDPOINT_URL = "/delete-place";
    public static final String GET_ALL_PLACES_ENDPOINT_URL = "/get-all-places";
    public static final String SAVE_NEW_PLACE_ENDPOINT_URL = "/save-new-place";
    public static final String UPDATE_PLACE_ENDPOINT_URL = "/update-place";
    public static final String PLACE_NAME_PARAM_NAME = "placeName";
    @Mock
    private PlaceService placeService;

    @Mock
    private PlaceValidator placeValidator;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private PlaceController placeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(placeController)
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
    }

    @Test
    @DisplayName("Тест на вызов удаления места")
    void deletePlaceShouldCallDeletingPlaceTest() throws Exception {
        mockMvc.perform(delete(DELETE_PLACE_ENDPOINT_URL).param(PLACE_NAME_PARAM_NAME, TEST_PLACE_NAME_0))
                .andExpect(status().isOk());

        verify(placeService).removePlace(anyString());
    }

    @Test
    @DisplayName("Тест на вызов списка всех мест")
    void getAllPlacesShouldCallListOfPlacesTest() throws Exception {
        mockMvc.perform(get(GET_ALL_PLACES_ENDPOINT_URL))
                .andExpect(status().isOk());

        verify(placeService).getAllPlaces();
    }

    @Test
    @DisplayName("Тест на вызов создания нового места")
    void savePlaceShouldCallSavingNewPlaceTest() throws Exception {
        mockMvc.perform(post(SAVE_NEW_PLACE_ENDPOINT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEST_PLACE_JSON))
                .andExpect(status().isOk());

        verify(placeService).createNewPlace(any());
    }

    @Test
    @DisplayName("Тест на вызов обновления данных существующего места")
    void updatePlaceShouldCallPlaceUpdatingTest() throws Exception {
        mockMvc.perform(put(UPDATE_PLACE_ENDPOINT_URL)
                        .param(PLACE_NAME_PARAM_NAME, TEST_PLACE_NAME_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEST_PLACE_JSON))
                .andExpect(status().isOk());

        verify(placeService).updatePlace(anyString(), any());
    }

}
