package ru.dlabs.library.email.util;

import jakarta.mail.MessagingException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The Utility class with helper methods for create repeatable mechanizes
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-11-10</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class RepeatableUtils {

    public <R> R repeatable(int count, long delayInMs, RepeatableSupplier<R> supplier) throws MessagingException {
        MessagingException messagingException;
        do {
            try {
                return supplier.get();
            } catch (MessagingException ex) {
                messagingException = ex;
                log.warn(ex.getMessage());
                if (delayInMs != 0) {
                    log.info("Start delay");
                    try {
                        Thread.sleep(delayInMs);
                    } catch (InterruptedException e) {
                        log.error("Error in delay: " + e.getMessage());
                    }
                }
                log.info("Repeat request. Count = " + count);
            }
        } while (count-- != 0);
        throw messagingException;
    }

    public void repeatable(int count, long delayInMs, RepeatableWorker worker) throws MessagingException {
        RepeatableUtils.repeatable(count, delayInMs, () -> {
            worker.work();
            return null;
        });
    }

    /**
     * Functional interface for using in the {@linkplain RepeatableUtils#repeatable(int, long, RepeatableSupplier)}.
     *
     * @param <R> returned type
     */
    @FunctionalInterface
    public interface RepeatableSupplier<R> {

        R get() throws MessagingException;
    }

    /**
     * Functional interface for using {@linkplain RepeatableUtils#repeatable(int, long, RepeatableWorker)}.
     * In contrast to {@linkplain RepeatableSupplier}, this interface has the method work(),
     * which it has void as a return type.
     */
    @FunctionalInterface
    public interface RepeatableWorker {

        void work() throws MessagingException;
    }
}
