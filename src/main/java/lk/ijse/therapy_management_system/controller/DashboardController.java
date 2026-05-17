package lk.ijse.therapy_management_system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.therapy_management_system.dto.UserDTO;
import lk.ijse.therapy_management_system.entity.User;
import lk.ijse.therapy_management_system.util.NavigationUtil;
import lk.ijse.therapy_management_system.util.SessionHolder;

public class DashboardController {

    /* ------------------------ Sidebar buttons ------------------------ */
    @FXML private Label     lblUsername;
    @FXML private Label     lblRole;
    @FXML private Label     lblTopbarUser;
    @FXML private Label     lblPageTitle;
    @FXML private AnchorPane contentArea;

    @FXML private Label  lblAdminSection;
    @FXML private Button btnTherapists;
    @FXML private Button btnPrograms;
    @FXML private Button btnUsers;
    @FXML private Button btnDashboard;
    @FXML private Button btnPatients;
    @FXML private Button btnSessions;
    @FXML private Button btnPayments;
    @FXML private Button btnReports;
    @FXML private Button btnChangeCredentials;

    @FXML
    public void initialize() {
        UserDTO user = SessionHolder.getLoggedInUser();
        if (user == null) return;

        lblUsername.setText(user.getFullName());
        lblRole.setText(user.getRole().name());
        lblTopbarUser.setText("Hello, " + user.getFullName().split(" ")[0] + "  |  " + user.getRole());

        if (user.getRole() == User.Role.ADMIN) {
            lblAdminSection.setVisible(true);
            lblAdminSection.setManaged(true);

            btnTherapists.setVisible(true);
            btnTherapists.setManaged(true);

            btnPrograms.setVisible(true);
            btnPrograms.setManaged(true);

            btnUsers.setVisible(true);
            btnUsers.setManaged(true);
        }
        loadDashboard();
    }

    @FXML private void loadDashboard() {
        setActive(btnDashboard, "Dashboard");
        loadPage("/view/DashboardHome.fxml");
    }

    @FXML private void loadPatients() {
        setActive(btnPatients, "Patient Management");
        loadPage("/view/Patient.fxml");
    }

    @FXML private void loadSessions() {
        setActive(btnSessions, "Therapy Sessions");
        loadPage("/view/TherapySession.fxml");
    }

    @FXML private void loadPayments() {
        setActive(btnPayments, "Payments & Invoices");
        loadPage("/view/Payment.fxml");
    }

    @FXML private void loadTherapists() {
        setActive(btnTherapists, "Therapist Management");
        loadPage("/view/Therapist.fxml");
    }

    @FXML private void loadPrograms() {
        setActive(btnPrograms, "Therapy Programs");
        loadPage("/view/TherapyProgram.fxml");
    }

    @FXML private void loadUsers() {
        setActive(btnUsers, "User Management");
        loadPage("/view/UserManagement.fxml");
    }

    @FXML private void loadReports() {
        setActive(btnReports, "Reports & Analytics");
        loadPage("/view/Reports.fxml");
    }

    @FXML private void loadChangeCredentials() {
        setActive(btnChangeCredentials, "Change Credentials");
        loadPage("/view/ChangeCredentials.fxml");
    }

    @FXML private void handleLogout() {
        SessionHolder.clearSession();
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/view/Login.fxml",
                    "Serenity Mental Health Therapy Center");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ------------------------ Helpers ------------------------ */
    private void loadPage(String fxmlPath) {
        try {
            NavigationUtil.loadPage(contentArea, fxmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActive(Button activeBtn, String pageTitle) {
        Button[] allBtns = {
                btnDashboard,
                btnPatients,
                btnSessions,
                btnPayments,
                btnTherapists,
                btnPrograms,
                btnUsers,
                btnReports,
                btnChangeCredentials
        };
        for (Button btn : allBtns) {
            if (btn != null) {
                btn.getStyleClass().remove("nav-btn-active");
                if (!btn.getStyleClass().contains("nav-btn")) {
                    btn.getStyleClass().add("nav-btn");
                }
            }
        }
        activeBtn.getStyleClass().add("nav-btn-active");
        lblPageTitle.setText(pageTitle);
    }
}
