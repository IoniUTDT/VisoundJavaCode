package com.turin.tur.main.diseno;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.turin.tur.main.diseno.RunningSound.NEXT;
import com.turin.tur.main.logic.LevelController.EstadoLoop;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.Constants;



public abstract class Boxes {

	/*
	 * La clase box es la que almacena la informacion visual, sonora, y espacial de que mostrar y donde en cada trial. Sirve como punto de interaccion basico del usuario con el programa 
	 */

	// Contantes
	public static final String TAG = Boxes.class.getName();
	
	public static abstract class Box {
		
		// Variable generales que definen a la caja
		public ExperimentalObject contenido; // Esta variable guarda toda la informacion del contenido de la caja usando una clase especialmente dise�ada para eso
		public Vector2 posicionCenter; // Esta es la posicion de la caja dada por las coordenadas de su centro. 
		public Sprite spr; // Guarda la imagen que se va a mostrar (se genera a partir del contenido de la caja)
		
	
		// Variables especificas de cada tipo pero que estan en la clase general porque se llaman desde afuera
		
		public void render(SpriteBatch batch, RunningSound runningSound) {
			// Render the main content of the box
			float x;
			float y;
			// Find the position of the main imagen and setup it
			spr.setSize(Constants.Box.TAMANO, Constants.Box.TAMANO);
			x = posicionCenter.x - Constants.Box.TAMANO / 2;
			y = posicionCenter.y - Constants.Box.TAMANO / 2;
			spr.setPosition(x, y);
			spr.draw(batch);
			specificRender(batch, runningSound);
		}
			
		protected abstract void specificRender (SpriteBatch batch, RunningSound runningSound);
		protected abstract void update(float deltaTime, RunningSound sound, EstadoLoop estadoLoop);
		public abstract void select(RunningSound runningSound);
		
		public void SetPosition(float xCenter, float yCenter) {
			this.posicionCenter.x = xCenter;
			this.posicionCenter.y = yCenter;
		}

	}
	
	/**
	 * Boxes pensados para realizar entrenamiento. La idea es que el usuario las toque y se reproduzca el sonido asociado.
	 * @author ionatan
	 *
	 */
	public static class TrainingBox extends Box {
	
		// Variables utiles para las cajas que son reproducibles
		private float soundDuracionReproduccion; //Tiempo total establecido para el sonido (ojo que no es necesariamente el tiempo total del sonido, pero se trabaja con sonidos a priori de longitud fija 
		private Sprite soundAnimationSpr; // imagen para mostrar la animacion de reproduccion del sonido 
		public boolean alreadySelected;
		
		public TrainingBox (ExperimentalObject contenido) {
			
			// Carga cosas relacionadas al contenido
			this.contenido = contenido;
			this.posicionCenter = new Vector2(0, 0);
			this.spr = this.contenido.imagen;
			
			
			// inicializa las variables que manejan la reproduccion del sonido
			this.soundDuracionReproduccion = Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA;	
			this.createSoundAnimationResources();
			
		}

		private void createSoundAnimationResources() {
			Pixmap pixmap = new Pixmap(10, 10, Format.RGBA8888);
			pixmap.setColor(0, 0, 0, 1);
			pixmap.fill();
			Texture texture = new Texture(pixmap);
			this.soundAnimationSpr = new Sprite (texture);
		}

	
		@Override
		protected void specificRender (SpriteBatch batch, RunningSound runningSound) {
			// Render the animation of the box
			if (runningSound.running) {
				if (runningSound.contenido == this.contenido) { // Esto funciona bien si el objeto cargado en el running proviene exactamente del mismo lugar que el cargado en el box. Hay que tener cuidado que si se repite contenido en dos boxes, sean instancias diferentes.
					soundAnimationSpr.setSize(Constants.Box.TAMANO_CONTORNO_X,Constants.Box.TAMANO_CONTORNO_Y);
					float x = posicionCenter.x - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_X /2;
					float y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_Y;
					float xShift = Constants.Box.TAMANO * runningSound.playTime / soundDuracionReproduccion;
					soundAnimationSpr.setPosition(x + xShift, y);
					soundAnimationSpr.draw(batch);
				}
			}
		}

		@Override
		public void select(RunningSound runningSound) {
			if (!this.contenido.noSound) {
				runningSound.action = NEXT.PLAY;
				runningSound.nextContenido = this.contenido;
			} else {
				runningSound.stop();
			}
		}

		@Override
		protected void update(float deltaTime, RunningSound runningSound, EstadoLoop estadoLoop) {
			if (runningSound.running) {
				if (runningSound.playTime > this.soundDuracionReproduccion) {
					runningSound.stop();
				}
			}	
		}
	}
		
	public static class OptionsBox extends Box {

		private float answerAnimationTime = 0; // Avance el la animacion de respuesta
		private Sprite answerUsedSprite; // Imagen con que se muestra la respuesta 
		private Sprite answerSprTrue; // Imagen para respuestas verdaderas
		private Sprite answerSprFalse; // Imagen para respuestas falsas
		public boolean givinFeedback;
		public boolean giveFeedback;
		public boolean answerCorrect;
		
		public OptionsBox (ExperimentalObject contenido,boolean feedback){
			// Carga cosas relacionadas al contenido
			this.giveFeedback = feedback;
			this.contenido = contenido;
			this.posicionCenter = new Vector2(0, 0);
			this.spr = this.contenido.imagen;
			
			
			// inicializa las variables relacionadas a la dinamica de la respuesta
			this.createAnswerAnimationResources();
		}

		private Pixmap createAnswerResources(boolean condicion) {
			Pixmap pixmap = new Pixmap(10, 10, Format.RGBA8888);
			if (condicion) {
				pixmap.setColor(0, 1, 0, 1);
			} else {
				pixmap.setColor(1, 0, 0, 1);
			}
			// crea un cuadrado relleno
			pixmap.fill();
			return pixmap;
		}

		private void createAnswerAnimationResources() {
			Pixmap pixmapTrue = createAnswerResources(true);
			Pixmap pixmapFalse = createAnswerResources(false);
			Texture textureTrue = new Texture(pixmapTrue);
			Texture textureFalse = new Texture(pixmapFalse);
			this.answerSprTrue = new Sprite(textureTrue);
			this.answerSprFalse = new Sprite(textureFalse);
		}

		private void contourRender(SpriteBatch batch) {
			float x;
			float y;
			if (this.answerCorrect) {
				answerUsedSprite = answerSprTrue;
			} else {
				answerUsedSprite = answerSprFalse;
			}
			// dibuja las esquinas
			answerUsedSprite.setSize(Constants.Box.SELECT_BOX_ANCHO_RTA,
					Constants.Box.SELECT_BOX_ANCHO_RTA);
			// sup izq
			x = posicionCenter.x - Constants.Box.TAMANO / 2
					- Constants.Box.SELECT_BOX_ANCHO_RTA;
			y = posicionCenter.y - Constants.Box.TAMANO / 2
					- Constants.Box.SELECT_BOX_ANCHO_RTA;
			answerUsedSprite.setPosition(x, y);
			answerUsedSprite.draw(batch);
			// sup der
			x = posicionCenter.x + Constants.Box.TAMANO / 2;
			y = posicionCenter.y - Constants.Box.TAMANO / 2
					- Constants.Box.SELECT_BOX_ANCHO_RTA;
			answerUsedSprite.setPosition(x, y);
			answerUsedSprite.draw(batch);
			// inf izq
			x = posicionCenter.x - Constants.Box.TAMANO / 2
					- Constants.Box.SELECT_BOX_ANCHO_RTA;
			y = posicionCenter.y + Constants.Box.TAMANO / 2;
			answerUsedSprite.setPosition(x, y);
			answerUsedSprite.draw(batch);
			// inf der
			x = posicionCenter.x + Constants.Box.TAMANO / 2;
			y = posicionCenter.y + Constants.Box.TAMANO / 2;
			answerUsedSprite.setPosition(x, y);
			answerUsedSprite.draw(batch);
			// dibuja los bordes
			// verticales
			answerUsedSprite.setSize(Constants.Box.SELECT_BOX_ANCHO_RTA,
					Constants.Box.TAMANO);
			// izq
			x = posicionCenter.x - Constants.Box.TAMANO / 2
					- Constants.Box.SELECT_BOX_ANCHO_RTA;
			y = posicionCenter.y - Constants.Box.TAMANO / 2;
			answerUsedSprite.setPosition(x, y);
			answerUsedSprite.draw(batch);
			// der
			x = posicionCenter.x + Constants.Box.TAMANO / 2;
			y = posicionCenter.y - Constants.Box.TAMANO / 2;
			answerUsedSprite.setPosition(x, y);
			answerUsedSprite.draw(batch);
			// horizontal
			answerUsedSprite.setSize(Constants.Box.TAMANO,
					Constants.Box.SELECT_BOX_ANCHO_RTA);
			// arriba
			x = posicionCenter.x - Constants.Box.TAMANO / 2;
			y = posicionCenter.y - Constants.Box.TAMANO / 2
					- Constants.Box.SELECT_BOX_ANCHO_RTA;
			answerUsedSprite.setPosition(x, y);
			answerUsedSprite.draw(batch);
			// abajo
			x = posicionCenter.x - Constants.Box.TAMANO / 2;
			y = posicionCenter.y + Constants.Box.TAMANO / 2;
			answerUsedSprite.setPosition(x, y);
			answerUsedSprite.draw(batch);
		}

		@Override
		protected void specificRender(SpriteBatch batch, RunningSound runningSound) {
			if (this.givinFeedback) {this.contourRender(batch);}
		}

		@Override
		protected void update(float deltaTime, RunningSound sound, EstadoLoop estadoLoop) {
			this.answerAnimationTime += deltaTime;
			if (answerAnimationTime > Constants.Box.ANIMATION_ANSWER_TIME) {
				this.givinFeedback = false;
				estadoLoop = EstadoLoop.CambiarTrial;
			}
		}

		@Override
		public void select(RunningSound runningSound) {
			if (this.giveFeedback) {
				this.givinFeedback = true;
				this.answerAnimationTime = 0;
				runningSound.stop();
			}
		}
	}
	
	public static class StimuliBox extends Box {
		
		private float delayAutoreproducir = Constants.Box.DELAY_ESTIMULO_MODO_SELECCIONAR;
		private Sprite stimuliAnimationSpr; // Sprite para la animacion del sonido 
		
		public StimuliBox (ExperimentalObject contenido) {	
			this.contenido = contenido;
			this.posicionCenter = new Vector2(0, 0);
			this.spr = new Sprite (Assets.imagenes.stimuliLogo);
			this.createSoundAnimationResources();
		}

		
		private void createSoundAnimationResources() {
			Pixmap pixmap = new Pixmap(10, 10, Format.RGBA8888);
			pixmap.setColor(0, 0, 0, 1);
			pixmap.fill();
			Texture texture = new Texture(pixmap);
			this.stimuliAnimationSpr = new Sprite (texture);
		}

		@Override
		protected void specificRender(SpriteBatch batch, RunningSound runningSound) {
			if (runningSound.running) {
				stimuliAnimationSpr.setSize(Constants.Box.TAMANO_CONTORNO_X,Constants.Box.TAMANO_CONTORNO_Y);
				float x = posicionCenter.x - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_X /2;
				float y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_Y;
				float xShift = Constants.Box.TAMANO * runningSound.playTime / Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA;
				stimuliAnimationSpr.setPosition(x + xShift, y);
				stimuliAnimationSpr.draw(batch);
			}
		}

		@Override
		protected void update(float deltaTime, RunningSound runningSound, EstadoLoop estadoLoop) {
			if (!this.contenido.noSound) {
				if (!runningSound.running) {
					this.delayAutoreproducir = this.delayAutoreproducir + deltaTime;
				}
				if (this.delayAutoreproducir > Constants.Box.DELAY_ESTIMULO_MODO_SELECCIONAR) { //TODO cambiar nombre a esta constante
					if (estadoLoop == EstadoLoop.EsperandoSeeleccionDeBox) {
						runningSound.action = NEXT.PLAY;
						runningSound.nextContenido = this.contenido;
						this.delayAutoreproducir = 0;
					}
				}
			}
		}

		@Override
		public void select(RunningSound runningSound) {} // No hace nada
		
	}
}
