package com.turin.tur.main.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.LevelInterfaz;
import com.turin.tur.main.diseno.TouchInfo;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.Visound;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.screens.ResultsScreen;
import com.turin.tur.main.util.CameraHelper;
import com.turin.tur.main.util.Constants;

public class LevelController implements InputProcessor {

	private static final String TAG = LevelController.class.getName();
	
	// Cosas relacionadas con la interfaz grafica
	public OrthographicCamera camera;
	public CameraHelper cameraHelper;

	// Copia de variables globales
	public LevelInterfaz levelInterfaz;
	private Visound game;
	private Level level; //Informacion del nivel cargado
	
	// Cosas relacionadas con los elementos del juego
	public Array<TouchInfo> touchSecuence = new Array<TouchInfo>();
	public Trial trial;
	public boolean nextTrialPending = false; // Genera la señal de que hay que cambiar de trial (para esperar a que finalicen cuestiones de animacion) 
	
	public final float blankTime = 0.2f; // Tiempo que debe dejar la pantalla en blanco entre trial y trial (en segundos) 
	public float currentblankTime = this.blankTime;
	
	public LevelController(Visound game, Level level) {
	
		this.game = game; // Hereda la info del game (cosa de ventanas y eso)
		this.level = level; 
		this.initCamera();
		this.game.expActivo.initLevel(this.level);
		
		// Selecciona el trial que corresponda
		this.trial = this.game.expActivo.getTrial();
		this.currentblankTime = 0;
		this.levelInterfaz = new LevelInterfaz(this.level, this.trial, this.game.expActivo);

		
	}

	private void initCamera() {
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		cameraHelper = new CameraHelper();
	}

	public void update(float deltaTime) {
		this.currentblankTime = this.currentblankTime + deltaTime;
		
		if (this.currentblankTime > this.blankTime) {
		
			// Actualiza el trial
			this.trial.update(deltaTime);
		
			// actualiza cosas generales
			cameraHelper.update(deltaTime);
			
			// Procesa cambios de trial si los hay pendientes
			if (trial.checkTrialCompleted()) {
				this.game.expActivo.returnAnswer(this.trial.lastAnswer());
				if (this.game.expActivo.islevelCompleted()) {
					this.game.expActivo.stopLevel();
					this.goToResults();
				} else {
					this.trial = this.game.expActivo.getTrial();
					this.currentblankTime = 0;
				}
			}
		}
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// Back to Menu
		if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
			this.game.expActivo.interrupt();
			goToResults();
		}
		return false;
	}

	
	private void goToResults() {
		trial.runningSound.stop();
		game.setScreen(new ResultsScreen(game, this.level));
	}
	
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (Gdx.graphics.getFramesPerSecond()>40) {
			// Crea un evento de toque
			TouchInfo touch = new TouchInfo();
			// calcula el toque en pantalla
			touch.coordScreen = new Vector3(screenX, screenY, 0);
			// calcula el toque en el juego 
			touch.coordGame = camera.unproject(touch.coordScreen.cpy()); // PREGUNTA: si no le pongo el copy, toma como el mismo vector y sobreescribe el coordScreen. RARO
			
			// procesa la info del toque en funcion de otros elementos del juego
			
			boolean elementoTocado = false;
			Box boxTocada = null;
			// se fija si se toco alguna imagen training
			for (Box box : this.trial.trainigBoxes) {
				if (box.spr.getBoundingRectangle().contains(touch.coordGame.x, touch.coordGame.y)) {
					elementoTocado = true;
					boxTocada = box;
				}
			}
			// se fija si se toco alguna imagen answer
			for (Box box : this.trial.testBoxes) {
				if (box.spr.getBoundingRectangle().contains(touch.coordGame.x, touch.coordGame.y)) {
					elementoTocado = true;
					boxTocada = box;
				}
			}
			
			if (elementoTocado) {
				this.trial.boxSelected(boxTocada);
			}
			
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
