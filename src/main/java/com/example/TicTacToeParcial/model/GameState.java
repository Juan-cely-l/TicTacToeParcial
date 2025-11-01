package com.example.TicTacToeParcial.model;

public class GameState {
    private String[] board;
    private String status;
    private String currentTurn;
    private String playerX;
    private String playerO;

    public GameState() {
        this.board = new String[9];
    }

    public GameState(String[] board, String status, String currentTurn) {
        this.board = board;
        this.status = status;
        this.currentTurn = currentTurn;
    }

    public String[] getBoard() {
        return board;
    }

    public void setBoard(String[] board) {
        this.board = board;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
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
}

