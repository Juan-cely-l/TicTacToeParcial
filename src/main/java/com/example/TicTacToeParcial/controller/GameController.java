package com.example.TicTacToeParcial.controller;

import com.example.TicTacToeParcial.model.*;
import com.example.TicTacToeParcial.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @MessageMapping("/game.join/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public GameState joinGame(@DestinationVariable String gameId, JoinRequest request) {
        String assignedPlayer = gameService.joinGame(gameId, request.getPlayerId());
        GameSession session = gameService.getGame(gameId);

        GameState state = new GameState();
        state.setBoard(session.getBoard());
        state.setCurrentTurn(session.getTurn());
        state.setPlayerX(session.getPlayerX());
        state.setPlayerO(session.getPlayerO());

        if ("FULL".equals(assignedPlayer)) {
            state.setStatus("FULL");
        } else if (session.isFull()) {
            state.setStatus("READY");
        } else {
            state.setStatus("WAITING");
        }

        return state;
    }

    @MessageMapping("/game.play/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public GameState playMove(@DestinationVariable String gameId, Move move) {
        GameSession session = gameService.getGame(gameId);

        if (session == null) {
            session = gameService.getOrCreateGame(gameId);
        }

        GameState state = new GameState();
        state.setBoard(session.getBoard());
        state.setCurrentTurn(session.getTurn());
        state.setPlayerX(session.getPlayerX());
        state.setPlayerO(session.getPlayerO());

        if (session.isGameOver()) {
            state.setStatus("GAME_OVER");
            return state;
        }

        if (!session.getTurn().equals(move.getPlayer())) {
            state.setStatus("NOT_YOUR_TURN");
            return state;
        }

        if (session.getBoard()[move.getIndex()] != null) {
            state.setStatus("OCCUPIED");
            return state;
        }

        if (move.getIndex() < 0 || move.getIndex() > 8) {
            state.setStatus("INVALID");
            return state;
        }

        session.makeMove(move.getIndex(), move.getPlayer());

        String winner = gameService.calculateWinner(session.getBoard());
        if (winner != null) {
            session.setGameOver(true);
            state.setStatus("WINNER:" + winner);
            state.setBoard(session.getBoard());
            state.setCurrentTurn(session.getTurn());
            return state;
        }

        if (gameService.isBoardFull(session.getBoard())) {
            session.setGameOver(true);
            state.setStatus("DRAW");
            state.setBoard(session.getBoard());
            state.setCurrentTurn(session.getTurn());
            return state;
        }

        session.switchTurn();

        state.setStatus("OK");
        state.setBoard(session.getBoard());
        state.setCurrentTurn(session.getTurn());

        return state;
    }

    @MessageMapping("/game.reset/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public GameState resetGame(@DestinationVariable String gameId) {
        gameService.resetGame(gameId);
        GameSession session = gameService.getGame(gameId);

        GameState state = new GameState();
        state.setBoard(session.getBoard());
        state.setCurrentTurn(session.getTurn());
        state.setPlayerX(session.getPlayerX());
        state.setPlayerO(session.getPlayerO());
        state.setStatus("RESET");

        return state;
    }
}

