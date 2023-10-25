package ru.dlabs.library.email.support;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import ru.dlabs.library.email.mime.FileParametersDetector;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-24</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class ApacheTikaDetector implements FileParametersDetector {

    private final TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
    private final Tika tika = new Tika(tikaConfig);

    @Override
    @SneakyThrows
    public String detectMimeType(File file) {
        return tika.detect(file);
    }

    @Override
    @SneakyThrows
    public Charset detectEncoding(File file) {
        Metadata metadata = new Metadata();
        InputStream stream = TikaInputStream.get(file, metadata);
        return tikaConfig.getEncodingDetector().detect(stream, metadata);
    }
}
