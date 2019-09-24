/*
 * Name: Justin Senia
 * E-Number: E00851822
 * Date: 10/6/2017
 * Class: COSC 461
 * Project: #1
 */
import java.util.LinkedList;
import java.io.*;
import java.util.*;

//This program solves sliding puzzle
public class Sliding
{
    //Board class (inner class)
    private class Board{
    private int[][] array;  //array
    private Board parent;        //filled rows

    //constructor of board class
    private Board(int[][] inArray, int bRows, int bCols)
    {
      this.array = new int[bRows][bCols]; //create array

      for (int i = 0; i < bRows; i++)
        for (int j = 0; j < bCols; j++)
          this.array[i][j] = inArray[i][j]; //array Inherits passed array

          this.parent = null;   //pointer to it's parent object in list
    }
  }

  private Board initial; //initial board supplied by file input
  private Board goal; //goal board supplied by file input
  private int boardRows; //board dimension (rows)
  private int boardCols; //board dimension (columns)
  private PrintWriter pW; //passed PrintWriter for file I/O
  private boolean DBSwitch; //if true = depth first, if false = breadth first
  private int swapLength = 0; //Keeps track of how many swaps
  private int alreadyExplored; //keeps track of how many were explored when solution is found
  private int yetToBeExplored; //keeps track of how many are yet to be explored when sol found
  private int totalStatesIdentified; //addition of alreadyExplored and yetToBeExplored
  LinkedList<Board> correctPath = new LinkedList<Board>(); //holds the correct path

  //Constructor of Sliding class
  public Sliding(int[][] initial, int[][] goal, int bRows, int bCols, PrintWriter PWOut)
  {
      this.initial = new Board(initial, bRows, bCols);
      this.goal = new Board(goal, bRows, bCols);
      this.boardRows = bRows;
      this.boardCols = bCols;
      this.pW = PWOut;
  }

  //initiates solver method which is timed
  public void timedSolve(boolean depthFirst){
      //decides whether search will be depth or breadth first
      //depthFirst == true: is depthFirst
      //depthfirst == false: is breadthfirst
      DBSwitch = depthFirst;

      //makes note of initial time before algo begins
      long startTime = System.currentTimeMillis();

      //executes solve method
      solve();

      //makes note of end time after solve has completed
      long endTime = System.currentTimeMillis();

      //prints out properly formatted data to describe timed results

      //Prints out formatted processed data to monitor
      System.out.println("***********************************************************");
      if (DBSwitch == true)
        System.out.println("Type of search: Depth First");
      else if (DBSwitch == false)
        System.out.println("Type of search: Breadth First");
      System.out.println("Time taken to calculate Sliding problem: " +
      (endTime - startTime) + " Milliseconds");
      System.out.println("Length of swap sequence: " + swapLength + " swaps");
      System.out.println("Number of states already explored when solution was found: " +
      alreadyExplored);
      System.out.println("Number of states yet to be explored when solution was found: " +
      yetToBeExplored);
      System.out.println("Total number of states identified when solution was found: " +
      totalStatesIdentified);
      System.out.println("***********************************************************");

      //prints out formatted processed data to output file
      pW.println("***********************************************************");
      if (DBSwitch == true)
        pW.println("Type of search: Depth First");
      else if (DBSwitch == false)
        pW.println("Type of search: Breadth First");
      pW.println("Time taken to calculate Sliding problem: " +
      (endTime - startTime) + " Milliseconds");
      pW.println("Length of swap sequence: " + swapLength + " swaps");
      pW.println("Number of states already explored when solution was found: " +
      alreadyExplored);
      pW.println("Number of states yet to be explored when solution was found: " +
      yetToBeExplored);
      pW.println("Total number of states identified when solution was found: " +
      totalStatesIdentified);
      pW.println("***********************************************************");
  }

  //Method solves sliding puzzle
  public void solve()
  {
    LinkedList<Board> openList = new LinkedList<Board>();
    LinkedList<Board> closedList = new LinkedList<Board>();

    openList.addFirst(initial); //adding initial board to openlist

    while (!openList.isEmpty()) //continues as long as there are states left to search
    {
      Board board = openList.removeFirst(); //remove initial board from openlist

      closedList.addLast(board); //add inital board to closed list

      if (complete(board)) //checks if goal board has been reached
      {
        alreadyExplored = closedList.size(); //makes note of closed list size
        yetToBeExplored = openList.size(); //makes note of open list size
        totalStatesIdentified = alreadyExplored + yetToBeExplored;
        displayPath(board); //display final path
        return;
      }
      else //if path not complete, create children
      {
        LinkedList<Board> children = generate(board); //generate children
        for (int i = 0; i < children.size(); i++)
        {
          Board child = children.get(i); //pop children off list

          //ifchild doesnt exist on open or closed list, put in open list
          //depending on depth or breadth first switch, changes add order
          //from last to first depending on variable value
          if (!exists(child, openList) && !exists(child, closedList))
          {
            if (DBSwitch == false)
              openList.addLast(child);
            else if (DBSwitch == true)
              openList.addFirst(child);
          }
        }
      }
    }

    //if no solution is found print no solution
    System.out.println("no solution");
  }

  //Method creates children of a board
  private LinkedList<Board> generate(Board board)
  {
    int i = 0, j = 0;
    boolean found = false;

    //loops, locate "0" in board for use in sliding calculations
    for (i = 0; i < boardRows; i++)
    {
      for (j = 0; j < boardCols; j++)
        if (board.array[i][j] == 0)
        {
          found = true;
          break;
        }

      if (found)
        break;
    }

    //create booleans to rule out illegal moves for children
    boolean north, south, east, west;
    north = i == 0 ? false : true;
    south = i == boardRows-1 ? false : true;
    east  = j == boardRows-1 ? false : true;
    west  = j == 0 ? false : true;

    //new linkedlist for children to be attached to
    LinkedList<Board> children = new LinkedList<Board>();

    //creates children based on legal moves
    if (north) children.addLast(createChild(board, i, j, 'N'));
    if (south) children.addLast(createChild(board, i, j, 'S'));
    if (east) children.addLast(createChild(board, i, j, 'E'));
    if (west) children.addLast(createChild(board, i, j, 'W'));

    return children;
  }

  //Method creates a child of a board by swapping empty slot in a
  //given direction
  private Board createChild(Board board, int i, int j, char direction)
  {
    Board child = copy(board);

    if (direction == 'N')
    {
      child.array[i][j] = child.array[i-1][j];
      child.array[i-1][j] = 0;
    }
    else if (direction == 'S')
    {
      child.array[i][j] = child.array[i+1][j];
      child.array[i+1][j] = 0;
    }
    else if (direction == 'E')
    {
      child.array[i][j] = child.array[i][j+1];
      child.array[i][j+1] = 0;
    }
    else
    {
      child.array[i][j] = child.array[i][j-1];
      child.array[i][j-1] = 0;
    }
    child.parent = board;

    return child;
  }

  //Method creates copy of a board
  private Board copy(Board board)
  {
    return new Board(board.array, boardRows, boardCols);
  }

  //Method decides whether a board is complete
  private boolean complete(Board board)
  {
    return identical(board, goal);
  }

  //Method decides whether a board exists in a list
  private boolean exists(Board board, LinkedList<Board> list)
  {
    for (int i = 0; i < list.size(); i++)
      if (identical(board, list.get(i)))
        return true;

    return false;
  }

  //Method decides whether two boards are identical
  private boolean identical(Board p, Board q)
  {
    for (int i = 0; i < boardRows; i++)
      for (int j = 0; j < boardCols; j++)
        if (p.array[i][j] != q.array[i][j])
          return false;

    return true;
  }

  //Method displays path from initial to current board
  private void displayPath(Board board)
  {
  //  LinkedList<Board> list = new LinkedList<Board>();
    LinkedList<Board> list = new LinkedList<Board>();

    Board pointer = board;

    while (pointer != null)
    {
      list.addFirst(pointer);

      //keeps track of # of swaps required
      swapLength = swapLength + 1;

      pointer = pointer.parent;
    }

    //modifies swap variable to accomodate for while loop
    swapLength = swapLength - 1;

    for (int i = 0; i < list.size(); i++)
      displayBoard(list.get(i));
  }

  //Method displays board
  private void displayBoard(Board board)
  {
    for (int i = 0; i < boardRows; i++)
    {
      for (int j = 0; j < boardCols; j++)
      {
          System.out.print(board.array[i][j] + " ");
          pW.print(board.array[i][j] + " ");
      }
        System.out.println();
        pW.println();
    }
    System.out.println();
    pW.println();
  }
}
