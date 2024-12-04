# demo-reactive-otel

Демонстрационный проект для интеграции Spring Boot приложения с OpenTelemetry и Jaeger.
Стек: Kotlin, Spring Boot Webflux, OpenTelemetry(agent, collector), Jaeger, Docker, Docker Compose, Git, Maven.

## Содержание

- [Установка](#установка)
- [Использование](#использование)
- [Конфигурация](#конфигурация)
- [Вклад](#вклад)
- [Лицензия](#лицензия)

## Установка
```bash
# Клонируйте репозиторий
git clone https://github.com/dmittriy13/demo-reactive-otel.git

# Перейдите в директорию проекта
cd demo-reactive-otel

# Установите зависимости
mvn install
```

## Использование
1. Запустите docker-compose.yml
2. Запустите приложение app-first с помощью run configuration
3. Запустите приложение app-second с помощью run configuration
4. Отправьте запрос в app-first с помощью consume_event run configuration
5. Отправьте запрос в app-first с помощью send_event run configuration
6. Перейдите на http://localhost:16686/ и посмотрите трейсы

## Конфигурация

### app-first
```yaml
server:
  # указываем на каком порту будет запущен сервер
  port: 8080

spring:
  # указываем имя приложения
  application:
    name: app-first

org:
  # конфигурируем обменники, очереди и биндинги для RabbitMQ
  rabbit:
    queues:
      - name: events.q
        durable: true
    exchanges:
      - name: events.ex
        type: direct
        durable: true
    bindings:
      - exchange: events.ex
        queue: events.q
        routingKey: events.rk
  
  # конфигурируем web-client для обращения в app-second
  web-client:
    app-second:
      url: http://localhost:8081
```

### app-second
```yaml
server:
  # указываем на каком порту будет запущен сервер
  port: 8081

spring:
  # указываем имя приложения
  application:
    name: app-second

org:
  # конфигурируем обменники, очереди и биндинги для RabbitMQ
  rabbit:
    queues:
    exchanges:
      - name: events.ex
        type: direct
        durable: true
    bindings:
```

## Вклад
1. Инструкции по внесению вклада в проект.  
2. Форкните репозиторий
3. Создайте ветку для ваших изменений (git checkout -b feature/ваша-функция)
4. Закоммитьте ваши изменения (git commit -m 'Добавлена новая функция')
5. Запушьте ветку (git push origin feature/ваша-функция)
6. Создайте Pull Request

## Лицензия
```
MIT License

Copyright (c) [2024] [Dmitriy Tsypov]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```