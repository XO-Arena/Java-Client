module com.mycompany.java.client.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.desktop;
    requires javafx.swing;
    requires com.google.gson;
    
    opens com.mycompany.java.client.project to javafx.fxml;
    opens com.mycompany.java.client.project.data to com.google.gson;
    opens enums to com.google.gson;
    opens dto to com.google.gson;
    opens models to com.google.gson;
    exports com.mycompany.java.client.project;
    
}
