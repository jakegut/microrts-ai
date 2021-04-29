package com.jakegut.microrts.evaluation;

import ai.evaluation.*;
import rts.GameState;

public class CMAESEvaluation extends EvaluationFunction {

    private final double[] weights;
    private final EvaluationFunction[] evalFunctions;

    public CMAESEvaluation(double[] weights){
        this.weights = weights;
        this.evalFunctions = new EvaluationFunction[]{
                new SimpleEvaluationFunction(),
                new LanchesterEvaluationFunction(),
                new SimpleOptEvaluationFunction(),
                new SimpleSqrtEvaluationFunction(),
                new SimpleSqrtEvaluationFunction2(),
                new SimpleSqrtEvaluationFunction3()
        };
    }


    @Override
    public float evaluate(int player1, int player2, GameState gameState) {
        float eval = 0;
        for(int i = 0; i < evalFunctions.length; i++){
            eval += weights[i] * evalFunctions[i].evaluate(player1, player2, gameState);
        }
        return eval;
    }

    @Override
    public float upperBound(GameState gameState) {
        float eval = 0;
        for(int i = 0; i < evalFunctions.length; i++){
            eval += weights[i] * evalFunctions[i].upperBound(gameState);
        }
        return eval;
    }
}
