/*
 * Name: Justin Senia
 * E-Number: E00851822
 * Date: 10/6/2017
 * Class: COSC 461
 * Project: #1
 */
import java.io.*;
import java.util.*;

public class SlidingTester
{

    //Main method for testing
    public static void main(String[] args) throws IOException
    {

      //creating buffered reader for getting user input
      java.io.BufferedReader keyIn = new BufferedReader(new InputStreamReader(System.in));

      //message welcoming to the program/giving instructions
      System.out.println("*****************************************");
      System.out.println("*    Welcome to the Sliding program     *");
      System.out.println("*****************************************");
      System.out.println("*  Please enter in a filename to start  *");
      System.out.println("* or type quit to terminate the program *");
      System.out.println("*****************************************");

      //start a loop that continues querying for input as long as user
      //does not enter "quit" (without the quotes)
      while (true)
      {
        System.out.print("Please make your entry now: ");
        String userIn = ""; //used for file entry and/or quitting

        userIn = keyIn.readLine(); //reading user input

        if (userIn.equalsIgnoreCase("quit")) //if user typed quit, stops program
          break;
        else{
              try
              {
                //establishing working directory for file I/O
                String currentDir = System.getProperty("user.dir");
                File fIn = new File(currentDir + '\\' + userIn);

                //using scanner with new input file based on user input
                Scanner scanIn = new Scanner(fIn);

                //creating printwriter for file output
                File fOut = new File("output_" + userIn);
                PrintWriter PWOut = new PrintWriter(fOut, "UTF-8");

                //scanning external file for Board dimensions
                int bRows = scanIn.nextInt();
                int bCols = scanIn.nextInt();

                //I manually added zeros to the text files for "empty" placeholders
                int[][] initialBoard = new int[bRows][bCols];
                int[][] finalBoard = new int[bRows][bCols];

                //gets information from files to populate initialBoard
                for (int i = 0; i < bRows; i++)
                {
                  for (int j = 0; j < bCols; j++)
                  {
                    initialBoard[i][j] = scanIn.nextInt();
                  }
                }

                //gets information from files to populate finalBoard
                for (int i = 0; i < bRows; i++)
                {
                  for (int j = 0; j < bCols; j++)
                  {
                    finalBoard[i][j] = scanIn.nextInt();
                  }
                }

                //Outputting formatted messages with read info
                System.out.println("Output for " + userIn);
                System.out.println("Rows: " + bRows + "  Cols: " + bCols);

                PWOut.println("Output for " + userIn);
                PWOut.println("Rows: " + bRows + "  Cols: " + bCols);

                //Creating new sliding object of sliding class with parameters
                //read in from files
                Sliding s = new Sliding(initialBoard, finalBoard, bRows, bCols, PWOut);
                s.timedSolve(true); //starting solve method depthfirst

                s = new Sliding(initialBoard, finalBoard, bRows, bCols, PWOut);
                s.timedSolve(false); //starting solve method Breadthfirst

                //closing I/O objects
                scanIn.close();
                PWOut.close();
              }
              catch (IOException e)
              {
                ;
              }
            }
      }
    }
}
