package ru.dlabs.library.email;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import ru.dlabs.library.email.client.sender.SMTPDClient;
import ru.dlabs.library.email.client.sender.SenderDClient;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.outgoing.DefaultOutgoingMessage;
import ru.dlabs.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs.library.email.dto.message.outgoing.TemplatedOutgoingMessage;
import ru.dlabs.library.email.exception.CreateMessageException;
import ru.dlabs.library.email.exception.TemplateCreationException;
import ru.dlabs.library.email.property.SmtpProperties;
import ru.dlabs.library.email.type.ContentMessageType;
import ru.dlabs.library.email.type.SendingStatus;
import ru.dlabs.library.email.util.AttachmentUtils;

/**
 * This class implements the Facade pattern for sending email messages.
 * This class use the SMTP protocol.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-18</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public final class DEmailSender {

    private final SenderDClient senderClient;
    private final Charset defaultCharset;

    /**
     * Constructor of the class.
     *
     * @param smtpProperties properties for connecting to an email server by the SMTP protocol ({@link SmtpProperties})
     */
    private DEmailSender(SmtpProperties smtpProperties) {
        this.defaultCharset = smtpProperties.getCharset();
        this.senderClient = new SMTPDClient(smtpProperties);
    }

    /**
     * Creates instance of the {@link DEmailSender} class.
     *
     * @param properties properties for connecting to an email server by the SMTP protocol ({@link SmtpProperties})
     *
     * @return object of the class {@link DEmailSender}
     */
    public static DEmailSender of(SmtpProperties properties) {
        return new DEmailSender(properties);
    }

    /**
     * Returns information about sender as object of the class {@link EmailParticipant}.
     *
     * @return the object of the class {@link EmailParticipant}
     */
    public EmailParticipant sender() {
        return senderClient.getPrincipal();
    }

    /**
     * The method is sending a message.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param email   a recipient email address. For example: example@mail.com
     * @param subject a subject of a message
     * @param content a message body
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendText(String email, String subject, String content) {
        return this.sendText(email, subject, content, new ArrayList<>());
    }

    /**
     * The method is distributing a message to a group recipients.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param emails  a collection of recipient email address. For example: example@mail.com
     * @param subject a subject of a message
     * @param content a message body
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendText(Collection<String> emails, String subject, String content) {
        return this.sendText(emails, subject, content, new ArrayList<>());
    }

    /**
     * The method is distributing a message to a group recipients with attachments.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param emails      a collection of recipient email address. For example: example@mail.com
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments an array of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendText(
        Collection<String> emails,
        String subject,
        String content,
        EmailAttachment... attachments
    ) {
        return this.sendText(emails, subject, content, Arrays.asList(attachments));
    }

    /**
     * The method is distributing a message to a group recipients with attachments.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param emails      a collection of recipient email address. For example: example@mail.com
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendText(
        Collection<String> emails,
        String subject,
        String content,
        List<EmailAttachment> attachments
    ) {
        Set<EmailParticipant> recipients = emails.stream().map(EmailParticipant::new).collect(Collectors.toSet());
        return this.sendText(recipients, subject, content, attachments);
    }

    /**
     * The method is sending a message with attachments.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param email       a recipient email address. For example: example@mail.com
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments an array of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendText(String email, String subject, String content, EmailAttachment... attachments) {
        return this.sendText(email, subject, content, Arrays.asList(attachments));
    }

    /**
     * The method is sending a message with attachments.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param email       a recipient email address. For example: example@mail.com
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendText(String email, String subject, String content, List<EmailAttachment> attachments) {
        Set<EmailParticipant> recipients = new HashSet<>();
        recipients.add(new EmailParticipant(email));
        return this.sendText(recipients, subject, content, attachments);
    }

    /**
     * The method is distributing a message to a group recipients with attachments.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param recipients  the set of information about recipients ({@link EmailParticipant})
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendText(
        Set<EmailParticipant> recipients,
        String subject,
        String content,
        List<EmailAttachment> attachments
    ) {
        return this.send(recipients, subject, content, ContentMessageType.TEXT, null, attachments);
    }

    /**
     * The method is sending a message.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param email   a recipient email address. For example: example@mail.com
     * @param subject a subject of a message
     * @param content a message body
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtml(String email, String subject, String content) {
        return this.sendHtml(email, subject, content, new ArrayList<>());
    }

    /**
     * The method is distributing a message to a group recipients.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param emails  a collection of recipient email address. For example: example@mail.com
     * @param subject a subject of a message
     * @param content a message body
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtml(Collection<String> emails, String subject, String content) {
        return this.sendHtml(emails, subject, content, new ArrayList<>());
    }

    /**
     * The method is distributing a message to a group recipients with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param emails      a collection of recipient email address. For example: example@mail.com
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments an array of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtml(Set<String> emails, String subject, String content, EmailAttachment... attachments) {
        return this.sendHtml(emails, subject, content, Arrays.asList(attachments));
    }

    /**
     * The method is distributing a message to a group recipients with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param emails      a collection of recipient email address. For example: example@mail.com
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtml(
        Collection<String> emails,
        String subject,
        String content,
        List<EmailAttachment> attachments
    ) {
        Set<EmailParticipant> recipients = emails.stream().map(EmailParticipant::new).collect(Collectors.toSet());
        return this.sendHtml(recipients, subject, content, attachments);
    }

    /**
     * The method is distributing a message to a group recipients with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param emails      a collection of recipient email address. For example: example@mail.com
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments an array of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtml(
        Collection<String> emails,
        String subject,
        String content,
        EmailAttachment... attachments
    ) {
        Set<EmailParticipant> recipients = emails.stream().map(EmailParticipant::new).collect(Collectors.toSet());
        return this.sendHtml(recipients, subject, content, Arrays.asList(attachments));
    }

    /**
     * The method is sending a message with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param email       a recipient email address. For example: example@mail.com
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments an array of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtml(String email, String subject, String content, EmailAttachment... attachments) {
        return this.sendHtml(email, subject, content, Arrays.asList(attachments));
    }

    /**
     * The method is sending a message with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param email       a recipient email address. For example: example@mail.com
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtml(String email, String subject, String content, List<EmailAttachment> attachments) {
        Set<EmailParticipant> recipients = new HashSet<>();
        recipients.add(new EmailParticipant(email));
        return this.sendHtml(recipients, subject, content, attachments);
    }

    /**
     * The method is distributing a message to a group recipients with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body takes from "content" argument. It has a type String.
     *
     * @param recipients  the set of information about recipients ({@link EmailParticipant})
     * @param subject     a subject of a message
     * @param content     a message body
     * @param attachments a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtml(
        Set<EmailParticipant> recipients,
        String subject,
        String content,
        List<EmailAttachment> attachments
    ) {
        return this.send(recipients, subject, content, ContentMessageType.HTML, null, attachments);
    }

    /**
     * The method is sending a message.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param email          a recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtmlTemplated(
        String email,
        String subject,
        String pathToTemplate,
        Map<String, Object> params
    ) {
        return this.sendHtmlTemplated(email, subject, pathToTemplate, params, new ArrayList<>());
    }

    /**
     * The method is distributing a message to a group recipients.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param emails         a collection of recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtmlTemplated(
        Collection<String> emails,
        String subject,
        String pathToTemplate,
        Map<String, Object> params
    ) {
        return this.sendHtmlTemplated(emails, subject, pathToTemplate, params, new ArrayList<>());
    }

    /**
     * The method is distributing a message to a group recipients with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param emails         a collection of recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param attachments    an array of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtmlTemplated(
        Collection<String> emails,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        EmailAttachment... attachments
    ) {
        return this.sendHtmlTemplated(emails, subject, pathToTemplate, params, Arrays.asList(attachments));
    }

    /**
     * The method is distributing a message to a group recipients with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param emails         a collection of recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param attachments    a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtmlTemplated(
        Collection<String> emails,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        List<EmailAttachment> attachments
    ) {
        Set<EmailParticipant> recipients = emails.stream().map(EmailParticipant::new).collect(Collectors.toSet());
        return this.sendHtmlTemplated(recipients, subject, pathToTemplate, params, attachments);
    }

    /**
     * The method is sending a message with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param email          a recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param attachments    an array of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtmlTemplated(
        String email,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        EmailAttachment... attachments
    ) {
        return this.sendHtmlTemplated(email, subject, pathToTemplate, params, Arrays.asList(attachments));
    }

    /**
     * The method is sending a message with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param email          a recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param attachments    a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtmlTemplated(
        String email,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        List<EmailAttachment> attachments
    ) {
        Set<EmailParticipant> recipients = new HashSet<>();
        recipients.add(new EmailParticipant(email));
        return this.sendHtmlTemplated(recipients, subject, pathToTemplate, params, attachments);
    }

    /**
     * The method is distributing a message to a group of recipients with attachments.
     *
     * <p>A message body has a content type of text/html.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param recipients     the set of information about recipients ({@link EmailParticipant})
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param attachments    a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendHtmlTemplated(
        Set<EmailParticipant> recipients,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        List<EmailAttachment> attachments
    ) {
        return this.sendTemplatedMessage(
            recipients,
            subject,
            pathToTemplate,
            params,
            ContentMessageType.HTML,
            null,
            attachments
        );
    }

    /**
     * The method is sending a message.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param email          a recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendTextTemplated(
        String email,
        String subject,
        String pathToTemplate,
        Map<String, Object> params
    ) {
        return this.sendTextTemplated(email, subject, pathToTemplate, params, new ArrayList<>());
    }

    /**
     * The method is distributing a message to a group of recipients.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param emails         a collection of recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendTextTemplated(
        Collection<String> emails,
        String subject,
        String pathToTemplate,
        Map<String, Object> params
    ) {
        return this.sendTextTemplated(emails, subject, pathToTemplate, params, new ArrayList<>());
    }

    /**
     * The method is distributing a message to a group of recipients with attachments.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param emails         a collection of recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param attachments    an array of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendTextTemplated(
        Collection<String> emails,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        EmailAttachment... attachments
    ) {
        return this.sendTextTemplated(emails, subject, pathToTemplate, params, Arrays.asList(attachments));
    }

    /**
     * The method is distributing a message to a group of recipients with attachments.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param emails         a collection of recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param attachments    a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendTextTemplated(
        Collection<String> emails,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        List<EmailAttachment> attachments
    ) {
        Set<EmailParticipant> recipients = emails.stream().map(EmailParticipant::new).collect(Collectors.toSet());
        return this.sendTextTemplated(recipients, subject, pathToTemplate, params, attachments);
    }

    /**
     * The method is sending a message with attachments.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param email          a recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param attachments    an array of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendTextTemplated(
        String email,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        EmailAttachment... attachments
    ) {
        return this.sendTextTemplated(email, subject, pathToTemplate, params, Arrays.asList(attachments));
    }

    /**
     * The method is sending a message with attachments.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param email          a recipient email address. For example: example@mail.com
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param attachments    a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendTextTemplated(
        String email,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        List<EmailAttachment> attachments
    ) {
        Set<EmailParticipant> recipients = new HashSet<>();
        recipients.add(new EmailParticipant(email));
        return this.sendTextTemplated(recipients, subject, pathToTemplate, params, attachments);
    }

    /**
     * The method is distributing a message to a group of recipients with attachments.
     *
     * <p>A message body has a content type of text/plain.
     *
     * <p>A message body is created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * @param recipients     the set of information about recipients ({@link EmailParticipant})
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param attachments    a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendTextTemplated(
        Set<EmailParticipant> recipients,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        List<EmailAttachment> attachments
    ) {
        return this.sendTemplatedMessage(
            recipients,
            subject,
            pathToTemplate,
            params,
            ContentMessageType.TEXT,
            null,
            attachments
        );
    }

    /**
     * The common method is sending a templated message.
     *
     * <p>A message body created using the Apache Velocity Template engine.
     *
     * <p>For more information about the velocity template engine, use the link:
     * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
     *
     * <p>For simplifying attachment creation, use the {@link AttachmentUtils} utility class
     *
     * @param recipients     the set of information about recipients ({@link EmailParticipant})
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template
     * @param params         parameters for the template
     * @param contentType    the content type of content
     * @param charsetContent the encoding of content
     * @param attachments    a list of attachments ({@link EmailAttachment})
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus sendTemplatedMessage(
        Set<EmailParticipant> recipients,
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        ContentMessageType contentType,
        Charset charsetContent,
        List<EmailAttachment> attachments
    ) {
        OutgoingMessage message;
        try {
            message = TemplatedOutgoingMessage.builder()
                .recipientEmail(recipients)
                .subject(subject)
                .template(pathToTemplate, params)
                .charsetContent(charsetContent == null ? defaultCharset : charsetContent)
                .contentType(contentType)
                .attachments(attachments)
                .build();
        } catch (TemplateCreationException e) {
            throw new CreateMessageException(e.getMessage(), e);
        }
        return this.send(message);
    }

    /**
     * The common method is sending a text message.
     *
     * <p>For simplifying attachment creation, use the {@link AttachmentUtils} utility class
     *
     * @param recipients  the set of recipients
     * @param subject     the message subject
     * @param content     the message body
     * @param attachments a list of attachments
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus send(
        Set<EmailParticipant> recipients,
        String subject,
        String content,
        ContentMessageType contentType,
        Charset charsetContent,
        List<EmailAttachment> attachments
    ) {
        OutgoingMessage message = DefaultOutgoingMessage.outgoingMessageBuilder()
            .recipientEmail(recipients)
            .subject(subject)
            .content(content)
            .contentType(contentType)
            .charsetContent(charsetContent == null ? defaultCharset : charsetContent)
            .attachments(attachments)
            .build();
        return this.send(message);
    }

    /**
     * The common method for sending {@link OutgoingMessage} messages.
     *
     * @param message an outgoing message
     *
     * @return a sending status {@link SendingStatus}
     */
    public SendingStatus send(OutgoingMessage message) {
        return this.senderClient.send(message);
    }
}
