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
import org.coworking.annotations.PlaceUpdatingAudit;
import org.coworking.dtos.PlaceDTO;
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
 * Сервлет позволяющий обновлять данные о месте
 */
@Loggable
@PlaceUpdatingAudit
public class UpdatePlaceServlet extends HttpServlet {

    /**
     * Сервис для работы с местами
     */
    private final PlaceService placeService;

    /**
     * Сервис для валидации данных о месте
     */
    private final PlaceValidator placeValidator;

    /**
     * Объект позволяеющий трансвормировать JSON в объекты и наоборот
     */
    private final ObjectMapperUtil objectMapperUtil;

    public UpdatePlaceServlet(PlaceService placeService, PlaceValidator placeValidator, ObjectMapperUtil objectMapperUtil) throws SQLException {
        this.placeService = placeService;
        this.placeValidator = placeValidator;
        this.objectMapperUtil = objectMapperUtil;
    }

    /**
     * Метод PUT, выполняющий обновление данных о месте
     *
     * @param req HTTP запрос
     * @param resp HTTP ответ
     * @throws ServletException если возникает броблемы с сервлетом
     * @throws IOException если возникает  проблемы с потоками I/O
     */
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            checkAdminAuthoritiesOfCurrentUser();
            String pathVarPlaceName = req.getParameter("placeName");
            var placeDto = objectMapperUtil.getDto(req, PlaceDTO.class);
            placeValidator.validatePlaceUpdating(pathVarPlaceName, placeDto);
            placeValidator.validateExistingPlaceType(placeDto.getPlaceType());
            var newPlace = PlaceMapper.INSTANCE.placeDtoToPlace(placeDto);
            placeService.updatePlace(pathVarPlaceName, newPlace);
            final String message = "Place было успешно обновлено";
            setMessage(resp, message, SC_OK);
        }catch (RequiredAuthorisationException e) {
            setMessage(resp, e.getMessage(), SC_UNAUTHORIZED);
        } catch (ForbiddenAccessException e) {
            setMessage(resp, e.getMessage(), SC_FORBIDDEN);
        } catch (Exception e) {
            setMessage(resp, e.getMessage(), SC_BAD_REQUEST);
        }
    }
}
