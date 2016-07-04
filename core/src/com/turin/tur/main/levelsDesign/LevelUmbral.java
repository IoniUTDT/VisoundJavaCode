package com.turin.tur.main.levelsDesign;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.util.Constants.ResourcesCategorias.Paths;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.builder.Imagenes.Linea;

public class LevelUmbral extends Level {

	private static class Dinamica {
		protected Estimulo estimuloActivo;
		protected Array<TrialConfig> pseudorandom = new Array<TrialConfig>();
		Array<Estimulo> estimulosCeros = new Array<Estimulo>();
		Array<Respuesta> historial = new Array<Respuesta>(); // Se almacena la info de lo que va pasando
		LISTAdeNIVELES identificadorNivel; // Algo para indentificar la dinamica
		boolean levelFinalizadoCorrectamente = false;
		int nivelEstimulo; // nivel de proxima señal a enviar
		int proporcionAciertos = 3; // Es la cantidad de aciertos que tiene que haber en el numero total de ultimas respuestas para que aumente la dificultad
		double referencia;
		int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
		Array<SerieEstimulos> seriesEstimulos = new Array<SerieEstimulos>();
		int trialsPorNivel;
		TrialType trialType; // Distinguimos si se trata de un trial que busca medir de verdad o si es un trial facil para verificar que el usuario esta entendiendo la consigna
	}
	
	private static class Respuesta {
		private boolean acertado;
		private float confianceTimeInTrial = -1;
		private float confianza;
		private Estimulo estimulo;
		private int nivelEstimulo;
		private float selectionTimeInTrial = -1;
		private int soundLoops = -1;
		private TrialType trialType;
		Respuesta (Estimulo estimulo, Boolean rta, float confianza, TrialType trialType, int nivelEstimulo, float selectionTime, float confianceTime, int soundLoops) {
			this.estimulo = estimulo;
			this.acertado = rta;
			this.confianza = confianza;
			this.trialType = trialType;
			this.nivelEstimulo = nivelEstimulo;
			this.selectionTimeInTrial = selectionTime;
			this.confianceTimeInTrial = confianceTime;
			this.soundLoops = soundLoops;
		}
	}

	private static class SerieEstimulos {
		boolean desdeAgudosOPos;
		String identificador; // Algo para indentificar cual convergencia es cual.
		double ladoFijo;
		Array<Estimulo> listaEstimulos = new Array<Estimulo>(); // Lista de estimulos ordenados de menor a mayor dificultad
	}

	static class SetupLevel {
		public float confianceProbability = 0;
		public boolean feedback;
		public LISTAdeNIVELES identificadorLevel;
		public int levelPriority; // Prioridad que tiene el nivel en la lista de niveles. Sirve para habilitar a que se tenga que completar un nivel antes que otro.
		public int saltoColaUNOFraccion = 2;
		public int saltoInicialFraccion = 4;
		public float signalProbability = 0.5f; // Esto tiene sentido que sea asi, mitad y mitad para que ande bien el sistema de medicion. No puede ser mas proibable una opcion que la otra (enm principio)
		public String tagButton;
		public float testProbability = 0f; // Representa la inversa del numero de test que se dedica a testear al usuario enviandole trials faciles.
		public int trialsPorNivel; // Numero de trial que conforman un nivel
		boolean allTestsConfianza = true; // Esto esta condicionado a que testProbability sea diferente de cero en la generacion del pseudorandom
	}
	
	private class TrialConfig {
		boolean confiance;
		TrialType trialType;
	}
	
	private enum TrialType {
		Estimulo,NoEstimulo,Test
	}
	
	static class Estimulo implements Comparable<Estimulo> {
		double anguloFijo; // Angulo de inclinacion de las rectas paralelas de
		// referencia
		double desviacion; // Desviacion respecto a la referencia
		int idResource; // Id del archivo con el recurso
							int idTrial; // Id del trial en que se evalua al recurso (esto es porque 
		int nivelSenal; // Nivel de intensidad de la señal. Cero representa el angulo recto o las rectas paralelas. Y despues representa una escala lineal que mapea las estimulos ordenados segun la intensidad del estimulo a medir (mas facil mayor intencidad)
		@Override
		public int compareTo(Estimulo o) {
			return Integer.valueOf(nivelSenal).compareTo(o.nivelSenal);
		}
	}
	
	static class EstimuloAngulo extends Estimulo implements Comparable<Estimulo> {
		double anguloLadoMovil; // Angulo absoluto del lado movil
		
		public int compareTo(EstimuloAngulo o) {
			return Double.valueOf(this.desviacion).compareTo(o.desviacion);
		}
		
	}
	
	
	
	
	
	
	public LevelUmbral() {
	}

	public LevelUmbral(LISTAdeNIVELES identificador) {
		super(identificador);
	}

	@Override
	public Trial getNextTrial() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean goConfiance() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void interrupt() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean islevelCompleted() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void levelCompleted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadDinamica() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void returnAnswer(boolean answerCorrect, float confianzaReportada, float timeSelecion, float timeConfiance,
			int loopsCount) {
		// TODO Auto-generated method stub

	}
}
