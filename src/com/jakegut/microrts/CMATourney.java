package com.jakegut.microrts;

import ai.RandomAI;
import ai.RandomBiasedAI;
import ai.abstraction.partialobservability.POLightRush;
import ai.coac.CoacAI;
import ai.core.AI;
import ai.montecarlo.MonteCarlo;
import com.jakegut.microrts.ai.CMAESMCTS;
import com.jakegut.microrts.experiment.RTSExperiment;
import com.jakegut.microrts.fitness.TerminalFitnessFunction;
import rts.units.UnitTypeTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CMATourney {

    private AI ai1;
    private AI[] ai2s;

//    private int numRounds;

    double[][] fitnesses;

    private String[] maps;

    private UnitTypeTable utt;


    public CMATourney(AI ai1, AI[] ai2s, String[] maps, UnitTypeTable utt){
        this.ai1 = ai1;
        this.ai2s = ai2s;
        this.maps = maps;

        this.fitnesses = new double[ai2s.length][maps.length];
        this.utt = utt;
    }

    public void run(){
        Object object = new Object();
        for(int i = 0; i < ai2s.length; i++){
            ExecutorService executor = Executors.newFixedThreadPool(maps.length/2+1);
            for(int j = 0; j < maps.length; j++){
                RTSExperiment exp = new RTSExperiment(ai1.clone(), ai2s[i].clone(), new TerminalFitnessFunction(), fitnesses[i], j, object, utt);
                exp.setMap(maps[j]);
                executor.execute(exp);
            }
            executor.shutdown();
            while(!executor.isTerminated()){}

            System.out.println(Arrays.toString(fitnesses[i]));
        }

    }

    public void toCSV(PrintWriter writer){
        writer.println("AI1, AI2, map, fitness");
        for(int i = 0; i < ai2s.length; i++){
            for(int j = 1; j <= maps.length; j++){
                writer.print(ai1.getClass().getSimpleName() +", ");
                writer.print(ai2s[i].getClass().getSimpleName() +", ");
                writer.print(maps[j-1] + ", ");
                writer.println(fitnesses[i][j-1]);
            }
        }
        writer.close();
    }

    public static void main(String[] args){
        double[] weights = new double[]{0.4080106698079029, -0.4485600777879678, 0.624138883587953, 0.14364300341258796, 0.23777625609798816, 0.24745954474957693};
        UnitTypeTable utt = new UnitTypeTable();
        AI ai1 = new CMAESMCTS(weights);
//        AI ai2 = new RandomBiasedAI(utt);
//
//        try {
//            GameVisualSimulationTest.runGameTest(ai1, utt, ai2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        AI[] ai2s = new AI[]{
                new RandomAI(),
                new RandomBiasedAI(),
                new MonteCarlo(utt),
//                new WorkerRush(utt),
//                new WorkerDefense(utt),
                new CoacAI(utt),
                new POLightRush(utt),
        };
//
        String[] maps = new String[]{
                "maps/8x8/basesWorkers8x8A.xml",
                "maps/16x16/basesWorkers16x16A.xml",
                "maps/BWDistantResources32x32.xml",
                "maps/BroodWar/(4)BloodBath.scmB.xml",
                "maps/8x8/FourBasesWorkers8x8.xml",
                "maps/16x16/TwoBasesBarracks16x16.xml",
                "maps/NoWhereToRun9x8.xml",
                "maps/DoubleGame24x24.xml"
        };

        CMATourney tourney = new CMATourney(ai1, ai2s, maps, utt);
        tourney.run();
        PrintWriter writer = null;
        try{
            File file = new File("output/tourney_" + System.currentTimeMillis() + ".csv");
            file.createNewFile();
            writer = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert writer != null;
        tourney.toCSV(writer);
    }

}
