package com.turin.tur.main.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.LevelInterfaz;
import com.turin.tur.main.diseno.RunningSound;
import com.turin.tur.main.diseno.TouchInfo;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.Visound;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.screens.ResultsScreen;
import com.turin.tur.main.util.CameraHelper;
import com.turin.tur.main.util.Constants;

public class LevelController implements InputProcessor {

	private static final String TAG = LevelController.class.getName();
	public EstadoLoop estadoLoop;

	public enum EstadoLoop {
		EsperandoSeeleccionDeBox(true), // Cuando esta esperando que el usuario selecciones una categoria
		ProcesandoToque(false),  // Cuando se esta procesando los toquees y esas cosas
		Iniciando(false), // Iniciando el programa
		PantallaBlanca(false), // El programa esta en el tiempo de reseteo de la imagen para separar un trial de otro
		EsperandoConfianza(true), // El programa esta esperando que se indique nivel de confianza 
		DandoFeedback(true), // Dando feedback
		TrialInterrumpido(false), // Reproduciendo sonido (en modo tutorial)
		ListoParaProcesarBox(false), // Hay un toque listo para procesar y activar el feedback, el audio o el proximo trial.
		CambiarTrial (false), // Indica que se debe buscar el proximo trial
		LevelFinalizado (false) // Indica que se debe finalizar el nivel ordinariamente
		;
		
		public boolean update;
		
		EstadoLoop(boolean update) {
			this.update = update;
		}
		
	}
	
	// Cosas relacionadas con la interfaz grafica
	public OrthographicCamera camera;
	public CameraHelper cameraHelper;

	// Copia de variables globales
	public LevelInterfaz levelInterfaz;
	private Visound game;
	private Level level; //Informacion del nivel cargado
	public RunningSound runningSound; // Maneja el sonido
	
	// Cosas relacionadas con los elementos del juego
	public Array<TouchInfo> touchSecuence = new Array<TouchInfo>();
	public Trial trial;
	
	public final float blankTime = 0.2f; // Tiempo que debe dejar la pantalla en blanco entre trial y trial (en segundos) 
	public float timeInTrial = 0;
	public Box boxTocada;
	private float confianzaReportada;
		
	public LevelController(Visound game, Level level) {
	
		this.estadoLoop = EstadoLoop.Iniciando;
		this.game = game; // Hereda la info del game (cosa de ventanas y eso)
		this.level = level; 
		this.initCamera();
		this.game.expActivo.initLevel(this.level);
		this.runningSound = new RunningSound(this.level.levelAssets);
		
		// Selecciona el trial que corresponda
		this.trial = this.game.expActivo.getNextTrial();
		this.levelInterfaz = new LevelInterfaz(this.level, this.trial, this.game.expActivo);

		// Indica que el programa esta listo para seleccionar un box
		this.estadoLoop = EstadoLoop.EsperandoSeeleccionDeBox;
		
	}

	private void initCamera() {
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		cameraHelper = new CameraHelper();
	}

	public void update(float deltaTime) {
		//Gdx.app.debug(TAG, estadoLoop.toString());
		this.timeInTrial = this.timeInTrial + deltaTime;
		
		if (this.estadoLoop == EstadoLoop.PantallaBlanca) {
			if (this.timeInTrial > this.blankTime) {
				this.estadoLoop = EstadoLoop.EsperandoSeeleccionDeBox;
			}
		}
		
		if (this.estadoLoop == EstadoLoop.ListoParaProcesarBox) {
			this.trial.boxSelected(this); // El box tiene que decidir si el estatus cambia a feedback, a reproducri sonido o a cambiar trial
		}
		
		if (this.estadoLoop.update) {
			// Actualiza el trial
			this.trial.update(deltaTime, this); // El trial tiene que decidir cuando termina el feedback o la reproduccion de sonido para activar el cambio de trial
			// actualiza cosas generales
			cameraHelper.update(deltaTime);
		}
			
		if (this.estadoLoop == EstadoLoop.CambiarTrial) {
			this.game.expActivo.returnAnswer(this.boxTocada.answerCorrect, this.confianzaReportada);
			if (this.game.expActivo.islevelCompleted()) {
				this.estadoLoop = EstadoLoop.LevelFinalizado;
				this.game.expActivo.levelCompleted();
				this.goToResults();
			} else {
				this.trial = this.game.expActivo.getNextTrial();
				this.timeInTrial = 0;
				this.estadoLoop = EstadoLoop.PantallaBlanca;
			}
		}
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// Back to Menu
		if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
			this.estadoLoop = EstadoLoop.TrialInterrumpido;
			this.game.expActivo.interrupt();
			goToResults();
		}
		if (keycode == Keys.DPAD_DOWN) {
			Visound.volumen = Visound.volumen - 0.1f;
			if (Visound.volumen < 0.1f) {
				Visound.volumen = 0.1f;
			}
			this.runningSound.sound.setVolume(this.runningSound.idSound, Visound.volumen);
		}
		if (keycode == Keys.DPAD_UP) {
			Visound.volumen = Visound.volumen + 0.1f;
			if (Visound.volumen > 1f) {
				Visound.volumen = 1f;
			}
			this.runningSound.sound.setVolume(this.runningSound.idSound, Visound.volumen);
		}
		return false;
	}

	
	private void goToResults() {
		runningSound.stop();
		game.setScreen(new ResultsScreen(game, this.level));
	}
	
	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (Gdx.graphics.getFramesPerSecond()>40) {
			if (this.estadoLoop == EstadoLoop.EsperandoSeeleccionDeBox) {
				this.estadoLoop = EstadoLoop.ProcesandoToque;
				// Crea un evento de toque
				TouchInfo touch = new TouchInfo();
				// calcula el toque en pantalla
				touch.coordScreen = new Vector3(screenX, screenY, 0);
				// calcula el toque en el juego 
				touch.coordGame = camera.unproject(touch.coordScreen.cpy()); // PREGUNTA: si no le pongo el copy, toma como el mismo vector y sobreescribe el coordScreen. RARO
				
				// procesa la info del toque en funcion de otros elementos del juego
				
				boolean elementoTocado = false;
				this.boxTocada = null;
				// se fija si se toco alguna imagen training
				for (Box box : this.trial.trainigBoxes) {
					if (box.spr.getBoundingRectangle().contains(touch.coordGame.x, touch.coordGame.y)) {
						elementoTocado = true;
						this.boxTocada = box;
					}
				}
				// se fija si se toco alguna imagen answer
				for (Box box : this.trial.testBoxes) {
					if (box.spr.getBoundingRectangle().contains(touch.coordGame.x, touch.coordGame.y)) {
						elementoTocado = true;
						this.boxTocada = box;
					}
				}
				
				if (elementoTocado) {
					if (MathUtils.randomBoolean(this.level.jsonLevel.genericSetup.confianceProbability)) {
						this.estadoLoop = EstadoLoop.EsperandoConfianza;
					} else {
						this.estadoLoop = EstadoLoop.ListoParaProcesarBox;
						//this.trial.boxSelected(boxTocada);
					}
				} else {
					this.estadoLoop = EstadoLoop.EsperandoSeeleccionDeBox;
				}
			}
			
			if (this.estadoLoop == EstadoLoop.EsperandoConfianza) {
				this.confianzaReportada = -1;
				//TODO que se fije si se toco la barra de confianza y cuanto dio 
				this.confianzaReportada = -1;
				this.estadoLoop = EstadoLoop.ListoParaProcesarBox;
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
