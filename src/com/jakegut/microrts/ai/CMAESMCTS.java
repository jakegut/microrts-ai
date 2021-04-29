package com.jakegut.microrts.ai;

import ai.RandomBiasedAI;
import ai.core.AI;
import ai.evaluation.*;
import ai.montecarlo.MonteCarlo;
import com.jakegut.microrts.evaluation.CMAESEvaluation;
import rts.units.UnitTypeTable;

public class CMAESMCTS extends MonteCarlo {

    private double[] weights;

    public CMAESMCTS(double[] weights){
        super(100, -1, 100,
                new RandomBiasedAI(),
                new CMAESEvaluation(weights));
        this.weights = weights;
    }

    @Override
    public AI clone(){
        return new CMAESMCTS(weights);
    }
}
