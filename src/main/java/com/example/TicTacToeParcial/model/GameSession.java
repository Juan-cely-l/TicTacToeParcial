package com.example.TicTacToeParcial.model;

import java.util.Arrays;

public class GameSession {
    private String[] board;
    private String playerX;
    private String playerO;
    private String turn;
    private boolean gameOver;

    public GameSession() {
        this.board = new String[9];
        Arrays.fill(this.board, null);
        this.turn = "X";
        this.gameOver = false;
    }

    public String[] getBoard() {
        return board;
    }

    public void setBoard(String[] board) {
        this.board = board;
    }

    public String getPlayerX() {
        return playerX;
    }

    public void setPlayerX(String playerX) {
        this.playerX = playerX;
    }

    public String getPlayerO() {
        return playerO;
    }

    public void setPlayerO(String playerO) {
        this.playerO = playerO;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isFull() {
        return playerX != null && playerO != null;
    }

    public void switchTurn() {
        this.turn = this.turn.equals("X") ? "O" : "X";
    }

    public void makeMove(int index, String player) {
        if (index >= 0 && index < 9 && board[index] == null) {
            board[index] = player;
        }
    }

    public void reset() {
        Arrays.fill(this.board, null);
        this.turn = "X";
        this.gameOver = false;
    }
}

