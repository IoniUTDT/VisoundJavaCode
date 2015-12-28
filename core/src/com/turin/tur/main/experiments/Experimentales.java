package com.turin.tur.main.experiments;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.experiments.Experimentales.Setups.SetupUmbralAngulos;
import com.turin.tur.main.util.builder.ResourcesMaker.InfoConceptualParalelismo;

/**
 * Este paquete esta pensado para almacenar todo lo que tiene que ver con los diseños experimentales.
 * @author ionatan
 *
 */
public class Experimentales {

	public static class Setups {
	
		/*
		 * Esta es una clase para manejar el setup experimental del diseño de experimento donde se quiere medir la sensibilidad al detectar el delta tita
		 */
		public static class SetupUmbralParalelismo {
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
			public int cantidadDeltas; // Cantidad de delta titas que se generan en cada condicion de angulo de referencia y de separacion	
			public String tagRefPos="+"; // Guarda el tag de la ref positiva
			public String tagRefNeg="-"; // Guarda el tag de la ref negativa
		}
		
		/*
		 * Esta es una clase para manejar el setup experimental del diseño de experimentos para medir sensibilidad en angulos.
		 * Todos los angulos estan en grados y todos los angulos son enteros (se asume que no hay necesidad de mayor presicion).
		 * En el momento de generar los recursos se asume que se quiere tener un set de recursos que cubra todos los angulos posibles con dos criterios
		 * Por un lado que haya angulos cubriendo la vuelta entera variando los lados de a un salto grande.
		 * Por otro lado que alrededor de angulos que se consideran criticos agregue una mayor densidad de angulos equidistantes a un salto "chico" 
		 */
		public static class SetupUmbralAngulos{
			public String nombre; // Nombre del setup
			public int saltoGrande; // Salto que hay entre angulo no critico y angulo no critico
			public int saltoChico; // Salto que hay entre angulo dos angulos consecutivos alrededor de los angulos criticos
			public Array<Integer> angulosCriticos = new Array<Integer>(); // Nota: tiene que estar entre los angulo pertenecientes al salto grande para que los considere
			public Array<Integer> angulosNoDetalle = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos de al salto grande
			public Array<Integer> angulosDetalle = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos de al salto chico
			public Array<Integer> angulos = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos
			
			public void searchAngles() {
				for (int i=0; i<=360; i=i+this.saltoGrande) {
					this.angulosNoDetalle.add(i);
					if (this.angulosCriticos.contains(i,false)){ // Si es un angulo critico
						int numeroDeAngulosExtra = this.saltoGrande/this.saltoChico;
						for (int j=1; j<numeroDeAngulosExtra; j++) {
							int value = j*this.saltoChico+i;
							if (value < 0) {
								value = 360 + value; 
							}
							angulosDetalle.add(value);
							value = -j*this.saltoChico+i;
							if (value < 0) {
								value = 360 + value; 
							}
							angulosDetalle.add(value);
						}
					}
				}
				
				this.angulos.addAll(this.angulosDetalle);
				this.angulos.addAll(this.angulosNoDetalle);
			}
		}

	}

	public static class Analisis {
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
	/**
	 * Esta clase sirve para hacer el analisis en tiempo real del experimento de umbral de deteccion de angulos
	 * La idea general se basa en genarar una señal que es la diferencia del angulo mostrado respecto al recto e ir disminuyendo 
	 * dicha señal hasta que el usuario no pueda distinguir si el angulo es agudo o grave. 
	 * Para eso se elije un lado de los angulos que se deja fijo (el angulo "referencia")
	 * y se mueve el otro variando el nivel de la señal. Como se puede realizar una aproximacion al angulo recto desde los
	 * agudos o desde los graves hay dos nieveles de deñal, al aguda y la grave, y ambas se van regulando a la vez de forma intercalada
	 * Un beneficio de este planteo es que permite preguntar de manera random al usuario por angulos agudos o graves y que por ende no 
	 * pueda asumir cual es la respuesta que se espera.
	 *  
	 * @author ionatan
	 *
	 */
	public static class AnalisisUmbralAngulos {
		
		/**
		 * Clase armada para poder ordenar los angulos en funcion de su angulo "corregido"
		 * @author ionatan
		 *
		 */
		public class AnguloOrdenable implements Comparable<AnguloOrdenable> {
			int angulo;
			int anguloRef;

			public AnguloOrdenable(int angulo, int anguloRef) {
				this.angulo = angulo;
			    this.anguloRef = anguloRef;
			}
			 
			@Override
			public int compareTo(AnguloOrdenable o) {
				return Integer.valueOf(anguloRef).compareTo(o.anguloRef);
			}
		}

		/**
		 * Esta clase guarda todos los parametros necesarios para hacer la convergencia. Es una clase aparte porque se repite x cuatro
		 * Hace falta una serie de datos para agudo y otro para grave en el angulo + 90 y - 90.
		 * Llamo a las cuatro señales segun el cuadrante asumiendo la refrencia como el angulo 0. 
		 * @author ionatan
		 *
		 */
		public class Parametros{
			private int nivelEstimulo; // nivel de señal enviada
			private int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
			private Array<Integer> historialAngulo = new Array<Integer>(); // Historial de angulos enviados
			private Array<Integer> historialNiveles = new Array<Integer>(); // Historial de niveles de señal enviado
			private Array<Boolean> historialAciertos = new Array<Boolean>(); // Historial de aciertos
			private Array<Integer> listaEstimulos = new Array<Integer>(); // Lista de estimulos ordenados de menor a mayor dificultad
		}
		
		private Array<Parametros> cuadrantes = new Array<Parametros>();
		private int anguloDeReferencia; // Angulo correspondiente al lado que se deja quieto.
		private int saltoInicialEnGrados=20; // Esta cantidad representa el salto inicial en terminos absolutos. Sirve para configurar el numero de saltos iniciales en funcion del setup experimental
		
		// Los siguientes dos numeros representan la relacion entre errores y aciertos que se espera para regular el umbral 
		private int proporcionAciertos=2;
		private int proporcionErrores=1;
		// Variables que regulan en intercambio de datos con el levelcontroller.
		private int next; //Proximo valor a medir (en terminos absolutos)
		private int cuadranteActivo; // En cual de los cuadrantes esta la señal que se va a medir
		private boolean waitingAnswer; //Si se esta esperando la rta.

		/**
		 * Al incializar el analisis hace falta a partir del setup determinar la siguiente informacion:
		 * Cuanto es el salto inicial en termino de numero de angulos en funcion de la relacion entre el salto buscado (en grados) y la densidad de angulos del setup
		 * Armar una lista ordenada de cuales son los angulos agudos y cuales los graves para poder iterar sobre esa lista el nivel de dificultad independientemente de los valores numericos
		 *   
		 */
		public AnalisisUmbralAngulos (SetupUmbralAngulos setup, int anguloReferencia) {
			this.anguloDeReferencia = anguloReferencia;
			// Agrega 4 parametros. Cada una corresponde a la convergencia al angulo recto en cada uno de los cuadrantes. Es decir la 0 es la convergencia de angulos agudos al angulo de 90 grados en sentido antihorario y el 3 la convcergencia desde los agudos al angulo recto en sentido horario.
			int salto = saltoInicialEnGrados%setup.saltoGrande;
			Array<AnguloOrdenable> angulosCuadrante1 = new Array<AnguloOrdenable>();
			Array<AnguloOrdenable> angulosCuadrante2 = new Array<AnguloOrdenable>();
			Array<AnguloOrdenable> angulosCuadrante3 = new Array<AnguloOrdenable>();
			Array<AnguloOrdenable> angulosCuadrante4 = new Array<AnguloOrdenable>();
			
			for (int angulo: setup.angulos) {
				int anguloRef = angulo - this.anguloDeReferencia;
				if (anguloRef < 0) {anguloRef = anguloRef + 360;} // corrige para que sean todos angulos en la primer vuelta
				if (anguloRef > 0 && anguloRef <= 90) {
					angulosCuadrante1.add(new AnguloOrdenable(angulo, anguloRef));
				}
				if (anguloRef >= 90 && anguloRef < 180) {
					angulosCuadrante2.add(new AnguloOrdenable(angulo, anguloRef));
				}
				if (anguloRef > 180 && anguloRef <= 270) {
					angulosCuadrante3.add(new AnguloOrdenable(angulo, anguloRef));
				}
				if (anguloRef >= 270 && anguloRef < 360) {
					angulosCuadrante4.add(new AnguloOrdenable(angulo, anguloRef));
				}
			}
			angulosCuadrante1.sort();
			angulosCuadrante2.sort();
			angulosCuadrante3.sort();
			angulosCuadrante4.sort();
			
			System.out.println(angulosCuadrante1);
			
			for (int i=0; i<4; i++) {
				Parametros cuadrante = new Parametros();
				cuadrante.saltosActivos = salto;
			}
		}

		/*
		 * Este metodo devuelve el valor absoluto del proximo angulo a preguntar  
		 */
		/*
		public int askNext() {
			if (!waitingAnswer) {
				// Elije si probar con angulo agudo o grave
				// Mientras pruebo el codigo trabajo solo con agudos
				boolean proximoAgudo = true; // MathUtils.randomBoolean();
				if (proximoAgudo) {
					boolean incrementarDificultad;
					if (this.historialAciertosAgudo.size >= (this.proporcionAciertos+this.proporcionErrores)) { // Estamos en el caso en que hay que mirar el historial
						int contadorAciertos=0;
						for (int i=0; i<(this.proporcionAciertos+this.proporcionErrores); i++){
							if (this.historialAciertosAgudo.get(this.historialAciertosAgudo.size-i)==true){
								contadorAciertos++;
							}
						}
						if (contadorAciertos>= this.proporcionAciertos) {
							incrementarDificultad=true;
						} else {
							incrementarDificultad=false;
						}
					} else { // Estamos en el caso de que no haya suficiente historia
						if (this.historialAciertosAgudo.size>0) {
							if (this.historialAciertosAgudo.peek()) { // Se fija si lo ultimo fue un acierto.
								incrementarDificultad=true;
							} else {
								incrementarDificultad=false;
							}
						} else { // Estamos en el caso de que no haya historial
							incrementarDificultad=true;
						}
					}
					// aca ya eligio si aumentar o disminuir la dificultad
					if (this.deltaAgudo > 0) { // En caso de que tenga sentido 
						
					}
				}
				
			}
			
			return this.next;
		}
		*/
	}
}
