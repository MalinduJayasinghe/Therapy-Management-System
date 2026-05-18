package lk.ijse.therapy_management_system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.therapy_management_system.bo.BOFactory;
import lk.ijse.therapy_management_system.bo.BOTypes;
import lk.ijse.therapy_management_system.bo.custom.UserBO;
import lk.ijse.therapy_management_system.dto.UserDTO;
import lk.ijse.therapy_management_system.exception.DuplicateEntryException;
import lk.ijse.therapy_management_system.exception.ValidationException;
import lk.ijse.therapy_management_system.util.SessionHolder;
import lk.ijse.therapy_management_system.util.ValidationUtil;

public class ChangeCredentialsController {

    @FXML private TextField     txtCurrentUser;
    @FXML private TextField     txtNewUsername;
    @FXML private PasswordField txtNewPassword;
    @FXML private TextField     txtNewPasswordVisible;
    @FXML private Button        btnToggleNew;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private TextField     txtConfirmPasswordVisible;
    @FXML private Button        btnToggleConfirm;

    @FXML private Label lblUsernameError;
    @FXML private Label lblPasswordError;
    @FXML private Label lblConfirmError;
    @FXML private Label lblStatus;

    private boolean newVisible     = false;
    private boolean confirmVisible = false;

    private final UserBO userBO = BOFactory.getInstance().getBO(BOTypes.USER);

    @FXML
    public void initialize() {
        txtNewPasswordVisible.textProperty().bindBidirectional(txtNewPassword.textProperty());
        txtConfirmPasswordVisible.textProperty().bindBidirectional(txtConfirmPassword.textProperty());

        UserDTO user = SessionHolder.getLoggedInUser();
        if (user != null) {
            txtCurrentUser.setText(user.getFullName() + "  (@" + user.getUsername() + ")");
            txtNewUsername.setText(user.getUsername());
        }

        // Live validation
        txtNewUsername.focusedProperty()
                .addListener((o, w, is) -> {
                    if (!is) validateUsername();
        });
        txtNewPassword.focusedProperty()
                .addListener((o, w, is) -> {
            if (!is) validatePassword();
        });
        txtConfirmPassword.focusedProperty()
                .addListener((o, w, is) -> {
            if (!is) validateConfirm();
        });
    }

    @FXML
    private void handleToggleNew() {
        newVisible = !newVisible;
        txtNewPasswordVisible.setVisible(newVisible);
        txtNewPasswordVisible.setManaged(newVisible);
        txtNewPassword.setVisible(!newVisible);
        txtNewPassword.setManaged(!newVisible);
        btnToggleNew.setText(newVisible ? "\uD83D\uDD12" : "👁");
        (newVisible ? txtNewPasswordVisible : txtNewPassword).requestFocus();
    }

    @FXML
    private void handleToggleConfirm() {
        confirmVisible = !confirmVisible;
        txtConfirmPasswordVisible.setVisible(confirmVisible);
        txtConfirmPasswordVisible.setManaged(confirmVisible);
        txtConfirmPassword.setVisible(!confirmVisible);
        txtConfirmPassword.setManaged(!confirmVisible);
        btnToggleConfirm.setText(confirmVisible ? "\uD83D\uDD12" : "👁");
        (confirmVisible ? txtConfirmPasswordVisible : txtConfirmPassword).requestFocus();
    }

    @FXML
    private void handleUpdate() {
        clearStatus();
        boolean ok = validateUsername() & validatePassword() & validateConfirm();
        if (!ok) return;

        String newUsername = txtNewUsername.getText().trim();
        String newPassword = newVisible
                ? txtNewPasswordVisible.getText()
                : txtNewPassword.getText();

        UserDTO currentUser = SessionHolder.getLoggedInUser();
        if (currentUser == null) {
            showStatus("⚠ Session expired. Please log in again.", true);
            return;
        }

        try {
            userBO.updateCredentials(currentUser.getUserId(), newUsername, newPassword);

            // Update the session holder with the new username
            currentUser.setUsername(newUsername);
            SessionHolder.setLoggedInUser(currentUser);

            showStatus("Credentials updated successfully.", false);
        } catch (DuplicateEntryException e) {
            setFieldError(lblUsernameError, txtNewUsername, e.getMessage());
        } catch (ValidationException e) {
            showStatus("⚠ " + e.getMessage(), true);
        } catch (Exception e) {
            showStatus("❌ Error: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleReset() {
        UserDTO user = SessionHolder.getLoggedInUser();
        if (user != null) txtNewUsername.setText(user.getUsername());
        txtNewPassword.clear();
        txtConfirmPassword.clear();
        clearAllErrors();
        clearStatus();
    }

    private boolean validateUsername() {
        String v = txtNewUsername.getText().trim();
        if (!ValidationUtil.isValidUsername(v)) {
            setFieldError(lblUsernameError, txtNewUsername,
                    "Username must be 4-30 alphanumeric characters.");
            return false;
        }
        clearFieldError(lblUsernameError, txtNewUsername);
        return true;
    }

    private boolean validatePassword() {
        String v = newVisible ? txtNewPasswordVisible.getText() : txtNewPassword.getText();
        if (!ValidationUtil.isValidPassword(v)) {
            setFieldError(lblPasswordError, txtNewPassword,
                    "Password must be at least 6 characters with letters and digits.");
            return false;
        }
        clearFieldError(lblPasswordError, txtNewPassword);
        return true;
    }

    private boolean validateConfirm() {
        String pass = newVisible ? txtNewPasswordVisible.getText() : txtNewPassword.getText();
        String confirm = confirmVisible ? txtConfirmPasswordVisible.getText() : txtConfirmPassword.getText();
        if (!pass.equals(confirm)) {
            setFieldError(lblConfirmError, txtConfirmPassword, "Passwords do not match.");
            return false;
        }
        clearFieldError(lblConfirmError, txtConfirmPassword);
        return true;
    }

    /* ----------------------------- Error helpers ----------------------------- */
    private void setFieldError(Label label, Control field, String msg) {
        label.setText(msg); label.setVisible(true); label.setManaged(true);
        field.setStyle(ValidationUtil.invalidStyle());
    }

    private void clearFieldError(Label label, Control field) {
        label.setVisible(false); label.setManaged(false);
        field.setStyle(ValidationUtil.validStyle());
    }

    private void clearAllErrors() {
        clearFieldError(lblUsernameError, txtNewUsername);
        clearFieldError(lblPasswordError, txtNewPassword);
        clearFieldError(lblConfirmError, txtConfirmPassword);
    }

    private void showStatus(String msg, boolean err) {
        lblStatus.setText(msg);
        lblStatus.setStyle(err ? "-fx-text-fill:#dc3545;" : "-fx-text-fill:#28a745;");
    }

    private void clearStatus() {
        lblStatus.setText("");
    }
}
