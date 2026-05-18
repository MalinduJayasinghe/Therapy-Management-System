package lk.ijse.therapy_management_system.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lk.ijse.therapy_management_system.bo.BOFactory;
import lk.ijse.therapy_management_system.bo.BOTypes;
import lk.ijse.therapy_management_system.bo.custom.UserBO;
import lk.ijse.therapy_management_system.dto.UserDTO;
import lk.ijse.therapy_management_system.entity.User;
import lk.ijse.therapy_management_system.exception.DuplicateEntryException;
import lk.ijse.therapy_management_system.util.ValidationUtil;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserManagementController {

    @FXML private TextField txtUserId;
    @FXML private TextField txtFullName;
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button btnToggle;
    @FXML private ComboBox<String> cmbRole;

    @FXML private Label lblNameError;
    @FXML private Label lblUsernameError;
    @FXML private Label lblPasswordError;
    @FXML private Label lblEmailError;
    @FXML private Label lblPhoneError;
    @FXML private Label lblStatus;

    @FXML private TableView<UserDTO> tblUsers;
    @FXML private TableColumn<UserDTO, String> colId;
    @FXML private TableColumn<UserDTO, String> colName;
    @FXML private TableColumn<UserDTO, String> colUsername;
    @FXML private TableColumn<UserDTO, String> colRole;
    @FXML private TableColumn<UserDTO, String> colEmail;
    @FXML private TableColumn<UserDTO, String> colPhone;

    private boolean pwVisible = false;
    private final UserBO userBO = BOFactory.getInstance().getBO(BOTypes.USER);

    @FXML
    public void initialize() {
        cmbRole.setItems(FXCollections.observableArrayList(
                Arrays.stream(User.Role.values()).map(Enum::name).collect(Collectors.toList())));
        cmbRole.setValue(User.Role.RECEPTIONIST.name());

        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());

        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        loadTable();
        setNextId();
    }

    @FXML private void handleToggle() {
        pwVisible = !pwVisible;
        txtPasswordVisible.setVisible(pwVisible); txtPasswordVisible.setManaged(pwVisible);
        txtPassword.setVisible(!pwVisible); txtPassword.setManaged(!pwVisible);
        btnToggle.setText(pwVisible ? "\uD83D\uDD12" : "👁");
        (pwVisible ? txtPasswordVisible : txtPassword).requestFocus();
    }

    @FXML private void handleSave() {
        if (!validateAll(true)) return;
        try {
            UserDTO dto = buildDTO();
            dto.setUserId(userBO.getNextId());
            dto.setActive(true);
            userBO.saveUser(dto);
            showStatus("✅ User saved.", false); loadTable(); handleClear();
        } catch (DuplicateEntryException e) {
            showStatus("⚠ " + e.getMessage(), true);
        } catch (Exception e) {
            showStatus("❌ " + e.getMessage(), true);
        }
    }

    @FXML private void handleUpdate() {
        if (txtUserId.getText().isBlank()) { showStatus("⚠ Select a user first.", true); return; }
        if (!validateAll(false)) return;
        try {
            userBO.updateUser(buildDTO());
            showStatus("✅ User updated.", false); loadTable();
        } catch (Exception e) { showStatus("❌ " + e.getMessage(), true); }
    }

    @FXML private void handleDelete() {
        String id = txtUserId.getText();
        if (id.isBlank()) { showStatus("⚠ Select a user first.", true); return; }
        Optional<ButtonType> r = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete user " + id + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if (r.isPresent() && r.get() == ButtonType.YES) {
            try { userBO.deleteUser(id); showStatus("✅ Deleted.", false); loadTable(); handleClear(); }
            catch (Exception e) { showStatus("❌ " + e.getMessage(), true); }
        }
    }

    @FXML private void handleClear() {
        txtUserId.clear(); txtFullName.clear(); txtUsername.clear();
        txtPassword.clear(); txtEmail.clear(); txtPhone.clear();
        cmbRole.setValue(User.Role.RECEPTIONIST.name());
        clearErrors(); setNextId(); lblStatus.setText("");
    }

    @FXML private void handleTableClick(MouseEvent e) {
        UserDTO sel = tblUsers.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        txtUserId.setText(sel.getUserId());
        txtFullName.setText(sel.getFullName());
        txtUsername.setText(sel.getUsername());
        txtPassword.clear();   // do not show hashed password
        txtEmail.setText(sel.getEmail());
        txtPhone.setText(sel.getPhone());
        cmbRole.setValue(sel.getRole().name());
        clearErrors();
    }

    private UserDTO buildDTO() {
        UserDTO dto = new UserDTO();
        dto.setUserId(txtUserId.getText().trim());
        dto.setFullName(txtFullName.getText().trim());
        dto.setUsername(txtUsername.getText().trim());
        dto.setPassword(pwVisible ? txtPasswordVisible.getText() : txtPassword.getText());
        dto.setEmail(txtEmail.getText().trim());
        dto.setPhone(txtPhone.getText().trim());
        dto.setRole(User.Role.valueOf(cmbRole.getValue()));
        dto.setActive(true);
        return dto;
    }

    private boolean validateAll(boolean requirePassword) {
        boolean ok = true;
        ok &= vf(txtFullName, lblNameError, ValidationUtil.isValidName(txtFullName.getText()), "Invalid name.");
        ok &= vf(txtUsername, lblUsernameError, ValidationUtil.isValidUsername(txtUsername.getText()), "4-30 alphanumeric chars.");
        if (requirePassword) {
            String pw = pwVisible ? txtPasswordVisible.getText() : txtPassword.getText();
            ok &= vf(txtPassword, lblPasswordError, ValidationUtil.isValidPassword(pw), "Min 6 chars, letters + digits.");
        }
        ok &= vf(txtEmail, lblEmailError, ValidationUtil.isValidEmail(txtEmail.getText()), "Invalid email.");
        ok &= vf(txtPhone, lblPhoneError, ValidationUtil.isValidPhone(txtPhone.getText()), "Invalid phone.");
        return ok;
    }

    private boolean vf(Control c, Label l, boolean valid, String msg) {
        if (!valid) { c.setStyle(ValidationUtil.invalidStyle()); l.setText(msg); l.setVisible(true); l.setManaged(true); return false; }
        c.setStyle(ValidationUtil.validStyle()); l.setVisible(false); l.setManaged(false); return true;
    }

    private void clearErrors() {
        for (Label l : new Label[]{
                lblNameError,
                lblUsernameError,
                lblPasswordError,
                lblEmailError,
                lblPhoneError
        }) {
            l.setVisible(false); l.setManaged(false);
        }

        for (Control c : new Control[]{
                txtFullName,
                txtUsername,
                txtPassword,
                txtEmail,
                txtPhone
        }) c.setStyle(ValidationUtil.defaultStyle());
    }

    private void loadTable() {

        try { tblUsers.setItems(FXCollections.observableArrayList(userBO.getAllUsers()));
        }catch (Exception e) {
            showStatus("❌ " + e.getMessage(), true);
        }
    }

    private void setNextId() {

        try {
            txtUserId.setText(userBO.getNextId());
        } catch(Exception e){
            txtUserId.setText("U003");
        }
    }

    private void showStatus(String m, boolean err) {
        lblStatus.setText(m);
        lblStatus.setStyle(err ? "-fx-text-fill:#dc3545;" : "-fx-text-fill:#28a745;");
    }
}
