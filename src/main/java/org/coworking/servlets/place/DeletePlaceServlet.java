package org.coworking.servlets.place;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.coworking.Utils.exceptions.ForbiddenAccessException;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.annotations.Loggable;
import org.coworking.annotations.PlaceDeletingAudit;
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
 * Сервлет отвечающий за удаление места администратором
 */
@Loggable
@PlaceDeletingAudit
public class DeletePlaceServlet extends HttpServlet {

    /**
     * Сервис для работы с метами
     */
    private final PlaceService placeService;

    /**
     * Сервис для валидации данных о местах
     */
    private final PlaceValidator placeValidator;

    public DeletePlaceServlet(PlaceService placeService, PlaceValidator placeValidator) throws SQLException {
        this.placeService = placeService;
        this.placeValidator = placeValidator;
    }

    /**
     * Метод DELETE, удаляющий место из БД
     *
     * @param req  HTTP запрос
     * @param resp HTTP ответ
     * @throws ServletException если возникает броблемы с сервлетом
     * @throws IOException      если возникает  проблемы с потоками I/O
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            checkAdminAuthoritiesOfCurrentUser();
            String pathVarPlaceName = req.getParameter("placeName");
            placeService.removePlace(pathVarPlaceName);
            final String message = "Place был успешно удален";
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
