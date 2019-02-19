package com.stucom.abou.game.model;

import java.io.Serializable;

public class Score  implements Serializable {

    int level;
    int score;
    String playedAt;

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