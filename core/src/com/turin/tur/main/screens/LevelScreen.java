package com.turin.tur.main.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.Trial.RunningSound.NEXT;
import com.turin.tur.main.experiments.Experiment;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.logic.LevelController;
import com.turin.tur.main.logic.LevelRenderer;
import com.turin.tur.main.util.LevelAsset;

public class LevelScreen extends AbstractGameScreen  {
	
	// Constantes 
	private static final String TAG = LevelScreen.class.getName();
	
	// Clases que se crean para manipular el contenido
	private LevelController levelController;
	private LevelRenderer levelRenderer;
	private LevelAsset levelAssets;
	private Experiment exp;
	
	// Variables del level
	private int levelNumber;
	private boolean paused;
	private Session session;
	
	
	public LevelScreen (Game game, int level, Session session, Experiment exp) {
		super(game);
		this.exp = exp;
		this.session = session;
		this.levelNumber=level;
		// this.levelAssets = new LevelAsset (level);
	}

	@Override
	public void render (float deltaTime) {
		// Do not update game world when paused.
		if (!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			levelController.update(deltaTime);
			levelController.trial.runningSound.update(deltaTime);
		}
		// Sets the clear screen color to: Cornflower Blue
		Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f,0xed /
				255.0f, 0xff / 255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Render level to screen
		levelRenderer.render();
	}
	
	@Override
	public void resize (int width, int height) {
		levelRenderer.resize(width, height);
	}

	@Override
	public void show () {
		this.levelAssets = new LevelAsset (this.levelNumber);
		Level level = new Level(levelNumber);
	    this.levelController = new LevelController(game, level, this.session, this.levelAssets, exp);
	    this.levelRenderer = new LevelRenderer(levelController);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide () {
		levelRenderer.dispose();
		this.levelAssets.dispose();
		Gdx.input.setCatchBackKey(false);
	}
	
	@Override
	public void pause () {
		// this.levelAssets.dispose();
		this.levelController.trial.runningSound.stop();
		paused = true;
	}
	
	@Override
	public void resume () {
		super.resume();
		this.levelController.trial.runningSound.action = NEXT.PLAY;
		// Only called on Android!
		paused = false;
	}
	
}
