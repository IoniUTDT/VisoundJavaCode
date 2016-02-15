package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;

/**
 * Este paquete esta pensado para almacenar todo lo que tiene que ver con los diseños experimentales.
 * @author ionatan
 *
 */
public class Experiments {

	public static class LevelStatus {
		String name;
		int id;
		boolean enable;
	}
	
	static final String TAG = Experiments.class.getName();

	
	/**
	 * Esta es una clase para manejar el setup experimental del diseño de experimento donde se quiere medir la sensibilidad al detectar el delta tita
	 */
	public class SetupUmbralParalelismo {
		public String tag;
		// Vamos a trabajar todas las cuentas en radianes
		public String nombre; // Nombre del setup
		public float titaRefInicial; // Angulo de referencia inicial
		public int saltoTitaRefInt; // Salto del tita de referencia 
		public float saltoTitaRef; //Salto del tita pero en formato float
		public float anguloMinimo; //Angulo minimo del delta
		public float anguloMaximo; //Angulo maximo del delta
		public float largo; //Largo de los segmentos
		public float separacionMinima; // Separacion minima de los segmentos
		public float separacionIncremento; // Incremento de la separacion de los segmentos
		public int cantidadReferencias; // Cantidad de angulos tita (referencia)
		public int cantidadSeparaciones; // Cantidad de saltos en la separacion de las rectas
		public int cantidadDeltas; // Cantidad de delta titas que se generan en cada condicion de angulo de referencia y de separacion	;
		public String tagRefPos="+"; // Guarda el tag de la ref positiva
		public String tagRefNeg="-"; // Guarda el tag de la ref negativa
	}		
		


		
	/**
	 * Clase para alamcenar la info conceptual relacionada a los recursos de paralelismo
	 * @author ionatan
	 *
	 */
	public static class InfoConceptualParalelismo {
		public float direccionAnguloReferencia;
		public float deltaAngulo;
		public int deltaAnguloLinealizado;
		public boolean seJuntan;
		public float separacion; 
		public String DescripcionDeParametros = "AnguloReferencia: direccion media entre las dos rectas; deltaAngulo: diferencia entre los angulos de ambas rectas, siempre en modulo; deltaAnguloLinealizado: el mismo parametro pero transformado de manera que una escala linea tenga mas densidad en angulos chicos; seJuntan: diferencia si las rectas se van juntando en la direccion de referencia o se van separando; separacion: deparacion en el punto medio"; 
	}
	
		
	
	/**
	 * Clase que se encarga de procesar (a medio hacer porque antes estaba fuera de esta clase los metodos) lo que tiene que ver con
	 * el analisis em tiempo real de los experimentos de paralelismo 
	 * @author ionatan
	 *
	 */
	public static class AnalisisUmbralParalelismo {
		public static class DetectionObject {
			public boolean answerTrue;
			public InfoConceptualParalelismo infoConceptual;
		}
		
		public float anguloReferencia;
		public int indiceAnguloRefrencia;
		public int cantidadDeNivelesDeDificultad;
		public float trueRate; // Nivel de aciertos de deteccion de señal que se quiere medir. Sirve para el setup experimental de umbral
		public Array<DetectionObject> historialAciertosCurvaSuperior = new Array<DetectionObject>();
		public int saltoCurvaSuperior;
		public int proximoNivelCurvaSuperior;  
	}
}
