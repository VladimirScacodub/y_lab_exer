# y_lab_exer

## Запуск приложения
Для запуска приложения требуется установленный докер, а также запустить docker-compose.yml файл через `docker-compose up --build -d`

Так же нужен установленный apache tomcat 10 версии. 

Перед запуском сервера следуем выполнить `mvn clean package` и из полученного target папки скопировать папку coworking-service в webapps из корневой папки tomcat 
Далее можно запустить сервер через \bin\startup что находится в корневой папке установленного Tomcat.

Если при сборке возникли проблемы с aspectJ, связанные с отсувствие метода aspectOf(),
то следует выполнить команду `mvn clean`

## Endpoints

Каждый endpoint требует авторизации, кроме endpoint регистрации и сам endpoint авторизации

### authorisation endpoint

POST:`http://localhost:8080/coworking-service/authorise-user` 

Имеет BODY:
```json
{
    "name" : "admin",
    "password" : "admin"
}
```

Описание:
Авторизирует пользователя с ролью ADMIN

### registration endpoint

POST:`http://localhost:8080/coworking-service/register-user`

BODY:
```json
{
    "name" : "user",
    "password" : "user",
    "role" : "USER"
}
```

Описание:
Регистрирует пользователя

### book place endpoint

POST:`http://localhost:8080/coworking-service/book-place`

BODY:
```json
{
        "placeDTO": {
            "id" : 1,
            "placeName": "Workplace 0",
            "placeType": "WORKPLACE"
        },
        "slotDTO": {
            "start": "2024-08-25 11:30",
            "end": "2024-08-25 14:30"
        }
}
```
Описание:
Бронирует место для пользователя

### get all available slots endpoint

GET:`http://localhost:8080/coworking-service/get-available-slots?date=2024-08-22`

Параметры:
date - дата в формате 'yyyy-MM-dd'

Описание:
Вычисляет свободные слоты для всех мест по указанной в param дате

### get all booked place endpoint

GET:`http://localhost:8080/coworking-service/get-all-booking?indexOfField=3`

Параметры:
indexOfField - индекс который означает поле по которому буде тсортировка. 
Всего 3 сортировки, 1 - по месту, 2- по пользователям, 3 - по временому слоту 
Описание:
Возвращает список всех бронирований, сортированый по полю.
Требует администраторсих прав у текущего авторизированного пользователя.

### get all current user booked place endpoint

GET:`http://localhost:8080/coworking-service/get-current-user-booked-place`

Описание:
Возвращает список бронирований принадлежащих пользователю

### delete booked place endpoint

DELETE:`http://localhost:8080/coworking-service/delete-booked-place?id=1`

Параметры:
id - id записи о бронировании

Описание:
Удаляет запись о бронировании по id

### get all places endpoint

GET:`http://localhost:8080/coworking-service/get-all-places`
Описание:
Получение информации о всех мест, существующих в БД

### save new place endpoint

POST:`http://localhost:8080/coworking-service/save-new-place`

BODY:
```json
{
    "placeName" : "WORK HALL",
    "placeType" : "WORKPLACE"
}
```

Описание:
Создание нового места в системе. 
Требует администраторсих прав у текущего авторизированного пользователя.

### update place endpoint

PUT:`http://localhost:8080/coworking-service/update-place?placeName=Workplace%200`

Параметры:
placeName - имя существующего места. 
Если имя содержит пробелы то их нужно заменить на '%20', например 'Workplace 0' 
в запросе будет 'Workplace%200'

BODY:
```json
{
    "placeName" : "WORK HALLS",
    "placeType" : "WORKPLACE"
}
```

Описание:
Обновляет данные у существующего места по его имени.
Требует администраторсих прав у текущего авторизированного пользователя.

### delete place endpoint

DELETE:`http://localhost:8080/coworking-service/delete-place?placeName=Workplace%201`
Параметры:
placeName - имя существующего места.
Если имя содержит пробелы то их нужно заменить на '%20', например 'Workplace 0'
в запросе будет 'Workplace%200'

Описание:
Удаление места из БД по имени.
Требует администраторсих прав у текущего авторизированного пользователя.
