import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static int numberOfHiddenNeurons = 128; // Total number of neurons
    public static double[] outputHidden = new double[numberOfHiddenNeurons]; // Output of hidden layer
    public static double[] outputNeuron = new double[10]; // Output of output-layer

    public static double[][] weightsOutput = new double[10][numberOfHiddenNeurons]; // Output weights
    public static double[][] weightsHidden = new double[numberOfHiddenNeurons][64]; // Hidden weights
    public static double[] dataSample = new double[65]; // 64 Features of each row
    public static void feedForward() {
        double weightedSum = 0;

        // Hidden layer
        for(int i = 0; i < numberOfHiddenNeurons; i++) {
            weightedSum = 0;
            for (int j = 0; j < 64; j++) {
                weightedSum += dataSample[j] * weightsHidden[i][j];
            }
            double sigmoidValue = 1/(1+Math.pow(2.7, -weightedSum));
            outputHidden[i] = sigmoidValue;
        } // End for loop

        // Output layer
        for(int i = 0; i < 10; i++) {
            weightedSum = 0;
            for(int j = 0; j < numberOfHiddenNeurons; j++)
                weightedSum += outputHidden[j] * weightsOutput[i][j];
            // Prediction
            if(weightedSum >= 0) {
                    outputNeuron[i] = 1;
                } else {
                    outputNeuron[i] = 0;
                } // End if-else

        } // End for loop
    }

    public static boolean testError(double[] targetOutputMap) {
        for (int i = 0; i < targetOutputMap.length; i++) {
            // If there is an error, return false
            if (targetOutputMap[i] != outputNeuron[i]) {
                return false;
            }
        }
        return true;
    }

    public static void training(double[] targetOutputMap) {

        double[] errorHidden = new double[numberOfHiddenNeurons]; // Hidden layer error
        double[] errorOutput = new double[10]; // Output layer error
        // Calculate output error
        for(int i = 0; i < 10; i++) {
            errorOutput[i] = targetOutputMap[i] - outputNeuron[i];
        }

        // Calculate hidden error
        for(int j = 0; j < numberOfHiddenNeurons; j++) {
            int errorTemp = 0;
            for(int i = 0; i < 10; i++) {
                errorTemp += errorOutput[i] * weightsOutput[i][j];
            }
            errorHidden[j] = outputHidden[j] * (1 - outputHidden[j]) * errorTemp;
        }

        // Adjust output layer weights
        for(int j = 0; j < numberOfHiddenNeurons; j++) {
            for(int i =0; i < 10; i++) {
                weightsOutput[i][j] = weightsOutput[i][j] + 0.0002 * outputHidden[j] * errorOutput[i];
            }
        }

        // Adjust hidden layer weights
        for(int j = 0; j < 10; j++) {
            for(int i =0; i < numberOfHiddenNeurons; i++) {
                weightsHidden[i][j] = weightsHidden[i][j] + 0.0002 * dataSample[j] * errorHidden[i];
            }
        }

    } // End of training method

    public static void main(String[] args) throws FileNotFoundException {
        int success = 0;
        int totalCycles = 500; // Total cycles
        int currentCycle = 0; // Current cycle
        Random random = new Random(); // Random doubles for hidden layer weights
        double targetOutput; // Digit to be predicted

        // Random hidden weights
        for (int i = 0; i < weightsHidden.length; i++) {
            for (int j = 0; j < weightsHidden[i].length; j++) {
                weightsHidden[i][j] = random.nextDouble(-0.5, 0.5);
            }
        }
        // Random output weights
        for (int i = 0; i < weightsOutput.length; i++) {
            for (int j = 0; j < weightsOutput[i].length; j++) {
                weightsOutput[i][j] = random.nextDouble(-0.5,0.5);
            }
        }

        System.out.println("\n=== Training ===");

        // Main loop
        while(currentCycle <= totalCycles) {
            // Specify the training set file here
            File file = new File("src/cw2DataSet2.csv");
            Scanner scI = new Scanner(file).useDelimiter(",").useLocale(Locale.US);
            int lineNum = 0; // Current line
            // Line loop
            while(scI.hasNextLine()) {
                // Read the line
                String[] line = scI.nextLine().split(",");

                // Store features
                for(int i = 0; i < 64; i++) {
                    dataSample[i] = Double.parseDouble(line[i]);
                }
                // Store target digit
                targetOutput = Double.parseDouble(line[64]);
                // Reset map
                double[] targetOutputMap = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
                // Set target digit index of map to 1
                targetOutputMap[(int)targetOutput] = 1;
                feedForward();
                // Get the testError method result, false meaning that there is an error
                boolean result = testError(targetOutputMap);
                // Check the result
                if(result == false) {
                    training(targetOutputMap);
                }
                else {
                    success++;
                }
                lineNum++;
            } // End line loop

            // Print accuracy every 50 epochs
            if(currentCycle % 50 == 0) {
                System.out.println("Epoch " + currentCycle + " | Accuracy " + (double)success/lineNum * 100 + "%");
            }
            // Reset success count
            success = 0;
            // Increment cycle number
            currentCycle++;
        } // End cycle while loop

        System.out.println("\n=== Testing ===");
        currentCycle = 0;

        // === TEST ===
        // Main loop
        while(currentCycle < 1) {
            // Specify the test set file here
            File file = new File("src/cw2DataSet2.csv");
            Scanner scI = new Scanner(file).useDelimiter(",").useLocale(Locale.US);
            int lineNum = 0; // Line number
            while(scI.hasNextLine()) {
                // Read the line
                String[] line = scI.nextLine().split(",");

                // Store features
                for(int i = 0; i < 64; i++) {
                    dataSample[i] = Double.parseDouble(line[i]);
                }
                // Store target digit
                targetOutput = Double.parseDouble(line[64]);
                // Reset map
                double[] targetOutputMap = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
                // Set target digit index of map
                targetOutputMap[(int)targetOutput] = 1;
                feedForward();
                // Get the testError method result, false meaning that there is an error
                boolean result = testError(targetOutputMap);
                // Check the result
                if(result == true) {success++;}
                lineNum++;
            } // End line loop
            // Print accuracy
            if(currentCycle % 50 == 0) {
                System.out.println("Epoch " + currentCycle + " | Accuracy " + (double)success/lineNum * 100 + "%");
            }
            // Reset success count
            success = 0;
            // Increment cycle number
            currentCycle++;
        }
    } // End main method
} // End Main class