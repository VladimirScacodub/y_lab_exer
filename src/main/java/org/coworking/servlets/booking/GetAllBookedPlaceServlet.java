package org.coworking.servlets.booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.coworking.Utils.ObjectMapperUtil;
import org.coworking.Utils.ServletUtils;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.exceptions.ForbiddenAccessException;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.annotations.BookingViewAudit;
import org.coworking.annotations.Loggable;
import org.coworking.models.BookedPlace;
import org.coworking.services.BookedPlaceService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.util.Objects.nonNull;
import static org.coworking.Utils.ServletUtils.checkAdminAuthoritiesOfCurrentUser;
import static org.coworking.Utils.ServletUtils.setMessage;

/**
 * Сервдет отвечающий за получение данных о бронировании
 */
@Loggable
@BookingViewAudit
public class GetAllBookedPlaceServlet extends HttpServlet {

    /**
     * Сервис для работы с бронированием
     */
    private final BookedPlaceService bookedPlaceService;

    /**
     * Объект позволяеющий трансвормировать JSON в объекты и наоборот
     */
    private final ObjectMapperUtil objectMapperUtil;

    public GetAllBookedPlaceServlet(BookedPlaceService bookedPlaceService, ObjectMapperUtil objectMapperUtil) throws SQLException {
        this.bookedPlaceService = bookedPlaceService;
        this.objectMapperUtil = objectMapperUtil;
    }

    /**
     * Метод GET, который возвращает в виде HTTP ответа данные об бронировании
     * @param req HTTP запрос
     * @param resp HTTP ответ
     * @throws ServletException если возникает броблемы с сервлетом
     * @throws IOException если возникает  проблемы с потоками I/O
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            checkAdminAuthoritiesOfCurrentUser();
            String param = req.getParameter("indexOfField");
            List<BookedPlace> bookedPlaces;
            bookedPlaces = nonNull(param) ? bookedPlaceService.getAllBookedPlacesSortedBy(param) :
                    bookedPlaceService.getAllBookedPlaces();
            var bookedPlacesDTO = ServletUtils.toBookedPlaceDtoList(bookedPlaces);
            objectMapperUtil.writeJson(resp, bookedPlacesDTO);
            resp.setStatus(SC_OK);
        } catch (RequiredAuthorisationException e) {
            setMessage(resp, e.getMessage(), SC_UNAUTHORIZED);
        } catch (ForbiddenAccessException e) {
            setMessage(resp, e.getMessage(), SC_FORBIDDEN);
        } catch (BookedPlaceConflictsException e) {
            setMessage(resp, e.getMessage(), SC_BAD_REQUEST);
        }
    }
}
