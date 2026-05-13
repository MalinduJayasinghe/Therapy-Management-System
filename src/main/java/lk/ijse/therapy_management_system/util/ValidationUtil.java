package lk.ijse.therapy_management_system.util;

public class ValidationUtil {

    // ------------- Name -------------
    private static final String NAME_PATTERN = "^[A-Za-z ]{2,100}$";

    // ------------- Email -------------
    private static final String EMAIL_PATTERN = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    // ------------- Phone -------------
    private static final String PHONE_PATTERN = "^(\\+94|0)(7[0-9])[0-9]{7}$";

    // ------------- NIC  -------------
    private static final String NIC_PATTERN = "^([0-9]{9}[vVxX]|[0-9]{12})$";

    // ------------- Username (alphanumeric + underscore, 4-30 chars) -------------
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{4,30}$";

    // ------------- Password (min 6 chars, at least one letter and one digit) -------------
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";

    // ------------- Therapist ID  T001 -------------
    private static final String THERAPIST_ID_PATTERN = "^T[0-9]{3}$";

    // ------------- Patient ID  P001 -------------
    private static final String PATIENT_ID_PATTERN = "^P[0-9]{3}$";

    // ------------- User ID  U001 -------------
    private static final String USER_ID_PATTERN = "^U[0-9]{3}$";

    // ------------- Public validators -------------

    public static boolean isValidName(String name) {
        return name != null && name.matches(NAME_PATTERN);
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_PATTERN);
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches(PHONE_PATTERN);
    }

    public static boolean isValidNic(String nic) {
        return nic != null && nic.matches(NIC_PATTERN);
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.matches(USERNAME_PATTERN);
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static String validStyle() {
        return "-fx-border-color: #4CAF50; -fx-border-width: 1.5px; -fx-border-radius: 4px;";
    }

    public static String invalidStyle() {
        return "-fx-border-color: #e74c3c; -fx-border-width: 1.5px; -fx-border-radius: 4px;";
    }

    public static String defaultStyle() {
        return "-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 4px;";
    }
}
