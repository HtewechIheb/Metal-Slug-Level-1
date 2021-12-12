package com.mygames.metalslug.misc;

import java.time.LocalDateTime;

public class ScoreEntry {
    private String playerName;
    private Integer score;
    private LocalDateTime time;

    public ScoreEntry(String playerName, Integer score, LocalDateTime time){
        this.playerName = playerName;
        this.score = score;
        this.time = time;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Integer getScore() {
        return score;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
