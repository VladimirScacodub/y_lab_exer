package org.coworking.servlets.booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.coworking.Utils.ObjectMapperUtil;
import org.coworking.Utils.ServletUtils;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.Utils.mappers.BookedPlaceMapper;
import org.coworking.annotations.BookPlaceAudit;
import org.coworking.annotations.Loggable;
import org.coworking.dtos.BookedPlaceDTO;
import org.coworking.models.BookedPlace;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.validators.BookedPlaceValidator;

import java.io.IOException;
import java.sql.SQLException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.coworking.Utils.ServletUtils.setMessage;

/**
 * Сервлет отвеяающий за бронирование новых мест
 */
@Loggable
@BookPlaceAudit
public class BookPlaceServlet extends HttpServlet {

    /**
     * Сервис выполняющий бронирование
     */
    private final BookedPlaceService bookedPlaceService;

    /**
     * Валидатор, которые проверяет данные для бронирования
     */
    private final BookedPlaceValidator bookedPlaceValidator;

    /**
     * Объект позволяеющий трансвормировать JSON в объекты и наоборот
     */
    private final ObjectMapperUtil objectMapperUtil;

    public BookPlaceServlet(BookedPlaceService bookedPlaceService, BookedPlaceValidator bookedPlaceValidator, ObjectMapperUtil objectMapperUtil) throws SQLException {

        this.bookedPlaceService = bookedPlaceService;
        this.bookedPlaceValidator = bookedPlaceValidator;
        this.objectMapperUtil = objectMapperUtil;
    }

    /**
     * Метод POST выполняющий бронирование
     * @param req HTTP запрос
     * @param resp HTTP ответ
     * @throws ServletException если возникает броблемы с сервлетом
     * @throws IOException если возникает  проблемы с потоками I/O
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ServletUtils.checkAuthorisation();

            var bookedPlaceDto = objectMapperUtil.getDto(req, BookedPlaceDTO.class);
            bookedPlaceValidator.validateExistingBookedDtoFields(bookedPlaceDto);
            bookedPlaceValidator.validateDateTimeFormat(bookedPlaceDto.getSlotDTO().getStart());
            bookedPlaceValidator.validateDateTimeFormat(bookedPlaceDto.getSlotDTO().getEnd());

            BookedPlace bookedPlace = BookedPlaceMapper.INSTANCE.bookedPlaceDtoToBookedPlace(bookedPlaceDto);

            bookedPlaceValidator.validateBookingPlace(bookedPlace);
            bookedPlaceService.bookPlace(bookedPlace, ServletUtils.getCurrentUser());
            String message = "Бронирование выполненно успешно";
            setMessage(resp, message, SC_OK);
        } catch (RequiredAuthorisationException e) {
            setMessage(resp, e.getMessage(), SC_UNAUTHORIZED);
        } catch (IOException e){
            String message = "JSON был непраильно описан";
            setMessage(resp, message, SC_BAD_REQUEST);
        } catch (BookedPlaceConflictsException e) {
            setMessage(resp, e.getMessage(), SC_BAD_REQUEST);
        }
    }

}
