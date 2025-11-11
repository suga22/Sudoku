/*
Jessica Chen jchen204@u.rochester.edu
Shadiya Akhter sakhter@u.rochester.edu
CSC242 | Adam Purtee
Last Modified: 2/26/25
 */

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

public class Main {
    // setting standard Sudoku grid size (9 x 9) & subgrids (3 x 3)
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;
    private static int[][] board = new int[SIZE][SIZE];

    public static void main(String [] args){
        // read Sudoku input
        Scanner cin = new Scanner(System.in);
        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                board[i][j] = cin.nextInt();
            }
        }
        cin.close();

        // apply AC-3 algorithm to enforce arc consistency before solving
        if(ac3() && solveSudoku()){
            printBoard();
        } else {
            System.out.println("No solution.");
        }
    }

    // getPossibleValues method: get list of valid numbers for given cell
    private static List<Integer> getPossibleValues(int row, int col){
        boolean[] used = new boolean[SIZE + 1];

        // check row & column constraints
        for (int i = 0; i < SIZE; i++){
            used[board[row][i]] = true;
            used[board[i][col]] = true;
        }

        // check subgrib constraints
        int boxRow = (row / SUBGRID) * SUBGRID;
        int boxCol = (col / SUBGRID) * SUBGRID;
        for (int i = 0; i < SUBGRID; i++){
            for (int j = 0; j < SUBGRID; j++){
                used[board[boxRow + i][boxCol + j]] = true;
            }
        }

        // get valid numbers
        List<Integer> values = new ArrayList<>();
        for (int num = 1; num <= SIZE; num++){
            if (!used[num]) values.add(num);
        }
        return values;
    }

    // findUnassigned method: find next empty cell using heuristic (cell w/ least options)
    private static int[] findUnassigned(){
        int minOptions = SIZE + 1;
        int[] bestCell = null;

        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                if (board[i][j] == 0){
                    int options = getPossibleValues(i, j).size();
                    if(options < minOptions){
                        minOptions = options;
                        bestCell = new int[]{i,j};
                    }
                }
            }
        }
        return bestCell;
    }

    // solveSudoku method: backtracking solver w/ heuristic
    private static boolean solveSudoku(){
        // find next empty cell
        int[] cell = findUnassigned();
        // if no empty cells, puzzle solved
        if (cell == null)
            return true;

        int row = cell[0], col = cell[1];
        List<Integer> values = getPossibleValues(row, col);

        for (int num : values){
            // try placing number
            board[row][col] = num;
            // recur to solve next cell
            if (solveSudoku())
                return true;
            // undo if solution is invalid
            board[row][col] = 0;
        }
        return false;
    }

    // AC-3 algorithm: constraint propagation before backtracking
    private static boolean ac3(){
        Queue<int[]> queue = new LinkedList<>();

        // initialize queue w/ empty cells
        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                if (board[i][j] == 0){
                    queue.add(new int[]{i, j});
                }
            }
        }

        // process constraints until queue is empty
        while (!queue.isEmpty()){
            int[] cell = queue.poll();
            int row = cell[0], col = cell[1];
            List<Integer> values = getPossibleValues(row, col);

            // if no valid values, puzzle = unsolvable
            if (values.isEmpty())
                return false;
        }
        return true;
    }

    // printBoard method
    private static void printBoard(){
        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
