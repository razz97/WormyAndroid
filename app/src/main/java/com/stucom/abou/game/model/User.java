package com.stucom.abou.game.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class User implements Comparable<User>, Serializable  {

    private int id;
    private String name;
    private String image;
    private String from;
    private String totalScore;
    private int lastLevel;
    private int lastScore;
    private List<Score> scores;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public int getLastLevel() {
        return lastLevel;
    }

    public void setLastLevel(int lastLevel) {
        this.lastLevel = lastLevel;
    }

    public int getLastScore() {
        return lastScore;
    }

    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
    }

    public List<Score> getScores() { return scores; }

    public void setScores(List<Score> scores) { this.scores = scores; }

    @Override
    public int compareTo(User o) {
        return Integer.parseInt(o.getTotalScore()) - Integer.parseInt(this.getTotalScore());
    }
}
