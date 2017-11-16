package com.prod.kardris.englishcheckers;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;


public class EnglishCheckers {

    // Global constants
    public static final int RED   = 1;
    public static final int BLUE  = -1;
    public static final int EMPTY = 0;
    public static final int MARK  = 3;
    public static final int RANDOM			= 1;
    public static final int DEFENSIVE		= 2;
    public static final int SIDES				= 3;
    public static final int CUSTOM			= 4;
    private final MainActivity mainActivity;
    private int startegy;
    private AtomicBoolean lock;
    public EnglishCheckers(MainActivity mainActivity,AtomicBoolean lock){
        this.mainActivity=mainActivity;
        this.lock=lock;


    }
//    public  Runnable loop() {
//
//
//        //showBoard(example);
//        //printMatrix(example);
//        return new Runnable() {
//            @Override
//            public void run() {
//                interactivePlay();
//            }
//        };
//
//
//
//    }
    public  Runnable loop(int gameType,int strategy) {

        this.startegy=strategy;
        if(gameType==0){
            return new Runnable() {
                @Override
                public void run() {
                    try {
                        interactivePlay();
                    } catch (InterruptedException e) {
                        System.out.println("loop is finished #334");
                    }
                    System.out.println("loop is finished #334");
                }
            };
        }
        else {
            return new Runnable() {
                @Override
                public void run() {
                    try {
                        twoPlayers();
                    } catch (InterruptedException e) {
                        System.out.println("loop is finished #334");
                    }
                    System.out.println("loop is finished #334");
                }
            };
        }

    }

    public static int[][] createBoard() {
        int[][] board =  {{1,0,1,0,1,0,1,0},
                {0,1,0,1,0,1,0,1},
                {1,0,1,0,1,0,1,0},
                {0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},
                {0,-1,0,-1,0,-1,0,-1},
                {-1,0,-1,0,-1,0,-1,0},
                {0,-1,0,-1,0,-1,0,-1}};
        return board;
    }
    public  int[][] playerDiscs(int[][] board, int player) {
        int[][] positions = null;
        int countDiscs=0;
        if(player==RED){

            for(int i=0;i<board.length;i=i+1)
            {
                for(int j=0;j<board[i].length;j++){
                    if(board[i][j]>0){
                        countDiscs=countDiscs+1;
                    }
                }
            }
            positions=new int[countDiscs][2];
            int countPosition=0;
            for(int i=0;i<board.length;i=i+1)
            {
                for(int j=0;j<board[i].length;j++){
                    if(board[i][j]>0){
                        positions[countPosition][0]=i;
                        positions[countPosition][1]=j;
                        countPosition=countPosition+1;
                    }
                }
            }
        }
        else{ //player BLUE
            for(int i=0;i<board.length;i=i+1)
            {
                for(int j=0;j<board[i].length;j++){
                    if(board[i][j]<0){
                        countDiscs=countDiscs+1;
                    }
                }
            }
            positions=new int[countDiscs][2];
            int countPosition=0;
            for(int i=0;i<board.length;i=i+1)
            {
                for(int j=0;j<board[i].length;j++){
                    if(board[i][j]<0){
                        positions[countPosition][0]=i;
                        positions[countPosition][1]=j;
                        countPosition=countPosition+1;
                    }
                }
            }

        }
        return positions;
    }
    public  boolean isBasicMoveValid(int[][] board, int player, int fromRow, int fromCol, int toRow, int toCol) {
        boolean ans = false;
        if(player==RED&&board[fromRow][fromCol]<=0)
            return ans;
        if(player==BLUE&&board[fromRow][fromCol]>=0)
            return ans;
        if(board[toRow][toCol]!=0) //Destination is available
            return ans;
        if(Math.abs(fromRow-toRow)!=1||Math.abs(fromCol-toCol)!=1)  //checks if destination if greater then 1
            return ans;
        //add queen eexeption
        if(board[fromRow][fromCol]==2||board[fromRow][fromCol]==-2) // queen can move both sides
            return true;

        if((player==RED && toRow-fromRow!=1 )|| (player==BLUE && toRow-fromRow!=-1))// if red go only up, if blue go only down , queen from both players can move up and down.
            return ans;
        ans=true;
        return ans;
    }

    //Count number of available moves or jumps , to insert into the array
    public  int countToAddIntoArray(int[][] board, int player, int [][] positions,int moveType){
        int countMoves =0;
        int destRow,destCol;
        int row,col;
        for(int i=0;i<positions.length;i=i+1){
            row=positions[i][0];// for row
            col=positions[i][1]; //for column
            if(row+moveType<=7&&col+moveType<=7)//for up right{
            {
                destRow=row+moveType;
                destCol=col+moveType;
                if(basicMoveOrJump(board, player, row, col, destRow, destCol, moveType))
                    countMoves = countMoves +1;
            }
            if(row+moveType<=7&&col-moveType>=0)//for up left
            {
                destRow=row+moveType;
                destCol=col-moveType;
                if(basicMoveOrJump(board, player, row, col, destRow, destCol, moveType))
                    countMoves = countMoves +1;
            }
            //for down right
            if(row-moveType>=0&&col+moveType<=7)
            {
                destRow=row-moveType;
                destCol=col+moveType;
                if(basicMoveOrJump(board, player, row, col, destRow, destCol, moveType))
                    countMoves = countMoves +1;
            }
            //for down left
            if(row-moveType>=0&&col-moveType>=0)//for up left
            {
                destRow=row-moveType;
                destCol=col-moveType;
                if (basicMoveOrJump(board, player, row, col, destRow, destCol, moveType))
                    countMoves = countMoves +1;
            }
        }
        return countMoves;
    }

    public boolean basicMoveOrJump(int[][] board, int player,int row,int col,int destRow,int destCol,int moveType){
        if(moveType==1) //BasicMove
            return isBasicMoveValid(board,player,row,col,destRow,destCol);
        else
        if(moveType==2) //Basic Jump
            return isBasicJumpValid(board, player, row, col, destRow, destCol);
        else
            return false;
    }
    //After number of cells have been found , insert them into the new array below
    public  int[][] addIntoArray(int[][] board, int player, int [][] positions,int moveType){
        int[][] moves=new int[countToAddIntoArray(board,player,positions,moveType)][4];
        int countBasicMoves=0; //reset the counter
        int col,row,destRow,destCol;
        //isBasicJumpValid(board,player,row,col,destRow,destCol)
        for(int i=0;i<positions.length;i=i+1){
            row=positions[i][0];// for row
            col=positions[i][1]; //for column
            if(row+moveType<=7&&col+moveType<=7)//for up right{
            {
                destRow=row+moveType;
                destCol = col + moveType;
                if (basicMoveOrJump(board, player, row, col, destRow, destCol, moveType)){
                    moves[countBasicMoves][0]=row;
                    moves[countBasicMoves][1]=col;
                    moves[countBasicMoves][2]=destRow;
                    moves[countBasicMoves][3]=destCol;
                    countBasicMoves=countBasicMoves+1;
                }
            }
            if(row+moveType<=7&&col-moveType>=0)//for up left
            {
                destRow = row + moveType;
                destCol=col-moveType;
                if(basicMoveOrJump(board, player, row, col, destRow, destCol, moveType)){
                    moves[countBasicMoves][0]=row;
                    moves[countBasicMoves][1]=col;
                    moves[countBasicMoves][2]=destRow;
                    moves[countBasicMoves][3]=destCol;
                    countBasicMoves=countBasicMoves+1;
                }
            }
            //for down right
            if(row-moveType>=0&&col+moveType <= 7) {
                destRow=row-moveType;
                destCol=col+moveType;
                if(basicMoveOrJump(board, player, row, col, destRow, destCol, moveType)){
                    moves[countBasicMoves][0]=row;
                    moves[countBasicMoves][1]=col;
                    moves[countBasicMoves][2]=destRow;
                    moves[countBasicMoves][3]=destCol;
                    countBasicMoves=countBasicMoves+1;
                }
            }
            //for down left
            if (row - moveType >= 0 && col - moveType >= 0)//for up left
            {
                destRow=row-moveType;
                destCol=col-moveType;
                if(basicMoveOrJump(board, player, row, col, destRow, destCol, moveType)){
                    moves[countBasicMoves][0]=row;
                    moves[countBasicMoves][1]=col;
                    moves[countBasicMoves][2]=destRow;
                    moves[countBasicMoves][3]=destCol;
                    countBasicMoves=countBasicMoves+1;
                }
            }
        }
        return moves;
    }
    public  int[][] getAllBasicMoves(int[][] board, int player) {
        int[][] moves = null;
        int[][] positions=playerDiscs(board,player);
        int moveType=1;
        moves=addIntoArray(board,player,positions,moveType);
        return moves;
    }
    public  boolean isBasicJumpValid(int[][] board, int player, int fromRow, int fromCol, int toRow, int toCol) {

        int discType=board[fromRow][fromCol];  //gets the disc type from the board in the from location
        if(player==RED&&discType<=0)
            return false;
        if(player==BLUE&&discType>=0)
            return false;

        if(board[toRow][toCol]!=0) //Destination is available
            return false;

        if(player==RED) {
            //checks if the wanted jump is valid - (not over the player , and not 0)
            if (toRow > fromRow) { //  Up
                if (toCol > fromCol) {
                    if (board[fromRow + 1][fromCol + 1] >= 0) //Up right
                        return false;
                } else {
                    if (board[fromRow + 1][fromCol - 1] >= 0) //up left
                        return false;
                }
            }
            else{ //RED disc cannot go down unless its not queen
                if(discType==1)
                    return false;
                if ((discType > 1)) {//queen can go down the board
                    if (toCol > fromCol) {

                        if (board[fromRow - 1][fromCol + 1] >= 0) //down right
                            return false;
                    } else {
                        if (board[fromRow - 1][fromCol - 1] >= 0)  //down left
                            return false;
                    }
                }
            }
        }
        else{ //Player is blue
            //checks if the discs valid - (not yours , and not 0)
            //queen can go down the board
            if(toRow<fromRow){
                if (toCol > fromCol) {
                    if (board[fromRow - 1][fromCol + 1] <= 0) //down right
                        return false;
                }
                else {
                    if (board[fromRow - 1][fromCol - 1] <= 0)  //down left
                        return false;
                }
            }
            else{ //BLUE cannot go up the board unless its queen
                if(discType==-1)
                    return false;

                if((discType < -1) ) { //for queen
                    if (toRow > fromRow) { //  Up
                        if (toCol > fromCol) {
                            if (board[fromRow + 1][fromCol + 1] <= 0) //Up right
                                return false;
                        }
                        else {
                            if (board[fromRow + 1][fromCol - 1] <= 0) //up left
                                return false;
                        }
                    }
                }
            }
        }
        return true;
    }

//returns an array for a each disc if it has any BasicJumps
    public  int [][] getRestrictedBasicJumps(int[][] board, int player, int row, int col) {
        int[][] moves;
        int destRow,destCol;
        int countBasicJumps=0;
        if(row+2<=7&&col+2<=7)//for up right{
        {
            destRow=row+2;
            destCol=col+2;
            if(isBasicJumpValid(board,player,row,col,destRow,destCol))
                countBasicJumps=countBasicJumps+1;
        }
        if(row+2<=7&&col-2>=0)//for up left
        {
            destRow=row+2;
            destCol=col-2;
            if(isBasicJumpValid(board, player, row, col, destRow, destCol))
                countBasicJumps=countBasicJumps+1;
        }
        //for down right
        if(row-2>=0&&col+2<=7)
        {
            destRow=row-2;
            destCol=col+2;
            if(isBasicJumpValid(board, player, row, col, destRow, destCol))
                countBasicJumps=countBasicJumps+1;
        }
        //for down left
        if(row-2>=0&&col-2>=0)//for up left
        {
            destRow=row-2;
            destCol=col-2;
            if(isBasicJumpValid(board,player,row,col,destRow,destCol))
                countBasicJumps=countBasicJumps+1;
        }
        //now we know how many basic Jumps! we have.
        moves=new int[countBasicJumps][4];   // Create array according to the jumps
        countBasicJumps=0; //reset the counter
        if(row+2<=7&&col+2<=7)//for up right{
        {
            destRow=row+2;
            destCol=col+2;
            if(isBasicJumpValid(board, player, row, col, destRow, destCol)){
                moves[countBasicJumps][0]=row;
                moves[countBasicJumps][1]=col;
                moves[countBasicJumps][2]=destRow;
                moves[countBasicJumps][3]=destCol;
                countBasicJumps=countBasicJumps+1;
            }
        }
        if(row+2<=7&&col-2>=0)//for up left
        {
            destRow=row+2;
            destCol=col-2;
            if(isBasicJumpValid(board, player, row, col, destRow, destCol)){
                moves[countBasicJumps][0]=row;
                moves[countBasicJumps][1]=col;
                moves[countBasicJumps][2]=destRow;
                moves[countBasicJumps][3]=destCol;
                countBasicJumps=countBasicJumps+1;
            }
        }
        //for down right
        if(row-2>=0&&col+2<=7)
        {
            destRow=row-2;
            destCol=col+2;
            if(isBasicJumpValid(board, player, row, col, destRow, destCol)){
                moves[countBasicJumps][0]=row;
                moves[countBasicJumps][1]=col;
                moves[countBasicJumps][2]=destRow;
                moves[countBasicJumps][3]=destCol;
                countBasicJumps=countBasicJumps+1;
            }
        }
        //for down left
        if(row-2>=0&&col-2>=0)//for up left
        {
            destRow=row-2;
            destCol = col - 2;
            if(isBasicJumpValid(board, player, row, col, destRow, destCol)){
                moves[countBasicJumps][0]=row;
                moves[countBasicJumps][1]=col;
                moves[countBasicJumps][2]=destRow;
                moves[countBasicJumps][3]=destCol;
            }
        }
        //not not delay this.
        return moves;
    }
    public  int[][] getAllBasicJumps(int[][] board, int player) {
        int[][] moves = null;
        int col,row;
        int[][] positions=playerDiscs(board,player);
        int[][] restrictedBasicJumpsForDisc;
        int[][] tempForAllMoves=new int[48][4];
        int countNumberOfMoves=0;
        int j=0,h=0;
        for(int i=0;i<positions.length;i=i+1) {  // Get how many possible jumps have for all discs accros the board
            row = positions[i][0];// for row
            col = positions[i][1]; //for column
            restrictedBasicJumpsForDisc = getRestrictedBasicJumps(board, player, row, col);
            while(h<restrictedBasicJumpsForDisc.length) { //copy all jumps cordinates to the temp
                tempForAllMoves[j+h][0] = restrictedBasicJumpsForDisc[h][0];
                tempForAllMoves[j+h][1] = restrictedBasicJumpsForDisc[h][1];
                tempForAllMoves[j+h][2] = restrictedBasicJumpsForDisc[h][2];
                tempForAllMoves[j+h][3] = restrictedBasicJumpsForDisc[h][3];
                h=h+1;
            }
            j = j + h;
            h=0;
            countNumberOfMoves = restrictedBasicJumpsForDisc.length + countNumberOfMoves; //Get number of moves from each cell
        }
        moves=new int[countNumberOfMoves][4];
        for(int i=0;i<moves.length;i=i+1){
            moves[i][0] = tempForAllMoves[i][0];
            moves[i][1] = tempForAllMoves[i][1];
            moves[i][2] = tempForAllMoves[i][2];
            moves[i][3] = tempForAllMoves[i][3];
        }
        return moves;
    }
    public  boolean canJump(int[][] board, int player) {
        int[][] getJumps=getAllBasicJumps(board,player);
        return getJumps.length!=0;
    }


    public  boolean isMoveValid(int[][] board, int player, int fromRow, int fromCol, int toRow, int toCol) {
        boolean ans = false;
        int rowDistance=Math.abs(fromRow-toRow);
        int colDistance=Math.abs(fromCol-toCol);
        boolean canJump=canJump(board,player);
        //First Seperate to 2 options: Move is a jump , then a basicMove.
        if(rowDistance==2&&colDistance==2){
            ans = isBasicJumpValid(board, player,fromRow,fromCol,toRow,toCol);
        }
        else //check for a basicMove , and make sure player cannot jump
        if(rowDistance==1&&colDistance==1&&!canJump){
            ans=isBasicMoveValid(board,player,fromRow,fromCol,toRow,toCol);
        }
        return ans;
    }

    //Find if a player has any valid jumps or moves.
    public  boolean hasValidMoves(int[][] board, int player) {
        boolean ans = false;
        if(getAllBasicJumps(board,player).length!=0)
            ans=true;
        if(getAllBasicMoves(board, player).length!=0)
            ans=true;
        return ans;
    }


    public  int[][] playMove(int[][] board, int player, int fromRow, int fromCol, int toRow, int toCol) {
        int rowDistance=Math.abs(fromRow-toRow);
        int colDistance=Math.abs(fromCol-toCol);

        if(rowDistance==2&&colDistance==2) //Means jump, other player will be removed
        {

            if (toRow > fromRow) { //  Up
                if (toCol > fromCol)
                    board[fromRow + 1][fromCol + 1] =0; //Up rig
                else
                    board[fromRow + 1][fromCol - 1] = 0; //up left
                }
            else {
                if (toCol > fromCol)
                    board[fromRow - 1][fromCol + 1] = 0; //down right
                else
                    board[fromRow - 1][fromCol - 1] = 0;  //down left
            }
            board[toRow][toCol]=board[fromRow][fromCol];
            //Queen Making
            if(player==RED&&toRow==board.length-1)
                board[toRow][toCol]=2;
            if(player==BLUE&&toRow==0)
                board[toRow][toCol]=-2;
        }
        else {
            if (rowDistance == 1 && colDistance == 1) //basic move
            {
                board[toRow][toCol] = board[fromRow][fromCol];
                //Queen Making
                if (player == RED && toRow == board.length - 1)
                    board[toRow][toCol] = 2;
                if (player == BLUE && toRow == 0)
                    board[toRow][toCol] = -2;
            }
        }
        board[fromRow][fromCol]=0;
        return board;
    }
    public  boolean gameOver(int[][] board, int player) {
        // True value for this method means gameOver for player
        //if the player has any discs left , return false
        // if the player has no valid moves anymore
        //ans=true;
        if(player==RED){
            if(playerDiscs(board,BLUE).length>0&&playerDiscs(board,player).length==0){
                return true;
            }
        }
        else
            if(playerDiscs(board,RED).length>0&&playerDiscs(board,player).length==0)
                return true;

        if(!hasValidMoves(board,player)){
            return true;
        }
        return false;
    }
    public  int findTheLeader(int[][] board) {
        int ans = 0;
        int totalRed=0;
        int totalBlue=0;
        for(int i=0;i<board.length;i=i+1){
            for(int j=0;j<board[i].length;j=j+1){
                if(board[i][j]>0){
                    totalRed=board[i][j]+totalRed;
                }
                else {
                    totalBlue = board[i][j] + totalBlue;
                }
            }
        }
        int sumDiscs=totalRed+totalBlue;
        if(sumDiscs>0)
            ans=1;
        else {
            if (sumDiscs < 0)
                ans = -1;
            else
                ans = 0;
        }
        return ans;
    }
    public  int[][] randomPlayer(int[][] board, int player) {
        int rnd;
        if(hasValidMoves(board,player)){ //if has any moves to do
            int[][] moves = getAllBasicMoves(board,player);
            int[][] jumps = getAllBasicJumps(board,player);

            if(jumps.length!=0){ //if has jumps
                Log.i(TAG, "###randomPlayer: in jumps");
                //int [][] nextJump=getRestrictedBasicJumps(board,player,jumps[rnd][2],jumps[rnd][3]);
                while(jumps.length!=0){
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    rnd = (int) (Math.random() * jumps.length);//pick a possible jump.
                    board = playMove(board, player, jumps[rnd][0], jumps[rnd][1], jumps[rnd][2], jumps[rnd][3]);
                    showBoard(board);//showBoard for multi jumps
                    jumps = getRestrictedBasicJumps(board,player,jumps[rnd][2],jumps[rnd][3]);
                }
            }
           else{ //no jumps - play basic moves
                rnd=(int)(Math.random()*moves.length);
                board=playMove(board,player,moves[rnd][0],moves[rnd][1],moves[rnd][2],moves[rnd][3]);
            }
        }
        return board;
    }
    public  int[][] defensivePlayer(int[][] board, int player) {

        if(getAllBasicJumps(board,player).length!=0) {
            board = randomPlayer(board, player);
        }
        else{
            int[][] movesPlayer = getAllBasicMoves(board,player);
            boolean isSafe;
            int count=0;
            int[][] safeMoves=new int[48][4];
            int currentDisc;
            for(int i=0;i<movesPlayer.length;i=i+1){
                isSafe=true;
                // Check each direction acccros the board.
                int fromRow=movesPlayer[i][0];
                int fromCol=movesPlayer[i][1];
                int toRow=movesPlayer[i][2];
                int toCol=movesPlayer[i][3];
                ///// copy the disc to a side integer.
                currentDisc=board[fromRow][fromCol];
                board[fromRow][fromCol]=0; //Mark the board like the disc has moved.
                ////
                if(player==RED){}
                //Player is BLUE
                else{

                    if(toRow<=6&&toCol<=6&&toRow>=1&&toCol>=1)//Check for boundries
                    {
                        if(board[toRow-1][toCol-1]>0&&board[toRow-1][toCol+1]>0) { // ENEMY IS DOWN (LEFT AND RIGHT)
                            isSafe=false;
                        }
                        else {
                            if (board[toRow - 1][toCol - 1] > 0) { // ENEMY IS DOWN LEFT OR RIGHT
                                if (board[toRow + 1][toCol + 1] != 0) {
                                    safeMoves[count][0] = movesPlayer[i][0];
                                    safeMoves[count][1] = movesPlayer[i][1];
                                    safeMoves[count][2] = movesPlayer[i][2];
                                    safeMoves[count][3] = movesPlayer[i][3];
                                    count = count + 1;
                                } else
                                    isSafe = false;
                            }
                            else
                            if(board[toRow-1][toCol+1]>0)
                                if(board[toRow - 1][toCol - 1]!=0){
                                    safeMoves[count][0] = movesPlayer[i][0];
                                    safeMoves[count][1] = movesPlayer[i][1];
                                    safeMoves[count][2] = movesPlayer[i][2];
                                    safeMoves[count][3] = movesPlayer[i][3];
                                    count = count + 1;
                                }
                                else
                                    isSafe = false;
                        }
                        if(board[toRow+1][toCol+1]>1||board[toRow+1][toCol-1]>1) { // ENEMY IS DOWN (LEFT AND RIGHT) YOU AND 2 RED QUEENS
                            isSafe=false;
                        }
                    }
                    else //If player goes to bounds , he is safe.
                    {
                        safeMoves[count][0] = movesPlayer[i][0];
                        safeMoves[count][1] = movesPlayer[i][1];
                        safeMoves[count][2] = movesPlayer[i][2];
                        safeMoves[count][3] = movesPlayer[i][3];
                        count = count + 1;
                    }
                    if(isSafe){
                        safeMoves[count][0] = movesPlayer[i][0];
                        safeMoves[count][1] = movesPlayer[i][1];
                        safeMoves[count][2] = movesPlayer[i][2];
                        safeMoves[count][3] = movesPlayer[i][3];
                        count = count + 1;
                    }
                }
                //Set back the moved disc back to its position
               board[fromRow][fromCol]=currentDisc;
            }
            if(count!=0) {
                int rnd=(int)(Math.random()*count); // Choose a random move between the safeMoves for the defensivePlayer
                board = playMove(board, player, safeMoves[rnd][0], safeMoves[rnd][1], safeMoves[rnd][2], safeMoves[rnd][3]);
            }
            else
                board=randomPlayer(board,player);
        }
        return board;
    }
    public  int[][] sidesPlayer(int[][] board, int player) {

        if(canJump(board,player)) //if can jump
            board=randomPlayer(board,player);
        else {
            int[][] moves = getAllBasicMoves(board, player);
            for(int i=0;i<moves.length;i++){
                System.out.println(moves[i][0]+"||"+moves[i][1]+"||"+moves[i][2]+"||"+moves[i][3]);
            }
            if (moves.length != 0) {
                int[][] randomMoves = new int[moves.length][moves[0].length]; // if every moves are equal

                int counter = 0;
                int maxRight = 3, lowLeft = 3;
                // Find the lowest and highest column locations by inspecting getAllBasicMoves method.
                for (int i = 0; i < moves.length; i++) {
                    int moveCol = moves[i][3];
                    if (moveCol >= 4 && moveCol > maxRight) {
                        maxRight = moveCol;
                    } else if (moveCol < 4 && moveCol < lowLeft) {
                        lowLeft = moveCol;
                    }
                }
                if (7 - maxRight == lowLeft) //Both sides are equal
                {
                    for (int i = 0; i < moves.length; i++) {
                        int moveCol = moves[i][3];
                        if  (moveCol == maxRight||moveCol==lowLeft) {
                            randomMoves[counter][0] = moves[i][0];
                            randomMoves[counter][1] = moves[i][1];
                            randomMoves[counter][2] = moves[i][2];
                            randomMoves[counter][3] = moves[i][3];
                            counter++;
                        }
                    }
                }
                else {
                    if (7 - lowLeft < maxRight) {//maxRight  is closer to the side
                        for (int i = 0; i < moves.length; i++) {
                            int moveCol = moves[i][3];
                            if (moveCol >= 3 && moveCol == maxRight) {
                                randomMoves[counter][0] = moves[i][0];
                                randomMoves[counter][1] = moves[i][1];
                                randomMoves[counter][2] = moves[i][2];
                                randomMoves[counter][3] = moves[i][3];
                                counter++;
                            }
                        }
                    }
                    else //lowLeft is closer to the side
                    {
                        for (int i = 0; i < moves.length; i++) {
                            int moveCol = moves[i][3];
                            if (moveCol <= 3 && moveCol == lowLeft) {
                                randomMoves[counter][0] = moves[i][0];
                                randomMoves[counter][1] = moves[i][1];
                                randomMoves[counter][2] = moves[i][2];
                                randomMoves[counter][3] = moves[i][3];
                                counter++;
                            }
                        }
                    }
                }
                System.out.println("maxRight:"+maxRight+"|| lowLEFT:"+lowLeft);
                    if (counter == 0) //couldn't find any move that suits SidePlayer
                       board=randomPlayer(board,player);
                    else {
                        System.out.println();

                        System.out.println();

                        System.out.println();
                        for(int i=0;i<counter;i++){
                            System.out.println(randomMoves[i][0]+"||"+randomMoves[i][1]+"||"+randomMoves[i][2]+"||"+randomMoves[i][3]);
                        }
                        int rnd = (int) (Math.random() * counter); //randomize between the found matches
                        board=playMove(board, player, randomMoves[rnd][0], randomMoves[rnd][1], randomMoves[rnd][2], randomMoves[rnd][3]);
                    }
            }
        }
        return board;
    }


    //******************************************************************************//

    /* ---------------------------------------------------------- *
     * Play an interactive game between the computer and you      *
     * ---------------------------------------------------------- */
    public  void interactivePlay() throws InterruptedException {
        int[][] board = createBoard();
        showBoard(board);

        Message msg=mainActivity.getHandler().obtainMessage(0,"Welcome to the interactive Checkers Game !");
        msg.sendToTarget();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
        msg=mainActivity.getHandler().obtainMessage(0,"You are the first player (RED discs)");
        msg.sendToTarget();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }

        int strategy = getStrategyChoice();
        boolean oppGameOver = false;
        while (!gameOver(board, RED) && !oppGameOver) {
            board = getPlayerFullMove(board, RED);

            oppGameOver = gameOver(board, BLUE);
            if (!oppGameOver) {
                //in order to have some delay.
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                board = getStrategyFullMove(board, BLUE, strategy);
            }
        }

        int winner = 0;
        if (playerDiscs(board, RED).length == 0  |  playerDiscs(board, BLUE).length == 0){
            winner = findTheLeader(board);
        }
        String state;
        if (winner == RED) {
            state="*You win !!*";
        }
        else if (winner == BLUE) {
            state="You lost :( ";
        }
        else{
            state="= DRAW =";
        }
        mainActivity.getHandler().obtainMessage(0,state).sendToTarget();

    }


    /* --------------------------------------------------------- *
     * A game between two players                                *
     * --------------------------------------------------------- */
    public  void twoPlayers() throws InterruptedException {
        int[][] board = createBoard();
        showBoard(board);

        System.out.println("Welcome to the 2-player Checkers Game !");

        boolean oppGameOver = false;
        while (!gameOver(board, RED)  &  !oppGameOver) {
            System.out.println("\nRED's turn");
            board = getPlayerFullMove(board, RED);

            oppGameOver = gameOver(board, BLUE);
            if (!oppGameOver) {
                System.out.println("\nBLUE's turn");
                board = getPlayerFullMove(board, BLUE);
            }
        }

        int winner = 0;
        if (playerDiscs(board, RED).length == 0  |  playerDiscs(board, BLUE).length == 0){
            winner = findTheLeader(board);
        }

        String state;
        if (winner == RED) {
            state="*The red player is the winner !!*";
        }
        else if (winner == BLUE) {
            state="* The blue player is the winner !! *";
        }
        else{
            state="= DRAW =";
        }
        mainActivity.getHandler().obtainMessage(0,state).sendToTarget();
    }

    //thread will wait on the object, and be notified when there is a new input.
    public Pair<Integer,Integer> syncGetInput() throws InterruptedException {
        synchronized (lock){
            while(!lock.get()) {
                lock.wait();
            }
            Pair<Integer,Integer> p=new Pair<>(mainActivity.inputVars[0],mainActivity.inputVars[1]);
            //fromRow=mainActivity.inputVars[0];
            //fromCol=mainActivity.inputVars[1];
            lock.set(false);
            return p;
        }

    }

    /* --------------------------------------------------------- *
     * Get a complete (possibly a sequence of jumps) move        *
     * from a human player.                                      *
     * --------------------------------------------------------- */
    public  int[][] getPlayerFullMove(int[][] board, int player) throws InterruptedException {
        // Get first move/jump
        int fromRow = -1, fromCol = -1, toRow = -1, toCol = -1;
        boolean jumpingMove = canJump(board, player);
        boolean badMove   = true;

        while (badMove) {
            if (player == 1){
                mainActivity.getHandler().obtainMessage(0,"Red, Please play:").sendToTarget();

                //System.out.println("Red, Please play:");
            } else {
                mainActivity.getHandler().obtainMessage(0,"Blue, Please play:").sendToTarget();
            }
            Pair<Integer,Integer> p=syncGetInput();
            fromRow=p.first;
            fromCol=p.second;
            int[][] moves = jumpingMove ? getAllBasicJumps(board, player) : getAllBasicMoves(board, player);
            markPossibleMoves(board, moves, fromRow, fromCol, MARK);
            Pair<Integer,Integer> px=syncGetInput();
            toRow=px.first;
            toCol=px.second;
            markPossibleMoves(board, moves, fromRow, fromCol, EMPTY);

            badMove = !isMoveValid(board, player, fromRow, fromCol, toRow, toCol);
            if (badMove){
                mainActivity.getHandler().obtainMessage(0,"\nThis is an illegal move").sendToTarget();
            }
        }

        // Apply move/jump
        board = playMove(board, player, fromRow, fromCol, toRow, toCol);
        showBoard(board);

        // Get extra jumps
        if (jumpingMove) {
            boolean longMove = (getRestrictedBasicJumps(board, player, toRow, toCol).length > 0);
            while (longMove) {
                fromRow = toRow;
                fromCol = toCol;

                int[][] moves = getRestrictedBasicJumps(board, player, fromRow, fromCol);

                boolean badExtraMove = true;
                while (badExtraMove) {
                    markPossibleMoves(board, moves, fromRow, fromCol, MARK);
                    mainActivity.getHandler().obtainMessage(0,"Continue jump:").sendToTarget();
                    Pair<Integer,Integer> pxx=syncGetInput();
                    toRow=pxx.first;
                    toCol=pxx.second;
                    markPossibleMoves(board, moves, fromRow, fromCol, EMPTY);

                    badExtraMove = !isMoveValid(board, player, fromRow, fromCol, toRow, toCol);
                    if (badExtraMove){
                        mainActivity.getHandler().obtainMessage(0,"\nThis is an illegal jump destination :(").sendToTarget();
                    }

                }

                // Apply extra jump
                board = playMove(board, player, fromRow, fromCol, toRow, toCol);
                showBoard(board);

                longMove = (getRestrictedBasicJumps(board, player, toRow, toCol).length > 0);
            }
        }
        return board;
    }


    /* --------------------------------------------------------- *
     * Get a complete (possibly a sequence of jumps) move        *
     * from a strategy.                                          *
     * --------------------------------------------------------- */
    public  int[][] getStrategyFullMove(int[][] board, int player, int strategy) {
        if (strategy == RANDOM)
            board = randomPlayer(board, player);
        else if (strategy == DEFENSIVE)
            board = defensivePlayer(board, player);
        else if (strategy == SIDES)
            board = sidesPlayer(board, player);

        showBoard(board);
        return board;
    }


    /* --------------------------------------------------------- *
     * Get a strategy choice before the game.                    *
     * --------------------------------------------------------- */
    public  int getStrategyChoice() {

        return startegy;
    }


    /* --------------------------------------- *
     * Print the possible moves                *
     * --------------------------------------- */
    public  void printMoves(int[][] possibleMoves) {
        for (int i = 0;  i < 4;  i = i+1) {
            for (int j = 0;  j < possibleMoves.length;  j = j+1)
                System.out.print(" " + possibleMoves[j][i]);
            System.out.println();
        }
    }


    /* --------------------------------------- *
     * Mark/unmark the possible moves          *
     * --------------------------------------- */
    public  void markPossibleMoves(int[][] board, int[][] moves, int fromRow, int fromColumn, int value) {
        for (int i = 0;  i < moves.length;  i = i+1)
            if (moves[i][0] == fromRow  &  moves[i][1] == fromColumn)
                board[moves[i][2]][moves[i][3]] = value;

        showBoard(board);
    }


    /* --------------------------------------------------------------------------- *
     * Shows the board in a graphic window                                         *
     * you can use it without understanding how it works.                          *
     * --------------------------------------------------------------------------- */
    public  void showBoard(int[][] board) {
        System.out.println("@@@@@@ in showBoard");
       // printMatrix(board);
        mainActivity.showBoard(board);
    }


    /* --------------------------------------------------------------------------- *
     * Print the board              					                           *
     * you can use it without understanding how it works.                          *
     * --------------------------------------------------------------------------- */
    public  void printMatrix(int[][] matrix){
        for (int i = matrix.length-1; i >= 0; i = i-1){
            for (int j = 0; j < matrix.length; j = j+1){
                System.out.format("%4d", matrix[i][j]);
            }
            System.out.println();
        }
    }

}
