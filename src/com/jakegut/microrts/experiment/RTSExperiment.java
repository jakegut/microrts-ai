package com.jakegut.microrts.experiment;

import ai.RandomBiasedAI;
import ai.core.AI;
import ai.montecarlo.MonteCarlo;
import com.jakegut.microrts.fitness.RTSFitnessFunction;
import com.jakegut.microrts.fitness.TerminalFitnessFunction;
import com.jakegut.microrts.util.Pair;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

import java.util.Arrays;


public class RTSExperiment implements Experiment {

    private static final int MAX_CYCLES = 5000;

    private final AI ai1;
    private final AI ai2;
    private final RTSFitnessFunction fitnessFunction;

    private Pair<double[], double[]> finalFitness;

    public static UnitTypeTable utt = new UnitTypeTable();
    PhysicalGameState pgs;

    public RTSExperiment(AI ai1, RTSFitnessFunction fitnessFunction){
        this(ai1, new RandomBiasedAI(), fitnessFunction);
    }

    public RTSExperiment(AI ai1, AI ai2, RTSFitnessFunction fitnessFunction){
        try {
            this.pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8.xml", utt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.ai1 = ai1;
        this.ai2 = ai2;
        this.fitnessFunction = fitnessFunction;
        this.fitnessFunction.setMaxCycles(MAX_CYCLES);
    }

    @Override
    public void run() {
        GameState gs = new GameState(pgs, utt);
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

//        List<AI> aiBots = new LinkedList<>(Arrays.asList(ai1, ai2));
//        List<PhysicalGameState> pgss = new LinkedList<>(Collections.singletonList(pgs));
//
//        try {
//            Experimenter.runExperiments(aiBots, pgss, utt, 2, 1000, 100, false, System.out, -1, true, false, true, true, "tests") ;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public int getFitness() {
        System.out.println(Arrays.toString(finalFitness.t1));
        System.out.println(Arrays.toString(finalFitness.t2));
        return 0;
    }

    public static void main(String[] args){
        RTSExperiment experiment = new RTSExperiment(new MonteCarlo(utt), new TerminalFitnessFunction());
        experiment.run();
        experiment.getFitness();
    }
}
