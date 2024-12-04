# demo-reactive-otel

## Description
Demonstration project for integrating a Spring Boot application with OpenTelemetry and Jaeger.
Stack: Kotlin, Spring Boot Webflux, OpenTelemetry (agent, collector), Jaeger, Docker, Docker Compose, Git, Maven.

## Contents

- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Contribution](#contribution)
- [License](#license)

## Installation
```bash
# Clone the repository
git clone https://github.com/dmittriy13/demo-reactive-otel.git

# Navigate to the project directory
cd demo-reactive-otel

# Install dependencies
mvn install
```

## Usage
1. Run `docker-compose.yml`
2. Start the `app-first` application using the run configuration
3. Start the `app-second` application using the run configuration
4. Send a request to `app-first` using the `consume_event` run configuration
5. Send a request to `app-first` using the `send_event` run configuration
6. Go to `http://localhost:16686/` and view the traces

## Configuration

### app-first
```yaml
server:
  # specify the port on which the server will run
  port: 8080

spring:
  # specify the application name
  application:
    name: app-first

org:
  # configure exchanges, queues, and bindings for RabbitMQ
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

  # configure the web-client to call app-second
  web-client:
    app-second:
      url: http://localhost:8081
```

### app-second
```yaml
server:
  # specify the port on which the server will run
  port: 8081

spring:
  # specify the application name
  application:
    name: app-second

org:
  # configure exchanges, queues, and bindings for RabbitMQ
  rabbit:
    queues:
    exchanges:
      - name: events.ex
        type: direct
        durable: true
    bindings:
```

## Contribution
1. Instructions for contributing to the project.
2. Fork the repository
3. Create a branch for your changes (`git checkout -b feature/your-feature`)
4. Commit your changes (`git commit -m 'Added new feature'`)
5. Push the branch (`git push origin feature/your-feature`)
6. Create a Pull Request

## License
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