package org.coworking.repositories;

import org.coworking.models.Slot;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Репозиторий для работы с хранилищем временых слотов
 */
public interface SlotRepository {

    /**
     * Созранение нового временого слота
     *
     * @param start начальная дата слота
     * @param end   конечная дата слота
     * @return id сохраненного слота из хранилища
     */
    int save(LocalDateTime start, LocalDateTime end);

    /**
     * Удаление слота по id из хранилища
     *
     * @param id id слота
     */
    void removeSlot(int id);

    /**
     * Поулчение слота из хранилища по его id
     *
     * @param id id слота
     * @return Optional объект, в который завернут объект временного слота
     */
    Optional<Slot> findById(int id);

}
