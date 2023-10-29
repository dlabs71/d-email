package ru.dlabs.library.email.tests.converter.outgoing.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.outgoing.DefaultOutgoingMessage;
import ru.dlabs.library.email.type.ContentMessageType;
import ru.dlabs.library.email.type.TransferEncoder;
import ru.dlabs.library.email.util.AttachmentUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-26</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class TestConverterUtils {

    public static final Set<EmailParticipant> recipients = new HashSet<>(
        Arrays.asList(
            EmailParticipant.of("billy@island.com", "Billy Bones"),
            EmailParticipant.of("livesey@island.com", "Dr. Livesey"),
            EmailParticipant.of("pew@island.com", "Blind Pew")
        ));

    public DefaultOutgoingMessage createSimpleMessage(String subject, String textContent) {
        return new DefaultOutgoingMessage(subject, textContent, recipients, null);
    }

    public DefaultOutgoingMessage createMessageWithAttachments(String subject, String textContent, List<String> paths) {
        List<EmailAttachment> emailAttachments = paths.stream()
            .map(AttachmentUtils::create)
            .collect(Collectors.toList());
        return new DefaultOutgoingMessage(subject, textContent, recipients, emailAttachments);
    }

    public DefaultOutgoingMessage createEmptyMessage() {
        return new DefaultOutgoingMessage(null, null, null, null);
    }

    public DefaultOutgoingMessage createMessageWithoutRecipients(String subject, String textContent) {
        return new DefaultOutgoingMessage(subject, textContent, new HashSet<>(), null);
    }

    public DefaultOutgoingMessage createHtmlMessage(String subject, String htmlContent) {
        return new DefaultOutgoingMessage(
            subject,
            htmlContent,
            StandardCharsets.UTF_8,
            ContentMessageType.HTML,
            recipients,
            null,
            TransferEncoder.EIGHT_BIT
        );
    }

    public DefaultOutgoingMessage createMessageWithEmptyContent(String subject) {
        return new DefaultOutgoingMessage(subject, null, recipients, null);
    }

    public DefaultOutgoingMessage createMessageWithEmptyContentAndAttachments(
        String subject,
        List<String> attachments
    ) {
        List<EmailAttachment> emailAttachments = attachments.stream()
            .map(AttachmentUtils::create)
            .collect(Collectors.toList());
        return new DefaultOutgoingMessage(subject, null, recipients, emailAttachments);
    }
}
