package lk.ijse.therapy_management_system.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtil {

    public static void navigateTo(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.centerOnScreen();
    }

    /* Load an FXML into an AnchorPane in the Dashboard content area */
    public static void loadPage(AnchorPane contentArea, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        AnchorPane page = loader.load();
        contentArea.getChildren().clear();
        contentArea.getChildren().add(page);
        AnchorPane.setTopAnchor(page, 0.0);
        AnchorPane.setBottomAnchor(page, 0.0);
        AnchorPane.setLeftAnchor(page, 0.0);
        AnchorPane.setRightAnchor(page, 0.0);
    }

    /* Get the controller from the most recently loaded FXML */
    public static <T> T loadPageWithController(AnchorPane contentArea, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        AnchorPane page = loader.load();
        contentArea.getChildren().clear();
        contentArea.getChildren().add(page);
        AnchorPane.setTopAnchor(page, 0.0);
        AnchorPane.setBottomAnchor(page, 0.0);
        AnchorPane.setLeftAnchor(page, 0.0);
        AnchorPane.setRightAnchor(page, 0.0);
        return loader.getController();
    }
}
