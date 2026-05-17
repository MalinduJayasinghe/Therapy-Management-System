package lk.ijse.therapy_management_system.bo.custom;

import lk.ijse.therapy_management_system.bo.SuperBO;
import lk.ijse.therapy_management_system.dto.UserDTO;
import lk.ijse.therapy_management_system.entity.User;

import java.util.List;

public interface UserBO extends SuperBO {
    UserDTO login(String username, String password);
    void saveUser(UserDTO dto);
    void updateUser(UserDTO dto);
    boolean deleteUser(String userId);
    List<UserDTO> getAllUsers();
    String getNextId();
    void updateCredentials(String userId, String newUsername, String newPassword);
}
