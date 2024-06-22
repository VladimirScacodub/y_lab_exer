package org.coworking;

import org.coworking.models.enums.PlaceType;
import org.coworking.services.validators.BookedPlaceValidator;
import org.coworking.services.validators.PlaceValidator;
import org.coworking.services.validators.UserValidator;
import org.coworking.services.BookedPlaceService;
import org.coworking.outputs.MenuOutput;
import org.coworking.services.PlaceService;
import org.coworking.services.UserService;

import java.time.LocalDateTime;

/**
 * Класс запускающий весь рабочий поток приложения
 */
public class Main {
    /**
     * метод апускающий весь рабочий поток приложения
     * @param args - аргументы, которые в данном приложении никак не обрабатываются
     */
    public static void main(String[] args) {
        UserService userService = new UserService();
        UserValidator userValidator = new UserValidator(userService);
        PlaceService placeService = new PlaceService();
        for (int i = 0; i < 5; i++) {
            placeService.createNewPlace("Workplace " + i, PlaceType.WORKPLACE);
            placeService.createNewPlace("Conference hall " + i, PlaceType.CONFERENCE_HALL);
        }
        PlaceValidator placeValidator = new PlaceValidator(placeService);
        BookedPlaceService bookedPlaceService = new BookedPlaceService(placeService, userService);
        bookedPlaceService.bookPlace(placeService.getAllPlaces().get(4), userService.findAll().get(0), LocalDateTime.now().minusHours(3), LocalDateTime.now().plusHours(1));
        bookedPlaceService.bookPlace(placeService.getAllPlaces().get(2), userService.findAll().get(0), LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        bookedPlaceService.bookPlace(placeService.getAllPlaces().get(0), userService.findAll().get(0), LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(2));
        BookedPlaceValidator bookedPlaceValidator = new BookedPlaceValidator(bookedPlaceService);
        MenuOutput menuOutput = new MenuOutput(userValidator, userService, placeService, placeValidator, bookedPlaceService, bookedPlaceValidator, null);

        menuOutput.performMenu();
    }

}