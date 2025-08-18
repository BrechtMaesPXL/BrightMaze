package be.pxl.services.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageTypeTest {

    @Test
    void testEnumValues() {
        MessageType[] expectedValues = {
                MessageType.ROUTE,
                MessageType.EVENTS_TIME_SPECIFIC,
                MessageType.EVENT_LIST,
                MessageType.EVENT_DETAIL,
                MessageType.EVENTS_DATE_SPECIFIC,
                MessageType.GENERAL_INFO
        };
        assertThat(MessageType.values()).containsExactly(expectedValues);
    }

    @Test
    void testValueOf() {
        assertThat(MessageType.valueOf("ROUTE")).isEqualTo(MessageType.ROUTE);
        assertThat(MessageType.valueOf("EVENT_DETAIL")).isEqualTo(MessageType.EVENT_DETAIL);
    }

    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> MessageType.valueOf("INVALID"));
    }
}
