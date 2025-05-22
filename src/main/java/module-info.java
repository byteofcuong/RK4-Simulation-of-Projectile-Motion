module com.npc.test_rk4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.npc.rk4 to javafx.fxml;
    exports com.npc.rk4;
    exports com.npc.rk4.Controllers;
    exports com.npc.rk4.Models;
    exports com.npc.rk4.Views;
}