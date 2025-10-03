package com.mvs.user_service.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
@Builder
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String phone;

    public static class UserRoles {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }
}
