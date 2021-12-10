package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {

    public Button singleBtn;
    public Button multipleBtn;
    public Button single_wBtn;
    public Button multiple_wBtn;

    public void create_frame_single(ActionEvent actionEvent) {
        create_frame(true,false);
    }

    public void create_frame_multiple(ActionEvent actionEvent) {
        create_frame(false,false);
    }

    public void create_frame_single_weighted(ActionEvent actionEvent) {
        create_frame(true,true);
    }

    public void create_frame_multiple_weighted(ActionEvent actionEvent) {
        create_frame(false,true);
    }

    public void create_frame(boolean single, boolean weighted){
        GraphController.single = single;
        GraphController.weighted = weighted;
        Stage other = new Stage();

        try {
            Scene graph = new Scene(FXMLLoader.load(getClass().getResource("schermata2.fxml")));
            other.setTitle("Graph");
            other.setScene(graph);
            other.setMinWidth(800);
            other.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
