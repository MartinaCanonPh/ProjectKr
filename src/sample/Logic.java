package sample;

import com.sun.org.apache.xerces.internal.parsers.IntegratedParserConfiguration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

class Logic {

    //TODO: lanciare una finestra di warning per le funzione "validate"
    static final void validateNumJobs(Integer nJobs) {
        assert (nJobs>1):"The number of jobs must be greater than 1";

    }

    static final void validateProcessTime(ArrayList<Integer> p){
        for (Integer t: p) {
            assert (t>0):"Process time not valid, it must be grater than 0";
        }

    }

    static final void validateWeights(ArrayList<Integer> w){
        for(Integer x: w) {
            assert (x>0): "Weight not valid, it must be at least 1";
        }

    }

    static final void validateNumMachines(Integer nMachines){
        assert (nMachines>1):"The number of the machines must be greater than 1, otherwise choose single machine solver";

    }

    static final void validateMachinesWeights(ArrayList<Integer> wMachines){
        for(Integer x: wMachines) {
            assert (x>0): "Machine Weight not valid, it must be at least 1";
        }

    }

//    static final ArrayList<Integer> sortProcessTime(ArrayList<Integer> p){
//        Collections.sort(p);
//        return p;
//    }

    //TODO
    static final void sortJobsByProcessTimeOverWeights(ArrayList<Integer> p, ArrayList<Integer> w){
        //TODO: maybe we need a mapping
    }

    static final void writeNumJobs(FileWriter myInputFile, Integer nJobs){
        try {
            myInputFile.write("njobs="+nJobs+";\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final void writeProcessTime(FileWriter myInputFile, ArrayList<Integer> p){
        try {
            myInputFile.write("p=[");
            for (int i=0; i<p.size()-1;i++)
                myInputFile.write(p.get(i)+", ");
            myInputFile.write(p.get(p.size()-1)+"];");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static final void writeWeights(FileWriter myInputFile, ArrayList<Integer> w){

        try{
            myInputFile.write("w=[");
            for (int i=0; i<w.size()-1;i++)
                myInputFile.write(w.get(i)+", ");
            myInputFile.write(w.get(w.size()-1)+"];");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static final void writeNumMachines(FileWriter myInputFile, Integer nMachines){
        try {
            myInputFile.write("nmachines="+nMachines+";\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static final void writeMachinesWeights(FileWriter myInputFile, ArrayList<Integer> wMachines){
        try {
            myInputFile.write("wMachines=[");
            for (int i = 0; i < wMachines.size() - 1; i++)
                myInputFile.write(wMachines.get(i) + ", ");
            myInputFile.write(wMachines.get(wMachines.size() - 1) + "];");
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

}
