package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sun.rmi.runtime.Log;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphController {

    public TextField njobs;
    public VBox jobBox;
    public HBox machineBox;
    public TextField macchine;
    public VBox machineWeightBox;
    public static boolean single;
    public static boolean weighted;
    public BorderPane borderPane;

    private ArrayList<TextField> jobsP;  //tempi di processamento dei job
    private ArrayList<TextField> weights;   //pesi dei job
    @FXML
    private ArrayList<TextField> machineWeights; //pesi delle macchine
    public FileWriter myInput;
    {
        try {
            myInput = new FileWriter("jobSchedulingInput.dzn",false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void initialize(){
        machineBox.setVisible(!single);
    }

    public void create_jobs(ActionEvent actionEvent) throws IOException {
        Integer jobs = 0;
        Integer machine=0;


        try {
            jobs = Integer.parseInt(njobs.getText());
            Logic.validateNumJobs(Integer.parseInt(njobs.getText()));
            Logic.writeNumJobs(myInput,Integer.parseInt(njobs.getText()));

            this.jobsP = new ArrayList<>();
            if (weighted)
                weights = new ArrayList<>();
            if(weighted && macchine.getText()!=null)
                machineWeights = new ArrayList<>();

            jobBox.getChildren().clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(jobs<=1){
            jobs=0;

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setContentText("Inserisci un numero di jobs superiori ad uno!");

                alert.showAndWait();
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
    public void create_machine(ActionEvent actionEvent) {
        Integer machine=0;
        try {
            machine = Integer.parseInt(macchine.getText());


            this.machineWeights = new ArrayList<>();
            machineWeightBox.getChildren().clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(machine<=1){
            machine=0;

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");

            alert.setContentText("Inserisci un numero di macchine superiori ad 1!");

            alert.showAndWait();
        }
        if(weighted) {

            for (int i = 0; i < machine; i++) {
                HBox nuovoMachine = new HBox();
                nuovoMachine.setSpacing(10);
                nuovoMachine.setPadding(new Insets(0, 0, 0, 5));

                TextField field = new TextField();
                field.setMaxWidth(40);
                this.machineWeights.add(field);


                nuovoMachine.getChildren().addAll(new Label("M " + (i + 1)), field);


                machineWeightBox.getChildren().add(nuovoMachine);
            }
        }

    }
    private void singleMachineInput(Integer njobs, ArrayList<Integer> p) throws IOException {
        //INPUT VALIDATION
        Logic.validateProcessTime(p);

        //SORTING
        Collections.sort(p);

        //CREATION FILE INPUT DZN
        Logic.writeProcessTime(myInput,p);
        try {
            myInput.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void singleMachineWeightInput(Integer njobs, ArrayList<Integer> p, ArrayList<Integer> w) throws IOException {
        //INPUT VALIDATION

        Logic.validateProcessTime(p);
        Logic.validateWeights(w);

        //TODO: SORTING, corrispondenza di indice tra tempi e pesi da ordinare in base al p[i]/w[i] più piccolo
        Logic.sortJobsByProcessTimeOverWeights(p,w);

        //CREATION FILE INPUT DZN

        Logic.writeProcessTime(myInput,p);
        Logic.writeWeights(myInput,w);

        try {
            myInput.write("");
            myInput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void multipleMachineInput(Integer njobs, Integer nmachines, ArrayList<Integer> p) throws IOException {

        //INPUT VALIDATION
        Logic.validateNumMachines(nmachines);
        Logic.validateProcessTime(p);

        //SORTING
        Collections.sort(p);

        //CREATION FILE INPUT DZN
        Logic.writeNumMachines(myInput,nmachines);
        Logic.writeProcessTime(myInput,p);

        try {
            myInput.write("");
            myInput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void multipleMachineWeightInput(Integer njobs, Integer nmachines, ArrayList<Integer> p, ArrayList<Integer> w, ArrayList<Integer> wMachines) throws IOException {

        //INPUT VALIDATION
        Logic.validateNumMachines(nmachines);
        Logic.validateProcessTime(p);
        Logic.validateWeights(w);
        Logic.validateMachinesWeights(wMachines);

        //TODO: SORTING
        Logic.sortJobsByProcessTimeOverWeights(p,w);

        //CREATION FILE INPUT DZN
        Logic.writeNumMachines(myInput,nmachines);
        Logic.writeProcessTime(myInput,p);
        Logic.writeWeights(myInput,w);
        Logic.writeMachinesWeights(myInput,wMachines);

        try {
            myInput.write("");
            myInput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  String runSolver(String s){
        String cmd = "C:\\Program Files\\MiniZinc\\minizinc.exe --solver Gecode jobSchedulingInput.dzn jobScheduling_"+s+".mzn";
        Runtime run = Runtime.getRuntime();
        Process pr = null;
        String l = "";
        try {
            pr = run.exec(cmd);
            try {
                pr.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while (true) {
                    if (!((line=buf.readLine())!=null)) break;
                System.out.println(line);
                l+="\n"+line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(l);
    return l;
    }

    public void execute(ActionEvent actionEvent) throws IOException {

            Integer num = Integer.parseInt(njobs.getText());
            String output = "";
            ArrayList<Integer> process = new ArrayList<>();
            for (TextField t : jobsP) {
                process.add(Integer.parseInt(t.getText()));
            }

            //richiamo l'input adatto
            if (single && !weighted) {

                singleMachineInput(num, process);
                output = runSolver("singleMachine");
            }
            if (single && weighted) {
                ArrayList<Integer> w = new ArrayList<>();

            /*for(TextField t : weights){
                process.add(Integer.parseInt(t.getText()));
            }*/

                for (int i = 0; i < weights.size(); i++) {
                    w.add(Integer.parseInt(weights.get(i).getText()));
                }


                singleMachineWeightInput(num, process, w);
                output = runSolver("singleMachine_weight");
            }
            if (!single) {
                Integer numMachines = Integer.parseInt(macchine.getText());
                if (!weighted) {

                    multipleMachineInput(num, numMachines, process);
                    output = runSolver("multipleMachine");

                } else if (weighted) {
                    ArrayList<Integer> w = new ArrayList<>();
                    for (TextField t : weights) {
                        w.add(Integer.parseInt(t.getText()));
                    }
                    ArrayList<Integer> wMachines = new ArrayList<>();
                    for (TextField t : machineWeights) {
                        wMachines.add(Integer.parseInt(t.getText()));
                    }
                    multipleMachineWeightInput(num, numMachines, process, w, wMachines);
                    output = runSolver("multipleMachine_weight");
                    showsuggestion(output);
                }
            }
            //TODO: ALTRI CASI CHE RICHIAMANO L'INPUT GIUSTO

            //prendi i dati dall'array
            //salva nel file data.dzn
            //chiama il processo da java es: 'C:\Program Files\MiniZinc\minizinc.exe --solver Gecode' nome_file_data.dzn nome_file_model.mzn
            //prendi l'output dal processo https://dzone.com/articles/execute-shell-command-java
            //parserizza l'output
            // chart

            Text tx1 = new Text(output);
//set text color
            tx1.setStroke(Color.RED);
            tx1.setFill(Color.WHITE);
//set text font size
            tx1.setFont(new Font(10));
            borderPane.setCenter(tx1);


        }
private void showsuggestion(String output){
        if(output.length()==24) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("prova a cambiare qualche campo !!");
            alert.showAndWait();
        }

    }
}
