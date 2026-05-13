package lk.ijse.therapy_management_system.dto;

import lombok.*;
import lk.ijse.therapy_management_system.entity.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    private String userId;
    private String username;
    private String password;
    private User.Role role;
    private String fullName;
    private String email;
    private String phone;
    private boolean active;
}
