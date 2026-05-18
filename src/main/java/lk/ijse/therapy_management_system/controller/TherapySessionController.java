package lk.ijse.therapy_management_system.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import lk.ijse.therapy_management_system.bo.BOFactory;
import lk.ijse.therapy_management_system.bo.BOTypes;
import lk.ijse.therapy_management_system.bo.custom.PatientBO;
import lk.ijse.therapy_management_system.bo.custom.TherapistBO;
import lk.ijse.therapy_management_system.bo.custom.TherapyProgramBO;
import lk.ijse.therapy_management_system.bo.custom.TherapySessionBO;
import lk.ijse.therapy_management_system.dto.TherapistDTO;
import lk.ijse.therapy_management_system.dto.TherapySessionDTO;
import lk.ijse.therapy_management_system.dto.tm.TherapySessionTM;
import lk.ijse.therapy_management_system.entity.TherapySession;
import lk.ijse.therapy_management_system.exception.ScheduleConflictException;
import lk.ijse.therapy_management_system.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


public class TherapySessionController {

    @FXML private TextField          txtSessionId;
    @FXML private ComboBox<String>   cmbPatient;
    @FXML private ComboBox<String>   cmbProgram;
    @FXML private ComboBox<String>   cmbTherapist;
    @FXML private DatePicker         dpSessionDate;
    @FXML private TextField          txtSessionTime;
    @FXML private ComboBox<String>   cmbStatus;
    @FXML private TextArea           txtNotes;
    @FXML private TextField          txtSearch;

    @FXML private Label lblPatientError;
    @FXML private Label lblTherapistError;
    @FXML private Label lblDateError;
    @FXML private Label lblTimeError;
    @FXML private Label lblStatus;

    @FXML private TableView<TherapySessionTM>               tblSessions;
    @FXML private TableColumn<TherapySessionTM, String>     colId;
    @FXML private TableColumn<TherapySessionTM, String>     colPatientName;
    @FXML private TableColumn<TherapySessionTM, String>     colTherapist;
    @FXML private TableColumn<TherapySessionTM, String>     colProgram;
    @FXML private TableColumn<TherapySessionTM, LocalDate>  colDate;
    @FXML private TableColumn<TherapySessionTM, LocalTime>  colTime;
    @FXML private TableColumn<TherapySessionTM, String>     colStatus;

    private final TherapySessionBO sessionBO   = BOFactory.getInstance().getBO(BOTypes.THERAPY_SESSION);
    private final PatientBO        patientBO   = BOFactory.getInstance().getBO(BOTypes.PATIENT);
    private final TherapistBO      therapistBO = BOFactory.getInstance().getBO(BOTypes.THERAPIST);
    private final TherapyProgramBO programBO   = BOFactory.getInstance().getBO(BOTypes.THERAPY_PROGRAM);

    private final Map<String, String> patientMap   = new LinkedHashMap<>();
    private final Map<String, String> programMap   = new LinkedHashMap<>();
    private final Map<String, String> therapistMap = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        setupTableColumns();
        cmbStatus.setItems(FXCollections.observableArrayList(
                Arrays.stream(TherapySession.SessionStatus.values())
                        .map(Enum::name).collect(Collectors.toList())));
        cmbStatus.setValue(TherapySession.SessionStatus.SCHEDULED.name());

        dpSessionDate.setValue(LocalDate.now());

        loadPatientCombo();
        loadProgramCombo();
        loadTable();
        setNextId();
    }

    private void loadPatientCombo() {
        patientMap.clear();
        patientBO.getAllPatients().forEach(p ->
                patientMap.put(p.getPatientId() + " – " + p.getName(), p.getPatientId()));
        cmbPatient.setItems(FXCollections.observableArrayList(patientMap.keySet()));
    }

    private void loadProgramCombo() {
        programMap.clear();
        programBO.getActivePrograms().forEach(p ->
                programMap.put(p.getProgramId() + " – " + p.getProgramName(), p.getProgramId()));
        cmbProgram.setItems(FXCollections.observableArrayList(programMap.keySet()));
    }

    @FXML
    private void handleProgramChanged() {
        String key = cmbProgram.getValue();
        if (key == null) return;
        String programId = programMap.get(key);
        therapistMap.clear();
        try {
            List<TherapistDTO> list = therapistBO.getTherapistsByProgram(programId);
            if (list.isEmpty()) {
                list = therapistBO.getAvailableTherapists(); // fallback: all active
            }
            list.forEach(t -> therapistMap.put(t.getTherapistId() + " – " + t.getName(), t.getTherapistId()));
            cmbTherapist.setItems(FXCollections.observableArrayList(therapistMap.keySet()));
        } catch (Exception e) {
            showStatus("Could not load therapists: " + e.getMessage(), true);
        }
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colTherapist.setCellValueFactory(new PropertyValueFactory<>("therapistName"));
        colProgram.setCellValueFactory(new PropertyValueFactory<>("programName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("sessionTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    @FXML
    private void handleBook() {
        if (!validateAll()) return;
        try {
            TherapySessionDTO dto = buildDTO();
            dto.setSessionId(sessionBO.getNextId());
            dto.setRegistrationDate(LocalDate.now());
            sessionBO.bookSession(dto);
            showStatus("✅ Session booked successfully.", false);
            loadTable();
            handleClear();
        } catch (ScheduleConflictException e) {
            showStatus("⚠ Conflict: " + e.getMessage(), true);
        } catch (Exception e) {
            showStatus("❌ Error: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleUpdate() {
        if (txtSessionId.getText().isBlank()) {
            showStatus("⚠ Select a session from the table first.", true);
            return;
        }
        if (!validateAll()) return;
        try {
            TherapySessionDTO dto = buildDTO();
            sessionBO.updateSession(dto);
            showStatus("✅ Session updated.", false);
            loadTable();
        } catch (ScheduleConflictException e) {
            showStatus("⚠ Conflict: " + e.getMessage(), true);
        } catch (Exception e) {
            showStatus("❌ " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleCancel() {
        String id = txtSessionId.getText();
        if (id.isBlank()) {
            showStatus("⚠ Select a session first.", true);
            return;
        }
        Optional<ButtonType> r = new Alert(Alert.AlertType.CONFIRMATION,
                "Cancel session " + id + "?", ButtonType.YES, ButtonType.NO).showAndWait();
        if (r.isPresent() && r.get() == ButtonType.YES) {
            try {
                sessionBO.cancelSession(id);
                showStatus("✅ Session cancelled.", false);
                loadTable();
                handleClear();
            } catch (Exception e) {
                showStatus("❌ " + e.getMessage(), true);
            }
        }
    }

    @FXML
    private void handleClear() {
        txtSessionId.clear();
        cmbPatient.getSelectionModel().clearSelection();
        cmbProgram.getSelectionModel().clearSelection();
        cmbTherapist.getSelectionModel().clearSelection();
        cmbTherapist.setItems(FXCollections.emptyObservableList());
        dpSessionDate.setValue(LocalDate.now());
        txtSessionTime.clear();
        txtNotes.clear();
        cmbStatus.setValue(TherapySession.SessionStatus.SCHEDULED.name());
        clearErrors();
        setNextId();
        lblStatus.setText("");
    }

    @FXML
    private void handleSearch() {
        String kw = txtSearch.getText().trim().toLowerCase();
        try {
            List<TherapySessionTM> rows = toTM(sessionBO.getAllSessions());
            if (!kw.isBlank()) {
                rows = rows.stream().filter(s ->
                        s.getPatientName().toLowerCase().contains(kw) ||
                        s.getTherapistName().toLowerCase().contains(kw) ||
                        s.getProgramName().toLowerCase().contains(kw) ||
                        s.getSessionId().toLowerCase().contains(kw))
                        .collect(Collectors.toList());
            }
            tblSessions.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            showStatus("❌ Search error: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleTableClick(MouseEvent event) {
        TherapySessionTM sel = tblSessions.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            sessionBO.getAllSessions().stream()
                    .filter(s -> s.getSessionId().equals(sel.getSessionId()))
                    .findFirst()
                    .ifPresent(this::populateForm);
        } catch (Exception e) {
            showStatus("❌ " + e.getMessage(), true);
        }
    }

    private void populateForm(TherapySessionDTO dto) {
        txtSessionId.setText(dto.getSessionId());
        patientMap.entrySet().stream()
                .filter(e -> e.getValue().equals(dto.getPatientId()))
                .findFirst().ifPresent(e -> cmbPatient.setValue(e.getKey()));
        programMap.entrySet().stream()
                .filter(e -> e.getValue().equals(dto.getProgramId()))
                .findFirst().ifPresent(e -> {
                    cmbProgram.setValue(e.getKey());
                    handleProgramChanged();
                });
        therapistMap.entrySet().stream()
                .filter(e -> e.getValue().equals(dto.getTherapistId()))
                .findFirst().ifPresent(e -> cmbTherapist.setValue(e.getKey()));
        dpSessionDate.setValue(dto.getSessionDate());
        txtSessionTime.setText(dto.getSessionTime() != null ? dto.getSessionTime().toString() : "");
        cmbStatus.setValue(dto.getStatus() != null ? dto.getStatus().name() : TherapySession.SessionStatus.SCHEDULED.name());
        txtNotes.setText(dto.getNotes() != null ? dto.getNotes() : "");
        clearErrors();
    }

    private TherapySessionDTO buildDTO() {
        TherapySessionDTO dto = new TherapySessionDTO();
        dto.setSessionId(txtSessionId.getText());
        dto.setPatientId(patientMap.get(cmbPatient.getValue()));
        dto.setProgramId(programMap.get(cmbProgram.getValue()));
        dto.setTherapistId(therapistMap.get(cmbTherapist.getValue()));
        dto.setSessionDate(dpSessionDate.getValue());
        dto.setSessionTime(LocalTime.parse(txtSessionTime.getText()));
        dto.setStatus(TherapySession.SessionStatus.valueOf(cmbStatus.getValue()));
        dto.setNotes(txtNotes.getText());
        return dto;
    }

    private boolean validateAll() {
        boolean ok = true;
        if (cmbPatient.getValue() == null) {
            setErr(lblPatientError, "Patient is required."); ok = false;
        } else {
            clrErr(lblPatientError);
        }

        if (cmbTherapist.getValue() == null) {
            setErr(lblTherapistError, "Therapist is required."); ok = false;
        } else {
            clrErr(lblTherapistError);
        }

        if (dpSessionDate.getValue() == null) {
            setErr(lblDateError, "Session date is required."); ok = false;
        } else {
            clrErr(lblDateError);
        }

        String timeText = txtSessionTime.getText().trim();
        if (timeText.isBlank()) {
            setErr(lblTimeError, "Session time is required."); ok = false;
        } else {
            try {
                LocalTime.parse(timeText);
                clrErr(lblTimeError);
                txtSessionTime.setStyle(ValidationUtil.validStyle());
            } catch (DateTimeParseException e) {
                setErr(lblTimeError, "Use proper format (e.g. 09:00).");
                txtSessionTime.setStyle(ValidationUtil.invalidStyle());
                ok = false;
            }
        }
        return ok;
    }

    private void loadTable() {
        try {
            tblSessions.setItems(FXCollections.observableArrayList(toTM(sessionBO.getAllSessions())));
        } catch (Exception e) {
            showStatus("❌ Failed to load sessions: " + e.getMessage(), true);
        }
    }

    private List<TherapySessionTM> toTM(List<TherapySessionDTO> dtos) {
        return dtos.stream().map(s -> new TherapySessionTM(
                s.getSessionId(), s.getPatientName(), s.getTherapistName(),
                s.getProgramName(), s.getSessionDate(), s.getSessionTime(), s.getStatus()))
                .collect(Collectors.toList());
    }

    private void setNextId() {

        try {
            txtSessionId.setText(sessionBO.getNextId());
        } catch (Exception e) {
            txtSessionId.setText("SS001");
        }
    }

    private void clearErrors() {
        for (Label l : new Label[]{lblPatientError, lblTherapistError, lblDateError, lblTimeError}) {
            clrErr(l);
        }
    }

    private void setErr(Label l, String msg) {
        l.setText(msg); l.setVisible(true); l.setManaged(true);
    }

    private void clrErr(Label l) {
        l.setVisible(false); l.setManaged(false);
    }

    private void showStatus(String m, boolean err) {
        lblStatus.setText(m);
        lblStatus.setStyle(err ? "-fx-text-fill:#dc3545;" : "-fx-text-fill:#28a745;");
    }
}
