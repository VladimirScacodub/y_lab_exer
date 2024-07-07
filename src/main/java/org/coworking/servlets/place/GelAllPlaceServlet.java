package org.coworking.servlets.place;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.coworking.Utils.ObjectMapperUtil;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.annotations.Loggable;
import org.coworking.annotations.PlacesViewAudit;
import org.coworking.services.PlaceService;

import java.io.IOException;
import java.sql.SQLException;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.coworking.Utils.ServletUtils.checkAuthorisation;
import static org.coworking.Utils.ServletUtils.setMessage;
import static org.coworking.Utils.ServletUtils.toPlaceDtoList;

/**
 * Сервлет отвечающий за получение данных о местах
 */
@Loggable
@PlacesViewAudit
public class GelAllPlaceServlet extends HttpServlet {

    /**
     * Сервис для рабоы с местами
     */
    private final PlaceService placeService;

    /**
     * Объект позволяеющий трансвормировать JSON в объекты и наоборот
     */
    private final ObjectMapperUtil objectMapperUtil;

    public GelAllPlaceServlet(PlaceService placeService, ObjectMapperUtil objectMapperUtil) throws SQLException {
        this.objectMapperUtil = objectMapperUtil;
        this.placeService = placeService;
    }

    /**
     * Метод GET, записывающий в HTTP ответ данные об всех местах из БД
     *
     * @param req HTTP запрос
     * @param resp HTTP ответ
     * @throws ServletException если возникает броблемы с сервлетом
     * @throws IOException если возникает  проблемы с потоками I/O
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            checkAuthorisation();
            var placeList = placeService.getAllPlaces();
            var placeDtoList = toPlaceDtoList(placeList);
            objectMapperUtil.writeJson(resp, placeDtoList);
            resp.setStatus(SC_OK);
        } catch (RequiredAuthorisationException e) {
            setMessage(resp, e.getMessage(), SC_UNAUTHORIZED);
        }
    }
}
