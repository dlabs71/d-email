package ru.dlabs71.library.email.tests.pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs71.library.email.dto.pageable.PageRequest;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-12</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(200)
public class PageRequestTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link PageRequest#getEnd()}</li>
     * </ul>
     */
    @Test
    public void getEndTest() {
        assertEquals(9, PageRequest.of(0, 10).getEnd());
        assertEquals(19, PageRequest.of(10, 10).getEnd());
        assertEquals(8, PageRequest.of(2, 7).getEnd());
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link PageRequest#incrementStart()}</li>
     * </ul>
     */
    @Test
    public void incrementStartTest() {
        assertEquals(10, PageRequest.of(0, 10).incrementStart().getStart());
        assertEquals(20, PageRequest.of(10, 10).incrementStart().getStart());
        assertEquals(9, PageRequest.of(2, 7).incrementStart().getStart());
    }
}
