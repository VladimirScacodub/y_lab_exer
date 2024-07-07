package org.coworking.servlets.booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.coworking.Utils.ObjectMapperUtil;
import org.coworking.Utils.ServletUtils;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.annotations.BookingViewAudit;
import org.coworking.annotations.Loggable;
import org.coworking.services.BookedPlaceService;

import java.io.IOException;
import java.sql.SQLException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.coworking.Utils.ServletUtils.checkAuthorisation;
import static org.coworking.Utils.ServletUtils.setMessage;
import static org.coworking.Utils.ServletUtils.toBookedPlaceDtoList;

/**
 * Сервлет реализующий получение данных о бронировании по текущему пользователю
 */
@Loggable
@BookingViewAudit
public class GetAllUserBookedPlaceServlet extends HttpServlet {

    /**
     * Сервис для работы с бронированием
     */
    private final BookedPlaceService bookedPlaceService;

    /**
     * Объект позволяеющий трансвормировать JSON в объекты и наоборот
     */
    private final ObjectMapperUtil objectMapperUtil;

    public GetAllUserBookedPlaceServlet(BookedPlaceService bookedPlaceService, ObjectMapperUtil objectMapperUtil) throws SQLException {

        this.bookedPlaceService = bookedPlaceService;
        this.objectMapperUtil = objectMapperUtil;
    }

    /**
     * Метод GET, который возвращает в HTTP ответ список всех бронирований текущего пользователя
     * @param req HTTP запрос
     * @param resp HTTP ответ
     * @throws ServletException если возникает броблемы с сервлетом
     * @throws IOException если возникает  проблемы с потоками I/O
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            checkAuthorisation();
            var bookedPlaces = bookedPlaceService.getAllBookedPlacesByUser(ServletUtils.getCurrentUser());
            var bookedPlacesDTO = toBookedPlaceDtoList(bookedPlaces);
            objectMapperUtil.writeJson(resp, bookedPlacesDTO);
            resp.setStatus(SC_OK);
        } catch (RequiredAuthorisationException e) {
            setMessage(resp, e.getMessage(), SC_BAD_REQUEST);
        }
    }
}
