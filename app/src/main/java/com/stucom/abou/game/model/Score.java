package com.stucom.abou.game.model;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Score  implements Serializable {

    private int level;
    private int score;
    private String playedAt;

    public int getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }

    public String getPlayedAt() {
        return playedAt;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setPlayedAt(String playedAt) {
        this.playedAt = playedAt;
    }
}