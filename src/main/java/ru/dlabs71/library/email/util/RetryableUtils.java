package ru.dlabs71.library.email.util;

import jakarta.mail.MessagingException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The Utility class with helper methods for create retryable mechanizes.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-11-10</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class RetryableUtils {

    /**
     * Retry mechanism for handler in the 'supplier' argument.
     *
     * @param count     max number of the attempts
     * @param delayInMs delay in milliseconds between attempts
     * @param supplier  handler function implements the {@link RetryableSupplier} interface
     * @param <R>
     *
     * @return result of the 'supplier' if it successfully executes
     *
     * @throws MessagingException error while 'supplier' is executing
     */
    public <R> R retry(int count, long delayInMs, RetryableSupplier<R> supplier) throws MessagingException {
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

    /**
     * Retry mechanism for handler in the 'worker' argument.
     *
     * @param count     max number of the attempts
     * @param delayInMs delay in milliseconds between attempts
     * @param worker    handler function implements the {@link RetryableWorker} interface
     *
     * @throws MessagingException error while 'worker' is executing
     */
    public void retry(int count, long delayInMs, RetryableWorker worker) throws MessagingException {
        RetryableUtils.retry(count, delayInMs, () -> {
            worker.work();
            return null;
        });
    }

    /**
     * Functional interface for using in the {@linkplain RetryableUtils#retry(int, long, RetryableSupplier)}.
     *
     * @param <R> returned type
     */
    @FunctionalInterface
    public interface RetryableSupplier<R> {

        R get() throws MessagingException;
    }

    /**
     * Functional interface for using {@linkplain RetryableUtils#retry(int, long, RetryableWorker)}.
     * In contrast to {@linkplain RetryableSupplier}, this interface has the method work(),
     * which it has void as a return type.
     */
    @FunctionalInterface
    public interface RetryableWorker {

        void work() throws MessagingException;
    }
}
