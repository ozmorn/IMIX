module imix.imix {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.swing;

    opens imix.imix to javafx.fxml;
    exports imix.imix;
}
