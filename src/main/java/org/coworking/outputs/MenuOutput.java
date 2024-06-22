package org.coworking.outputs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.coworking.services.validators.BookedPlaceValidator;
import org.coworking.services.validators.PlaceValidator;
import org.coworking.services.validators.UserValidator;
import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.Utils.exceptions.UserAuthorisationException;
import org.coworking.Utils.exceptions.UserRegistrationException;
import org.coworking.models.Place;
import org.coworking.models.User;
import org.coworking.models.enums.PlaceType;
import org.coworking.models.enums.Role;
import org.coworking.services.BookedPlaceService;
import org.coworking.services.PlaceService;
import org.coworking.services.UserService;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Scanner;

import static org.coworking.Utils.TimeUtils.getFormatedTime;
import static org.coworking.models.enums.Role.ADMIN;

/**
 * Класс выводящий на экоран меню взаимодействе с приложение
 */
@AllArgsConstructor
public class MenuOutput {

    /**
     * зависимость требуемоея для валидации пользовательских данных
     */
    private UserValidator userValidator;

    /**
     * Сервис для работы с пользовательскими данными
     */
    private UserService userService;

    /**
     * Сервис для работы с местами
     */
    private PlaceService placeService;

    /**
     * зависимость требуемоея для валидации данных об местах
     */
    private PlaceValidator placeValidator;

    /**
     * Сервис для работы с бронированием
     */
    private BookedPlaceService bookedPlaceService;

    /**
     * зависимость требуемоея для валидации данных о бронировании
     */
    private BookedPlaceValidator bookedPlaceValidator;

    /**
     * Инструемент чтения данных с клавиатуры
     */


    /**
     * Текущий зрегестрированный пользователб
     */
    @Getter
    User currentUser;

    /**
     * Запуск меню входа в систему
     */

    public String inputLine() {
        return new Scanner(System.in).nextLine();
    }

    public void performMenu() {
        String answer = "";
        while (!Objects.equals(answer, "0")) {
            if (currentUser == null) {
                answer = doLoginMenu();
                switch (answer) {
                    case "0":
                        break;
                    case "1": {
                        currentUser = authoriseUser();
                        break;
                    }
                    case "2": {
                        registerUser();
                        break;
                    }
                    default: {
                        System.out.println("Неправильно введен ответ, введите заново");
                        break;
                    }
                }
            } else {
                doAuthorizedMenu(currentUser);
                currentUser = null;
            }
        }
    }

    /**
     * Запуск главного меню
     * @param authorisedUser данные об авторизированом пользователе
     */
    private void doAuthorizedMenu(User authorisedUser) {
        String answer = "";
        while (!Objects.equals(answer, "0")) {
            answer = showAuthorisedMenu(authorisedUser.getRole());
            switch (answer) {
                case "1": {
                    showListOfPlaces();
                    break;
                }
                case "2": {
                    showFreeSlots();
                    break;
                }
                case "3": {
                    doCancelBookingMenu();
                    break;
                }
                case "4": {
                    if (Objects.equals(authorisedUser.getRole(), ADMIN)) {
                        showAllBookingList();
                    }
                    break;
                }
                case "5": {
                    if (Objects.equals(authorisedUser.getRole(), ADMIN)) {
                        doPlaceCreationMenu();
                    }
                    break;
                }
                case "6": {
                    if (Objects.equals(authorisedUser.getRole(), ADMIN)) {
                        doPlaceRemovalMenu();
                    }
                    break;
                }
                case "7": {
                    if (Objects.equals(authorisedUser.getRole(), ADMIN)) {
                        doPlaceUpdatingMenu();
                    }
                    break;
                }
                case "0": {
                    break;
                }
                default: {
                    System.out.println("Неправильно введен ответ, введите заново");
                    break;
                }
            }
        }
    }

    /**
     * Запуск меню для изменения данных места
     */
    private void doPlaceUpdatingMenu() {
        System.out.println("Введите имя существующего place для изменения:");
        String oldPlaceName = inputLine();
        System.out.println("Введите новое имя!");
        String newPlaceName = inputLine();
        System.out.println("Ввыедите новый тип дял place (WORKPLACE или CONFERENCE_HALL)");
        String newPlaceType = inputLine();
        try {
            placeValidator.validatePlaceUpdating(oldPlaceName, newPlaceName);
            PlaceType placeType = PlaceType.valueOf(newPlaceType);
            placeService.updatePlace(oldPlaceName, newPlaceName, placeType);
            System.out.println("Place было успешно изменено!");
        } catch (PlaceNamingException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Такого типа не существует!");
        }
    }

    /**
     * Запуск меню для удаления места
     */
    private void doPlaceRemovalMenu() {
        System.out.println("Введите имя существующего place:");
        String placeName = inputLine();
        System.out.println("""
                    Вы действительно хотите это удалить?
                    1. Да
                    2. Нет
                """);
        String answer = inputLine();
        if (answer.equals("1")) {
            try {
                bookedPlaceService.cancelBookingWithRemovingPlace(placeName);
                System.out.println("Place было удалено!");
            } catch (PlaceNamingException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     *  запуск меню для создания нового места
     */
    private void doPlaceCreationMenu() {
        while (true) {
            try {
                System.out.println("Введите имя для нового place:");
                String placeName = inputLine();
                System.out.println("Введите тип нового place (WORKPLACE или CONFERENCE_HALL):");
                String placeType = inputLine();

                placeValidator.validateExistedPlaceName(placeName);
                placeService.createNewPlace(placeName, PlaceType.valueOf(placeType));
                System.out.println("Новый place был успешно создан!");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Такого типа не существует!");
            } catch (PlaceNamingException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     * Демонстрация всех данных об бронировании
     */
    private void showAllBookingList() {
        String answer = "";
        while (true) {
            answer = showSortingParametersMenu();
            if (Objects.equals(answer, "0")) {
                break;
            }
            try {
                bookedPlaceService.getAllBookedPlacesSortedBy(answer).forEach(System.out::println);
            } catch (BookedPlaceConflictsException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     * Запуск меню для выбора параметра сортировки
     * @return введенный параметр
     */
    private String showSortingParametersMenu() {
        System.out.println("""
                Выберете вариант сортировки:
                1. По рабочему месту
                2. По пользователю
                3. По слотам
                                
                0.Назад
                Ваш ответ:
                """);
        return inputLine();
    }

    /**
     * Запуск меню для отмены бронирования
     */
    private void doCancelBookingMenu() {
        String answer = "";
        while (true) {
            answer = showCancelBookingMenu();
            if (Objects.equals(answer, "0")) {
                break;
            }
            try {
                int id = Integer.parseInt(answer);
                bookedPlaceService.cancelBooking(id);
                System.out.println("Бронирование было отмененно!");
            } catch (NumberFormatException e) {
                System.out.println("ID букинга был неправильно введен!");
            } catch (BookedPlaceConflictsException e) {
                System.out.println(e.getMessage());
                ;
            }
        }
    }

    /**
     * демонстрация списка бронирований для их отмены
     * @return id конкретного бронирования, которое будет удалено
     */
    private String showCancelBookingMenu() {
        System.out.println("Выберите забронированные вами слоты для отмены:");
        bookedPlaceService.getAllBookedPlacesByUser(currentUser)
                .forEach(bookedPlace -> System.out.println(bookedPlace.getId() + ". " + bookedPlace.getPlace() + " " + bookedPlace.getSlot()));
        System.out.println("0. Назад");
        System.out.println("Ваш ответ:");
        return inputLine();
    }

    /**
     * Демострация свободных слотов за определенную дату
     */
    private void showFreeSlots() {
        System.out.println("Введите нужную дату в формате: dd/MM/yyyy");
        String answer = inputLine();

        try {
            LocalDateTime dateTime = LocalDateTime.parse(answer + " 00:00", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            System.out.println("Доступные слоты:");
            showFreePlaces(dateTime);
            doBookingMenu();

        } catch (DateTimeException e) {
            System.out.println("Данные были введены неправильно");
        }
    }

    /**
     * Запуск меню для нового бронирования места
     */
    private void doBookingMenu() {
        String answer = "";
        while (!Objects.equals(answer, "0")) {
            answer = showBookingMenu();
            switch (answer) {
                case "1": {
                    enterDataForBookingMenu();
                    break;
                }
                case "0": {
                    break;
                }
                default: {
                    System.out.println("Данные были введены неправилно! Веедите их заново:");
                    break;
                }
            }
        }
    }

    /**
     * Запуск пеню для вода данных нового бронирования
     */
    private void enterDataForBookingMenu() {
        System.out.println("Введите имя нужного рабочего места или конференц зала:");
        String placeName = inputLine();
        System.out.println("Введите дату и время начала бронирования (в формате dd/MM/yyyy HH:mm):");
        String startDate = inputLine();
        System.out.println("Введите дату и время окончания бронирования (в формате dd/MM/yyyy HH:mm):");
        String endDate = inputLine();

        try {
            Place place = placeService.findByName(placeName)
                    .orElseThrow(() -> new PlaceNamingException("Рабочего места с таким именем не существует"));
            LocalDateTime from = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            LocalDateTime to = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            bookedPlaceValidator.validateBookingPlace(place, from, to);
            bookedPlaceService.bookPlace(place, currentUser, from, to);
            System.out.println("Вы успешно забронировали рабочее место!");
        } catch (DateTimeParseException e) {
            System.out.println("Даты и время были введены неправильно!");
        } catch (PlaceNamingException | BookedPlaceConflictsException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Демонстрация опций по бронированию места
     * @return введеный id опции
     */
    private String showBookingMenu() {
        System.out.println("""
                Выберите опцию:
                1. Забронировать слот
                0. Вернуться назад
                Ваш ответ:
                """);
        return inputLine();
    }

    /**
     * Демострация списка всех доступных мест
     */
    private void showListOfPlaces() {
        System.out.println("Список всех доступных рабочих мест и конференц-залов:");
        placeService.getAllPlaces().forEach(System.out::println);
        System.out.println();
    }

    /**
     * Демонтрация главного меню, зависящая от роли пользователя (опции 4,5,6,7 доступны только админ-ам)
     * @param roleOfUser - роль пользователя
     * @return
     */
    private String showAuthorisedMenu(Role roleOfUser) {
        System.out.println("""
                Пожалуйста выберите опции:
                1. просмотр списка всех доступных рабочих мест и конференц-залов;
                2. просмотр и бронирование доступных слотов на конкретную дату;
                3. отмена бронирования;
                """);
        if (Objects.equals(roleOfUser, ADMIN)) {
            System.out.println("""
                    4. просмотр всех бронирований и их фильтрация по дате, пользователю или ресурсу;
                    5. добавление новых рабочих мест и конференц-залов;
                    6. Удаление рабочих мест и конференц-залов;
                    7. Изменение рабочих мест и конференц-залов;
                    """);
        }
        System.out.println("0. Logout");
        System.out.println("Ваш ответ:");
        return inputLine();
    }

    /**
     * Демонстрация меню входа в систему
     * @return выбранный id опции
     */
    private String doLoginMenu() {
        System.out.println("Приветствую, пожалуйста авторизируйтесь или зарегистрируетесь:");
        System.out.println("1. Авторизация");
        System.out.println("2. Регистрация");
        System.out.println("0. Выход из программы");
        System.out.println("Ваш ответ:");

        return inputLine();
    }

    /**
     * Выводит список рабочих мест и их свободные слоты на определенныую дату
     * @param date - день по которому вычисляются все свободные слоты
     */
    public void showFreePlaces(LocalDateTime date) {
        placeService.getAllPlaces().forEach(place -> bookedPlaceService.getAvailableSlots(place, date)
                .forEach(slot -> System.out.println(place + " - " + getFormatedTime(slot.getStart()) + " " + getFormatedTime(slot.getEnd()))));

    }

    /**
     * Запуск меню регистрации
     */
    private void registerUser() {

        System.out.println("Запущен процесс Регистрации!");
        System.out.println("Пожалуйста введите Username");
        String username = inputLine();
        System.out.println("Пожалуйста введите Password");
        String password = inputLine();

        try {
            userValidator.validateUserRegistration(username, password);
            var user = userService.registerNewUser(username, password, Role.USER);
            System.out.println("Регистрация была выполнена успешно!\n" +
                    "Теперь вы можете аутентифицироваться используя эти данные!");
        } catch (UserRegistrationException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Запуск меню авторизации
     * @return авторизированый объект User
     */
    private User authoriseUser() {

        System.out.println("Запущен процесс авторизации!");
        System.out.println("Пожалуйста введите Username");
        String username = inputLine();
        System.out.println("Пожалуйста введите Password");
        String password = inputLine();

        try {
            var user = userValidator.getValidatedAuthorisedUser(username, password);
            System.out.println("Авторизация была выполнена успешно!");
            return user;
        } catch (UserAuthorisationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
