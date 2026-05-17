package lk.ijse.therapy_management_system.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.therapy_management_system.bo.BOFactory;
import lk.ijse.therapy_management_system.bo.BOTypes;
import lk.ijse.therapy_management_system.bo.custom.PatientBO;
import lk.ijse.therapy_management_system.bo.custom.PaymentBO;
import lk.ijse.therapy_management_system.bo.custom.TherapistBO;
import lk.ijse.therapy_management_system.bo.custom.TherapySessionBO;
import lk.ijse.therapy_management_system.dto.tm.TherapySessionTM;
import lk.ijse.therapy_management_system.util.SessionHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardHomeController {

    @FXML private Label lblWelcome;
    @FXML private Label lblDate;
    @FXML private Label lblPatientCount;
    @FXML private Label lblSessionCount;
    @FXML private Label lblTherapistCount;
    @FXML private Label lblRevenue;

    @FXML private TableView<TherapySessionTM>    tblRecentSessions;
    @FXML private TableColumn<TherapySessionTM, String>  colSessionId;
    @FXML private TableColumn<TherapySessionTM, String>  colPatientName;
    @FXML private TableColumn<TherapySessionTM, String>  colTherapistName;
    @FXML private TableColumn<TherapySessionTM, String>  colProgramName;
    @FXML private TableColumn<TherapySessionTM, String>  colSessionDate;
    @FXML private TableColumn<TherapySessionTM, String>  colStatus;

    private final PatientBO       patientBO  = BOFactory.getInstance().getBO(BOTypes.PATIENT);
    private final TherapySessionBO sessionBO = BOFactory.getInstance().getBO(BOTypes.THERAPY_SESSION);
    private final TherapistBO     therapistBO= BOFactory.getInstance().getBO(BOTypes.THERAPIST);
    private final PaymentBO       paymentBO  = BOFactory.getInstance().getBO(BOTypes.PAYMENT);

    @FXML
    public void initialize() {
        setupWelcome();
        setupTableColumns();
        loadStats();
        loadRecentSessions();
    }

    private void setupWelcome() {
        String name = SessionHolder.getLoggedInUser() != null
                ? SessionHolder.getLoggedInUser().getFullName() : "User";
        lblWelcome.setText("Welcome back, " + name + "!");
        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
    }

    private void setupTableColumns() {
        colSessionId.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colTherapistName.setCellValueFactory(new PropertyValueFactory<>("therapistName"));
        colProgramName.setCellValueFactory(new PropertyValueFactory<>("programName"));
        colSessionDate.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadStats() {
        try {
            lblPatientCount.setText(String.valueOf(patientBO.getAllPatients().size()));
            lblSessionCount.setText(String.valueOf(sessionBO.getAllSessions().size()));
            lblTherapistCount.setText(String.valueOf(therapistBO.getAllTherapists().size()));
            BigDecimal revenue = paymentBO.getTotalRevenue();
            lblRevenue.setText("LKR " + String.format("%,.0f", revenue));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecentSessions() {
        try {
            List<TherapySessionTM> rows = sessionBO.getAllSessions().stream()
                    .map(dto -> new TherapySessionTM(
                            dto.getSessionId(),
                            dto.getPatientName(),
                            dto.getTherapistName(),
                            dto.getProgramName(),
                            dto.getSessionDate(),
                            dto.getSessionTime(),
                            dto.getStatus()))
                    .collect(Collectors.toList());
            tblRecentSessions.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
