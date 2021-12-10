package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class GraphController {

    public TextField njobs;
    public VBox jobBox;
    public HBox machineBox;
    public TextField macchine;

    public static boolean single;
    public static boolean weighted;
    public BorderPane borderPane;

    private ArrayList<TextField> jobsP;  //tempi di processamento dei job
    private ArrayList<TextField> weights;   //pesi dei job

    public void initialize(){
        machineBox.setVisible(!single);
    }

    public void create_jobs(ActionEvent actionEvent) {
        Integer jobs = 0;

        try {

            jobs = Integer.parseInt(njobs.getText());

            if (jobs <=1)
                throw new Exception("njobs must be grater tha 0: you have to schedule at least 2 job");
            //TODO: lanciare una finestra di warning

            this.jobsP = new ArrayList<>();
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
            this.jobsP.add(field);

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

    private void singleMachineInput(Integer njobs, ArrayList<Integer> p){
        //INPUT VALIDATION
        for (Integer t: p) {
            assert (t>0):"Process time not valid, it must be grater than 0";
        }
        Collections.sort(p);

        //CREATION FILE INPUT DZN
        try {
            FileWriter myInput = new FileWriter("jobSchedulingInput2.dzn");
            myInput.write("njobs="+njobs+";\n");
            myInput.write("p=[");
            for (int i=0; i<p.size()-1;i++)
                myInput.write(p.get(i)+", ");
            myInput.write(p.get(p.size()-1)+"];");
            myInput.close();
            System.out.println("Successfully wrote to the file single machine input");
        }catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    private void singleMachineWeightInput(Integer njobs, ArrayList<Integer> p, ArrayList<Integer> w){

    }

    private void multipleMachineInput(Integer njobs, Integer nmachines, ArrayList<Integer> p){

    }

    private void multipleMachineWeightInput(Integer njobs, Integer nmachines, ArrayList<Integer> p, ArrayList<Integer> w, ArrayList<Integer> wMachines){

    }

    public void execute(ActionEvent actionEvent) {

        Integer num = Integer.parseInt(njobs.getText());

        ArrayList<Integer> process = new ArrayList<>();
        for(TextField t : jobsP){
            process.add(Integer.parseInt(t.getText()));
        }

        //richiamo l'input adatto
        if(single && !weighted){
            singleMachineInput(num, process);
            String cmd = "C:\\Program Files\\MiniZinc\\minizinc.exe --solver Gecode jobSchedulingInput2.dzn jobScheduling_singleMachine.mzn";
            Runtime run = Runtime.getRuntime();
            Process pr = null;
            try {
                pr = run.exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                pr.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while (true) {
                try {
                    if (!((line=buf.readLine())!=null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(line);
            }
        }
        if(single && weighted) {
            ArrayList<Integer> w = new ArrayList<>();
            for(TextField t : weights){
                process.add(Integer.parseInt(t.getText()));
            }
            singleMachineWeightInput(num, process, w);

        }
        if(!single){
            Integer numMachines = Integer.parseInt(macchine.getText());
            if(!weighted){
                multipleMachineInput(num,numMachines,process);
            }
            else if(weighted) {
                ArrayList<Integer> w = new ArrayList<>();
                for(TextField t : weights){
                    process.add(Integer.parseInt(t.getText()));
                }
                ArrayList<Integer> wMchines = new ArrayList<>();
//                for(TextField t : weightsMachines){
//                    process.add(Integer.parseInt(t.getText()));
//                }
//                multipleMachineWeightInput(num,numMachines,process,w,wMachines);
            }
        }
        //TODO: ALTRI CASI CHE RICHIAMANO L'INPUT GIUSTO

        //prendi i dati dall'array
        //salva nel file data.dzn
        //chiama il processo da java es: 'C:\Program Files\MiniZinc\minizinc.exe --solver Gecode' nome_file_data.dzn nome_file_model.mzn
        //prendi l'output dal processo https://dzone.com/articles/execute-shell-command-java
        //parserizza l'output
        // chart


        //borderPane.setCenter();
        System.out.println("output di minizinc");
    }
}
