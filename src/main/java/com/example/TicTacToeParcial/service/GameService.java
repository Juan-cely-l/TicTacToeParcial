package com.example.TicTacToeParcial.service;

import com.example.TicTacToeParcial.model.GameSession;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final ConcurrentHashMap<String, GameSession> games = new ConcurrentHashMap<>();

    public GameSession getOrCreateGame(String gameId) {
        return games.computeIfAbsent(gameId, id -> new GameSession());
    }

    public GameSession getGame(String gameId) {
        return games.get(gameId);
    }

    public String joinGame(String gameId, String playerId) {
        GameSession session = getOrCreateGame(gameId);

        if (session.getPlayerX() == null) {
            session.setPlayerX(playerId);
            return "X";
        } else if (session.getPlayerO() == null) {
            session.setPlayerO(playerId);
            return "O";
        } else if (session.getPlayerX().equals(playerId)) {
            return "X";
        } else if (session.getPlayerO().equals(playerId)) {
            return "O";
        } else {
            return "FULL";
        }
    }

    public String calculateWinner(String[] board) {
        int[][] lines = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
        };

        for (int[] line : lines) {
            int a = line[0];
            int b = line[1];
            int c = line[2];
            if (board[a] != null && board[a].equals(board[b]) && board[a].equals(board[c])) {
                return board[a];
            }
        }
        return null;
    }

    public boolean isBoardFull(String[] board) {
        for (String cell : board) {
            if (cell == null) {
                return false;
            }
        }
        return true;
    }

    public void resetGame(String gameId) {
        GameSession session = games.get(gameId);
        if (session != null) {
            session.reset();
        }
    }
}

