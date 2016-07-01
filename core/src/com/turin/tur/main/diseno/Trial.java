package com.turin.tur.main.diseno;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Boxes.OptionsBox;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.Boxes.StimuliBox;
import com.turin.tur.main.diseno.Boxes.TrainingBox;
import com.turin.tur.main.logic.LevelController;
import com.turin.tur.main.logic.LevelController.EstadoLoop;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.ResourcesCategorias.CategoriasImagenes;

public class Trial {

	public static final String TAG = Trial.class.getName();

	public JsonTrial jsonTrial; // Toda la info del json del trial
	private Array<ExperimentalObject> elementos = new Array<ExperimentalObject>(); // Esto debe ser cargado antes de ejecutarse la creacion de los elementos
	
	// private int Id; // Id q identifica al trial 
	private ExperimentalObject estimulo;
	public Array<TrainingBox> trainigBoxes = new Array<TrainingBox>();
	public Array<OptionsBox> testBoxes = new Array<OptionsBox>();
	public StimuliBox stimuliBox;
	public Array<Box> allBox = new Array<Box>();
	// boolean somethingTouched = false; // almacena si ya se respondio el trial o no 
	
	public Trial (Array<ExperimentalObject> elementos, JsonTrial jsonTrial, ExperimentalObject estimulo) {
		this.elementos = elementos;
		this.jsonTrial = jsonTrial;
		this.estimulo = estimulo;
		this.configureElements();
	}
	
	public void configureElements () {
		Array<Integer> orden = new Array<Integer>();
		// Crea un orden random o no segun corresponda
		for (int i = 0; i < this.jsonTrial.distribucion.distribucion.length; i++) {
			orden.add(i);
		}
		if (this.jsonTrial.randomSort) {
			orden.shuffle();
		}
		// Crea las cajas segun corresponda a su tipo
		if (this.jsonTrial.modo == Constants.Diseno.TIPOdeTRIAL.ENTRENAMIENTO) {
			for (ExperimentalObject elemento : this.elementos) {
				TrainingBox box = new TrainingBox(elemento);
				box.SetPosition(jsonTrial.distribucion.X(orden.get(this.elementos.indexOf(elemento, true))),
						jsonTrial.distribucion.Y(orden.get(this.elementos.indexOf(elemento, true))));
				this.trainigBoxes.add(box);
			}
		}
		if (this.jsonTrial.modo == Constants.Diseno.TIPOdeTRIAL.TEST){
			for (ExperimentalObject elemento : this.elementos) {
				OptionsBox box = new OptionsBox(elemento,this.jsonTrial.feedback);
				box.SetPosition(jsonTrial.distribucion.X(orden.get(this.elementos.indexOf(elemento, true))) + Constants.Box.SHIFT_MODO_SELECCIONAR,
						jsonTrial.distribucion.Y(orden.get(this.elementos.indexOf(elemento, true))));
				this.testBoxes.add(box);
			}
			// Crea el box de estimulo
			stimuliBox = new StimuliBox(estimulo);
			stimuliBox.SetPosition(0 + Constants.Box.SHIFT_ESTIMULO_MODO_SELECCIONAR, 0);
			allBox.add(stimuliBox);
		}
		// Junta todas las cajas en una unica lista para que funcionen los
		// update, etc.
		for (Box box : testBoxes) {
			allBox.add(box);
		}
		for (Box box : trainigBoxes) {
			allBox.add(box);
		}
	}
	
	public void update(float deltaTime, LevelController levelController) {
		// Actualiza las boxes
		for (Box box : allBox) {
			box.update(deltaTime, levelController);
		}
	}
	
	public static class JsonTrial {
		public String caption; // Texto que se muestra debajo
		public int Id; // Id q identifica al trial
		public String title; // Titulo optativo q describe al trial
		public TIPOdeTRIAL modo; // Tipo de trial
		public int[] elementosId; // Lista de objetos del trial.
		public int rtaCorrectaId; // Respuesta correcta en caso de que sea test.
		public boolean rtaRandom; // Determina si se elije una rta random
		public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones
														// de los elementos a
														// mostrar
		public boolean feedback=false; // Sirve para configurar que en algunos test no haya feedback
		public boolean randomSort;
		public int resourceVersion;
	}

	public static class ResourceId {
		public int id;
		public int resourceVersion;
	}


	/*
	 * Metodo que se ejecuta al tocar un box (ya sea Entrenamiento o Test)
	 */
	public void boxSelected(LevelController levelController) {
		// Revisamos que onda si se acerto o no. Acertar significa que el elemento tocado y el estimulo compartan al menos una categoria.
		// El el tutorial la categoria del estimulo (que no se ve) coincide con la del boton de siguiente
		boolean answerCorrect = false;
		for (CategoriasImagenes categoria : levelController.boxTocada.contenido.categorias) {
			if (this.estimulo.categorias.contains(categoria, false)) {
				answerCorrect = true;
			}
		}
		levelController.boxTocada.answerCorrect = answerCorrect;
		levelController.boxTocada.select(levelController);
	}
}
