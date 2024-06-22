package org.coworking.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс который содержит методы для удобной работы со временем
 */
public class TimeUtils {

    /**
     * Возвращает строку отобращающее форматированное время, выделенное из LocalDateTime
     * @param dateTime - дата и время
     * @return строку содержащую часы и минуты
     */
    public static String getFormatedTime(LocalDateTime dateTime) {
        return dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
