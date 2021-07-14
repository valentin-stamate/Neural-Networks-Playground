package neural_network;

import neural_network.bias.BiasInit;
import neural_network.bias.RandomBias;
import neural_network.color.Color;
import neural_network.activation.TanhFunction;
import neural_network.weights.WeightsInit;
import neural_network.weights.RandomWeightsInit;
import neural_network.matrix.Matrix;
import neural_network.activation.ActivationFunction;

public class NeuralNetwork {
    private static final double LEARNING_RATE = 0.07;

    private final double[][][] brain;
    private final double[][][] biases;

    private final ActivationFunction activationFunction;
    private final WeightsInit weightsInit;
    private final BiasInit biasInit;

    public NeuralNetwork(int[] layerDimension) {
        this.activationFunction = new TanhFunction();
        this.weightsInit = new RandomWeightsInit();
        this.biasInit = new RandomBias();

        int len = layerDimension.length;

        this.brain = new double[len - 1][][];
        this.biases = new double[len - 1][][];

        initBias(layerDimension);
        initBrain(layerDimension);
    }

    public NeuralNetwork(int[] layerDimension, ActivationFunction activationFunction, WeightsInit weightsInit, BiasInit biasInit) {
        this.activationFunction = activationFunction;
        this.weightsInit = weightsInit;
        this.biasInit = biasInit;

        int len = layerDimension.length;

        this.brain = new double[len - 1][][];
        this.biases = new double[len][][];

        initBias(layerDimension);
        initBrain(layerDimension);
    }

    /* INITIALIZATION */
    private void initBias(int[] layerDimension) {
        for (int i = 0; i < biases.length; i++) {
            int layer = layerDimension[i];
            biases[i] = biasInit.generate(layer);

            if (i == 0) {
                biases[i] = new double[layer][1];
            }
        }
    }

    private void initBrain(int[] layerDimension) {
        for (int i = 0; i < brain.length; i++) {
            int n = layerDimension[i + 1];
            int m = layerDimension[i];
            brain[i] = weightsInit.create(n, m);
        }
    }

    /**/
    public double[][] feedForward(final double[][] input) {
        double[][] currentInput = input;

        int loops = brain.length;

        for (int i = 0; i < loops; i++) {
            double[][] weights = brain[i];

            currentInput = Matrix.multiply(weights, currentInput);
            currentInput = Matrix.add(currentInput, biases[i]);

            currentInput = Matrix.map(currentInput, activationFunction::fun);
        }

        return currentInput;
    }

    public void backPropagation(double[][] input, double[][] target) {
        int layers = brain.length + 1;

        double[][][] rawInput = getRawInputs(input);
        double[][] currentError = Matrix.subtract(target, rawInput[layers - 1]);
        double[][][] errors = getErrors(currentError);

        System.out.println("Input");
        for (int i = 0; i < layers; i++) {
            Matrix.println(rawInput[i]);
        }

        System.out.println("Deltas");
        for (int i = layers - 1; i >= 1; i--) {
            double[][] previousInput = rawInput[i - 1];
            double[][] error = errors[i];

            double[][] gradients = Matrix.map(previousInput, activationFunction::slope);

            double[][] deltaWeights = Matrix.multiply(gradients, error);

            deltaWeights = Matrix.map(deltaWeights, x -> x * LEARNING_RATE);

            double[][] deltaBiases = Matrix.copyOf(deltaWeights);

            deltaWeights = Matrix.multiply(deltaWeights, Matrix.transpose(previousInput));

            // now, how to update the weights and biases?
        }

    }

    private double[][][] getRawInputs(double[][] input) {
        int layers = brain.length + 1;

        double[][][] inputs = new double[layers][][];
        double[][][] rawInputs = new double[layers][][];

        inputs[0] = Matrix.copyOf(input);
        rawInputs[0] = Matrix.copyOf(input);

        for (int i = 1; i < layers; i++) {
            double[][] weights = brain[i - 1];
            double[][] bias = biases[i - 1];

            inputs[i] = Matrix.multiply(weights, inputs[i - 1]);
            inputs[i] = Matrix.add(inputs[i], bias);

            rawInputs[i] = Matrix.copyOf(inputs[i]);
            inputs[i] = Matrix.map(inputs[i], activationFunction::fun);
        }

        return rawInputs;
    }

    private double[][][] getErrors(double[][] output) {
        int layers = brain.length + 1;

        double[][][] errors = new double[layers][][];
        errors[layers - 1] = Matrix.copyOf(output);

        for (int i = layers - 2; i >= 0; i--) {
            double[][] weightsTransposed = Matrix.transpose(brain[i]);

            errors[i] = Matrix.multiply(weightsTransposed, errors[i + 1]);
        }

        return errors;
    }

    /* GETTERS AND SETTERS */
    public void printBrain() {
        int n = brain.length;

        System.out.println(Color.GREEN_BOLD + "---=== Neural Network Brain ===---" + Color.RESET);

        for (int i = 0; i < n; i++) {
            System.out.printf("Layer: %d - %d\n", i, i + 1);
            Matrix.print(brain[i]);
        }

        System.out.println("");
    }

    public void printBiases() {
        int n = biases.length;

        System.out.println(Color.GREEN_BOLD + "---=== Neural Network Biases ===---" + Color.RESET);

        for (int i = 0; i < n; i++) {
            System.out.printf("Layer: %d\n", i + 1);
            Matrix.print(biases[i]);
        }

        System.out.println("");
    }
}
