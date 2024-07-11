package org.coworking.servlets.place;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.coworking.Utils.ObjectMapperUtil;
import org.coworking.Utils.exceptions.ForbiddenAccessException;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.Utils.mappers.PlaceMapper;
import org.coworking.annotations.Loggable;
import org.coworking.annotations.PlaceCreationAudit;
import org.coworking.dtos.PlaceDTO;
import org.coworking.models.Place;
import org.coworking.services.PlaceService;
import org.coworking.services.validators.PlaceValidator;

import java.io.IOException;
import java.sql.SQLException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.coworking.Utils.ServletUtils.checkAdminAuthoritiesOfCurrentUser;
import static org.coworking.Utils.ServletUtils.setMessage;

/**
 * Сервлет позволяющий сохранить новое место
 */
@Loggable
@PlaceCreationAudit
public class SaveNewPlaceServlet extends HttpServlet {

    /**
     * Сервис для работы с местами
     */
    private final PlaceService placeService;

    /**
     * Сервис для валидации данных о местах
     */
    private final PlaceValidator placeValidator;

    /**
     * Объект позволяеющий трансвормировать JSON в объекты и наоборот
     */
    private final ObjectMapperUtil objectMapperUtil;

    public SaveNewPlaceServlet(PlaceService placeService, PlaceValidator placeValidator, ObjectMapperUtil objectMapperUtil) throws SQLException {
        this.placeService = placeService;
        this.placeValidator = placeValidator;
        this.objectMapperUtil = objectMapperUtil;
    }

    /**
     * Метод POST, созраняющий новое место в БД
     *
     * @param req HTTP запрос
     * @param resp HTTP ответ
     * @throws ServletException если возникает броблемы с сервлетом
     * @throws IOException если возникает  проблемы с потоками I/O
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            checkAdminAuthoritiesOfCurrentUser();
            var placeDto = objectMapperUtil.getDto(req, PlaceDTO.class);
            placeValidator.validateExistedPlaceName(placeDto);
            placeValidator.validateExistingPlaceType(placeDto.getPlaceType());
            Place place = PlaceMapper.INSTANCE.placeDtoToPlace(placeDto);
            placeService.createNewPlace(place);
            final String message = "Place был успешно создан";
            setMessage(resp, message, SC_OK);
        } catch (RequiredAuthorisationException e) {
            setMessage(resp, e.getMessage(), SC_UNAUTHORIZED);
        } catch (ForbiddenAccessException e) {
            setMessage(resp, e.getMessage(), SC_FORBIDDEN);
        } catch (Exception e) {
            setMessage(resp, e.getMessage(), SC_BAD_REQUEST);
        }
    }
}
