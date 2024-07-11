package org.coworking.servlets.booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.coworking.Utils.ObjectMapperUtil;
import org.coworking.Utils.ServletUtils;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.annotations.AvailableSlotsAudit;
import org.coworking.annotations.Loggable;
import org.coworking.repositories.BookedPlaceRepositoryImpl;
import org.coworking.repositories.PlaceRepositoryImpl;
import org.coworking.repositories.SlotRepositoryImpl;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.PlaceService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.coworking.Utils.ServletUtils.setMessage;

/**
 * Сервлет отвечающий получение данных о доступных слотах
 */
@Loggable
@AvailableSlotsAudit
public class GetAllAvailableSlotsServlet extends HttpServlet {
    /**
     * Сервис для работы с бронированием
     */
    private final BookedPlaceService bookedPlaceService;

    /**
     * Сервис для работы с местами
     */
    private final PlaceService placeService;

    /**
     * Объект позволяеющий трансвормировать JSON в объекты и наоборот
     */
    private final ObjectMapperUtil objectMapperUtil;

    public GetAllAvailableSlotsServlet(BookedPlaceService bookedPlaceService, PlaceService placeService, ObjectMapperUtil objectMapperUtil) throws SQLException {

        this.bookedPlaceService = bookedPlaceService;
        this.placeService = placeService;
        this.objectMapperUtil = objectMapperUtil;
    }

    /**
     * Метод GET выполняющий загрузку из БД данных об доступных слотах
     * @param req HTTP запрос
     * @param resp HTTP ответ
     * @throws ServletException если возникает броблемы с сервлетом
     * @throws IOException если возникает  проблемы с потоками I/O
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            ServletUtils.checkAuthorisation();
            String param = req.getParameter("date");
            if (Objects.isNull(param)){
                String message = "Нужно вести date в формате: yyyy-MM-dd";
                setMessage(resp, message, SC_BAD_REQUEST);
                return;
            }
            var date = LocalDate.parse(param, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            var allAvailableSlotsMap = bookedPlaceService.getAllAvailableDTOSlots(date.atStartOfDay());

            objectMapperUtil.writeJson(resp, allAvailableSlotsMap);
            resp.setStatus(SC_OK);
        } catch (DateTimeParseException e){
            final String message = "date была введена неправильно (нужый формат: yyyy-MM-dd)";
            setMessage(resp, message, SC_BAD_REQUEST);
        } catch (RequiredAuthorisationException e) {
            setMessage(resp, e.getMessage(), SC_UNAUTHORIZED);
        }
    }


}
