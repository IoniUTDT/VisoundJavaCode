package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.Visound;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.LevelAsset;

public class RunningSound {
	
	private static final String TAG = RunningSound.class.getName();
	
	public ExperimentalObject contenido; // Todo el objeto que se esta reproduciendo
	public Sound sound; // Elemento de sonido
	public boolean running = false; // Si se esta reproduciendo o no
	public int id; // El id que identifica el recurso del ultimo sonido
	public long instance; // instancia que identifica cada reproduccion unequivocamente
	private LevelAsset assets;
	public long idSound;
	
	// Info para el update
	public NEXT action = NEXT.NADA;
	public float playTime; 
	public ExperimentalObject nextContenido;
	

	public RunningSound (LevelAsset assets) {
		this.assets = assets;
	}
	
	public void update(float deltaTime) {
		if (this.running) {
			this.playTime = this.playTime + deltaTime;
		} 
		if (this.playTime > Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA) {
			this.stop();
		}
		if (this.action == NEXT.PLAY) {
			if (Gdx.graphics.getFramesPerSecond()>40) {
				if (nextContenido!=null) {
					this.play();
					this.playTime =0;
					this.action = NEXT.NADA;
				}
			}
		}
	}
	
	public void play() {
		// Primer detiene cualquier reproduccion previa 
		if (running) {
			stop();
		}
		// Prepara la info en la clase
		contenido = nextContenido;
		id = contenido.resourceId.id;
		instance = TimeUtils.millis();

		// Cargamos el sonido
		this.sound = this.assets.sound(this.id);
		this.idSound = this.sound.play(Visound.volumen); 
		this.running = true;
	}

	public void stop() {
		if (running) {
			
			// Detiene el sonido
			sound.stop();
			running = false;
		}
	}
	
	public enum NEXT {
		PLAY,STOP,NADA;
	}
}

