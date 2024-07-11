import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.coworking.Utils.ServletUtils;
import org.coworking.aspects.UserAuditAspect;
import org.coworking.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.sql.SQLException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static utils.TestUtils.ADMIN_LOGIN;
import static utils.TestUtils.ADMIN_TEST_OBJECT;

@DisplayName("Тест аудит аспектов")
public class UserAuditAspectTest {

    @Spy
    private UserAuditAspect userAuditAspect;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        userAuditAspect = Mockito.spy(new UserAuditAspect());
        Mockito.doNothing().when(userAuditAspect).makeAudit(any(), any());
        Object[] objects = new Object[]{request, response};
        Mockito.doReturn(objects).when(proceedingJoinPoint).getArgs();
    }

    @Test
    @DisplayName("Тест на вызов аудит при регистрации пользователя")
    void registerUserShouldMakeAuditTest() throws Throwable {
        when(request.getParameter("username")).thenReturn(ADMIN_LOGIN);
        when(response.getStatus()).thenReturn(SC_OK);
        Mockito.doReturn(Optional.of(ADMIN_TEST_OBJECT)).when(userAuditAspect).getUserByName(any());
        userAuditAspect.registerAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(), any());
    }

    @Test
    @DisplayName("Тест на вызов отсутствие вызова аудит при повторной авторизации пользователя")
    void authorisationUserShouldNotMakeMakeAuditWhenLoginRepeating() throws Throwable {
        ServletUtils.setCurrentUser(ADMIN_TEST_OBJECT);

        userAuditAspect.loginAudit(proceedingJoinPoint);

        verify(userAuditAspect, times(0)).makeAudit(anyString(),any(User.class));
    }

    @Test
    @DisplayName("тест на вызов аудит при просмотре всех мест")
    void viewAllPlacesShouldMakeAuditTest() throws Throwable {
        when(response.getStatus()).thenReturn(SC_OK);

        userAuditAspect.placeViewAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при просмотре всех доступных слотов")
    void viewAllSlotsShouldMakeAuditTest() throws Throwable {
        when(response.getStatus()).thenReturn(SC_OK);

        userAuditAspect.availableSlotsAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при бронировании")
    void bookPlaceShouldMakeAuditTest() throws Throwable {
        when(response.getStatus()).thenReturn(SC_OK);

        userAuditAspect.bookPlaceAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при отмене бронировании")
    void deleteBookingShouldMakeAuditTest() throws Throwable {
        when(response.getStatus()).thenReturn(SC_OK);

        userAuditAspect.deleteBookingAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при просмотре всех бронировании")
    void viewBookingShouldMakeAuditTest() throws Throwable {
        when(response.getStatus()).thenReturn(SC_OK);

        userAuditAspect.viewBookingAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при удалении места")
    void placeDeletingShouldMakeAuditTest() throws Throwable {
        when(response.getStatus()).thenReturn(SC_OK);

        userAuditAspect.placeDeletingAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при создании места")
    void placeCreationShouldMakeAuditTest() throws Throwable {
        when(response.getStatus()).thenReturn(SC_OK);

        userAuditAspect.placeCreationAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

    @Test
    @DisplayName("тест на вызов аудит при обновлении места")
    void placeUpdatingShouldMakeAuditTest() throws Throwable {
        when(response.getStatus()).thenReturn(SC_OK);

        userAuditAspect.placeUpdatingAudit(proceedingJoinPoint);

        verify(userAuditAspect).makeAudit(any(),any());
    }

}
