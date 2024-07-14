package org.coworking.services.validators;

import lombok.AllArgsConstructor;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.annotations.Loggable;
import org.coworking.dtos.PlaceDTO;
import org.coworking.models.Place;
import org.coworking.models.enums.PlaceType;
import org.coworking.services.PlaceService;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Класс используется для валидации данных рабочих мест и конференц-залов
 */
@Loggable
@AllArgsConstructor
@Component
public class PlaceValidator {

    /**
     * Зависимость используемая для получения данных об доступных местах
     */
    private PlaceService placeService;

    /**
     * Проверяет отсуствие места с указанным именем
     *
     * @param placeName имя места
     * @throws PlaceNamingException в случае если такое место существует
     */
    public void validateExistedPlaceName(String placeName) throws PlaceNamingException {
        if (placeService.findByName(placeName).isPresent()) {
            throw new PlaceNamingException("Рабочее место с таким именем уже существует!");
        }
    }

    /**
     * Проверяет отсуствие места с указанным именем
     *
     * @param placeDTO объект содержащий имя места
     * @throws PlaceNamingException в случае если такое место существует
     */
    public void validateExistedPlaceName(PlaceDTO placeDTO) throws PlaceNamingException {
        validateExistedPlaceName(placeDTO.getPlaceName());
    }

    /**
     * Валидирует данные используемые для обновления рабочего места
     *
     * @param placeName    имя места, которое используется для поиска
     * @param newPlaceName новое имя для текщего места
     * @throws PlaceNamingException выбрасывается в случае передачи некоректных данных (несуществующего места или занятого имени)
     */
    public void validatePlaceUpdating(String placeName, String newPlaceName) throws PlaceNamingException {
        Place oldPlace = placeService.findByName(placeName)
                .orElseThrow(() -> new PlaceNamingException("Рабочее место с таким именем не существует!"));

        if (doesAnotherPlaceWithThisNameExists(newPlaceName, oldPlace)) {
            throw new PlaceNamingException("Другое place с таким именем уже существует!");
        }
    }

    /**
     * Проверяет на существование типа места
     *
     * @param placeType тип места
     * @throws RuntimeException если такого места нет
     */
    public void validateExistingPlaceType(String placeType) throws RuntimeException {
        try {
            PlaceType.valueOf(placeType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Такого типа не существует");
        }
    }

    /**
     * Валидирует данные используемые для обновления рабочего места
     *
     * @param placeName имя старого рабочего места
     * @param placeDTO объект содерщащий новые данные для обновления
     * @throws PlaceNamingException если места с таким именем не сущестует
     */
    public void validatePlaceUpdating(String placeName, PlaceDTO placeDTO) throws PlaceNamingException {
        validatePlaceUpdating(placeName, placeDTO.getPlaceName());
    }

    /**
     * Проверяет новое имя для места на занятость другим местом
     *
     * @param placeName Новое имя для места
     * @param oldPlace  Текущее имя для места
     * @return true если новое имя занято, иначе false
     */
    private boolean doesAnotherPlaceWithThisNameExists(String placeName, Place oldPlace) {
        return placeService.getAllPlaces().stream()
                .filter(place -> !Objects.equals(place, oldPlace))
                .anyMatch(place -> Objects.equals(place.getPlaceName(), placeName));
    }
}
