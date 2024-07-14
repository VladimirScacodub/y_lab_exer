package org.coworking.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.coworking.Utils.exceptions.ForbiddenAccessException;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.Utils.exceptions.UserAuthorisationException;
import org.coworking.Utils.mappers.PlaceMapper;
import org.coworking.annotations.Loggable;
import org.coworking.dtos.MessageDTO;
import org.coworking.dtos.PlaceDTO;
import org.coworking.models.Place;
import org.coworking.services.PlaceService;
import org.coworking.services.validators.PlaceValidator;
import org.coworking.services.validators.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Класс контроллер, который обрабатывает запросы, связанные с местами
 */
@RestController
@Loggable
@Api(value = "Рабочие места", description = "CRUD операции связанные с рабочими местами")
@RequiredArgsConstructor
public class PlaceController {

    /**
     * Сервис для работы с местами
     */
    private final PlaceService placeService;

    /**
     * Сервис для валидации данных о местах
     */
    private final PlaceValidator placeValidator;

    /**
     * Сервис для валидации пользовательских данных
     */
    private final UserValidator userValidator;

    /**
     * Метод DELETE для удаления места из БД по имени
     *
     * @param placeName   имя места
     * @param credentials данные из Basic Auth header
     * @return ResponseEntity, содержащий ответ
     * @throws RequiredAuthorisationException если пользователь не авторизировался через Basic Auth
     * @throws UserAuthorisationException     если пользователь ввел неправельные данные для авторизации
     * @throws ForbiddenAccessException       если у пользователя нет администраторских прав для выполнении данной операции
     * @throws PlaceNamingException           если возникают конфликты имен для мест
     */

    @ApiOperation(value = "Удаление места",
            notes = "Удаляет место из БД по имени")
    @DeleteMapping("/delete-place")
    public ResponseEntity<MessageDTO> deletePlace(@RequestParam(value = "placeName", required = false) String placeName,
                                                  @ApiParam(hidden = true) @RequestHeader(value = "Authorization", required = false) String credentials) throws RequiredAuthorisationException, UserAuthorisationException, ForbiddenAccessException, PlaceNamingException {
        userValidator.authorizeAdmin(credentials);
        placeService.removePlace(placeName);
        return ResponseEntity.ok(new MessageDTO("Место было успешно удаленно"));
    }

    /**
     * Метод GET возвращающий список всех мест
     *
     * @param credentials данные из Basic Auth header
     * @return Список всех мест
     * @throws RequiredAuthorisationException если пользователь не авторизировался через Basic Auth
     * @throws UserAuthorisationException     если пользователь ввел неправельные данные для авторизации
     */
    @ApiOperation(value = "Получение информации о всех местах",
            notes = "Возвращает список всех мест, которые есть в БД")
    @GetMapping("/get-all-places")
    public ResponseEntity<List<PlaceDTO>> getAllPlaces(@ApiParam(hidden = true) @RequestHeader(value = "Authorization", required = false) String credentials) throws RequiredAuthorisationException, UserAuthorisationException {
        userValidator.authoriseUser(credentials);
        var placeList = placeService.getAllPlaces();
        var placeDtoList = toPlaceDtoList(placeList);
        return ResponseEntity.ok(placeDtoList);
    }

    /**
     * Метод POST для добавления нового места в БД
     *
     * @param placeDTO    данные нового места
     * @param credentials данные из Basic Auth header
     * @return ResponseEntity, содержащий ответ
     * @throws PlaceNamingException           если возникают конфликты имен для мест
     * @throws RequiredAuthorisationException если пользователь не авторизировался через Basic Auth
     * @throws UserAuthorisationException     если пользователь ввел неправельные данные для авторизации
     * @throws ForbiddenAccessException       если у пользователя нет администраторских прав для выполнении данной операции
     */
    @ApiOperation(value = "Создание места",
            notes = "Создает новое место для бронирования")
    @PostMapping("/save-new-place")
    public ResponseEntity<MessageDTO> savePlace(@RequestBody PlaceDTO placeDTO,
                                                @ApiParam(hidden = true) @RequestHeader(value = "Authorization", required = false) String credentials) throws PlaceNamingException, RequiredAuthorisationException, UserAuthorisationException, ForbiddenAccessException {
        userValidator.authorizeAdmin(credentials);
        placeValidator.validateExistedPlaceName(placeDTO);
        placeValidator.validateExistingPlaceType(placeDTO.getPlaceType());
        Place place = PlaceMapper.INSTANCE.placeDtoToPlace(placeDTO);
        placeService.createNewPlace(place);
        return ResponseEntity.ok(new MessageDTO("Новое место было успешно добавленно"));
    }

    /**
     * Метод PUT, который обновляет данные о месте по его имени
     *
     * @param placeName   имя существующего места, у которого нужно обновить данные
     * @param placeDTO    новые данные для обновления
     * @param credentials данные из Basic Auth header
     * @return ResponseEntity, содержащий ответ
     * @throws PlaceNamingException           если возникают конфликты имен для мест
     * @throws RequiredAuthorisationException если пользователь не авторизировался через Basic Auth
     * @throws UserAuthorisationException     если пользователь ввел неправельные данные для авторизации
     * @throws ForbiddenAccessException       если у пользователя нет администраторских прав для выполнении данной операции
     */
    @ApiOperation(value = "Обновление места",
            notes = "Обновляет данные существующего места по имени")
    @PutMapping("/update-place")
    public ResponseEntity<MessageDTO> updatePlace(@RequestParam("placeName") String placeName,
                                                  @RequestBody PlaceDTO placeDTO,
                                                  @ApiParam(hidden = true) @RequestHeader(value = "Authorization", required = false) String credentials) throws RequiredAuthorisationException, UserAuthorisationException, ForbiddenAccessException, PlaceNamingException {
        userValidator.authorizeAdmin(credentials);
        placeValidator.validatePlaceUpdating(placeName, placeDTO);
        placeValidator.validateExistingPlaceType(placeDTO.getPlaceType());
        var newPlace = PlaceMapper.INSTANCE.placeDtoToPlace(placeDTO);
        placeService.updatePlace(placeName, newPlace);
        return ResponseEntity.ok(new MessageDTO("Место было успешно обновленно"));
    }

    /**
     * Мэппит список Place в список PlaceDTO
     *
     * @param places список мест
     * @return PlaceDTO содержащий данные оместах
     */
    private static List<PlaceDTO> toPlaceDtoList(List<Place> places) {
        return places.stream()
                .map(PlaceMapper.INSTANCE::placeToPlaceDto)
                .toList();
    }
}
