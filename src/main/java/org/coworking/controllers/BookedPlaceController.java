package org.coworking.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.exceptions.ForbiddenAccessException;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.Utils.exceptions.UserAuthorisationException;
import org.coworking.Utils.mappers.BookedPlaceMapper;
import org.coworking.annotations.Loggable;
import org.coworking.dtos.AvailableSlotsDTO;
import org.coworking.dtos.BookedPlaceDTO;
import org.coworking.dtos.MessageDTO;
import org.coworking.models.BookedPlace;
import org.coworking.models.User;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.validators.BookedPlaceValidator;
import org.coworking.services.validators.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Класс контроллер, отвечающий за запросы, связанные с бронированием
 */
@RestController
@Loggable
@Api(value = "Бронирование мест", description = "CRUD операции связанные с бронированием мест")
@RequiredArgsConstructor
public class BookedPlaceController {

    /**
     * Сервис для валидации пользовательских данных
     */
    private final UserValidator userValidator;

    /**
     * Сурвис для работы с бронированием
     */
    private final BookedPlaceService bookedPlaceService;

    /**
     * Класс валидирующий данные о бронировании
     */
    private final BookedPlaceValidator bookedPlaceValidator;

    /**
     * Метод POST, который сохраняет данные о бронировании в БД
     *
     * @param bookedPlaceDTO данные о бронировании
     * @param credentials    данные из Basic Auth header
     * @return ResponseEntity, содержащий ответ
     * @throws RequiredAuthorisationException если пользователь не авторизировался через Basic Auth
     * @throws UserAuthorisationException     если пользователь ввел неправильные данные для авторизации
     * @throws BookedPlaceConflictsException  если новое бронирование конфликтуют с существующими или нарушают ограничения
     */

    @ApiOperation(value = "Создание бронирования",
            notes = "Создает новое бронирование для текущего пользователя")
    @PostMapping("/book-place")
    public ResponseEntity<MessageDTO> bookPlace(@RequestBody BookedPlaceDTO bookedPlaceDTO,
                                                @ApiParam(hidden = true) @RequestHeader(value = "Authorization", required = false) String credentials) throws RequiredAuthorisationException, UserAuthorisationException, BookedPlaceConflictsException {
        User authorisedUser = userValidator.authoriseUser(credentials);
        bookedPlaceValidator.validateExistingBookedDtoFields(bookedPlaceDTO);
        bookedPlaceValidator.validateDateTimeFormat(bookedPlaceDTO.getSlotDTO().getStart());
        bookedPlaceValidator.validateDateTimeFormat(bookedPlaceDTO.getSlotDTO().getEnd());
        BookedPlace bookedPlace = BookedPlaceMapper.INSTANCE.bookedPlaceDtoToBookedPlace(bookedPlaceDTO);
        bookedPlaceValidator.validateBookingPlace(bookedPlace);
        bookedPlaceService.bookPlace(bookedPlace, authorisedUser);
        final String message = "Бронирование выполненно успешно";

        return ResponseEntity.ok(new MessageDTO(message));
    }

    /**
     * Метод DELETE удаляюзий запись о бронировании из БД
     *
     * @param id          id бронирования в БД
     * @param credentials данные из Basic Auth header
     * @return ResponseEntity, содержащий ответ
     * @throws RequiredAuthorisationException если пользователь не авторизировался через Basic Auth
     * @throws UserAuthorisationException     если пользователь ввел неправильные данные для авторизации
     * @throws BookedPlaceConflictsException  если новое бронирование конфликтуют с существующими или нарушают ограничения
     */
    @ApiOperation(value = "Удаление бронирования",
            notes = "Удаляет бронирование по определенному id")
    @DeleteMapping("/delete-booked-place")
    public ResponseEntity<MessageDTO> deleteBookedPlace(@RequestParam(value = "id", required = false) int id,
                                                        @ApiParam(hidden = true) @RequestHeader(value = "Authorization", required = false) String credentials) throws RequiredAuthorisationException, UserAuthorisationException, BookedPlaceConflictsException {
        User user = userValidator.authoriseUser(credentials);
        bookedPlaceValidator.validateCancelingBookedPlaceByNonAdmin(id, user);
        bookedPlaceService.cancelBooking(id);
        final String message = "Удаление было выполненно успешно";

        return ResponseEntity.ok(new MessageDTO(message));
    }

    /**
     * Метод GET возвращающий список всех доступных слотов по определенной дате
     *
     * @param date        дата, по которой будет происходить поиск доступных мест
     * @param credentials данные из Basic Auth header
     * @return ResponseEntity, содержащий список доступных слотов
     * @throws RequiredAuthorisationException если пользователь не авторизировался через Basic Auth
     * @throws UserAuthorisationException     если пользователь ввел неправильные данные для авторизации
     */
    @ApiOperation(value = "Просмотр доступных слотов",
            notes = "Просмотр всех доступных слотов для бронирования")
    @GetMapping("/get-available-slots")
    public ResponseEntity<List<AvailableSlotsDTO>> getAvailableBookedPlaces(@RequestParam(value = "date", required = false) String date,
                                                                            @ApiParam(hidden = true) @RequestHeader(value = "Authorization", required = false) String credentials) throws RequiredAuthorisationException, UserAuthorisationException {
        userValidator.authoriseUser(credentials);
        var dateTime = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        var allAvailableSlotsMap = bookedPlaceService.getAllAvailableDTOSlots(dateTime.atStartOfDay());
        return ResponseEntity.ok(allAvailableSlotsMap);
    }

    /**
     * Метод GET возвращающий список всех бронирований
     *
     * @param indexOfField индекс, по которому происходит сортировка. (1 - имя места 2 - имя пользователя 3 - слот)
     * @param credentials  данные из Basic Auth header
     * @return Список всех бронирований
     * @throws RequiredAuthorisationException если пользователь не авторизировался через Basic Auth
     * @throws UserAuthorisationException     если пользователь ввел неправильные данные для авторизации
     * @throws ForbiddenAccessException       если у пользователя нет администраторских прав для выполнения данной операции
     * @throws BookedPlaceConflictsException  если новое бронирование конфликтуют с существующими или нарушают ограничения
     */
    @ApiOperation(value = "Список всх бронирований",
            notes = "Получение списка всех бронирований всех пользователей")
    @GetMapping("/get-all-booking")
    public ResponseEntity<List<BookedPlaceDTO>> getAllBooking(@RequestParam(value = "indexOfField", required = false) String indexOfField,
                                                              @ApiParam(hidden = true) @RequestHeader(value = "Authorization", required = false) String credentials) throws RequiredAuthorisationException, UserAuthorisationException, ForbiddenAccessException, BookedPlaceConflictsException {
        userValidator.authorizeAdmin(credentials);
        List<BookedPlace> bookedPlaces = nonNull(indexOfField) ? bookedPlaceService.getAllBookedPlacesSortedBy(indexOfField) :
                bookedPlaceService.getAllBookedPlaces();
        var bookedPlacesDTO = toBookedPlaceDtoList(bookedPlaces);
        return ResponseEntity.ok(bookedPlacesDTO);
    }

    /**
     * Метод GET, возвращающий список бронирований, принадлежащий текущему пользователю
     *
     * @param credentials данные из Basic Auth header
     * @return список бронирований, текущего пользователя
     * @throws RequiredAuthorisationException если пользователь не авторизировался через Basic Auth
     * @throws UserAuthorisationException     если пользователь ввел неправильные данные для авторизации
     */
    @ApiOperation(value = "Список бронирования пользователя",
            notes = "Просмотр списка бронирований текущего пользователя")
    @GetMapping("/get-current-user-booked-place")
    public ResponseEntity<List<BookedPlaceDTO>> getCurrentUserBooking(@ApiParam(hidden = true) @RequestHeader(value = "Authorization", required = false) String credentials) throws RequiredAuthorisationException, UserAuthorisationException {
        User user = userValidator.authoriseUser(credentials);
        var bookedPlaces = bookedPlaceService.getAllBookedPlacesByUser(user);
        var bookedPlacesDTO = toBookedPlaceDtoList(bookedPlaces);
        return ResponseEntity.ok(bookedPlacesDTO);
    }

    /**
     * Трансформирует список бронирований в BookedPlaceDTO список
     *
     * @param bookedPlaceList список бронирований
     * @return BookedPlaceDTO список
     */
    private List<BookedPlaceDTO> toBookedPlaceDtoList(List<BookedPlace> bookedPlaceList) {
        return bookedPlaceList.stream()
                .map(BookedPlaceMapper.INSTANCE::bookedPlaceToBookedPlaceDto)
                .toList();
    }


}
