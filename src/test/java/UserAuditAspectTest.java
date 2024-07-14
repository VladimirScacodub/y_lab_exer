import org.aspectj.lang.ProceedingJoinPoint;
import org.coworking.aspects.UserAuditAspect;
import org.coworking.repositories.impl.UserActionAuditRepositoryImpl;
import org.coworking.services.validators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.valueOf;
import static utils.TestUtils.ADMIN_BASIC_AUTH_HEADER_VALUE;
import static utils.TestUtils.ADMIN_LOGIN;
import static utils.TestUtils.ADMIN_TEST_OBJECT;

@DisplayName("Тест аудит аспектов")
public class UserAuditAspectTest {

    @Spy
    @InjectMocks
    private UserAuditAspect userAuditAspect;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private UserValidator userValidator;

    @Mock
    private Connection connection;
    @Mock
    private UserActionAuditRepositoryImpl userActionAuditRepository;

    @Mock
    ResponseEntity<String> responseEntity;

    @Mock
    HttpHeaders httpHeaders;

    @BeforeEach
    void setUp() throws Throwable {
        MockitoAnnotations.openMocks(this);
        Mockito.doNothing().when(userAuditAspect).makeAudit(any(), any());
        Object[] objects = new Object[]{ADMIN_BASIC_AUTH_HEADER_VALUE};
        Mockito.doReturn(objects).when(proceedingJoinPoint).getArgs();
        when(proceedingJoinPoint.proceed()).thenReturn(responseEntity);
        when(userValidator.authoriseUser(anyString())).thenReturn(ADMIN_TEST_OBJECT);
    }

    @Test
    @DisplayName("Тест на вызов аудит при регистрации пользователя")
    void registerUserShouldMakeAuditTest() throws Throwable {
        when(responseEntity.getStatusCode()).thenReturn(valueOf(200));
        when(responseEntity.getHeaders()).thenReturn(httpHeaders);
        when(httpHeaders.getFirst("name")).thenReturn(ADMIN_LOGIN);
        Mockito.doReturn(Optional.of(ADMIN_TEST_OBJECT)).when(userAuditAspect).getUserByName(any());

        userAuditAspect.registerAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(), any());
    }


    @Test
    @DisplayName("тест на вызов аудит при просмотре всех мест")
    void viewAllPlacesShouldMakeAuditTest() throws Throwable {
        when(responseEntity.getStatusCode()).thenReturn(valueOf(200));

        userAuditAspect.placeViewAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при просмотре всех доступных слотов")
    void viewAllSlotsShouldMakeAuditTest() throws Throwable {
        when(responseEntity.getStatusCode()).thenReturn(valueOf(200));

        userAuditAspect.availableSlotsAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при бронировании")
    void bookPlaceShouldMakeAuditTest() throws Throwable {
        when(responseEntity.getStatusCode()).thenReturn(valueOf(200));

        userAuditAspect.bookPlaceAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при отмене бронировании")
    void deleteBookingShouldMakeAuditTest() throws Throwable {
        when(responseEntity.getStatusCode()).thenReturn(valueOf(200));

        userAuditAspect.deleteBookingAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при просмотре всех бронировании")
    void viewBookingShouldMakeAuditTest() throws Throwable {
        when(responseEntity.getStatusCode()).thenReturn(valueOf(200));

        userAuditAspect.viewBookingAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при удалении места")
    void placeDeletingShouldMakeAuditTest() throws Throwable {
        when(responseEntity.getStatusCode()).thenReturn(valueOf(200));

        userAuditAspect.placeDeletingAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при создании места")
    void placeCreationShouldMakeAuditTest() throws Throwable {
        when(responseEntity.getStatusCode()).thenReturn(valueOf(200));

        userAuditAspect.placeCreationAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при обновлении места")
    void placeUpdatingShouldMakeAuditTest() throws Throwable {
        when(responseEntity.getStatusCode()).thenReturn(valueOf(200));

        userAuditAspect.placeUpdatingAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

}
