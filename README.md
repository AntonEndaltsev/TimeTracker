<b>Проект «Дневной тайм-трекер»:</b><br/>
Задача: разработать Backend сервиса многопользовательского тайм-трекера, доступ к которому
можно получить по REST.<br/>
Сервис умеет:<br/>
1. Создавать пользователя трекинга<br/>
2. Изменять данные пользователя<br/>
3. Стартовать отсчет времени по задаче Х<br/>
4. Финишировать отсчет времени по задаче Х<br/>
5. Показывает все трудозатраты пользователя Y за необходимый период в виде связного списка
«Задача – сумма потраченного времени с сортировкой от большего к меньшему»<br/>
6. Показывает сумму трудозатрат по всем задачам пользователя Y за необходимый период<br/>
7. Очищает данные трекинга пользователя Z<br/>
8. Удаляет пользователя Z<br/>
9. Сделан деплой с развертыванием Docker контейнера: http://80.87.107.222:8087<br/>
10. Для упрощения тестирования все взаимодействие с приложением реализовано через GET запросы<br/><br/>
Под капотом: Restful, HTTP, JSON, Swagger, Spring Boot, Data JPA, PostgreSQL, деплои через Docker, тестирование через
TestContainers, логирование<br/>
