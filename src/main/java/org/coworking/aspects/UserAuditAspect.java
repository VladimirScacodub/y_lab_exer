package org.coworking.aspects;

import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.coworking.Utils.JDBCUtils;
import org.coworking.Utils.ServletUtils;
import org.coworking.models.User;
import org.coworking.repositories.UserActionAuditRepository;
import org.coworking.repositories.UserActionAuditRepositoryImpl;
import org.coworking.repositories.UserRepositoryImpl;
import org.coworking.services.UserService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.coworking.Utils.ServletUtils.isUserAuthorised;

/**
 * Аспект, который выполняет аудит основных действий пользователя
 */
@Aspect
public class UserAuditAspect {

    private static final String LOGIN_USER_DESCRIPTION = "Пользователь вошел в систему";

    private static final String REGISTER_USER_DESCRIPTION = "Пользователь зарегистрировался в системе";

    private static final String USER_BOOKED_PLACE_DESCRIPTION = "Пользователь забронировал место";

    private static final String USER_DELETE_BOOKING_DESCRIPTION = "Пользователь отменил бронирование";

    private static final String USER_BOOKING_VIEW_DESCRIPTION = "Пользователь просмотрел бронированые места";

    private static final String USER_AVAILABLE_SLOTS_DESCRIPTION = "Пользователь просмотрел доступные слоты";

    private static final String USER_VIEW_PLACES_DESCRIPTION = "Пользователь просмотрел список мест";

    private static final String ADMIN_CREATION_PLACE_DESCRIPTION = "Администратор создал новое место";

    private static final String ADMIN_UPDATING_PLACE_DESCRIPTION = "Администратор обновил данные о месте";

    private static final String ADMIN_DELETING_PLACE_DESCRIPTION = "Администратор удалил место";

    public UserAuditAspect() throws SQLException {
    }

    /**
     * Pointcut авторизации пользователя
     */
    @Pointcut("within(@org.coworking.annotations.LoginAudit *) && execution(* * (..))")
    public void annotatedByLoginAudit() {
    }

    /**
     * Pointcut регистрации пользователя
     */
    @Pointcut("within(@org.coworking.annotations.RegisterAudit *) && execution(* * (..))")
    public void annotatedByRegisterAudit() {
    }

    /**
     * Pointcut бронирования мест
     */
    @Pointcut("within(@org.coworking.annotations.BookPlaceAudit *) && execution(* * (..))")
    public void annotatedByBookPlaceAudit() {
    }

    /**
     * Pointcut отмены бронирования
     */
    @Pointcut("within(@org.coworking.annotations.DeleteBookingAudit *) && execution(* * (..))")
    public void annotatedByDeleteBookingAudit() {
    }

    /**
     * Pointcut просмотра бронирования
     */
    @Pointcut("within(@org.coworking.annotations.BookingViewAudit *) && execution(* * (..))")
    public void annotatedByBookingViewAudit() {
    }

    /**
     * Pointcut простмотра доступных слотов
     */
    @Pointcut("within(@org.coworking.annotations.AvailableSlotsAudit *) && execution(* * (..))")
    public void annotatedByAvailableSlotsAudit() {
    }

    /**
     * Pointcut просмотра мест
     */
    @Pointcut("within(@org.coworking.annotations.PlacesViewAudit *) && execution(* * (..))")
    public void annotatedByPlaceViewAudit() {}

    /**
     * Pointcut создания места
     */
    @Pointcut("within(@org.coworking.annotations.PlaceCreationAudit *) && execution(* * (..))")
    public void annotatedByPlaceCreationAudit() {
    }

    /**
     * Pointcut обновления места
     */
    @Pointcut("within(@org.coworking.annotations.PlaceUpdatingAudit *) && execution(* * (..))")
    public void annotatedByPlaceUpdatingAudit() {
    }

    /**
     * Pointcut удаления места
     */
    @Pointcut("within(@org.coworking.annotations.PlaceDeletingAudit *) && execution(* * (..))")
    public void annotatedByPlaceDeletingAudit() {
    }

    /**
     * Advice, который выполняет инструкции по аудированию удалени места пользователем
     */
    @Around("annotatedByPlaceDeletingAudit()")
    public Object placeDeletingAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, ADMIN_DELETING_PLACE_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию удалени места пользователем
     */
    @Around("annotatedByPlaceUpdatingAudit()")
    public Object placeUpdatingAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, ADMIN_UPDATING_PLACE_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию создания места
     */
    @Around("annotatedByPlaceCreationAudit()")
    public Object placeCreationAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, ADMIN_CREATION_PLACE_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию просмотра мест
     */
    @Around("annotatedByPlaceViewAudit()")
    public Object placeViewAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, USER_VIEW_PLACES_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию простмотра доступных мест
     */
    @Around("annotatedByAvailableSlotsAudit()")
    public Object availableSlotsAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, USER_AVAILABLE_SLOTS_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию просмотра бронирований
     */
    @Around("annotatedByBookingViewAudit()")
    public Object viewBookingAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, USER_BOOKING_VIEW_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию удаления бронирования
     */
    @Around("annotatedByDeleteBookingAudit()")
    public Object deleteBookingAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, USER_DELETE_BOOKING_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию создания нового бронирования
     */
    @Around("annotatedByBookPlaceAudit()")
    public Object bookPlaceAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, USER_BOOKED_PLACE_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию регистрции пользователя
     */
    @Around("annotatedByRegisterAudit()")
    public Object registerAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var result = proceedingJoinPoint.proceed();
        HttpServletResponse response = getResponse(proceedingJoinPoint);
        String username = response.getHeader("username");

        if (isStatusIsOk(response)) {
            var userOptional = getUserByName(username);
            userOptional.ifPresent(user -> makeAudit(REGISTER_USER_DESCRIPTION, user));
        }
        return result;
    }

    /**
     * Advice, который выполняет инструкции по аудированию авторизации пользователя
     */
    @Around("annotatedByLoginAudit()")
    public Object loginAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        User previousUser = null;
        if (isUserAuthorised()) {
            previousUser = ServletUtils.getCurrentUser();
        }
        Object proceed = proceedingJoinPoint.proceed();
        User newUser = null;
        if (isUserAuthorised()) {
            newUser = ServletUtils.getCurrentUser();
        }
        if (!Objects.equals(newUser, previousUser) && isUserAuthorised()) {
            makeAudit(LOGIN_USER_DESCRIPTION, newUser);
        }
        return proceed;
    }

    /**
     * Метод выполняющий аудирование связанное с местами и их бронированием
     * @param proceedingJoinPoint объект, контролирующий точку наблюдения
     * @param description описание события
     * @return Результат работы proceed метода
     * @throws Throwable в случае если возникнет проблема
     */
    private Object performPlaceAudit(ProceedingJoinPoint proceedingJoinPoint, String description) throws Throwable {
        var result = proceedingJoinPoint.proceed();
        HttpServletResponse response = getResponse(proceedingJoinPoint);
        if (isStatusIsOk(response)) {
            User user = ServletUtils.getCurrentUser();
            makeAudit(description, user);
        }
        return result;
    }

    /**
     * Репозиторий, который позволяет рпаботать с хранилищем записией о событияъ
     */
    private UserActionAuditRepository userActionAuditRepository;

    /**
     * Запись события в БД
     * @param description Описание события
     * @param user Пользователь который совершил дейстиве
     */
    public void makeAudit(String description, User user) {
        if (Objects.isNull(userActionAuditRepository)){
            try {
                userActionAuditRepository = new UserActionAuditRepositoryImpl(JDBCUtils.getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        userActionAuditRepository.save(user, description, LocalDateTime.now());
    }

    /**
     * Получение HttpServletResponse из ProceedingJoinPoint
     * @param proceedingJoinPoint объект, контролирующий точку наблюдения
     * @return HttpServletResponse объект
     */
    private static HttpServletResponse getResponse(ProceedingJoinPoint proceedingJoinPoint) {
        return (HttpServletResponse) proceedingJoinPoint.getArgs()[1];
    }

    /**
     * Проверяет если статус HTTP ответа равен OK
     * @param response HTTP ответ
     * @return true если равен, иначе false
     */
    private static boolean isStatusIsOk(HttpServletResponse response) {
        return response.getStatus() == HttpServletResponse.SC_OK;
    }

    /**
     * Получение объект User из БД по его имени
     * @param username имя пользователя
     * @return обернутый в Optional объект User
     * @throws SQLException если возникла ошибка при связи с БД
     */
    public Optional<User> getUserByName(String username) throws SQLException {
        return new UserService(new UserRepositoryImpl(JDBCUtils.getConnection())).getUserByName(username);
    }

}
