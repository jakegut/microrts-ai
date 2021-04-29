package com.jakegut.microrts.ai;

import ai.evaluation.EvaluationFunction;
import ai.minimax.RTMiniMax.IDRTMinimax;
import com.jakegut.microrts.evaluation.CMAESEvaluation;

public class CMAMinimax extends IDRTMinimax {

    public CMAMinimax(double[] weights) {
        super(200, new CMAESEvaluation(weights));
    }
}
