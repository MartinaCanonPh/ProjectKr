package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sun.rmi.runtime.Log;

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
    private ArrayList<TextField> machineWeights; //pesi delle macchine

    private FileWriter myInput;
    {
        try {
            myInput = new FileWriter("jobSchedulingInput.dzn");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize(){
        machineBox.setVisible(!single);
    }

    public void create_jobs(ActionEvent actionEvent) {
        Integer jobs = 0;

        try {

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
        Logic.validateProcessTime(p);

        //SORTING
        Logic.sortProcessTime(p);

        //CREATION FILE INPUT DZN
        Logic.writeProcessTime(myInput,p);
        try {
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void singleMachineWeightInput(Integer njobs, ArrayList<Integer> p, ArrayList<Integer> w){
        //INPUT VALIDATION
        Logic.validateProcessTime(p);
        Logic.validateWeights(w);

        //TODO: SORTING, corrispondenza di indice tra tempi e pesi da ordinare in base al p[i]/w[i] più piccolo
        Logic.sortJobsByProcessTimeOverWeights(p,w);

        //CREATION FILE INPUT DZN
        Logic.writeProcessTime(myInput,p);
        Logic.writeWeights(myInput,w);
        try {
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void multipleMachineInput(Integer njobs, Integer nmachines, ArrayList<Integer> p){

        //INPUT VALIDATION
        Logic.validateNumMachines(nmachines);
        Logic.validateProcessTime(p);

        //SORTING
        Logic.sortProcessTime(p);

        //CREATION FILE INPUT DZN
        Logic.writeNumMachines(myInput,nmachines);
        Logic.writeProcessTime(myInput,p);
        try {
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void multipleMachineWeightInput(Integer njobs, Integer nmachines, ArrayList<Integer> p, ArrayList<Integer> w, ArrayList<Integer> wMachines){

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
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runSolver(String s){
        String cmd = "C:\\Program Files\\MiniZinc\\minizinc.exe --solver Gecode jobSchedulingInput2.dzn jobScheduling_"+s+".mzn";
        Runtime run = Runtime.getRuntime();
        Process pr = null;
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            runSolver("singleMachine");
        }
        if(single && weighted) {
            ArrayList<Integer> w = new ArrayList<>();
            for(TextField t : weights){
                process.add(Integer.parseInt(t.getText()));
            }
            singleMachineWeightInput(num, process, w);
            runSolver("singleMachine_weight");
        }
        if(!single){
            Integer numMachines = Integer.parseInt(macchine.getText());
            if(!weighted){
                multipleMachineInput(num,numMachines,process);
                runSolver("multipleMachine");
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
                runSolver("multipleMachine_weight");
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
