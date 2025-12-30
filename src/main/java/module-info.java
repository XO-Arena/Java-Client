module com.mycompany.java.client.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.desktop;

    opens com.mycompany.java.client.project to javafx.fxml;
    exports com.mycompany.java.client.project;
}
