package lk.ijse.therapy_management_system.dao.custom;

import lk.ijse.therapy_management_system.dao.CrudDAO;
import lk.ijse.therapy_management_system.entity.User;

import java.util.Optional;

public interface UserDAO extends CrudDAO<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean updateCredentials(String userId, String newUsername, String newHashedPassword);
}
