import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * The purpose of this program is to impliment a simplified perceptron algoithm,
 * apply it to a training data set and then use the trained weights to classify
 * a second data set.
 *
 * @author Monal Daterao
 *
 *         JHED: mdatera1
 *
 *         1/10/2024
 * 
 */
public class ProjectA {

//start of main
   public static void main(String[] args) throws IOException {

      String trainingData = "training.txt";
      String validateData = "validate.txt";

      // number of features
      int mColumns = 4;

      // number of rows in training.txt
      int nTraining = 1048;

      // number of rows in validate.txt
      int nValidation = 324;

      // read training feature data into 2D array X
      double[][] xArray = readData(trainingData, nTraining, mColumns);

      // read training authenticity data into 1D array Y
      int[] yArray = readAuthenticityData(trainingData, nTraining);

      // create weights array from perceptron
      double[] wArray = trainingPerceptron(xArray, yArray, mColumns, nTraining);

      // read validate feature data into 2D array P
      double[][] pArray = readData(validateData, nValidation, mColumns);

      // read validate authenticity data into 1D array validateY
      int[] yValidate = readAuthenticityData(validateData, nValidation);

      /*
       * validate perceptron and create classificationData array, which has the
       * classification predictions to be written into predict.txt
       */
      int[] classificationData = validatePerceptron(pArray, wArray, yValidate,
            nValidation, mColumns);

      /*
       * write validate image features and classification predictions to
       * predict.txt
       */
      try {
         writePredict(pArray, classificationData, nValidation, mColumns);
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   /**
    * Reads feature data from the input file into a 2D array.
    * 
    * The feature data is the first 4 columns of each row in the file
    *
    * @param fileName input file name
    * @param rows     number of rows in the input file
    * @param columns  number of columns in the input file (features)
    * @return 2D array of feature data
    * @throws IOException if opening or closing the file fails
    */
   public static double[][] readData(String fileName, int rows, int columns)
         throws IOException {

      // FileInputStream object
      FileInputStream fileByteStream = new FileInputStream(fileName);

      // Scanner object
      Scanner scnr = new Scanner(fileByteStream);

      // Initialize 2D array X
      double[][] xArray = new double[rows][columns];

      /*
       * reads features, which are the first four values in each row of
       * training.txt
       */
       
      
      int skipOrNot = 1;
     
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < columns; j = j + skipOrNot) {
            if (scnr.hasNext()) {
               if (scnr.hasNextInt()) {
                  int value = scnr.nextInt();

                  // Check if the value is neither 0 nor 1
                  if (value != 0 && value != 1) {
                     xArray[i][j] = value;
                     skipOrNot = 1;
                  } else {
                     /* Skip this value, overwrite this position
                     in the next iteration*/
                     if (j == 0) {
                        skipOrNot = 0;
                     }
                     else {
                        skipOrNot = -1;
                     }
                  }
               } else if (scnr.hasNextDouble()) {
                  double value = scnr.nextDouble();

                  // Check if the value is neither 0 nor 1
                  if (value != 0 && value != 1) {
                     xArray[i][j] = value;
                     skipOrNot = 1;
                  } else {
                     /*Skip this value, overwrite this position 
                     in the next iteration*/
                     if (j == 0) {
                        skipOrNot = 0;
                     }
                     else {
                        skipOrNot = -1;
                     }

                  }
               }
            }
         }
      }
      return xArray;
   }

   /**
    * Reads authenticity values into a 1D array from inout file.
    * 
    * The authenticity values are the last column. Additionally, this method
    * changes 0 to 1 and 1 to -1
    * 
    * @param fileName input file name
    * @param rows     number of rows in the input file
    * @return 1D array of authenticity data
    * @throws IOException if opening or closing the file fails
    */
   public static int[] readAuthenticityData(String fileName, int rows)
         throws IOException {

      // initialize 2D array Y
      int[] yArray = new int[rows];

      // FileInputStream object
      FileInputStream fileByteStream = new FileInputStream(fileName);

      // Scanner object
      Scanner scnr = new Scanner(fileByteStream);

      // Read integers into 1D array Y
      
      int skipOrNot = 0;
      for (int i = 0; i < rows; i = i + skipOrNot) {
         if (scnr.hasNextInt()) {
            
            skipOrNot = 1;
            int value = scnr.nextInt();
            
            if (value == 0) {
               yArray[i] = 1;  
            } else {
               yArray[i] = -1;
            }
         }
         /* If element isn't of data type int, skip over it
         so that the same index in the array is considered*/
         else {
            scnr.next();
            skipOrNot = 0;               
         }
      }
      return yArray;
   }

   /**
    * Computes the dot product of two 1D arrays.
    * 
    * @param arrayOne first 1D array
    * @param arrayTwo second 1D array
    * @param columns  number of columns in the input file (features)
    * @return value of dot product
    */
   public static double computeDotProd(double[] arrayOne, double[] arrayTwo,
         int columns) {

      double dotProd = 0.0;

      // calculate the dot product of W and row Xi in X
      for (int i = 0; i < columns; i++) {
         dotProd = dotProd + arrayOne[i] * arrayTwo[i];
      }

      return dotProd;

   }

   /**
    * Adds two 1D arrays, which is the pairwise sum of the arrays.
    * 
    * @param arrayOne first 1D array
    * @param arrayTwo second 1D array
    * @param columns  number of columns in the input file (features)
    * @return 1D array of the sum of the arrays
    */
   public static double[] addArray(double[] arrayOne, double[] arrayTwo,
         int columns) {
      
      // New array that added elements will be assigned to
      double[] addedArray = new double[columns];

      for (int i = 0; i < columns; i++) {
         addedArray[i] = arrayOne[i] + arrayTwo[i];
      }

      return addedArray;
   }

   /**
    * Computes the scalar product of a constant and an array.
    * 
    * @param scalar     constant that will be multiplied with the array
    * @param otherArray 1D array
    * @param columns    number of columns in the input file (features)
    * @return 1D array of the scalar product
    */
   public static double[] scalarArray(int scalar, double[] otherArray,
         int columns) {
      
      // New array that multiplied elements will be assigned to
      double[] scalarArray = new double[columns];

      for (int i = 0; i < columns; i++) {
         scalarArray[i] = (scalar) * otherArray[i];
      }

      return scalarArray;
   }

   /**
    * Writes the weights array to output file weights.txt.
    * 
    * The weights are formatted to five digits of precision
    * 
    * @param wArray  1D weights array
    * @param columns number of columns in the input file (features)
    * @throws IOException if opening or closing the file fails
    */
   public static void writeWeights(double[] wArray, int columns)
         throws IOException {
      
      //FileOutPutStream and PrintWriter Objects
      try (FileOutputStream FILESTREAM = new FileOutputStream("weights.txt",
            true); PrintWriter OUTFS = new PrintWriter(FILESTREAM)) {
         
         //Wrtie wArray elements formatted to 5 digits of precision
         for (int i = 0; i < columns; i++) {
            double printWeight = wArray[i];
            OUTFS.printf("%.5f ", printWeight);
         }

         OUTFS.println();

         OUTFS.close();
      }
   }

   /**
    * Uses the training feature data to train a 1D vector of weights.
    * 
    * The weights array is written to output file weights.txt after every
    * iteration
    *
    * @param xArray  2D array of feature data from training.txt
    * @param yArray  1D array of authenticity data
    * @param columns number of columns in the input file (features)
    * @param rows    number of rows in xArray
    * @return 1D array of weights
    */
   public static double[] trainingPerceptron(double[][] xArray, int[] yArray,
         int columns, int rows) {

      double[] wArray = new double[columns];
      int prediction = 0;

      // Initialize W to 0
      for (int i = 0; i < columns; i++) {
         wArray[i] = 0.0;
      }

      // Compute the classification (1 or -1) based on sign of W dot Xi
      for (int i = 0; i < rows; i++) {
         
         //Write weights to weights.txt for every iteration
         try {
            writeWeights(wArray, columns);
         } catch (IOException e) {
            e.printStackTrace();
         }

         // Compute dot product of W and Xi
         double dotProduct = computeDotProd(wArray, xArray[i], columns);

         // Prediction of classification based on sign of W dot Xi
         if (dotProduct >= 0) {
            prediction = 1;
         } else if (dotProduct < 0) {
            prediction = -1;
         }

         /*Set W to W + Yi * Xi if predicted classification doesn't match
         expected Yi value*/
         if (prediction != yArray[i]) {
            wArray = addArray(wArray,
                  scalarArray(yArray[i], xArray[i], columns), columns);
         }
      }
      return wArray;
   }

   /**
    * Writes validate features and predicted authenticities into output file.
    *
    * Writes feature data and predicted authenticiy values from validate.txt
    * into predict.txt
    * 
    * @param pArray             2D array of feature data
    * @param classificationData 1D array of predicted authenticity values
    * @param rows               number of rows in pArray
    * @param columns            number fo columns in pArray
    * @throws IOException if opening or closing the file fails
    */

   public static void writePredict(double[][] pArray, int[] classificationData,
         int rows, int columns) throws IOException {
      
      //FileOutPutStream and PrintWriter Objects
      try (FileOutputStream FILESTREAM = new FileOutputStream("predict.txt",
            true); PrintWriter OUTFS = new PrintWriter(FILESTREAM)) {

         for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
               OUTFS.print(pArray[i][j] + " ");
            }
            OUTFS.println(classificationData[i]);
         }

         OUTFS.close();
      }

   }

   /**
    * Uses calculated weights to predict the classification for each record.
    * 
    * Weights in wArray are used to predict the classification for each record
    * in validate.txt
    *
    * @param pArray    2D array of feature data from validate.txt
    * @param wArray    1D array of weights
    * @param yValidate 1D array of authenticity values from validate.txt
    * @param rows      number of rows in pArray
    * @param columns   number of columns in pArray
    * @return 1D array of predicted classifications for each record
    */
   public static int[] validatePerceptron(double[][] pArray, double[] wArray,
         int[] yValidate, int rows, int columns) {
      
      int prediction = 0;
      
      //Array that will store authenticity value predictions
      int[] classificationData = new int[rows];
      
      //Variables for print statements
      int correctPredictions = 0;
      int incorrectPredictions = 0;
      double percentCorrect = 0.0;

      for (int i = 0; i < rows; i++) {
         
         //Compute dot product
         double dotProd = computeDotProd(wArray, pArray[i], columns);
         
         //Make prediction based on dot product sign
         if (dotProd >= 0) {
            prediction = 1;
            classificationData[i] = 1;
         } else if (dotProd < 0) {
            prediction = -1;
            classificationData[i] = -1;
         }

         // Determining number of correct predictions
         if (prediction != yValidate[i]) {
            incorrectPredictions += 1;
         } else {
            correctPredictions += 1;
         }
      }
      // Calculate percentage of correct predictions
      percentCorrect = ((double) correctPredictions / (double) rows) * 100.0;
      
      // Print statements
      System.out.println("correct predictions: " + correctPredictions);
      System.out.println("incorrect predictions: " + incorrectPredictions);
      System.out.println("percent correct: " + percentCorrect);
      
      return classificationData;
   }

}
