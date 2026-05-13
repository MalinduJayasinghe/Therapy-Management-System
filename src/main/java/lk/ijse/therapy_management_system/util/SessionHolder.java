package lk.ijse.therapy_management_system.util;

import lk.ijse.therapy_management_system.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

public class SessionHolder {

    @Getter
    @Setter
    private static UserDTO loggedInUser;

    public static boolean isAdmin() {
        return loggedInUser != null &&
               loggedInUser.getRole() == lk.ijse.serenity.entity.User.Role.ADMIN;
    }

    public static boolean isReceptionist() {
        return loggedInUser != null &&
               loggedInUser.getRole() == lk.ijse.serenity.entity.User.Role.RECEPTIONIST;
    }

    public static void clearSession() {
        loggedInUser = null;
    }
}
