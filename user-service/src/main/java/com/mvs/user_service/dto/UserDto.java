package com.mvs.user_service.dto;

import com.mvs.user_service.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    private String id;

    @Size(min = 6, max = 100)
    @NotBlank(message = "email can not be blank")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    @Size(min = 6, max = 100)
    private String password;

    private String role;

    @Size(min = 1, max = 100)
    @NotBlank
    private String firstName;

    @Size(min = 1, max = 100)
    @NotBlank
    private String lastName;

    @Size(min = 3, max = 15)
    @NotBlank
    private String phone;

    public static UserDto init(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        return dto;
    }
}
