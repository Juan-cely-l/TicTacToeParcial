package com.example.TicTacToeParcial.model;

public class Move {
    private int index;
    private String player;
    private String gameId;

    public Move() {
    }

    public Move(int index, String player, String gameId) {
        this.index = index;
        this.player = player;
        this.gameId = gameId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}

