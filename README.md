# D-email

[![Java CI][java-ci-img]][java-ci-url]
[![Coverage Status][coverage-status-img]][coverage-status-url]
[![Maven Central][maven-central-img]][maven-central-url]
[![Javadocs][javadocs-img]][javadocs-url]
[![CodeQL][codeql-img]][codeql-url]
[![OpenSSF Scorecard][openssf-img]][openssf-url]

Библиотека для отправки и чтения email сообщений. Основная идея создания данной библиотеки заключается в том, чтобы
создать некий "фасад" над `jakarta.mail-api` и `angus-mail`, который позволит быстро и легко настраивать подключение по
протоколам SMTP, IMAP и др. и также легко отправлять и читать сообщения с email-сервера.

# Установка MAVEN

Для подключения данной библиотеки к вашему проекту воспользуйтесь репозиторием maven central.

```xml

<dependency>
    <groupId>ru.dlabs.library</groupId>
    <artifactId>d-email</artifactId>
    <version>1.0.0</version>
</dependency>
```

# Сборка из исходников


# Использование

# Документация

## Оглавление

* [1. Настройка подключения](#section1)
* [2. DTO классы сообщений](#section2)
    * [2.1 Базовые классы сообщений](#section21)
    * [2.2 Входящие сообщения](#section22)
    * [2.3 Исходящие сообщения](#section23)
        * [2.3.1 Строковые исходящие сообщения](#section231)
        * [2.3.2 Шаблонные исходящие сообщения](#section232)
* [3. Utility классы](#section3)
    * [3.1 AttachmentUtils](#section31)
    * [3.2 TemplateUtils](#section32)
* [4. FileParametersDetector](#section4)
* [5. Клиенты подключений](#section5)
* [6. Facade классы](#section6)
    * [6.1 Класс DEmailSender](#section61)
    * [6.2 Класс DEmailReceiver](#section62)

## <h2 id="section1">1. Настройка подключения</h2>

## <h2 id="section2">2. DTO классы сообщений</h2>

### <h3 id="section21">2.1 Базовые классы сообщений</h3>

### <h3 id="section22">2.2 Входящие сообщения</h3>

### <h3 id="section23">2.3 Исходящие сообщения</h3>

#### <h4 id="section231">2.3.1 Строковые исходящие сообщения</h4>

#### <h4 id="section232">2.3.2 Шаблонные исходящие сообщения</h4>

## <h2 id="section3">3. Utility классы</h2>

### <h3 id="section31">3.1 AttachmentUtils</h3>

### <h3 id="section32">3.2 TemplateUtils</h3>

## <h2 id="section4">4. FileParametersDetector</h2>

## <h2 id="section5">5. Клиенты подключений</h2>

## <h2 id="section6">6. Facade классы</h2>

### <h3 id="section61">6.1 Класс DEmailSender</h3>

### <h3 id="section62">6.2 Класс DEmailReceiver</h3>

[java-ci-img]: https://github.com/dlabs71/d-email/actions/workflows/maven.yml/badge.svg

[java-ci-url]: https://github.com/dlabs71/d-email/actions/workflows/maven.yml

[coverage-status-img]: https://codecov.io/gh/dlabs71/d-email/branch/master/graph/badge.svg

[coverage-status-url]: https://app.codecov.io/gh/dlabs71/d-email

[maven-central-img]: https://maven-badges.herokuapp.com/maven-central/commons-io/commons-io/badge.svg?gav=true

[maven-central-url]: https://maven-badges.herokuapp.com/maven-central/commons-io/commons-io/?gav=true

[javadocs-img]: https://javadoc.io/badge/d-email/d-email/2.15.0.svg

[javadocs-url]: https://javadoc.io/doc/d-email/d-email/2.15.0

[codeql-img]: https://github.com/dlabs71/d-email/actions/workflows/codeql-analysis.yml/badge.svg

[codeql-url]: https://github.com/dlabs71/d-email/actions/workflows/codeql-analysis.yml

[openssf-img]: https://api.securityscorecards.dev/projects/github.com/dlabs71/d-email/badge

[openssf-url]: https://api.securityscorecards.dev/projects/github.com/dlabs71/d-email

[license-image]: https://img.shields.io/badge/license-MIT-blue.svg

[license-url]: LICENSE