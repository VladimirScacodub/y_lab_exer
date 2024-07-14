package org.coworking.aspects;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.coworking.dtos.MessageDTO;
import org.coworking.models.User;
import org.coworking.repositories.UserActionAuditRepository;
import org.coworking.repositories.impl.UserRepositoryImpl;
import org.coworking.services.UserService;
import org.coworking.services.validators.UserValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Аспект, который выполняет аудит основных действий пользователя
 */
@Aspect
@Component
@RequiredArgsConstructor
public class UserAuditAspect {

    private final Connection connection;

    private final UserValidator userValidator;

    private static final String REGISTER_USER_DESCRIPTION = "Пользователь зарегистрировался в системе";

    private static final String USER_BOOKED_PLACE_DESCRIPTION = "Пользователь забронировал место";

    private static final String USER_DELETE_BOOKING_DESCRIPTION = "Пользователь отменил бронирование";

    private static final String USER_BOOKING_VIEW_DESCRIPTION = "Пользователь просмотрел бронированые места";

    private static final String USER_AVAILABLE_SLOTS_DESCRIPTION = "Пользователь просмотрел доступные слоты";

    private static final String USER_VIEW_PLACES_DESCRIPTION = "Пользователь просмотрел список мест";

    private static final String ADMIN_CREATION_PLACE_DESCRIPTION = "Администратор создал новое место";

    private static final String ADMIN_UPDATING_PLACE_DESCRIPTION = "Администратор обновил данные о месте";

    private static final String ADMIN_DELETING_PLACE_DESCRIPTION = "Администратор удалил место";

    /**
     * Advice, который выполняет инструкции по аудированию удалени места пользователем
     */
    @Around("bean(placeController) && execution(* org.coworking.controllers.PlaceController.deletePlace(..))")
    public Object placeDeletingAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, ADMIN_DELETING_PLACE_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию удалени места пользователем
     */
    @Around("bean(placeController) && execution(* org.coworking.controllers.PlaceController.updatePlace(..))")
    public Object placeUpdatingAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, ADMIN_UPDATING_PLACE_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию создания места
     */
    @Around("bean(placeController) && execution(* org.coworking.controllers.PlaceController.savePlace(..))")
    public Object placeCreationAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, ADMIN_CREATION_PLACE_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию просмотра мест
     */
    @Around("bean(placeController) && execution(* org.coworking.controllers.PlaceController.getAllPlaces(..))")
    public Object placeViewAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, USER_VIEW_PLACES_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию простмотра доступных мест
     */
    @Around("bean(bookedPlaceController) && execution(* org.coworking.controllers.BookedPlaceController.getAvailableBookedPlaces(..))")
    public Object availableSlotsAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, USER_AVAILABLE_SLOTS_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию просмотра бронирований
     */
    @Around("bean(bookedPlaceController) && (execution(* org.coworking.controllers.BookedPlaceController.getAllBooking(..)) || execution(* org.coworking.controllers.BookedPlaceController.getCurrentUserBooking(..)))")
    public Object viewBookingAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, USER_BOOKING_VIEW_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию удаления бронирования
     */
    @Around("bean(bookedPlaceController) && execution(* org.coworking.controllers.BookedPlaceController.deleteBookedPlace(..))")
    public Object deleteBookingAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, USER_DELETE_BOOKING_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию создания нового бронирования
     */
    @Around("bean(bookedPlaceController) && execution(* org.coworking.controllers.BookedPlaceController.bookPlace(..))")
    public Object bookPlaceAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return performPlaceAudit(proceedingJoinPoint, USER_BOOKED_PLACE_DESCRIPTION);
    }

    /**
     * Advice, который выполняет инструкции по аудированию регистрции пользователя
     */
    @Around("bean(userController) && execution(* org.coworking.controllers.UserController.registerUser(..))")
    public Object registerAudit(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object proceededObject = proceedingJoinPoint.proceed();
        var result = (ResponseEntity<MessageDTO>) proceededObject;
        String username = result.getHeaders().getFirst("name");
        if (isStatusIsOk(result)) {
            var userOptional = getUserByName(username);
            userOptional.ifPresent(user -> makeAudit(REGISTER_USER_DESCRIPTION, user));
        }
        return result;
    }

    private static boolean isStatusIsOk(ResponseEntity<?> result) {
        return result.getStatusCode() == HttpStatus.OK;
    }

    /**
     * Метод выполняющий аудирование связанное с местами и их бронированием
     * @param proceedingJoinPoint объект, контролирующий точку наблюдения
     * @param description описание события
     * @return Результат работы proceed метода
     * @throws Throwable в случае если возникнет проблема
     */
    private Object performPlaceAudit(ProceedingJoinPoint proceedingJoinPoint, String description) throws Throwable {
        var result = (ResponseEntity<?>) proceedingJoinPoint.proceed();
        if (isStatusIsOk(result)) {
            String credentials = getAuthCredentials(proceedingJoinPoint);
            User user = userValidator.authoriseUser(credentials);
            makeAudit(description, user);
        }
        return result;
    }

    private static String getAuthCredentials(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args = proceedingJoinPoint.getArgs();
        return Stream.of(args)
                .filter(o -> o instanceof String)
                .map(o -> (String)o)
                .filter(s -> s.toLowerCase().contains("basic"))
                .findAny()
                .orElseThrow();
    }

    /**
     * Репозиторий, который позволяет рпаботать с хранилищем записией о событияъ
     */
    private final UserActionAuditRepository userActionAuditRepository;

    /**
     * Запись события в БД
     * @param description Описание события
     * @param user Пользователь который совершил дейстиве
     */
    public void makeAudit(String description, User user) {
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
     * Получение объект User из БД по его имени
     * @param username имя пользователя
     * @return обернутый в Optional объект User
     * @throws SQLException если возникла ошибка при связи с БД
     */
    public Optional<User> getUserByName(String username){
        return new UserService(new UserRepositoryImpl(connection)).getUserByName(username);
    }

}
