package be.pxl.services.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");

        assertThat(user)
                .extracting(User::getId, User::getEmail, User::getPassword, User::getFirstName, User::getLastName)
                .containsExactly(1L, "test@example.com", "password", "John", "Doe");
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User(2L, "user@example.com", "secret", "Jane", "Smith");

        assertThat(user)
                .extracting(User::getId, User::getEmail, User::getPassword, User::getFirstName, User::getLastName)
                .containsExactly(2L, "user@example.com", "secret", "Jane", "Smith");
    }

    @Test
    void testBuilder() {
        User user = User.builder()
                .id(3L)
                .email("builder@example.com")
                .password("builderpass")
                .firstName("Builder")
                .lastName("User")
                .build();

        assertThat(user)
                .extracting(User::getId, User::getEmail, User::getPassword, User::getFirstName, User::getLastName)
                .containsExactly(3L, "builder@example.com", "builderpass", "Builder", "User");
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
                .id(1L)
                .email("a@b.com")
                .password("1234")
                .firstName("A")
                .lastName("B")
                .build();

        User user2 = User.builder()
                .id(1L)
                .email("a@b.com")
                .password("1234")
                .firstName("A")
                .lastName("B")
                .build();

        User user3 = User.builder()
                .id(2L)
                .email("c@d.com")
                .password("5678")
                .firstName("C")
                .lastName("D")
                .build();

        assertThat(user1).satisfies(user -> {
            assertThat(user).isEqualTo(user2);
            assertThat(user).hasSameHashCodeAs(user2);
            assertThat(user).isNotEqualTo(user3);
        });
    }
}
