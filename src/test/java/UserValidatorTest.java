import org.coworking.services.validators.UserValidator;
import org.coworking.Utils.exceptions.UserAuthorisationException;
import org.coworking.Utils.exceptions.UserRegistrationException;
import org.coworking.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static utils.TestUtils.EMPTY_STRING;
import static utils.TestUtils.EXISTENT_NAME;
import static utils.TestUtils.NEW_NAME_STRING;
import static utils.TestUtils.USER_TEST_LIST;
import static utils.TestUtils.USER_TEST_OBJECT;

@DisplayName("Тест Валидатра для User service")
public class UserValidatorTest {

    @Mock
    UserService userService;

    @InjectMocks
    UserValidator userValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Проверка на выброс исключений при неправильной регистрации пользователя")
    void validateUserRegistrationShouldThrowExceptionWithIncorrectDataTest() {
        when(userService.findAll()).thenReturn(USER_TEST_LIST);

        assertThatThrownBy(() -> userValidator.validateUserRegistration(EMPTY_STRING, EXISTENT_NAME))
                .isInstanceOf(UserRegistrationException.class);
        assertThatThrownBy(()-> userValidator.validateUserRegistration(EXISTENT_NAME, EXISTENT_NAME))
                .isInstanceOf(UserRegistrationException.class);
        assertThatThrownBy(()-> userValidator.validateUserRegistration(NEW_NAME_STRING, EMPTY_STRING))
                .isInstanceOf(UserRegistrationException.class);
    }

    @Test
    @DisplayName("Проверка на выброс исключений при авторизации несуществующего пользователя")
    void getValidatedAuthorisedUserShouldThrowExceptionsWhenUserDoesNotExistsTest(){
        when(userService.getUserByName(NEW_NAME_STRING))
                .thenReturn(Optional.empty());

        assertThatThrownBy(()->userValidator.getValidatedAuthorisedUser(NEW_NAME_STRING, NEW_NAME_STRING))
                .isInstanceOf(UserAuthorisationException.class);
    }

    @Test
    @DisplayName("Проверка на выброс исключений при авторизации пользователя с неправильным паролем")
    void getValidatedAuthorisedUserShouldThrowExceptionsWhenIncorrectPasswordExistsTest(){
        when(userService.getUserByName(EXISTENT_NAME))
                .thenReturn(Optional.of(USER_TEST_OBJECT));

        assertThatThrownBy(()->userValidator.getValidatedAuthorisedUser(EXISTENT_NAME, NEW_NAME_STRING))
                .isInstanceOf(UserAuthorisationException.class);
    }
}
