package org.coworking.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Аннотация, которой помечаются сервлеты работающие с регистрацией пользователей
 */
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterAudit {
}
