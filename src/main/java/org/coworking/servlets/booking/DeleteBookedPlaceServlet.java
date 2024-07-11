package org.coworking.servlets.booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.coworking.Utils.ServletUtils;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.annotations.DeleteBookingAudit;
import org.coworking.annotations.Loggable;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.validators.BookedPlaceValidator;

import java.io.IOException;
import java.sql.SQLException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.util.Objects.isNull;
import static org.coworking.Utils.ServletUtils.checkAuthorisation;
import static org.coworking.Utils.ServletUtils.setMessage;


/**
 * Сервлет отвечающий за удаления бронирования
 */
@Loggable
@DeleteBookingAudit
public class DeleteBookedPlaceServlet extends HttpServlet {

    /**
     * Сервис выполняющий удаления бронирования
     */
    private final BookedPlaceService bookedPlaceService;

    /**
     * Валидатор, которые проверяет данные для удаления бронирования
     */
    private final BookedPlaceValidator bookedPlaceValidator;

    public DeleteBookedPlaceServlet(BookedPlaceService bookedPlaceService, BookedPlaceValidator bookedPlaceValidator) throws SQLException {

        this.bookedPlaceService = bookedPlaceService;
        this.bookedPlaceValidator = bookedPlaceValidator;
    }

    /**
     * Метод DELETE удаляющий бронирование
     * @param req HTTP запрос
     * @param resp HTTP ответ
     * @throws ServletException если возникает броблемы с сервлетом
     * @throws IOException если возникает  проблемы с потоками I/O
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            checkAuthorisation();
            String param = req.getParameter("id");
            if (isNull(param)){
                final String message = "нужно ввести id";
                setMessage(resp, message, SC_BAD_REQUEST);
                return;
            }
            int id = Integer.parseInt(param);

            bookedPlaceValidator.validateCancelingBookedPlaceByNonAdmin(id, ServletUtils.getCurrentUser());
            bookedPlaceService.cancelBooking(id);
            final String message = "Удаление было выполненно успешно";
            setMessage(resp, message, SC_OK);
        } catch (RequiredAuthorisationException e) {
            setMessage(resp, e.getMessage(), SC_UNAUTHORIZED);
        } catch (NumberFormatException e) {
            final String message = "id был вввден неправильно";
            setMessage(resp, message, SC_BAD_REQUEST);
        } catch (BookedPlaceConflictsException e) {
            setMessage(resp, e.getMessage(), SC_BAD_REQUEST);
        }
    }
}
