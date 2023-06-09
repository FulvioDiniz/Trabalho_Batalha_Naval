module JavaFXApplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.base;
    requires java.sql;

    opens batalha_naval to javafx.fxml;
    opens batalha_naval.controller to javafx.fxml; 
    opens batalha_naval.model to javafx.base;
    opens batalha_naval.dao to javafx.fxml;
    opens batalha_naval.dao.core to javafx.fxml;   
   
    exports batalha_naval;
}
