package org.coworking.Utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.coworking.Utils.exceptions.ForbiddenAccessException;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.Utils.mappers.BookedPlaceMapper;
import org.coworking.Utils.mappers.PlaceMapper;
import org.coworking.Utils.mappers.SlotMapper;
import org.coworking.annotations.Loggable;
import org.coworking.dtos.BookedPlaceDTO;
import org.coworking.dtos.PlaceDTO;
import org.coworking.dtos.SlotDTO;
import org.coworking.models.BookedPlace;
import org.coworking.models.Place;
import org.coworking.models.Slot;
import org.coworking.models.User;
import org.coworking.models.enums.Role;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Класс который предоставляет полезные функции по хранению
 * текущего авторизированного пользователя и другие другие функции
 * работающие полезные для данных сервлетов
 */
@Loggable
public class ServletUtils {

    /**
     * Текущий авторизированный пользователь
     */
    @Getter
    @Setter
    private static User currentUser;

    /**
     * Проверка если пользователь авторизовался
     * @return true - если да, иначе false
     */
    public static boolean isUserAuthorised() {
        return nonNull(currentUser);
    }

    /**
     * Устанавливает сообщение и HTTP статус в HTTP ответ
     *
     * @param resp HTTP ответ
     * @param messageText сообщение
     * @param status Код статуса
     * @throws IOException если возникли проблемы с I/O
     */
    public static void setMessage(HttpServletResponse resp, String messageText, int status) throws IOException {
        resp.setStatus(status);
        String message = "{\"message\" : \"" + messageText + "\"}";
        writeTextInResponse(resp, message);
    }

    /**
     * Запись сообщения в тело HTTP ответа
     *
     * @param resp HTTP ответ
     * @param text сообщение
     * @throws IOException если возникли проблемы с I/O
     */
    private static void writeTextInResponse(HttpServletResponse resp, String text) throws IOException {
        resp.getOutputStream().write(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Трансформирует список мест в PlaceDTO список
     *
     * @param places список мест
     * @return PlaceDTO список
     */
    public static List<PlaceDTO> toPlaceDtoList(List<Place> places) {
        return places.stream()
                .map(PlaceMapper.INSTANCE::placeToPlaceDto)
                .collect(Collectors.toList());
    }

    /**
     * Трансформирует список слотов в SlotsDTO список
     *
     * @param slots список слотов
     * @return SlotsDTO список
     */
    public static List<SlotDTO> toSlotsDtoList(List<Slot> slots){
        return slots.stream()
                .map(SlotMapper.INSTANCE::slotToSlotDto)
                .collect(Collectors.toList());
    }

    /**
     * Валидирует авторизацию польнователя
     *
     * @throws RequiredAuthorisationException если пользоватьль не авторизирован
     */
    public static void checkAuthorisation() throws RequiredAuthorisationException {
        if (!isUserAuthorised()) {
            throw new RequiredAuthorisationException("Требуется авторизация");
        }
    }

    /**
     * Проверяет права пользователя на наличие прав администратора
     *
     * @return true если пользователь - администратор, false если нет
     * @throws RequiredAuthorisationException если пользоватьль не авторизирован
     */
    public static boolean isCurrentUserAdmin() throws RequiredAuthorisationException {
        checkAuthorisation();
        return Objects.equals(getCurrentUser().getRole(), Role.ADMIN);
    }

    /**
     * Валидирует наличие администраторских прав у пользователя
     *
     * @throws ForbiddenAccessException если у пользователя нет администраторских прав
     * @throws RequiredAuthorisationException если пользоватьль не авторизирован
     */
    public static void checkAdminAuthoritiesOfCurrentUser() throws ForbiddenAccessException, RequiredAuthorisationException {
        if (!isCurrentUserAdmin()) {
            throw new ForbiddenAccessException("Доступ только для администратора");
        }
    }

    /**
     * Трансформирует список бронирований в BookedPlaceDTO список
     *
     * @param bookedPlaceList список бронирований
     * @return BookedPlaceDTO список
     */
    public static List<BookedPlaceDTO> toBookedPlaceDtoList(List<BookedPlace> bookedPlaceList) {
        return bookedPlaceList.stream()
                .map(BookedPlaceMapper.INSTANCE::bookedPlaceToBookedPlaceDto)
                .collect(Collectors.toList());
    }

}
