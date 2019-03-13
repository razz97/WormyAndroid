package com.stucom.abou.game.utils;

import android.media.MediaPlayer;
import android.media.SoundPool;

import alex_bou.stucom.com.alex.R;

public class Media {

    private static final Media ourInstance = new Media();

    private MediaPlayer playerMenu;
    private MediaPlayer playerGame;
    private SoundPool soundPool;
    private int coin;
    private int over;
    private int game;

    public static Media getInstance() {
        return ourInstance;
    }

    private Media() {
        playerMenu = MediaPlayer.create(App.getAppContext(), R.raw.menu);
        playerGame = MediaPlayer.create(App.getAppContext(), R.raw.game);
        soundPool = new SoundPool.Builder().build();
        coin = soundPool.load(App.getAppContext(), R.raw.coin,1);
        over = soundPool.load(App.getAppContext(), R.raw.over,1);
        game = soundPool.load(App.getAppContext(), R.raw.game,1);
    }

    public void startMenu() {
        playerMenu.start();
    }

    public void restartMenu() {
        playerMenu.pause();
        playerMenu.seekTo(0);
        playerMenu.start();
    }

    public void startGame() {
        playerMenu.pause();
        playerGame.start();
    }

    public void coin() {
        soundPool.play(coin,1,1,1,0,1);
    }

    public void gameOver() {
        playerMenu.start();
        playerGame.pause();
        soundPool.play(over,1,1,1,0,1);
    }

    public void stopAll() {
        playerMenu.pause();
        playerGame.pause();
    }


}
