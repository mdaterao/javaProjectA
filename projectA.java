import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;


public class projectA {

public static void main(String[] args) throws IOException {

String trainingData = "training.txt";
String validateData = "validate.txt";

//number of features
int M = 4;

//number of rows in training.txt
int NTraining = 1048;

//number of rows in validate.txt
int NValidation = 324;

//read training feature data into 2D array X
double [][] X = readData(trainingData, NTraining, M);


//read training authenticity data into 1D array Y
int[] Y = readAuthenticityData(trainingData, NTraining);


//create weights array from perceptron
double [] W = trainingPerceptron(X,Y);

//read validate feature data into 2D array P
double [][] P = readData(validateData, NValidation, M);

//read validate authenticity data into 1D array validateY
int[] validateY = readAuthenticityData(validateData, NValidation);

//validate perceptron and create classificationData array, which has the classification predictions to be written into predict.txt
int[] classificationData = validatePerceptron(P, W, validateY, NValidation);

//write validate image features and classification predictions to predict.txt
   try {
            writePredict(P, classificationData, NValidation, M);
        } catch (IOException e) {
            e.printStackTrace();
        }
}



public static double[][] readData(String fileName, int N, int M) throws IOException {

   FileInputStream fileByteStream = null;
   fileByteStream = new FileInputStream(fileName);

   Scanner scnr = null;
   scnr = new Scanner(fileByteStream);

   
   double [][] X = new double[N][M];

   for (int i = 0; i < N; i++) {
      
      //reads features, which are the first four double values in each row of training.txt
      for (int j = 0; j < M; j++) {
         if (scnr.hasNextDouble()) {
            X[i][j] = scnr.nextDouble();
         }  
      }
      //skips authenticity daya
      for (int k = M; k < M + 1; k++) {
         if (scnr.hasNext()) {
             scnr.next();
         }
      }
   }


   return X;

}

public static int[] readAuthenticityData(String fileName, int N) throws IOException{
   
   int[] Y = new int[N];
   
   FileInputStream fileByteStream = new FileInputStream(fileName);

   Scanner scnr = new Scanner(fileByteStream);
   
   for (int i = 0; i < N ; i++) {
       if (scnr.hasNextInt()) {
            Y[i] = scnr.nextInt();
       }
       // If element isn't of data type int, skip over it
       //Decrement i so that the same index in the array is considered
       else { 
       scnr.next();
       i--;
       } 
    }
    for (int i = 0; i < N; ++i) {
         if (Y[i] == 0) {
            Y[i] = 1;
         }
         else if (Y[i] == 1) {
            Y[i] = -1;
         }
   }

 
   return Y;
   
   }
   
public static double computeDotProd(double [] W, double [] Xi) {
   // M is the number of features
   int M = 4;
   
   double dotProd = 0.0;
   
   //calculate the dot product of W and row Xi in X
   for (int i = 0; i < M; i++) {
      dotProd = dotProd + (W[i] * Xi[i]);
   }
   
   return dotProd;
   
}



//Write weights to weights.txt
public static void writeWeights(double [] W) throws IOException{
   int M = 4;
   
   try (FileOutputStream fileStream = new FileOutputStream("weights.txt", true);
        PrintWriter outFS = new PrintWriter(fileStream)) {
   
        for (int i = 0; i < M; i++) {
            double printWeight = W[i];
            outFS.printf("%.5f ",printWeight);
        }
   
        outFS.println();

        outFS.close();
   } 
}

public static double [] trainingPerceptron(double [][] X, int [] Y) {
   int M = 4;
   int N = 1048;
   double [] W = new double [M];
   int prediction = 0;
   
   //Initialize W to 0
   for (int i = 0; i < M; i++) {
      W[i] = 0.0;
   }
   
   // Compute the classification (1 or -1) based on sign of W dot Xi
   for (int i = 0; i < N; i++) {
      
            
      // Compute dot product of W and Xi
      double dotProduct = computeDotProd(W, X[i]);
      
      //Prediction of classification based on sign of W dot Xi
      if (dotProduct >= 0){
         prediction = 1;
      }
      else if (dotProduct < 0) {
         prediction = -1;
      }
      
      //Set W to W + Yi * Xi if predicted classification doesn't match expected Yi value
      if (prediction != Y[i]) {
         for (int j = 0; j < M; j++) {
            W[j] = W[j] + Y[i] * X[i][j];;
         }
      }
      try {
            writeWeights(W);
        } catch (IOException e) {
            e.printStackTrace();
        }
 
   }
   
   return W;
   
}


public static void writePredict(double [][] P, int [] classificationData, int N, int M) throws IOException {

   try (FileOutputStream fileStream = new FileOutputStream("predict.txt", true);
        PrintWriter outFS = new PrintWriter(fileStream)) {
   
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
               outFS.print(P[i][j] + " "); 
            } 
            outFS.println(classificationData[i]);   
        }

        outFS.close();
   } 

   
}

public static int[] validatePerceptron(double [][] P, double [] W, int [] validateY, int N) {

 
 int classification = 0;
 int[] classificationData = new int[N];
 int correctPredictions = 0;
 int incorrectPredictions = 0;
 double percentCorrect = 0;
 
 for (int i = 0; i < N; i++) {
   double dotProd = computeDotProd(W, P[i]);
   
   if (dotProd >= 0) {
      classification = 1;
      classificationData[i] = 1;
   }
   else if (dotProd < 0) {
      classification = -1;
      classificationData[i] = -1;
   }
   
   //Determining number of correct predictions
   if (classification != validateY[i]) {
      incorrectPredictions += 1;
   }
   else {
      correctPredictions += 1;
   }
   
   
 }
 //calculate percentage of correct predictions
   percentCorrect = ((double) correctPredictions / (double) N) * 100.0;
   
   
   System.out.println("correct predictions: " + correctPredictions);
   System.out.println("incorrect predictions: " + incorrectPredictions);
   System.out.println("percent correct: " + percentCorrect);

 return classificationData;
}

}

