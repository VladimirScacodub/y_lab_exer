# y_lab_exer

## Запуск приложения
Для запуска приложения требуется установленный докер, а также запустить docker-compose.yml файл через `docker-compose up --build -d`

Так же нужен установленный apache tomcat 9.0.80 версии. 

Перед запуском сервера следуем выполнить `mvn clean package` и из полученного target папки скопировать coworking-service.war в webapps из корневой папки tomcat 
Далее можно запустить сервер через \bin\startup что находится в корневой папке установленного Tomcat.

Если при сборке возникли проблемы с aspectJ, связанные с отсутствие метода aspectOf(),
то следует выполнить команду `mvn clean`

## Endpoints

Каждый endpoint требует авторизации (через Basic Auth), кроме endpoint регистрации.
Для авторизации можно использовать администраторские credentials (admin:admin)

Так же стоит отметить, что по http://localhost:8080/coworking-service/swagger-ui.html доступна документация Swagger ui. 

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
indexOfField - индекс, который означает поле, по которому будет сортировка. 
Всего 3 сортировки, 1 - по месту, 2- по пользователям, 3 - по временному слоту 
Описание:
Возвращает список всех бронирований, сортированный по полю.
Требует администраторских прав у текущего авторизированного пользователя.

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
Требует администраторских прав у текущего авторизированного пользователя.

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
Требует администраторских прав у текущего авторизированного пользователя.

### delete place endpoint

DELETE:`http://localhost:8080/coworking-service/delete-place?placeName=Workplace%201`
Параметры:
placeName - имя существующего места.
Если имя содержит пробелы то их нужно заменить на '%20', например 'Workplace 0'
в запросе будет 'Workplace%200'

Описание:
Удаление места из БД по имени.
Требует администраторских прав у текущего авторизированного пользователя.
