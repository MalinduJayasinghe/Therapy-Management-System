package lk.ijse.therapy_management_system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import lk.ijse.therapy_management_system.bo.BOFactory;
import lk.ijse.therapy_management_system.bo.BOTypes;
import lk.ijse.therapy_management_system.bo.custom.UserBO;
import lk.ijse.therapy_management_system.dto.UserDTO;
import lk.ijse.therapy_management_system.exception.LoginException;
import lk.ijse.therapy_management_system.util.NavigationUtil;
import lk.ijse.therapy_management_system.util.SessionHolder;
import lk.ijse.therapy_management_system.util.ValidationUtil;


public class LoginController {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField     txtPasswordVisible;    // overlaid text field for "show password"
    @FXML private Button        btnTogglePassword;
    @FXML private Button        btnLogin;

    @FXML private Label         lblUsernameError;
    @FXML private Label         lblPasswordError;
    @FXML private Label         lblGeneralError;

    /* ------------------------ Password State ------------------------ */
    private boolean passwordVisible = false;
    private final UserBO userBO = BOFactory.getInstance().getBO(BOTypes.USER);

    @FXML
    public void initialize() {
        // Keep the two password fields in sync
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());

        txtUsername.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) txtPassword.requestFocus();
        });

        // Live validation on focus-out
        txtUsername.focusedProperty().addListener(
                (obs, wasFocused, isFocused) -> {
            if (!isFocused) validateUsername();
        });

        txtPassword.focusedProperty().addListener(
                (obs, wasFocused, isFocused) -> {
            if (!isFocused) validatePassword();
        });
    }

    /* ------------------------ Show / Hide password ------------------------ */
    @FXML
    private void handleTogglePassword() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
            btnTogglePassword.setText("🙈");
        } else {
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
            btnTogglePassword.setText("👁");
        }
    }

    /* ------------------------ Login ------------------------ */
    @FXML
    private void handleLogin() {
        clearErrors();

        boolean Username = validateUsername();
        boolean Password = validatePassword();

        if (!Username || !Password) return;

        try {
            String username = txtUsername.getText().trim();
            String password = passwordVisible
                    ? txtPasswordVisible.getText()
                    : txtPassword.getText();

            UserDTO user = userBO.login(username, password);

            SessionHolder.setLoggedInUser(user);

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/view/Dashboard.fxml",
                    "Serenity MHT Center — " + user.getRole().name());

            stage.setMinWidth(1200);
            stage.setMinHeight(700);
            stage.setWidth(1280);
            stage.setHeight(750);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (LoginException ex) {
            showGeneralError(ex.getMessage());
        } catch (Exception ex) {
            showGeneralError("An unexpected error occurred. Please try again.");
            ex.printStackTrace();
        }
    }

    /* ------------------------ Validation ------------------------ */
    private boolean validateUsername() {
        String username = txtUsername.getText().trim();
        if (!ValidationUtil.isNotEmpty(username)) {
            showFieldError(lblUsernameError, txtUsername, "Username is required.");
            return false;
        }
        clearFieldError(lblUsernameError, txtUsername);
        return true;
    }

    private boolean validatePassword() {
        String password = passwordVisible
                ? txtPasswordVisible.getText()
                : txtPassword.getText();
        if (!ValidationUtil.isNotEmpty(password)) {
            showFieldError(lblPasswordError, txtPassword, "Password is required.");
            return false;
        }
        clearFieldError(lblPasswordError, txtPassword);
        return true;
    }

    /* ------------------------ Show Error messages ------------------------ */
    private void showFieldError(Label label, Control field, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
        field.setStyle(ValidationUtil.invalidStyle());
    }

    private void clearFieldError(Label label, Control field) {
        label.setVisible(false);
        label.setManaged(false);
        field.setStyle(ValidationUtil.defaultStyle());
    }

    private void showGeneralError(String message) {
        lblGeneralError.setText(message);
        lblGeneralError.setVisible(true);
        lblGeneralError.setManaged(true);
    }

    private void clearErrors() {
        clearFieldError(lblUsernameError, txtUsername);
        clearFieldError(lblPasswordError, txtPassword);
        lblGeneralError.setVisible(false);
        lblGeneralError.setManaged(false);
    }
}
