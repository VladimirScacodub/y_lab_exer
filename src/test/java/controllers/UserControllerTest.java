package controllers;

import org.coworking.Utils.exceptions.UserRegistrationException;
import org.coworking.controllers.ExceptionHandlerController;
import org.coworking.controllers.UserController;
import org.coworking.services.UserService;
import org.coworking.services.validators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.TestUtils.TEST_USER_JSON;

@DisplayName("Тест пользовательского Controller")
public class UserControllerTest {

    public static final String REGISTER_USER_ENDPOINT_URL = "/register-user";
    @Mock
    private UserService userService;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserController userController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ExceptionHandlerController())
                .build();
    }

    @Test
    @DisplayName("Тест на вызов регистрации пользователя")
    void registerUserShouldCallRegistrationTest() throws Exception {

        mockMvc.perform(post(REGISTER_USER_ENDPOINT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEST_USER_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        Mockito.verify(userService).registerNewUser(any());
    }
    @Test
    @DisplayName("Тест на вызов неудачной регистрации пользователя")
    void registerUserShouldNotCallRegistrationTest() throws Exception {
        doThrow(new UserRegistrationException(""))
                .when(userValidator).validateUserRegistration(any());

        mockMvc.perform(post(REGISTER_USER_ENDPOINT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEST_USER_JSON)
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());

        Mockito.verify(userValidator).validateUserRegistration(any());
    }

}
