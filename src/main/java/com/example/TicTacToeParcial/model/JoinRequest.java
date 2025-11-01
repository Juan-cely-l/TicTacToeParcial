package com.example.TicTacToeParcial.model;

public class JoinRequest {
    private String playerId;

    public JoinRequest() {
    }

    public JoinRequest(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}

