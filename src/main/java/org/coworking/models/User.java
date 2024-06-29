package org.coworking.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.coworking.models.enums.Role;

import java.util.Objects;

/**
 * Класс отвечающий за сущность пользователя
 */
@Getter
@AllArgsConstructor
@Builder
public class User implements Comparable<User> {

    /**
     * Идентификатор пользователя
     */
    private int id;

    /**
     * Имя пользователя нужный для авторизации
     */
    private String name;

    /**
     * Пароль пользователя нужный для авторизации
     */
    private String password;

    /**
     * Роль пользователя в системе
     */
    private Role role;

    /**
     * Метод сравнивающий два объекта на равность
     *
     * @param o другой объект
     * @return true если объекты равны, иначе false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return id == user.id && Objects.equals(name, user.getName());
    }

    /**
     * Отображение объекта User в String
     *
     * @return String Отображение User
     */
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    /**
     * Отображеие текущего объекта в int
     *
     * @return hashcode текущего объекта
     */
    @Override
    public int hashCode() {
        return id;
    }

    /**
     * Метод стравнения двух объектов User
     *
     * @param o объект для сравнения.
     * @return 0 если два объекта равны, положительное число
     * если вызывающий объект больше объекта, переданного в качестве параметра
     * отрицательное, если вызывающий объект меньше объекта, переданного в качестве параметра
     */
    @Override
    public int compareTo(User o) {
        return name.compareTo(o.getName());
    }
}
