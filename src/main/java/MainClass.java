import neural_network.NeuralNetwork;
import neural_network.activation.SigmoidFunction;
import neural_network.bias.RandomBias;
import neural_network.matrix.Matrix;
import neural_network.training.TrainingData;
import neural_network.weights.RandomWeightsInit;

import java.io.FileNotFoundException;

public class MainClass {

    public static void main(String... args) {

        NeuralNetwork neuralNetwork = new NeuralNetwork(
                new int[]{2, 2, 1},
                new SigmoidFunction(),
                new RandomWeightsInit(),
                new RandomBias()
        );

    }

}
