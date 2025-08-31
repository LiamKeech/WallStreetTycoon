/**
 * Author: Gareth Munnings
 * Created on 2025/08/31
 */

package com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel;

public class Network {
    // Hard-coded weights
    private static final double[][] weightsInputHidden1 = {
            {0.2, -0.5, 0.3},   // from input neuron 1
            {0.7,  0.1, -0.2}   // from input neuron 2
    };

    private static final double[][] weightsHidden1Hidden2 = {
            {0.6, -0.1, 0.4},   // from hidden1 neuron 1
            {-0.3, 0.8, 0.5},   // from hidden1 neuron 2
            {0.2, -0.4, 0.9}    // from hidden1 neuron 3
    };

    private static final double[][] weightsHidden2Output = {
            {0.5, -0.7},        // from hidden2 neuron 1
            {0.1,  0.6},        // from hidden2 neuron 2
            {-0.2, 0.3}         // from hidden2 neuron 3
    };

    // Forward pass
    public double[] forward(int[] input) {
        // Input → Hidden1
        double[] hidden1 = activateLayer(toDoubleArray(input), weightsInputHidden1);

        // Hidden1 → Hidden2
        double[] hidden2 = activateLayer(hidden1, weightsHidden1Hidden2);

        // Hidden2 → Output
        return activateLayer(hidden2, weightsHidden2Output);
    }

    // Generic layer activation: input[] × weights[][] → output[]
    private static double[] activateLayer(double[] input, double[][] weights) {
        int outSize = weights[0].length;   // number of neurons in next layer
        double[] output = new double[outSize];

        for (int j = 0; j < outSize; j++) {
            double sum = 0;
            for (int i = 0; i < input.length; i++) {
                sum += input[i] * weights[i][j];
            }
            output[j] = sigmoid(sum);
        }

        return output;
    }

    // Convert int[] to double[]
    private static double[] toDoubleArray(int[] arr) {
        double[] result = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i];
        }
        return result;
    }

    // Sigmoid activation
    private static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
}
