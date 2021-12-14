package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
        //esegui.setDisable(true);
        machineBox.setVisible(!single);
    }

    public void checkNumber(KeyEvent event){
        if(event.getText() != null && event.getCode()!= KeyCode.ENTER && event.getCode()!= KeyCode.BACK_SPACE && event.getCode()!= KeyCode.ESCAPE
                && event.getCode()!= KeyCode.LEFT && event.getCode()!= KeyCode.RIGHT && event.getCode()!= KeyCode.DOWN && event.getCode()!= KeyCode.UP
                && event.getCode()!= KeyCode.TAB
                && (!event.getText().matches("[0-9]") || event.getText().equals(" "))){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Sono validi solo numeri!");

            alert.showAndWait();
            return;
        }
    }


    public void create_jobs(ActionEvent actionEvent) throws IOException {
        Integer jobs = 0;
        Integer machine=0;


        try {
            if(njobs.getText() != null && njobs.getText().isEmpty()){return;}
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
            field.setOnKeyPressed(this::checkNumber);

            field.setMaxWidth(40);
            this.jobsP.add(field);

            if (weighted) {
                TextField w = new TextField();
                w.setOnKeyPressed(this::checkNumber);
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
            if(macchine.getText().isEmpty()){
                showAlert();
                return;
            }
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
                field.setOnKeyPressed(this::checkNumber);
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

    private String runExample(String s)
    {
        String cmd = "C:\\Program Files\\MiniZinc\\minizinc.exe --solver Gecode example_"+s+".dzn jobScheduling_"+s+".mzn";
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
            BufferedReader bufferedReader = new BufferedReader(new FileReader("example_"+s+".dzn"));
            String line = "";
            while (true) {
                if (!((line=bufferedReader.readLine())!=null)) break;
                System.out.println(line);
                l+="\n"+line;
            }
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            while (true) {
                if (!((line=buf.readLine())!=null)) break;
                System.out.println(line);
                l+="\n"+line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l;
    }

    public void showExample(ActionEvent actionEvent){
        String example="";
        if(single && !weighted)
        {
            example = runExample("singleMachine");
        }else if(single && weighted){
            example = runExample("singleMachine_weight");
        }else if(!single && !weighted){
            example = runExample("multipleMachine");
        }else if(!single && weighted){
            example = runExample("multipleMachine_weight");
        }

        Text tx1 = new Text(example);

        tx1.setStroke(Color.RED);
        tx1.setFill(Color.WHITE);

        tx1.setFont(new Font(20));
        borderPane.setCenter(tx1);

    }

    private void showAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setContentText("Controlla di aver riempito correttamento tutti i campi.");

        alert.showAndWait();
    }

    public void execute(ActionEvent actionEvent) throws IOException {
            if(!areFilled()){
                return;
            }

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
                showsuggestion(output, "singleMachine");
            }
            if (single && weighted) {
                ArrayList<Integer> w = new ArrayList<>();

                for (int i = 0; i < weights.size(); i++) {
                    w.add(Integer.parseInt(weights.get(i).getText()));
                }

                singleMachineWeightInput(num, process, w);
                output = runSolver("singleMachine_weight");
                showsuggestion(output, "singleMachine_weight");
            }
            if (!single) {
                Integer numMachines = Integer.parseInt(macchine.getText());
                if (!weighted) {

                    multipleMachineInput(num, numMachines, process);
                    output = runSolver("multipleMachine");
                    showsuggestion(output, "multipleMachine");
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
                    showsuggestion(output, "multipleMachine_weight");
                }
            }

            Text tx1 = new Text(output);

            tx1.setStroke(Color.RED);
            tx1.setFill(Color.WHITE);

            tx1.setFont(new Font(20));
            borderPane.setCenter(tx1);
        }

        private boolean areFilled(){
            if(njobs != null && njobs.getText().isEmpty()){
                showAlert();
                return false;
            }
            for (TextField t : jobsP) {
                if(t != null && t.getText().isEmpty()){
                    showAlert();
                    return false;
                }
            }
            if(!single && macchine.getText().isEmpty()){
                showAlert();
                return false;
            }
            if(weighted){
                for (TextField t : weights) {
                    if(t.getText().isEmpty()){
                        showAlert();
                        return false;
                    }
                }
                if(!single){
                    for (TextField t : machineWeights) {
                        if(t.getText().isEmpty()){
                            showAlert();
                            return false;
                        }
                    }
                }

            }
            return true;
        }
    private void showsuggestion(String output ,String program){
        if((output.length()==24) ){
            String suggestion="";
            if(program.equals("singleMachine")){
                suggestion = "Prova a mettere valori non duplicati.";
            }else if(program.equals("singleMachine_weight")){
                suggestion = "Prova a mettere qualche valore di peso duplicato.";
            }else if(program.equals("multipleMachine")){
                suggestion = "Prova a diminuire il numero delle macchine o ad aumentare il numero di job.";
            }else if(program.equals("multipleMachine_weight")){
                suggestion = "Prova ad aumentare i pesi sulle macchine o a diminuire i pesi sui jobs.";
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Unsatisfiable");
            alert.setContentText(suggestion);
            alert.showAndWait();
        }
    }


}
