// UserRequest.java
package be.pxl.services.domain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 21, message = "First name must be between 3 and 21 characters")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-']+$", message = "First name contains invalid characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 21, message = "Last name must be between 3 and 21 characters")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s\\-']+$", message = "Last name contains invalid characters")
    private String lastName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}