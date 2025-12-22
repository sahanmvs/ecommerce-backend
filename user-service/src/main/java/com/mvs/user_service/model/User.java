package com.mvs.user_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String phone;
    private String status;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public static class UserRoles {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }

    public static class UserStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String DEACTIVATED = "DEACTIVATED";
    }
}
