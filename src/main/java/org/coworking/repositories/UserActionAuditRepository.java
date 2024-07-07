package org.coworking.repositories;

import org.coworking.models.User;

import java.time.LocalDateTime;

/**
 * Репозиторий для работы с хранилищем записей действия пользователя
 */
public interface UserActionAuditRepository {

    /**
     * Созранение записи о действиях пользователя в хранилище
     * @param user пользователя соверщивщий действия
     * @param actionDescription описание действия
     * @param actionDateTime дата и время действия
     */
    void save(User user, String actionDescription, LocalDateTime actionDateTime);

}
