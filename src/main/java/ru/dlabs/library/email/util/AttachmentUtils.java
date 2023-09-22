package ru.dlabs.library.email.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * This is the utility class for creating {@link EmailAttachment} objects.
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-11
 */
@Slf4j
@UtilityClass
public class AttachmentUtils {

    /**
     * Creates a file object from the file path argument.
     * The path to file must start with the following prefixes: "file://", "classpath:" or a path separator.
     * Relatives paths is not supported.
     *
     * @param pathToFile the path to file
     *
     * @return object of the class {@link File}
     *
     * @throws AttachmentException exception may occur when the file path doesn't satisfy conditions or doesn't parse
     */
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

    /**
     * Creates an object of the {@link EmailAttachment} class by the path to file argument
     * The path to file must start with the following prefixes: "file://", "classpath:" or a path separator.
     * Relatives paths is not supported.
     *
     * @param pathToFile the path to file
     *
     * @return an object of the {@link EmailAttachment} class
     *
     * @throws AttachmentException exception may occur when the file path doesn't satisfy conditions or doesn't parse.
     *                             Also, an exception may occur while reading the file.
     */
    public EmailAttachment create(String pathToFile) throws AttachmentException {
        File file = createFile(pathToFile);
        String contentType = findContentType(file);
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

    public String findContentType(File file) {
        String contentType = URLConnection.guessContentTypeFromName(file.getName());

        if (contentType == null) {
            contentType = EmailMessageUtils.TEXT_CONTENT_TYPE;
        }
        if (AttachmentType.TEXT.equals(AttachmentType.find(contentType))) {
            String encoding = null;
            try {
                FileInputStream is = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(is);
                encoding = reader.getEncoding();
            } catch (IOException ex) {
                log.error(ex.getLocalizedMessage(), ex);
            }
            if (encoding != null) {
                return EmailMessageUtils.contentTypeWithEncoding(contentType, encoding);
            }
        }
        return contentType;
    }
}
