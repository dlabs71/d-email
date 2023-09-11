package ru.dlabs.library.email.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.exception.AttachmentException;
import ru.dlabs.library.email.type.AttachmentType;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-11
 */
@Slf4j
@UtilityClass
public class AttachmentUtils {

    public File createFile(String pathToFile) throws AttachmentException {
        pathToFile = pathToFile.trim();
        try {
            URL url = null;
            if (pathToFile.startsWith("file://") || pathToFile.startsWith(File.separator)) {
                url = new URL(pathToFile);
            }
            if (pathToFile.startsWith("classpath:")) {
                pathToFile = pathToFile.replace("classpath:", "");
                url = AttachmentUtils.class.getClassLoader().getResource(pathToFile);
            }

            if (url == null) {
                throw new AttachmentException(
                    "The resource cannot be loaded. The parameter pathToFile must start with a: 'file://'; 'classpath:' or '/'. The pathToFile = " +
                        pathToFile);
            }

            File file = new File(url.toURI());
            if (!file.exists()) {
                throw new AttachmentException("File doesn't exist. The pathToFile = " + pathToFile);
            }
            return file;
        } catch (URISyntaxException | MalformedURLException ex) {
            log.error(ex.getLocalizedMessage(), ex);
            throw new AttachmentException(
                "The resource cannot be loaded. The parameter pathToFile must start with a: 'file://'; 'classpath:' or '/'. The pathToFile = " +
                    pathToFile + ". The error: " + ex.getLocalizedMessage());
        }
    }

    public EmailAttachment create(String pathToFile) throws AttachmentException {
        File file = createFile(pathToFile);
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        byte[] content;
        try {
            InputStream inputStream = new FileInputStream(file);
            content = IOUtils.toByteArray(inputStream);
        } catch (IOException ex) {
            throw new AttachmentException("Read the file was failed. " + ex.getLocalizedMessage());
        }
        return EmailAttachment.builder()
            .name(file.getName())
            .data(content)
            .size(file.length())
            .contentType(contentType)
            .type(AttachmentType.find(contentType))
            .build();
    }
}
