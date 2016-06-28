package com.turin.tur.main.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.turin.tur.main.experiments.Experiment.GenericExp;
import com.turin.tur.Visound;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.RunningSound.NEXT;
import com.turin.tur.main.logic.LevelController;
import com.turin.tur.main.logic.LevelController.EstadoLoop;
import com.turin.tur.main.logic.LevelRenderer;
import com.turin.tur.main.util.LevelAsset;

public class LevelScreen extends AbstractGameScreen  {
	
	// Constantes 
	private static final String TAG = LevelScreen.class.getName();
	
	// Clases que se crean para manipular el contenido
	private LevelController levelController;
	private LevelRenderer levelRenderer;
	private Level level;
	
	// Variables del level
	private int levelNumber;
	private boolean paused;
	
	public LevelScreen (Visound game, int levelNumber, String levelName) {
		super(game);
		for (GenericExp exp : this.game.exps) {
			if (exp.getLevelName().equals(levelName)) {
				this.game.expActivo = exp;
			}
		}
		this.levelNumber=levelNumber;
	}

	@Override
	public void render (float deltaTime) {
		// Do not update game world when paused.
		if (!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			levelController.update(deltaTime);
			levelController.runningSound.update(deltaTime);
		}
		// Sets the clear screen color to: Cornflower Blue
		Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f,0xed /
				255.0f, 0xff / 255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Render level to screen
		if (levelController.estadoLoop != EstadoLoop.PantallaBlanca) {
			levelRenderer.render();
		}
	}
	
	@Override
	public void resize (int width, int height) {
		levelRenderer.resize(width, height);
	}

	@Override
	public void show () {
		this.level = new Level(levelNumber);
		level.levelAssets = new LevelAsset (this.levelNumber);
	    this.levelController = new LevelController(game, level);
	    this.levelRenderer = new LevelRenderer(levelController);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide () {
		levelRenderer.dispose();
		this.level.levelAssets.dispose();
		Gdx.input.setCatchBackKey(false);
	}
	
	@Override
	public void pause () {
		// this.levelAssets.dispose();
		this.levelController.runningSound.stop();
		paused = true;
	}
	
	@Override
	public void resume () {
		super.resume();
		this.levelController.runningSound.action = NEXT.PLAY;
		// Only called on Android!
		paused = false;
	}
	
}
