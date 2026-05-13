package lk.ijse.therapy_management_system.;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.therapy_management_system.config.FactoryConfiguration;
import lk.ijse.therapy_management_system.entity.TherapyProgram;
import lk.ijse.therapy_management_system.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.math.BigDecimal;

public class AppInitializer extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FactoryConfiguration.getInstance();
        seedDatabase();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Serenity Mental Health Therapy Center");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    @Override
    public void stop() {
        FactoryConfiguration.getInstance().shutdown();
    }

    private void seedDatabase() {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            Long programCount = session
                    .createQuery("SELECT COUNT(tp) FROM TherapyProgram tp", Long.class)
                    .uniqueResult();

            Long userCount = session
                    .createQuery("SELECT COUNT(u) FROM User u", Long.class)
                    .uniqueResult();

            if (userCount != null && userCount == 0) {
                Transaction tx = session.beginTransaction();
                try {
                    // --------------------- Default Admin ---------------------
                    User admin = new User();
                    admin.setUserId("U001");
                    admin.setUsername("admin");
                    admin.setPassword(BCrypt.withDefaults().hashToString(12, "Admin@123".toCharArray()));
                    admin.setRole(User.Role.ADMIN);
                    admin.setFullName("System Administrator");
                    admin.setEmail("admin@serenity.lk");
                    admin.setPhone("0771234567");
                    admin.setActive(true);
                    session.persist(admin);

                    // --------------------- Default Receptionist ---------------------
                    User receptionist = new User();
                    receptionist.setUserId("U002");
                    receptionist.setUsername("receptionist");
                    receptionist.setPassword(BCrypt.withDefaults().hashToString(12, "Recep@123".toCharArray()));
                    receptionist.setRole(User.Role.RECEPTIONIST);
                    receptionist.setFullName("Front Desk Receptionist");
                    receptionist.setEmail("reception@serenity.lk");
                    receptionist.setPhone("0777654321");
                    receptionist.setActive(true);
                    session.persist(receptionist);

                    tx.commit();
                    System.out.println("username: admin | password: Admin@123");
                    System.out.println("username: receptionist | password: Recep@123");
                } catch (Exception e) {
                    tx.rollback();
                    System.err.println("Failed to seed users: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}