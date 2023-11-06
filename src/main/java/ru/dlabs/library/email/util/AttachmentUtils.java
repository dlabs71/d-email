package ru.dlabs.library.email.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.exception.AttachmentException;
import ru.dlabs.library.email.mime.DefaultFileParametersDetector;
import ru.dlabs.library.email.mime.FileParametersDetector;
import ru.dlabs.library.email.type.AttachmentType;

/**
 * This is the utility class is for creating {@link EmailAttachment} objects.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-11</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
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
        log.debug("Starts getting file for an attachment by path {}", pathToFile);
        try {
            URL url = null;
            if (pathToFile.startsWith("file://") || pathToFile.startsWith(File.separator)) {
                url = new URL(pathToFile);
            }
            if (pathToFile.startsWith("classpath:")) {
                pathToFile = pathToFile.replace("classpath:", "");
                url = AttachmentUtils.class.getClassLoader().getResource(pathToFile);
            }

            log.debug("Url is {}", url);
            if (url == null) {
                throw new AttachmentException(
                    "The resource cannot be loaded. "
                        + "The parameter pathToFile must start with a: 'file://'; 'classpath:' or '/'. "
                        + "The pathToFile = " + pathToFile);
            }

            File file = new File(url.toURI());
            if (!file.exists()) {
                throw new AttachmentException("File doesn't exist. The pathToFile = " + pathToFile);
            }
            return file;
        } catch (URISyntaxException | MalformedURLException ex) {
            log.error(ex.getMessage(), ex);
            throw new AttachmentException(
                "The resource cannot be loaded. "
                    + "The parameter pathToFile must start with a: 'file://'; 'classpath:' or '/'. "
                    + "The pathToFile = " + pathToFile + ". The error: " + ex.getMessage());
        }
    }

    /**
     * Creates an object of the {@link EmailAttachment} class by the path to file.
     *
     * @see AttachmentUtils#create(String, FileParametersDetector)
     */
    public EmailAttachment create(String pathToFile) throws AttachmentException {
        return create(pathToFile, DefaultFileParametersDetector.getInstance());
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
    public EmailAttachment create(String pathToFile, FileParametersDetector detector) throws AttachmentException {
        File file = createFile(pathToFile);
        log.debug("Creates attachment from the file {}", file);
        if (file.length() > Integer.MAX_VALUE) {
            throw new AttachmentException(
                "The file is too large. A file cannot be larger than "
                    + Integer.MAX_VALUE + ". Filename = "
                    + pathToFile);
        }
        String contentType = createContentTypeForAttachment(file, detector);
        log.debug("Content type for file is {}", contentType);
        byte[] content;
        try {
            InputStream inputStream = Files.newInputStream(file.toPath());
            content = JavaCoreUtils.toByteArray(inputStream);
        } catch (IOException ex) {
            throw new AttachmentException("Read the file was failed. " + ex.getMessage());
        }
        return EmailAttachment.builder()
            .name(file.getName())
            .data(content)
            .size((int) file.length())
            .contentType(contentType)
            .type(AttachmentType.find(contentType))
            .build();
    }

    /**
     * This method creates string for the value of Content-Type header.
     *
     * @see AttachmentUtils#createContentTypeForAttachment(File, FileParametersDetector)
     */
    public String createContentTypeForAttachment(File file) {
        return createContentTypeForAttachment(file, null);
    }

    /**
     * This method creates string for the value of Content-Type header.
     * If a mime type of file is a 'text/' then a result will contain 'charset='.
     *
     * @param file     a path to the file
     * @param detector {@linkplain FileParametersDetector} detector for getting different file parameters
     *
     * @return a completed string for using in the Content-Type header
     */
    public String createContentTypeForAttachment(File file, FileParametersDetector detector) {
        log.debug("Tries to create content type for file {} with using the detector is {}", file, detector);
        String mimeType = FileSystemUtils.detectFileMimeType(file, detector);
        log.debug("MIME type for the file {} is {}", file, mimeType);
        AttachmentType attachmentType = AttachmentType.find(mimeType);

        if (AttachmentType.TEXT.equals(attachmentType)) {
            Charset encoding = FileSystemUtils.detectFileEncoding(file, detector);
            log.debug("Charset content of the file {} is {}", file, encoding);
            if (encoding != null) {
                return HttpUtils.contentTypeWithCharset(mimeType, encoding);
            }
        }
        return mimeType;
    }
}
