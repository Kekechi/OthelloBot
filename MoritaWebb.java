/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mrjaffesclass.othello;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author kekec
 */
public class MoritaWebb extends Player {

    private final Player opponent;
    private final HashSet < Position > takenCords;

    /**
     * Constructor
     * @param color Player color: one of Constants.BLACK or Constants.WHITE
     */
    public MoritaWebb(int color) {
        super(color);
        opponent = new Player(-color);
        // A list of coordinates that are already taken
        takenCords = new HashSet < > ();
        takenCords.add(new Position(3, 3));
        takenCords.add(new Position(4, 3));
        takenCords.add(new Position(3, 4));
        takenCords.add(new Position(4, 4));
    }

    /**
     *
     * @param board
     * @return The player's next move
     */
    @Override
    public Position getNextMove(Board board) {
        // Updates the list of taken coordinates
        addTakenCords(board);
        // Runs main algorithm
        Position move = minimax(board, 9, Integer.MIN_VALUE, Integer.MAX_VALUE, this).getBestMove();
        takenCords.add(move);
        return move;

    }

    //Main algorithm class
    /*
     * @param board the current game board
     * @param depth the number of moves ahead the player will look
     * @param alpha the minimum score that the maximizing player can expect
     * @param beta the maximum score tha the minimum player can expect
     * @param player which player you are
     * @return the best move that the player can make based on the current game board
     */
    public minimaxReturn minimax(Board board, int depth, int alpha, int beta, Player player) {
        //Variable setup
        minimaxReturn best = new minimaxReturn(null, 0);
        int tmp;
        ArrayList < Position > possibleMoves = getPossibleMoves(board, player);

        //If the algorithm is at the base of the tree, return the score of the node
        if (depth == 0) {
            best.setBestScore(score_board(board));
            return best;
        }
        if (possibleMoves.isEmpty()) {
            if (takenCords.size() == 64) {
                best.setBestScore(score_board(board));
                return best;
            }
            if (player == this) {
                player = opponent;
            } else {
                player = this;
            }
        }

        //If the player is the maximizing player...
        if (player == this) {
            //Set the best score to equal the minimum possible value with the available system (-inf)
            best.setBestScore(Integer.MIN_VALUE);

            for (Position move: possibleMoves) {
                //Creates a copy of the board to to test the decision tree on
                Board copyBoard = copyBoard(board, this, move);
                takenCords.add(move);
                tmp = minimax(copyBoard, depth - 1, alpha, beta, opponent).getBestScore();
                takenCords.remove(move);
                //Checks if the node chosen is the best move
                if (best.getBestScore() < tmp) {
                    best.setBestScore(tmp);
                    best.setBestMove(move);
                }
                //Checks if the best score is greater than the minimizing value
                if (best.getBestScore() >= beta) {
                    break;
                }
                alpha = Math.max(best.getBestScore(), alpha);
            }
        }

        //If the player is the minimizing player
        else {
            //Set the best score to equal the maximum possible value with the available system (inf)
            best.setBestScore(Integer.MAX_VALUE);
            for (Position move: possibleMoves) {
                //Creates a copy of the board to to test the decision tree on
                Board copyBoard = copyBoard(board, opponent, move);
                takenCords.add(move);
                tmp = minimax(copyBoard, depth - 1, alpha, beta, this).getBestScore();
                takenCords.remove(move);
                //Checks if the node chosen is the best move
                if (best.getBestScore() > tmp) {
                    best.setBestScore(tmp);
                    best.setBestMove(move);
                }
                //Checks if the best score is greater than the minimizing value
                if (best.getBestScore() <= alpha) {
                    break;
                }
                beta = Math.min(best.getBestScore(), beta);
            }
        }
        return best;
    }
    /*A function to add all of the owned coordinates to a list
     * speeds up processing time
     * @param board the current board state*/
    public void addTakenCords(Board board) {
        Position checkPos;
        for (int column = 0; column < Constants.SIZE; column++) {
            for (int row = 0; row < Constants.SIZE; row++) {
                checkPos = new Position(row, column);
                if (takenCords.contains(checkPos)) {
                    continue;
                }
                if (board.getSquare(checkPos).getStatus() != Constants.EMPTY) {
                    takenCords.add(checkPos);
                }
            }
        }
    }

    /*Gets all of the possible moves for a player
     * @param board the current board
     * @param player the current player
     * @return an array of all of the possible moves*/
    public ArrayList < Position > getPossibleMoves(Board board, Player player) {
        Position checkPos;
        ArrayList < Position > legalMoves = new ArrayList < Position > ();
        for (int column = 0; column < Constants.SIZE; column++) {
            for (int row = 0; row < Constants.SIZE; row++) {
                checkPos = new Position(row, column);
                if (takenCords.contains(checkPos)) {
                    continue;
                }
                if (board.isLegalMove(player, checkPos)) {
                    legalMoves.add(checkPos);
                }
            }
        }
        return legalMoves;
    }



    /*A function to create a copy of the board so you can move without moving
     * @param board the current board
     * @param player the current player
     * @param move which move you want to test
     * @return a copy of the current board*/
    public Board copyBoard(Board board, Player player, Position move) {
        //variable initialization
        Position checkPos;
        int squareColor;
        Board copy = new Board();
        //iterates through the board
        for (int column = 0; column < Constants.SIZE; column++) {
            for (int row = 0; row < Constants.SIZE; row++) {
                //creates a copy of the current square
                checkPos = new Position(row, column);
                squareColor = board.getSquare(checkPos).getStatus();
                //adds the square and the state that it is in to the copy of the board
                if (squareColor == this.getColor()) {
                    copy.setSquare(this, checkPos);
                } else if (squareColor == opponent.getColor()) {
                    copy.setSquare(opponent, checkPos);
                }
            }
        }
        //Makes the move that the player specifies on the copy of the board
        copy.makeMove(player, move);
        return copy;
    }

    /*A utility function to get the average of a list of data
     * @param data an integer list of the data you want to average
     * @return the average of the presented data*/
    public float average(int[] data) {
        int total = 0;
        int e = 0;
        float avg = 0;
        for (e = 0; e < data.length; e++) {
            total = total + data[e];
        }
        avg = total / e;
        return avg;
    }

    /*Evaluates the board and returns a 'score' that helps decide whether this is a good investment to make
     * @param board the current board
     * @return an integer score based on the amount of corners and edges owned and the moves you can make*/
    public int score_board(Board board) {
        //Variable initialization
        int cornersOwned = 0;
        int edgesOwned = 0;
        int movesPossible = 0;
        Position posCheck;
        //iterates through the board
        for (int column = 0; column < Constants.SIZE; column++) {
            for (int row = 0; row < Constants.SIZE; row++) {
                //the position to check
                posCheck = new Position(row, column);
                //if the stored position is a corner, increase the cornersOwned value
                if (column == 0 && row == 0 || column == 0 && row == 7 || column == 7 && row == 0 || column == 7 && row == 7) {
                    cornersOwned++;
                }
                //if the stored position is on an edge, increase the edgesOwned value
                if (column == 0 || row == 0 || column == 7 || row == 7) {
                    edgesOwned++;
                }
                //Counts the number of legal moves
                if (board.isLegalMove(this, posCheck)) {
                    movesPossible++;
                }
            }
        }
        //Creates an integer list of the data to make it easier to handle
        int data[] = {
            cornersOwned,
            edgesOwned,
            movesPossible
        };
        //Returns an average of the data with a heavy bias towards corners and a medium bias towards edges
        return Math.round(average(data) * (cornersOwned * 4) * (edgesOwned));
    }
}

//A class to help return multiple values that the minimax algorithm makes
class minimaxReturn {
    //variable initialization
    private Position bestMove;
    private int bestScore;

    minimaxReturn(Position bestMove, int bestScore) {
        this.bestMove = bestMove;
        this.bestScore = bestScore;
    }

    //returns the best score of a given board
    public int getBestScore() {
        return this.bestScore;
    }

    //returns the best possible calculated move
    public Position getBestMove() {
        return this.bestMove;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public void setBestMove(Position bestMove) {
        this.bestMove = bestMove;
    }

}
 
