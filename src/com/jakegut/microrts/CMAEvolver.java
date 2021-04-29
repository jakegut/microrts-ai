package com.jakegut.microrts;

import ai.core.AI;
import com.jakegut.microrts.ai.CMAESMCTS;
import com.jakegut.microrts.experiment.Experiment;
import com.jakegut.microrts.experiment.RTSExperiment;
import com.jakegut.microrts.fitness.TerminalFitnessFunction;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;
import fr.inria.optimization.cmaes.fitness.IObjectiveFunctionParallel;
import rts.units.UnitTypeTable;
import tests.GameVisualSimulationTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CMAEvolver {

    static int TIME = 1;

    public static class RTSFunction implements IObjectiveFunctionParallel {

        /**
         * @param x a point (candidate solution) in the pre-image of the objective function
         * @return objective function value of the input search point
         */
        public double valueOf(double[] x) {
//            Experiment exp = new RTSExperiment(new CMAESMCTS(x), new TerminalFitnessFunction(), null, 0, null);
            Experiment exp = new RTSExperiment(new CMAESMCTS(x), new TerminalFitnessFunction(), null, 0, null, new UnitTypeTable());
            exp.run();
            return exp.getFitness();
        }
//
//        @Override
//        public boolean isFeasible(double[] x) {
//            return true;
//        }

        @Override
        public double[] valuesOf(double[][] population) {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            Object mutex = new Object();
            double[] fitness = new double[population.length];
            for(int i = 0; i < population.length; i++){
                double[] weights = population[i];
                Runnable exp = new RTSExperiment(new CMAESMCTS(weights), new TerminalFitnessFunction(), fitness, i, mutex, new UnitTypeTable());
                executor.execute(exp);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {   }
            return fitness;
        }
    }

    public static void main(String[] args){
        RTSFunction fitfun = new RTSFunction();
        PrintWriter printWriter = null;
        try {
            File file = new File("output/fitness-" + TIME + "-" + System.currentTimeMillis() + ".txt");
            file.createNewFile();
            printWriter = new PrintWriter(new FileWriter(file));
            printWriter.println(java.time.LocalDateTime.now());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert printWriter != null;
        CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
        cma.readProperties(); // read options, see file CMAEvolutionStrategy.properties
        cma.setDimension(6); // overwrite some loaded properties
        cma.setInitialX(0.2); // in each dimension, also setTypicalX can be used
        cma.setInitialStandardDeviation(0.2); // also a mandatory setting
        cma.options.stopFitness = -Double.MAX_VALUE;
        cma.options.stopMaxIter = 50;
        cma.parameters.setPopulationSize(10);
        


//        double[] fitness = cma.init();

        cma.writeToDefaultFilesHeaders(0); // 0 == overwrites old files

        while(cma.stopConditions.getNumber() == 0) {

            // core iteration step
            double[][] pop = cma.samplePopulation(); // get a new population of solutions
//            for(int i = 0; i < pop.length; ++i) {    // for each candidate solution i
//                while (!fitfun.isFeasible(pop[i]))   //    test whether solution is feasible,
//                    pop[i] = cma.resampleSingle(i);  //       re-sample solution until it is feasible
//                fitness[i] = fitfun.valueOf(pop[i]); //    compute fitness value, where fitfun
//            }	                                     //    is the function to be minimize
            double[] fitness = fitfun.valuesOf(pop);
            cma.updateDistribution(fitness);         // pass fitness array to update search distribution
            printWriter.printf("%d, %d, ", cma.getCountIter(), TIME);
            printWriter.println(Arrays.toString(fitness));

            // output to console and files
            cma.writeToDefaultFiles();
            int outmod = 150;
            cma.printlnAnnotation(); // might write file as well
            cma.println();

        }
        double bestFitness = fitfun.valueOf(cma.getMeanX());
        cma.setFitnessOfMeanX(bestFitness); // updates the best ever solution



        // final output
        cma.writeToDefaultFiles(1);
        cma.println();
        cma.println("Terminated due to");
        for (String s : cma.stopConditions.getMessages())
            cma.println("  " + s);
        cma.println("best function value " + cma.getBestFunctionValue()
                + " at evaluation " + cma.getBestEvaluationNumber());
        printWriter.println("best function value " + cma.getBestFunctionValue()
                + " at evaluation " + cma.getBestEvaluationNumber());
        printWriter.println(java.time.LocalDateTime.now());
        printWriter.println(Arrays.toString(cma.getBestX()));
        printWriter.close();
        System.out.println(Arrays.toString(cma.getBestX()));
        UnitTypeTable utt = new UnitTypeTable();
        AI ai = new CMAESMCTS(cma.getBestX());
        try {
            GameVisualSimulationTest.runGameTest(ai, utt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
