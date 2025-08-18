package be.pxl.services.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CordiLocationTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        CordiLocation location = new CordiLocation();
        location.setId(10L);
        location.setLocation("Antwerp");

        assertThat(location)
                .extracting(CordiLocation::getId, CordiLocation::getLocation)
                .containsExactly(10L, "Antwerp");
    }

    @Test
    void testAllArgsConstructor() {
        CordiLocation location = new CordiLocation(20L, "Brussels");

        assertThat(location)
                .extracting(CordiLocation::getId, CordiLocation::getLocation)
                .containsExactly(20L, "Brussels");
    }

    @Test
    void testBuilder() {
        CordiLocation location = CordiLocation.builder()
                .id(30L)
                .location("Ghent")
                .build();

        assertThat(location)
                .extracting(CordiLocation::getId, CordiLocation::getLocation)
                .containsExactly(30L, "Ghent");
    }

    @Test
    void testToStringContainsFields() {
        CordiLocation location = CordiLocation.builder()
                .id(40L)
                .location("Leuven")
                .build();

        assertThat(location.toString())
                .satisfies(s -> {
                    assertThat(s).contains("id=40");
                    assertThat(s).contains("location=Leuven");
                });
    }

    @Test
    void testEqualsAndHashCode() {
        CordiLocation loc1 = CordiLocation.builder()
                .id(1L)
                .location("Test")
                .build();

        CordiLocation loc2 = CordiLocation.builder()
                .id(1L)
                .location("Test")
                .build();

        CordiLocation loc3 = CordiLocation.builder()
                .id(2L)
                .location("Other")
                .build();

        assertThat(loc1)
                .satisfies(loc -> {
                    assertThat(loc).isEqualTo(loc2);
                    assertThat(loc).hasSameHashCodeAs(loc2);
                    assertThat(loc).isNotEqualTo(loc3);
                });
    }
}
