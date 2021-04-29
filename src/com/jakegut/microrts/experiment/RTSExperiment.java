package com.jakegut.microrts.experiment;

import ai.RandomBiasedAI;
import ai.abstraction.WorkerRush;
import ai.core.AI;
import ai.montecarlo.MonteCarlo;
import com.jakegut.microrts.fitness.RTSFitnessFunction;
import com.jakegut.microrts.fitness.TerminalFitnessFunction;
import com.jakegut.microrts.util.Pair;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;

import java.util.Arrays;


public class RTSExperiment implements Experiment, Runnable {

    private static final int MAX_CYCLES = 5000;

    private final AI ai1;
    private final AI ai2;
    private final RTSFitnessFunction fitnessFunction;
    private String map = "maps/8x8/basesWorkers8x8.xml";

    private Pair<double[], double[]> finalFitness;

    public UnitTypeTable utt;
    PhysicalGameState pgs;

    private double[] fitness;
    private int fitnessIndex;
    private final Object mutex;

    public RTSExperiment(AI ai1, RTSFitnessFunction fitnessFunction, double[] fitness, int fitnessIndex, Object mutex, UnitTypeTable utt){
        this(ai1, new MonteCarlo(utt), fitnessFunction, fitness, fitnessIndex, mutex, utt);
    }

    public RTSExperiment(AI ai1, AI ai2, RTSFitnessFunction fitnessFunction, double[] fitness, int fitnessIndex, Object mutex, UnitTypeTable utt){
        this.utt = utt;
        try {
            this.pgs = PhysicalGameState.load(map, utt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.ai1 = ai1;
        this.ai2 = ai2;
        this.fitnessFunction = fitnessFunction;
        this.fitnessFunction.setMaxCycles(MAX_CYCLES);
        this.fitness = fitness;
        this.fitnessIndex = fitnessIndex;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        GameState gs = new GameState(pgs, utt);
        System.out.println(ai1.getClass().getSimpleName());
        System.out.println(ai2.getClass().getSimpleName());
        boolean gameover;
        int numCycles = 0;
        do{
            PlayerAction pa1, pa2;
            try {
                pa1 = ai1.getAction(0, gs);
                pa2 = ai2.getAction(1, gs);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            gs.issueSafe(pa1);
            gs.issueSafe(pa2);
            gameover = gs.cycle();
            numCycles++;
        }while(!gameover && numCycles < MAX_CYCLES);
        try {
            ai1.gameOver(gs.winner());
            ai2.gameOver(gs.winner());
        } catch (Exception e) {
            e.printStackTrace();
        }

        finalFitness = fitnessFunction.getFitness(gs);

        if(mutex != null)
            synchronized (mutex){
                fitness[fitnessIndex] = this.getFitness();
            }

        System.gc();

//        List<AI> aiBots = new LinkedList<>(Arrays.asList(ai1, ai2));
//        List<PhysicalGameState> pgss = new LinkedList<>(Collections.singletonList(pgs));
//
//        try {
//            Experimenter.runExperiments(aiBots, pgss, utt, 2, 1000, 100, false, System.out, -1, true, false, true, true, "tests") ;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void setMap(String map){
        this.map = map;
    }

    @Override
    public double getFitness() {
        assert finalFitness != null;
        System.out.println(Arrays.toString(finalFitness.t1));
        double fit = 1;
        for(double value : finalFitness.t1)
            fit *= value;
        return -fit;
    }

    public static void main(String[] args){
        UnitTypeTable utt = new UnitTypeTable();
        RTSExperiment experiment = new RTSExperiment(new MonteCarlo(utt), new TerminalFitnessFunction(), null, 0, null, utt);
        experiment.run();
        System.out.println(experiment.getFitness());
    }
}
