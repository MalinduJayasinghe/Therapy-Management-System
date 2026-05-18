package lk.ijse.therapy_management_system.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lk.ijse.therapy_management_system.bo.BOFactory;
import lk.ijse.therapy_management_system.bo.BOTypes;
import lk.ijse.therapy_management_system.bo.custom.PatientBO;
import lk.ijse.therapy_management_system.dto.PatientDTO;
import lk.ijse.therapy_management_system.dto.tm.PatientTM;
import lk.ijse.therapy_management_system.exception.DuplicateEntryException;
import lk.ijse.therapy_management_system.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PatientController {

    @FXML private TextField txtPatientId;
    @FXML private ComboBox<String> cmbGender;
    @FXML private TextField txtName;
    @FXML private TextField txtNic;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private DatePicker dpDob;
    @FXML private DatePicker dpRegistration;
    @FXML private TextArea txtAddress;
    @FXML private TextArea txtMedicalHistory;
    @FXML private TextField txtEmergencyName;
    @FXML private TextField txtEmergencyPhone;
    @FXML private TextField txtSearch;

    @FXML private Label lblNameError;
    @FXML private Label lblNicError;
    @FXML private Label lblEmailError;
    @FXML private Label lblPhoneError;
    @FXML private Label lblEmergencyPhoneError;
    @FXML private Label lblStatus;

    @FXML private TableView<PatientTM> tblPatients;
    @FXML private TableColumn<PatientTM, String> colId;
    @FXML private TableColumn<PatientTM, String> colName;
    @FXML private TableColumn<PatientTM, String> colNic;
    @FXML private TableColumn<PatientTM, String> colEmail;
    @FXML private TableColumn<PatientTM, String> colPhone;
    @FXML private TableColumn<PatientTM, String> colGender;
    @FXML private TableColumn<PatientTM, LocalDate> colRegDate;

    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOTypes.PATIENT);

    @FXML
    public void initialize() {
        cmbGender.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        dpRegistration.setValue(LocalDate.now());

        setupTableColumns();
        setupLiveValidation();
        loadTable();
        setNextId();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colNic.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colRegDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
    }

    private void setupLiveValidation() {
        txtName.focusedProperty().addListener((o, was, is) -> {
            if (!is) validateField(txtName, lblNameError,
                    ValidationUtil.isValidName(txtName.getText()), "Invalid name.");
        });
        txtNic.focusedProperty().addListener((o, was, is) -> {
            if (!is) validateField(txtNic, lblNicError,
                    ValidationUtil.isValidNic(txtNic.getText()), "Invalid NIC format.");
        });
        txtEmail.focusedProperty().addListener((o, was, is) -> {
            if (!is) validateField(txtEmail, lblEmailError,
                    ValidationUtil.isValidEmail(txtEmail.getText()), "Invalid email.");
        });
        txtPhone.focusedProperty().addListener((o, was, is) -> {
            if (!is) validateField(txtPhone, lblPhoneError,
                    ValidationUtil.isValidPhone(txtPhone.getText()), "Invalid phone (e.g. 0771234567).");
        });
        txtEmergencyPhone.focusedProperty().addListener((o, was, is) -> {
            if (!is) {
                String v = txtEmergencyPhone.getText();
                if (!v.isBlank()) {
                    validateField(txtEmergencyPhone, lblEmergencyPhoneError,
                            ValidationUtil.isValidPhone(v), "Invalid emergency phone.");
                }
            }
        });
    }

    @FXML
    private void handleSave() {
        if (!validateAll()) return;
        try {
            PatientDTO dto = buildDTO();
            dto.setPatientId(patientBO.getNextId());
            patientBO.savePatient(dto);
            showStatus("✅ Patient saved successfully.", false);
            loadTable();
            handleClear();
        } catch (DuplicateEntryException e) {
            showStatus("⚠ " + e.getMessage(), true);
        } catch (Exception e) {
            showStatus("❌ Error: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleUpdate() {
        if (txtPatientId.getText().isBlank()) {
            showStatus("⚠ Select a patient from the table first.", true);
            return;
        }
        if (!validateAll()) return;
        try {
            PatientDTO dto = buildDTO();
            patientBO.updatePatient(dto);
            showStatus("✅ Patient updated successfully.", false);
            loadTable();
        } catch (Exception e) {
            showStatus("❌ " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleDelete() {
        String id = txtPatientId.getText();
        if (id.isBlank()) {
            showStatus("⚠ Select a patient from the table first.", true);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete patient " + id + "? This will also remove their sessions and payments.",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                patientBO.deletePatient(id);
                showStatus("✅ Patient deleted.", false);
                loadTable();
                handleClear();
            } catch (Exception e) {
                showStatus("❌ " + e.getMessage(), true);
            }
        }
    }

    @FXML
    private void handleClear() {
        txtPatientId.clear();
        txtName.clear();
        txtNic.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtAddress.clear();
        txtMedicalHistory.clear();
        txtEmergencyName.clear();
        txtEmergencyPhone.clear();
        cmbGender.getSelectionModel().clearSelection();
        dpDob.setValue(null);
        dpRegistration.setValue(LocalDate.now());
        clearAllErrors();
        setNextId();
        lblStatus.setText("");
    }

    @FXML
    private void handleSearch() {
        String kw = txtSearch.getText().trim();
        try {
            List<PatientTM> rows;
            if (kw.isBlank()) {
                rows = toTM(patientBO.getAllPatients());
            } else {
                rows = toTM(patientBO.search(kw));
            }
            tblPatients.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            showStatus("❌ Search error: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleTableClick(MouseEvent event) {
        PatientTM selected = tblPatients.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            patientBO.getAllPatients().stream()
                    .filter(p -> p.getPatientId().equals(selected.getPatientId()))
                    .findFirst()
                    .ifPresent(this::populateForm);
        } catch (Exception e) {
            showStatus("❌ " + e.getMessage(), true);
        }
    }

    private void populateForm(PatientDTO dto) {
        txtPatientId.setText(dto.getPatientId());
        txtName.setText(dto.getName());
        txtNic.setText(dto.getNic());
        txtEmail.setText(dto.getEmail());
        txtPhone.setText(dto.getPhone());
        txtAddress.setText(dto.getAddress() != null ? dto.getAddress() : "");
        txtMedicalHistory.setText(dto.getMedicalHistory() != null ? dto.getMedicalHistory() : "");
        txtEmergencyName.setText(dto.getEmergencyContactName() != null ? dto.getEmergencyContactName() : "");
        txtEmergencyPhone.setText(dto.getEmergencyContactPhone() != null ? dto.getEmergencyContactPhone() : "");
        cmbGender.setValue(dto.getGender());
        dpDob.setValue(dto.getDateOfBirth());
        dpRegistration.setValue(dto.getRegistrationDate());
        clearAllErrors();
    }

    private PatientDTO buildDTO() {
        PatientDTO dto = new PatientDTO();
        dto.setPatientId(txtPatientId.getText().trim());
        dto.setName(txtName.getText().trim());
        dto.setNic(txtNic.getText().trim());
        dto.setEmail(txtEmail.getText().trim());
        dto.setPhone(txtPhone.getText().trim());
        dto.setGender(cmbGender.getValue());
        dto.setAddress(txtAddress.getText().trim());
        dto.setMedicalHistory(txtMedicalHistory.getText().trim());
        dto.setEmergencyContactName(txtEmergencyName.getText().trim());
        dto.setEmergencyContactPhone(txtEmergencyPhone.getText().trim());
        dto.setDateOfBirth(dpDob.getValue());
        dto.setRegistrationDate(dpRegistration.getValue() != null ? dpRegistration.getValue() : LocalDate.now());
        return dto;
    }

    private boolean validateAll() {
        boolean ok = true;
        ok &= validateField(txtName, lblNameError,
                ValidationUtil.isValidName(txtName.getText()), "Name is required (letters only).");

        ok &= validateField(txtNic, lblNicError,
                ValidationUtil.isValidNic(txtNic.getText()), "Invalid NIC (e.g. 200012345678).");

        ok &= validateField(txtEmail, lblEmailError,
                ValidationUtil.isValidEmail(txtEmail.getText()), "Invalid email address.");

        ok &= validateField(txtPhone, lblPhoneError,
                ValidationUtil.isValidPhone(txtPhone.getText()), "Invalid phone (e.g. 0771234567).");

        String ep = txtEmergencyPhone.getText().trim();

        if (!ep.isBlank()) {
            ok &= validateField(txtEmergencyPhone, lblEmergencyPhoneError,
                    ValidationUtil.isValidPhone(ep), "Invalid emergency phone.");
        }
        return ok;
    }

    private boolean validateField(Control field, Label errorLabel, boolean valid, String msg) {

        if (!valid) {
            field.setStyle(ValidationUtil.invalidStyle());
            errorLabel.setText(msg);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return false;
        }

        field.setStyle(ValidationUtil.validStyle());
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        return true;
    }

    private void clearAllErrors() {
        for (Label l : new Label[]{lblNameError, lblNicError, lblEmailError,
                                    lblPhoneError, lblEmergencyPhoneError}) {
            l.setVisible(false); l.setManaged(false);
        }
        for (Control c : new Control[]{txtName, txtNic, txtEmail, txtPhone, txtEmergencyPhone}) {
            c.setStyle(ValidationUtil.defaultStyle());
        }
    }

    private void loadTable() {
        try {
            tblPatients.setItems(FXCollections.observableArrayList(
                    toTM(patientBO.getAllPatients())));
        } catch (Exception e) {
            showStatus("❌ Failed to load patients: " + e.getMessage(), true);
        }
    }

    private List<PatientTM> toTM(List<PatientDTO> dtos) {
        return dtos.stream()
                .map(p -> new PatientTM(p.getPatientId(), p.getName(), p.getNic(),
                        p.getEmail(), p.getPhone(), p.getGender(), p.getRegistrationDate()))
                .collect(Collectors.toList());
    }

    private void setNextId() {
        try { txtPatientId.setText(patientBO.getNextId()); }
        catch (Exception e) { txtPatientId.setText("P001"); }
    }

    private void showStatus(String msg, boolean isError) {
        lblStatus.setText(msg);
        lblStatus.setStyle(isError ? "-fx-text-fill:#dc3545;" : "-fx-text-fill:#28a745;");
    }
}
