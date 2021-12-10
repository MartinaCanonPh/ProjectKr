package sample;

import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class GraphController {

    public TextField njobs;
    public VBox jobBox;
    public HBox machineBox;
    public TextField macchine;

    public static boolean single;
    public static boolean weighted;
    public BorderPane borderPane;

    private ArrayList<TextField> jobs;
    private ArrayList<TextField> weights;

    public void initialize(){
        machineBox.setVisible(!single);
    }

    public void create_jobs(ActionEvent actionEvent) {
        int jobs = 0;

        try {

            jobs = Integer.parseInt(njobs.getText());

            if (jobs <=0)
                throw new Exception("inserisci un numero >0");

            this.jobs = new ArrayList<>();
            if (weighted)
                weights = new ArrayList<>();

            jobBox.getChildren().clear();

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jobs; i++) {
            HBox nuovoJob = new HBox();
            nuovoJob.setSpacing(10);
            nuovoJob.setPadding(new Insets(0,0,0,5));

            TextField field = new TextField();
            field.setMaxWidth(40);
            this.jobs.add(field);

            if (weighted) {
                TextField w = new TextField();
                w.setMaxWidth(40);
                weights.add(w);
                nuovoJob.getChildren().addAll(new Label("Job "+(i+1)), field, new Label("W"+(i+1)), w);
            }else{
                nuovoJob.getChildren().addAll(new Label("Job "+(i+1)), field);
            }
            jobBox.getChildren().add(nuovoJob);
        }
    }

    public void execute(ActionEvent actionEvent) {
        //prendi i pesi dall'array
        //salva nel file data.dzn
        //chiama il processo da java es: 'C:\Program Files\MiniZinc\minizinc.exe --solver Gecode' nome_file_data.dzn nome_file_model.mzn
        //prendi l'output dal processo https://dzone.com/articles/execute-shell-command-java
        //parserizza l'output
        // chart


        //borderPane.setCenter();
        System.out.println("output di minizinc");
    }
}
