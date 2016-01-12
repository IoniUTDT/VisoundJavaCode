package com.turin.tur.main.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.turin.tur.main.util.Assets;

public abstract class AbstractGameScreen implements Screen {
	
	public Game game;
	public Assets assets= new Assets();
	// Informacion general
	
	public AbstractGameScreen (Game game) {
		this.game = game;
	}
	
	@Override
	public abstract void render (float deltaTime);
	@Override
	public abstract void resize (int width, int height);
	@Override
	public abstract void show ();
	@Override
	public abstract void hide ();
	@Override
	public abstract void pause ();
	
	@Override
	public void resume () {
		assets = new Assets();
	}
	
	@Override
	public void dispose () {
		assets.dispose();
	}
}
