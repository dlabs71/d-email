# D-email

[![Maven Central][maven-central-img]][maven-central-url]
[![Javadocs][javadocs-img]][javadocs-url]

Библиотека для отправки и чтения email сообщений. Основная идея создания данной библиотеки заключается в том, чтобы
создать некий "фасад" над `jakarta.mail-api` и `angus-mail`, который позволит быстро и легко настраивать подключение по
протоколам SMTP, IMAP и др. и также легко отправлять и читать сообщения с email-сервера.

# Установка MAVEN

Для подключения данной библиотеки к вашему проекту воспользуйтесь репозиторием maven central.

```xml

<dependency>
    <groupId>ru.dlabs71.library</groupId>
    <artifactId>d-email</artifactId>
    <version>0.0.1</version>
</dependency>
```

# Использование

## Общая информация

В этой части документа описано базовое использование данной библиотеки. Для более детального описания смотрите раздел "
Документация" или JavaDoc.

Библиотека предоставляет два основных класса-фасада, через которые вы можете легко и без лишнего кода отправлять и
принимать сообщения.

- `DEmailReceiver` - предназначен для получения сообщений по протоколу IMAP.
- `DEmailSender` - предназначен для отправки сообщений по протоколу SMTP.

В каждом из этих классов содержатся исчерпывающие количество методов для отправки и чтения сообщений. Так что для
большинства разработчиков, которые будут использовать эту библиотеку, этих классов будет достаточно. Но если потребуется
что-то большее, то вы всегда можете обратиться к разделу "Документация" и найти там необходимую информацию.

## Отправка сообщения

Рассмотрим отправку сообщений. Прежде всего, необходимо изначально понимать, что содержимое отправляемых сообщения
могут быть нескольких типов:

- обычное текстовое сообщение (имеет content-type = text/plain)
- текстовое html сообщение (имеет content-type = text/html)

В библиотеке предусмотрен для отправки таких сообщений класс - `DefaultOutgoingMessage`. Также для удобства
разработчиков,
использующих данную библиотеку, добавлен класс - `TemplatedOutgoingMessage`, который наследует `DefaultOutgoingMessage`.
Данный класс предназначен для создания сообщений по шаблону используя проект Apache Velocity. При этом шаблон может быть
как текстовый, так и путь до файла (например файла html). Для удобства работы с шаблонами библиотека поставляет
utility-класс - `TemplateUtils`.

В сообщении может быть несколько "контентов" (содержимых) с разными content-type. Содержание сообщения описывается
классом `ContentMessage`. В нём есть поле по которому можно идентифицировать какого типа это содержимое сообщения.
Данное поле называется `type` и имеет тип перечисления `ContentMessageType`.

Отдельно стоит упомянуть, что отправляемое сообщение может иметь вложения. Вложение описывается
классом `EmailAttachment`. Для создания вложений пользуйтесь utility-классом `AttachmentUtils`.

Используя фасад `DEmailSender` можно очень легко отправлять сообщения. Данный класс содержит множество перегруженных
методов отправки сообщения, чтобы упростить создание и отправку сообщения. Прежде чем конструировать отдельно сообщение,
рекомендуется исследовать возможные методы этого класса.
Для создания экземпляра класса `DEmailSender` потребуется передать настройки подключения, которые описываются
классом `SmtpProperties`. Большая часть настроек уже имеет значение по умолчанию. Для детального изучения имеющихся
настроек смотрите JavaDoc.

Простейшая отправка сообщений.

```java
import ru.dlabs71.library.email.DEmailSender;
import ru.dlabs71.library.email.property.SmtpProperties;
import ru.dlabs71.library.email.type.SendingStatus;

class Test {

    public void test() {
        SmtpProperties smtpProperties = SmtpProperties.builder()
            .host("123.23.32.12")
            .port(587)
            .name("Name of a sender")
            .email("sender@example.com")
            .password("password")
            .build();

        DEmailSender sender = DEmailSender.of(smtpProperties);
        SendingStatus result = sender.sendText("recipient@example.com", "Subject", "Text body");
    }
}
```

Отправка сообщения нескольким получателям (рассылка).

```java
import ru.dlabs71.library.email.DEmailSender;
import ru.dlabs71.library.email.property.SmtpProperties;
import ru.dlabs71.library.email.type.SendingStatus;

class Test {

    public void test() {
        SmtpProperties smtpProperties = SmtpProperties.builder()
            .host("123.23.32.12")
            .port(587)
            .name("Name of a sender")
            .email("sender@example.com")
            .password("password")
            .build();

        DEmailSender sender = DEmailSender.of(smtpProperties);
        SendingStatus result = sender.sendText(
            Arrays.asList("recipient1@example.com", "recipient2@example.com"),
            "Subject",
            "Text body"
        );
    }
}
```

Отправка сообщения с вложениями.

```java
import ru.dlabs71.library.email.DEmailSender;
import ru.dlabs71.library.email.property.SmtpProperties;
import ru.dlabs71.library.email.type.SendingStatus;
import ru.dlabs71.library.email.util.AttachmentUtils;

class Test {

    public void test() {
        SmtpProperties smtpProperties = SmtpProperties.builder()
            .host("123.23.32.12")
            .port(587)
            .name("Name of a sender")
            .email("sender@example.com")
            .password("password")
            .build();

        DEmailSender sender = DEmailSender.of(smtpProperties);
        SendingStatus result = sender.sendText(
            Arrays.asList("recipient1@example.com", "recipient2@example.com"),
            "Subject",
            "Text body",
            AttachmentUtils.create("classpath:attachments/file.txt"),
            AttachmentUtils.create("file:///home/attachments/file.txt")
        );
    }
}
```

Отправка шаблонного сообщения.

```java
import java.util.HashMap;
import java.util.Map;
import ru.dlabs71.library.email.DEmailSender;
import ru.dlabs71.library.email.property.SmtpProperties;
import ru.dlabs71.library.email.type.SendingStatus;
import ru.dlabs71.library.email.util.AttachmentUtils;

class Test {

    public void test() {
        SmtpProperties smtpProperties = SmtpProperties.builder()
            .host("123.23.32.12")
            .port(587)
            .name("Name of a sender")
            .email("sender@example.com")
            .password("password")
            .build();

        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("header", "Header of the message");
        templateParams.put("content", "Content of the message");


        DEmailSender sender = DEmailSender.of(smtpProperties);
        SendingStatus result = sender.sendTextTemplated(
            "recipient@example.com",
            "Subject",
            "classpath:sender-test/template.txt",
            templateParams
        );
    }
}
```

Также в классе `DEmailSender` есть методы для отправки HTML сообщения. Для детальной информации смотрите JavaDoc.

## Получение сообщения

Теперь рассмотрим чтение сообщений с email сервера. Как и с отправкой сообщений, тип контента сообщения может быть:

- обычное текстовое сообщение (имеет content-type = text/plain)
- текстовое html сообщение (имеет content-type = text/html)

Все сообщения, читаемые с email сервера имеют тип `DefaultIncomingMessage`. Данный класс реализует
интерфейс `IncomingMessage` и обладает рядом дополнительных методов, помогающих получить содержимое сообщения
конкретного типа. Например: `getHtmlContents()` или `getTextContents()`. Также есть методы получения содержимого
сообщения в виде строки.

Так как интерфейс `IncomingMessage` наследует интерфейс `Message`, аналогично интерфейсу `OutgoingMessage`,
то для описания содержимого сообщения используется класс `ContentMessage`, а для вложений используется
класс `EmailAttachment`. То есть всё аналогично отправляемым сообщениям.

Используя фасад `DEmailReceiver` можно легко читать сообщения с email сервера. На данный момент реализовано
взаимодействие с email сервером только по протоколу IMAP. Поддержка протокола POP3 будет добавлена позже.

Стоит понимать три основных момента при использовании этого фасада.

1. **Проверка** email сообщений - это получение списка сообщений без тела. Результатом будет список объектов
   класса `MessageView`. После получения сообщений они не являются прочитанными.
2. **Чтение** email сообщений - это получение списка сообщений с телом. Результатом будет список объектов
   класса `DefaultIncomingMessage`. После получения сообщений они меняют свой статус на "прочитано".
3. Любое получение списка сообщений работает с использованием Pageable запроса. Т.е. вы указываете начальную позицию и
   размер выборки через специальный класс `PageRequest`. Результат будет в виде класса `PageResponse`, внутри которого
   находиться список с данными, общее количество данных и метаданные. В роли метаданных может выступать имя папки из
   которой читали сообщение.

Для создания экземпляра класса `DEmailReceiver` потребуется передать настройки подключения, которые описываются
классом `ImapProperties`. Большая часть настроек уже имеет значение по умолчанию. Для детального изучения имеющихся
настроек смотрите JavaDoc.

Простейшая проверка email сообщений из папки "INBOX".

```java
import ru.dlabs71.library.email.DEmailReceiver;
import ru.dlabs71.library.email.dto.message.incoming.MessageView;
import ru.dlabs71.library.email.dto.pageable.PageRequest;
import ru.dlabs71.library.email.dto.pageable.PageResponse;
import ru.dlabs71.library.email.property.ImapProperties;

class Test {

    public void test() {
        ImapProperties imapProperties = ImapProperties.builder()
            .host("123.23.32.12")
            .port(143)
            .email("receiver@example.com")
            .password("password")
            .build();

        DEmailReceiver receiver = DEmailReceiver.of(imapProperties).folder("INBOX");

        // Получение первых 50 сообщений. Используется PageRequest по умолчанию
        PageResponse<MessageView> emails1 = receiver.checkEmail();

        // Получение от 50-ого до 150-ого сообщения. Используется собственный PageRequest
        PageResponse<MessageView> emails2 = receiver.checkEmail(PageRequest.of(50, 100));
    }
}
```

Чтение email сообщений из папки "INBOX".

```java
import ru.dlabs71.library.email.DEmailReceiver;
import ru.dlabs71.library.email.dto.message.incoming.MessageView;
import ru.dlabs71.library.email.dto.pageable.PageRequest;
import ru.dlabs71.library.email.dto.pageable.PageResponse;
import ru.dlabs71.library.email.property.ImapProperties;

class Test {

    public void test() {
        ImapProperties imapProperties = ImapProperties.builder()
            .host("123.23.32.12")
            .port(143)
            .email("receiver@example.com")
            .password("password")
            .build();

        DEmailReceiver receiver = DEmailReceiver.of(imapProperties).folder("INBOX");

        // Чтение первых 50 сообщений. Используется PageRequest по умолчанию
        PageResponse<MessageView> emails1 = receiver.readEmail();

        // Чтение от 50-ого до 150-ого сообщения. Используется собственный PageRequest
        PageResponse<MessageView> emails2 = receiver.readEmail(PageRequest.of(50, 100));
    }
}
```

Удаление сообщений из папки "INBOX".

```java
import java.util.Map;
import ru.dlabs71.library.email.DEmailReceiver;
import ru.dlabs71.library.email.dto.message.incoming.MessageView;
import ru.dlabs71.library.email.dto.pageable.PageRequest;
import ru.dlabs71.library.email.dto.pageable.PageResponse;
import ru.dlabs71.library.email.property.ImapProperties;

class Test {

    public void test() {
        ImapProperties imapProperties = ImapProperties.builder()
            .host("123.23.32.12")
            .port(143)
            .email("receiver@example.com")
            .password("password")
            .build();

        DEmailReceiver receiver = DEmailReceiver.of(imapProperties).folder("INBOX");

        // удалить все сообщения из папки INBOX
        Map<Integer, Boolean> result = receiver.clearCurrentFolder();

        // удалить сообщение с id = 1 из папки INBOX
        boolean result = receiver.deleteMessageById(1);
    }
}
```

## Дополнительные возможности

Помимо двух основных классов-фасадов для отправки и приёма сообщений, библиотека также предоставляет ряд вспомогательных
utility-классов. Они описаны ниже:

- AttachmentUtils - класс помогающий создать объект класса `EmailAttachment`.
- FileSystemUtils - класс помогающий получить такие характеристики файла, находящегося в файловой системе, как: MIME
  type, Encoding. При его использовании обратите внимание на `FileParametersDetector`.
- TemplateUtils - класс помогающий создать `Apache Velocity Template`. Вся работа с проектом `Apache Velocity`
  инкапсулирована в этом классе.

Обратите внимание на интерфейс `FileParametersDetector`. Он необходим для получения параметров файла таких как MIME type
и Encoding. Так как получить данные параметры средствами Java не всегда получается корректно, то создан этот интерфейс
чтобы можно было указать собственную реализацию для получения вышеописанных параметров файла. Библиотека предоставляет
реализацию по умолчанию - `DefaultFileParametersDetector`, но использование её не даёт гарантий получения корректного
MIME type или кодировки файла. Однако, для увеличения качества отправки вложений, необходимо корректное получение данных
параметров. Посмотрите на использование проекта [Apache Tika](https://tika.apache.org/) в качестве основы для построения
собственной реализации `FileParametersDetector`.

Также стоит упомянуть о том что все запросы к email серверу используют механизм `retryable`. Он позволяет сделать
повторный запрос при возникновении ошибки. Через настройки подключений (`ImapProperties`, `SmtpProperties`) можно
указать, как максимальное количество попыток запроса, прежде чем выброситься исключение. Так и задержку в миллисекундах
между запросами. С задержкой между запросами будьте внимательны, на данный момент эта функциональность реализована
через `Thread.sleep()`.

# Документация

## Оглавление

* [1. Настройка подключения](#section1)
* [2. DTO классы сообщений](#section2)
    * [2.1 Базовые классы сообщений](#section21)
    * [2.2 Входящие сообщения](#section22)
    * [2.3 Исходящие сообщения](#section23)
    * [2.4 Pageable DTO](#section24)
* [3. Utility классы](#section3)
    * [3.1 AttachmentUtils](#section31)
    * [3.2 TemplateUtils](#section32)
* [4. FileParametersDetector](#section4)
* [5. Клиенты подключений](#section5)
    * [5.1 Получатели сообщений](#section51)
    * [5.2 Отправители сообщений](#section52)
* [6. Facade классы](#section6)
    * [6.1 Класс DEmailSender](#section61)
    * [6.2 Класс DEmailReceiver](#section62)
* [7. Сборка из исходников](#section7)
* [8. Checkstyle](#section8)

## <h2 id="section1">1. Настройка подключения</h2>

Для настройки получения используются специальные классы находящиеся в пакете `ru.dlabs71.library.email.property`. Что
настройки подключения по SMTP протоколу, что по IMAP протоколу, оба обладают общим набором свойств. Данные свойства
описаны в классе [CommonProperties](./src/main/java/ru/dlabs/library/email/property/CommonProperties.java).

Все свойства соответствуют значения свойств подключения `jakarta-mail` которые можно
найти [здесь (SMTP)](https://jakarta.ee/specifications/mail/1.6/apidocs/?com/sun/mail/smtp/package-summary.html)
и [здесь (IMAP)](https://jakarta.ee/specifications/mail/1.6/apidocs/?com/sun/mail/imap/package-summary.html).

Также стоит упомянуть о том что все запросы к email серверу используют механизм `retryable`. Он позволяет сделать
повторный запрос при возникновении ошибки. Через параметры: `maxAttemptsOfRequest` и `attemptDelayOfRequest`, можно
указать, максимальное количество попыток запроса, прежде чем выброситься исключение и задержку в миллисекундах
между запросами. С задержкой между запросами будьте внимательны, на данный момент эта функциональность реализована
через `Thread.sleep()`.

Для настроек подключения по протоколу SMTP существует
класс [SmtpProperties](./src/main/java/ru/dlabs/library/email/property/SmtpProperties.java). Соответственно, для
настроек подключения по протоколу IMAP существует
класс [ImapProperties](./src/main/java/ru/dlabs/library/email/property/ImapProperties.java).

Для более детальной информации по полям класса смотрите JavaDoc.

## <h2 id="section2">2. DTO классы сообщений</h2>

Все классы предназначенные для хранения информации о сообщении находятся в
пакете `ru.dlabs71.library.email.dto.message`.
В пакете существует 3 под-пакета:

- common - классы и интерфейсы являющиеся общими для входящих и исходящих сообщений.
- incoming - классы и интерфейсы предназначенные для входящих сообщений.
- outgoing - классы и интерфейсы предназначенные для исходящих сообщений.

### <h3 id="section21">2.1 Базовые классы сообщений</h3>

Любое сообщение, будь то входящее или исходящее, реализует
интерфейс [Message](./src/main/java/ru/dlabs/library/email/dto/message/common/Message.java). Данный интерфейс определяет
все основные методы сообщения. Как можно видеть, используются также следующие классы:

- `ContentMessage` - предназначен для хранения информации о содержимом сообщения.
- `EmailAttachment` - предназначен для хранения информации о вложении.
- `EmailParticipant` - предназначен для хранения информации об отправителе или получателе.

Существует также класс `BaseMessage` который реализует данный интерфейс. Данный класс представляет собой основную
структуру любого email сообщения. Также он содержит дополнительные методы для работы с содержимым сообщения:

| Метод                                    | Описание                                                                         |
|------------------------------------------|----------------------------------------------------------------------------------|
| getAllContentsAsString()                 | Получение всех содержимых в виде строки разделённые знаком `\n`                  |
| getAllContentsAsString(String delimiter) | Получение всех содержимых в виде строки разделённые знаком указанным в параметре |

Все классы имеют внутренний класс реализующий шаблон Builder. Прежде чем создавать класс при помощи конструктора,
попробуйте создать его через Builder.

### <h3 id="section22">2.2 Входящие сообщения</h3>

Входящие сообщения должны реализовывать
интерфейс [IncomingMessage](./src/main/java/ru/dlabs/library/email/dto/message/incoming/IncomingMessage.java). Данный
интерфейс унаследован от общего интерфейса `Message` и определяет несколько вспомогательных методов для работы с
содержимым сообщения:

| Метод                                     | Описание                                                                                                                                     |
|-------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| getHtmlContents()                         | Получение частей содержимого сообщения для которых указан content-type = text/html                                                           |
| getHtmlContentsAsString()                 | Получение частей содержимого сообщения для которых указан content-type = text/html в виде строки разделённой символом `\n`                   |
| getHtmlContentsAsString(String delimiter) | Получение частей содержимого сообщения для которых указан content-type = text/html в виде строки разделённой символом указанным в параметре  |
| getTextContents()                         | Получение частей содержимого сообщения для которых указан content-type = text/plain                                                          |
| getTextContentsAsString()                 | Получение частей содержимого сообщения для которых указан content-type = text/plain в виде строки разделённой символом `\n`                  |
| getTextContentsAsString(String delimiter) | Получение частей содержимого сообщения для которых указан content-type = text/plain в виде строки разделённой символом указанным в параметре |

Реализацией этого интерфейса по умолчанию является `DefaultIncomingMessage`.

В пакете также имеется класс `MessageView`. Он предназначен для хранения только общей информации о сообщении. Этот класс
применяется при проверке почтового ящика. Получаем только основную информацию о сообщении, при этом сообщение не
является прочитанным после этого.

Все классы имеют внутренний класс реализующий шаблон Builder. Прежде чем создавать класс при помощи конструктора,
попробуйте создать его через Builder.

### <h3 id="section23">2.3 Исходящие сообщения</h3>

Исходящие сообщения должны реализовывать
интерфейс [OutgoingMessage](./src/main/java/ru/dlabs/library/email/dto/message/outgoing/OutgoingMessage.java). Данный
интерфейс унаследован от общего интерфейса `Message`. Реализацией этого интерфейса по умолчанию
является `DefaultOutgoingMessage`, определённый в этом же пакете.

Наряду с реализацией по умолчанию, имеется ещё одна реализация. Это класс `TemplatedOutgoingMessage`, который добавляет
возможность создать содержимое сообщения указав путь до шаблона сообщения и параметры к этому шаблону. Механизм
генерации исходного содержимого по шаблону основан на проекте `Apache Velocity`. Весь механизм инкапсулирован в
классе `TemplateUtils`.

Все классы имеют внутренний класс реализующий шаблон Builder. Прежде чем создавать класс при помощи конструктора,
попробуйте создать его через Builder.

### <h3 id="section24">2.4 Pageable DTO</h4>

Все необходимые DTO при работе механизмов pageable находятся в пакете `ru.dlabs71.library.email.dto.pageable`.

Для реализации pageable запросов применяется
класс [PageRequest](./src/main/java/ru/dlabs/library/email/dto/pageable/PageRequest.java). Данный класс содержит два
поля.

| Поле       | Описание                  |
|------------|---------------------------|
| int start  | Начальная позиция курсора |
| int length | Длинна выборки            |

Для создания экземпляра класса используйте статистический метод `of(int start, int length)`.

В данном классе также имеется ряд вспомогательных методов.

| Метод            | Описание                                                                              |
|------------------|---------------------------------------------------------------------------------------|
| incrementStart() | Увеличивает указывает следующую позицию курсора. Возвращает текущий экземпляр класса. |
| getEnd()         | Получает конечную позицию курсора.                                                    |

## <h2 id="section3">3. Utility классы</h2>

Библиотека предоставляет ряд вспомогательных классов. Эти классы содержат только статистические методы и константы.
Предназначены в основном для удобного и быстрого создания частей сообщения. Все эти классы находятся в
пакете `ru.dlabs71.library.email.util`.

### <h3 id="section31">3.1 AttachmentUtils</h3>

[AttachmentUtils](./src/main/java/ru/dlabs/library/email/util/AttachmentUtils.java) - предоставляет набор методом для
создания экземпляра класса `EmailAttachment`.

Для создания `EmailAttachment` используйте следующие методы.

| Метод                                                      | Описание                                                                                                                                                                           |
|------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| create(String pathToFile)                                  | Создаёт экземпляр класса на основе пути к файлу. Критерии к пути до файла описаны ниже. Для получения MIME type и кодировки файла использует класс `DefaultFileParametersDetector` |
| create(String pathToFile, FileParametersDetector detector) | Создаёт экземпляр класса на основе пути к файлу. Для получения MIME type и кодировки файла использует реализацию интерфейса `FileParametersDetector` указанную в параметре.        |

Путь до файла может быть:

- начинаться с 'file://'. В таком случае файл будет искаться в файловой системе.
- начинаться с 'classpath:'. В таком случае файл будет искаться в classpath-е.
- начинаться с разделителя ('/' или '\' в зависимости от ОС). В таком случае файл будет искаться в файловой системе.

Остальные методы являются служебными и используются внутри вышеописанных методов. Информацию по ним можно найти в
JavaDoc.

### <h3 id="section32">3.2 TemplateUtils</h3>

[TemplateUtils](./src/main/java/ru/dlabs/library/email/util/TemplateUtils.java) - инкапсулирует в себе всю работу с
проектом `Apache Velocity`. В основном предназначен для создания целевого текста исходя из шаблона и его параметров.
Используется при создании контента сообщения в `TemplatedOutgoingMessage`.

Основные методы описаны в таблице ниже.

| Метод                                                        | Описание                                                                                                          |
|--------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| construct(String pathToTemplate, Map<String, Object> params) | Создает целевой текст используя шаблон из файла и набор параметров из Map. Критерии к пути до файла описаны ниже. |
| construct(Template template, Map<String, Object> params)     | Создает целевой текст используя экземпляр класса `Template` и набор параметров из Map.                            |
| Template createTemplate(String pathToTemplate)               | Создает экземпляр класса `Template` из файла. Критерии к пути до файла описаны ниже.                              |

Путь до файла может быть:

- начинаться с 'file://'. В таком случае файл будет искаться в файловой системе.
- начинаться с 'classpath:'. В таком случае файл будет искаться в classpath-е.
- начинаться с разделителя ('/' или '\' в зависимости от ОС). В таком случае файл будет искаться в файловой системе.
- начинаться с 'jar:file:' для поиска внутри jar архива.

Остальные методы являются служебными и используются внутри вышеописанных методов. Информацию по ним можно найти в
JavaDoc.

## <h2 id="section4">4. FileParametersDetector</h2>

Интерфейс `FileParametersDetector` - используется для создания реализации получения MIME type файла и его кодировки.
Стандартными средствами Java невозможно предугадать или получить данные параметры на 100% корректными, так как многое
зависит от ОС и содержимого файла. Но так как для отправки вложений email сообщения в виде файлов данные параметры
необходимы для построения заголовка `Content-Type`, был создан данный интерфейс чем добавлена возможность самостоятельно
указать собственную реализацию разработчику, использующему данную библиотеку.

Также библиотека предосталяет реализацию по умолчанию - `DefaultFileParametersDetector`. Но данной реализацией **НЕ
ГАРАНТИРУЕТСЯ** корректное получение данных параметров.

Рекомендуется обратить внимание на использование проекта [Apache Tika](https://tika.apache.org/) в качестве основы для
построения собственной реализации `FileParametersDetector`. В тестах данной библиотеки уже используется данный
проект. [ApacheTikaDetector](./src/test/java/ru/dlabs/library/email/support/ApacheTikaDetector.java).

## <h2 id="section5">5. Клиенты подключений</h2>

Все клиенты протоколов подключения к email серверу реализовывают основной интерфейс `DClient` и находиться в
пакете `ru.dlabs71.library.email.client`.
Основные методы описаны в таблице ниже:

| Метод              | Описание                                                               |
|--------------------|------------------------------------------------------------------------|
| connect()          | Создаёт экземпляр класса `Session` из проекта `jakarta-mail`           |
| getProtocolName()) | Возвращает наименование протокола.                                     |
| getPrincipal()     | Возвращает текущий email и имя почтового ящика к которому подключается |

В данном пакете также имеются следующие под-пакеты:

- `receiver` - содержит всё что касается получения сообщения.
- `sender` - содержит все что касается отправки сообщения.

### <h3 id="section51">5.1 Получатели сообщений</h3>

Классы клиентов получения сообщений находятся в пакете `ru.dlabs71.library.email.client.receiver`. Любой подобный класс
будет реализовывать интерфейс `ReceiverDClient`.

На текущий момент существует только один клиент для получения сообщений, который работает по
протоколу `IMAP` - `IMAPDClient`.

В `IMAPDClient` подключение создаётся сразу при создании экземпляра класса. Также существует две открытые
константы `DEFAULT_INBOX_FOLDER_NAME` и `DEFAULT_OUTBOX_FOLDER_NAME` - наименования папки входящих и исходящих
сообщений. Данные константы помогут если имена папок в вашем почтовом ящике совпадают со значениями данных констант.
Будьте внимательны, не всегда имя папки входящих сообщений называется `INBOX`, а исходящей `OUTBOX`.

Как можно заметить в клиентах реализуются методы `checkEmailMessages()` и `readMessages()`. Первое предназначено для "
проверки" сообщений, а второе для чтения сообщений. При проверке сообщений можно получить только основную информацию о
сообщении без его содержимого и вложений (`MessageView`). При этом, полученное сообщение через этот метод не будет
являться прочитанным. При чтении сообщения, сообщение становиться прочитанным и возвращается вся информация о сообщении
вместе с его содержимым и вложениями. Также, стоит подсветить что данные методы являются pageable-запросами, то есть все
сообщения разом получить нельзя только партиями, размер которых указан в параметре с типом `PageRequest`.

### <h3 id="section52">5.2 Отправители сообщений</h3>

Классы клиентов отправления сообщений находятся в пакете `ru.dlabs71.library.email.client.sender`. Любой подобный класс
будет реализовывать интерфейс `SenderDClient`.

На текущий момент существует только один клиент для получения сообщений, который работает по
протоколу `SMTP` - `SMTPDClient`.

В `SMTPDClient` подключение создаётся сразу при создании экземпляра класса. Данный класс имеет только один
метод `send(OutgoingMessage message)` для отправки сообщений, классы которых реализуют интерфейс `OutgoingMessage`.

## <h2 id="section6">6. Facade классы</h2>

Библиотека предоставляет ряд классов помогающих в отправке или получении сообщения. Также эти классы значительно
уменьшают исходный код для работы с email сервером и скрывают всю внутреннюю реализацию библиотеки в себе. Находятся все
данные классы в корневом пакете `ru.dlabs71.library.email`.

### <h3 id="section61">6.1 Класс DEmailSender</h3>

[DEmailSender](./src/main/java/ru/dlabs/library/email/DEmailSender.java) - предназначен для отправки сообщений. Он имеет
внутри себя множество перегруженных вариантов метода `send()`. Ряд дополнительных методов описаны в таблице ниже.

| Метод                         | Описание                                                 |
|-------------------------------|----------------------------------------------------------|
| of(SmtpProperties properties) | Используется для создания экземпляра класса DEmailSender |
| sender()                      | Получение информации об отправителе.                     |

### <h3 id="section62">6.2 Класс DEmailReceiver</h3>

[DEmailReceiver](./src/main/java/ru/dlabs/library/email/DEmailReceiver.java) - предназначен для получения сообщений.

Главной особенностью данного фасада является то что при работе обязательно используйте синхронизированный
метод `folder(String folderName)` до всех манипуляций с сообщениями. Он устанавливает имя папки с которой будут дальше
работать остальные методы. Это сделано для удобства, чтобы не указывать каждый раз имя папки. Если имя папки не будет
задано, то будет использоваться имя по умолчанию `INBOX`.

Также существуют методы `checkEmail()` и `readEmail()` без параметров. Данные методы получают список первых 50-и
сообщений из папки.

Ряд дополнительных методов описаны в таблице ниже.

| Метод                         | Описание                                                 |
|-------------------------------|----------------------------------------------------------|
| of(ImapProperties properties) | Используется для создания экземпляра класса DEmailSender |
| getCurrentFolder()            | Получение информации о текущей директории                |
| receiver()                    | Получение информации о получателе (email адрес)          |

## <h2 id="section7">7. Сборка из исходников</h2>

Для сборки из исходников понадобиться система автосборки Maven 3.9.2 или выше. Используемая версия Java 1.8.
Для сборки с выполнением тестов достаточно выполнить следующую команду из корня проекта:

```shell
mvn clean install -DEMAIL_1=<?> -DEMAIL_1_PASSWORD=<?> -DEMAIL_2=<?> -DEMAIL_2_PASSWORD=<?> -DEMAIL_3=<?> -DEMAIL_3_PASSWORD=<?>
```

Для выполнения тестов потребуется 3 почтовых ящика. Настройки подключения к ним необходимо передать в параметрах
вышеуказанной команды. Будьте внимательны, при работе тестов используется функция очистки почтового ящика, поэтому
указанные почтовые ящики должны быть тестовыми.

Все настройки для тестов можно найти в директории ресурсов `src/test/recources`.

- [imap.properties](src%2Ftest%2Fresources%2Fimap.properties) - настройки для чтения email
  сообщений (`ImapProperties`).
- [smtp.properties](src%2Ftest%2Fresources%2Fsmtp.properties) - настройки для отправки email
  сообщений (`SmtpProperties`).
- [tests.properties](src%2Ftest%2Fresources%2Ftests.properties) - общие настройки тестов.
    - `send.delay.after` - задержка между операциями записи и чтения в ms при интенсивной работе с почтовым ящиком.
      Необходим, так как удаление/отправка email сообщения на сервере может произойти чуть позже нежели последующее
      чтение, что приведёт к неправильному выполнению теста.

Для сборки без выполнения тестов:

```shell
mvn clean install -DskipTests
```

## <h2 id="section8">8. Checkstyle</h2>

В проекте настроен Checkstyle при сборке проекта. Используемая версия checkstyle 9.3. Файлы настроек checkstyle
находятся в директории `checkstyle` в корне проекта. Для проекта был выбран формат Google Checkstyle. Но также были
внесены следующие изменения.

1. Разрешены аббревиатуры SMTPD и IMAPD

```xml

<property name="allowedAbbreviations" value="SMTPD,IMAPD"/>
```

2. Увеличены отступы

```xml

<module name="Indentation">
    <property name="basicOffset" value="4"/>
    <property name="braceAdjustment" value="2"/>
    <property name="caseIndent" value="4"/>
    <property name="throwsIndent" value="4"/>
    <property name="lineWrappingIndentation" value="4"/>
    <property name="arrayInitIndent" value="4"/>
</module>
```

Для детальной информации о настройке maven-checkstyle-plugin смотрите [pom.xml](pom.xml).

Также в директории `checkstyle` добавлена директория `idea`. В ней находиться xml конфигурация Code Style для Intellij
IDEA. Данная конфигурация настроена таким образом, чтобы не противоречить checkstyle.

[maven-central-img]: https://maven-badges.herokuapp.com/maven-central/ru.dlabs71.library/d-email/badge.svg?gav=true

[maven-central-url]: https://maven-badges.herokuapp.com/maven-central/ru.dlabs71.library/d-email/?gav=true

[javadocs-img]: https://javadoc.io/badge2/ru.dlabs71.library/d-email/javadoc.svg

[javadocs-url]: https://javadoc.io/doc/ru.dlabs71.library/d-email

[license-image]: https://img.shields.io/badge/license-MIT-blue.svg

[license-url]: LICENSE