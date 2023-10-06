module com.java.cloudStore.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.codec;
    requires com.java.cloudStore;


    opens com.java.cloudStore.client to javafx.fxml;
    exports com.java.cloudStore.client;
}