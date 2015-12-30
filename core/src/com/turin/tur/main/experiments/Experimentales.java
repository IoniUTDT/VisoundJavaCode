package com.turin.tur.main.experiments;

import com.badlogic.gdx.math.CumulativeDistribution;
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
			
			public boolean cumpleCriterioDistanciaMinima (int angulo1, int angulo2) {
				int deltaAngulo = angulo2-angulo1;
				if (deltaAngulo < 0) {deltaAngulo=-deltaAngulo;}  // Hacemos que sean todos los numeros positivos
				if (deltaAngulo >= 180) {deltaAngulo = 360 - deltaAngulo;} // Hacemos que los angulos sean considerados siempre del lado "concavo")
				return (deltaAngulo >= this.saltoGrande);
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
			int nivel;
			
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
		 * Clase que agrupa en el historial de eventos, el angulo, el nivel de dificultad, y si se acerto o no
		 * @author ionatan
		 *
		 */
		public class Historial {
			AnguloOrdenable angulo;
			boolean acertado;
		}
		/**
		 * Esta clase guarda todos los parametros necesarios para hacer la convergencia. Es una clase aparte porque se repite x cuatro
		 * Hace falta una serie de datos para agudo y otro para grave en el angulo + 90 y - 90.
		 * Llamo a las cuatro señales segun el cuadrante asumiendo la refrencia como el angulo 0.
		 * 
		 *  
		 *  En ask se encuentra todo el algoritmo encargado de proponer un proximo estimulo.
		 *  en aswer el algoritmo encargado de procesar la respuesta y modificar los registros.
		 * @author ionatan
		 *
		 */
		public class Parametros{
			private int nivelEstimulo; // nivel de señal enviada
			private int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
			private boolean convergenciaAlcanzada=false;
			private Array<Historial> historial = new Array<Historial>(); // Se almacena la info de lo que va pasando
			private Array<AnguloOrdenable> listaEstimulos = new Array<AnguloOrdenable>(); // Lista de estimulos ordenados de menor a mayor dificultad
		}
		
		private Array<Parametros> cuadrantes = new Array<Parametros>();
		private int anguloDeReferencia; // Angulo correspondiente al lado que se deja quieto.
		private int saltoInicialEnGrados=20; // Esta cantidad representa el salto inicial en terminos absolutos. Sirve para configurar el numero de saltos iniciales en funcion del setup experimental
		
		// Los siguientes dos numeros representan la relacion entre errores y aciertos que se espera para regular el umbral 
		private int proporcionAciertos=2;
		private int proporcionErrores=1;
		// Variables que regulan en intercambio de datos con el levelcontroller.
		private AnguloOrdenable next; //Proximo valor a medir (en terminos absolutos)
		private int cuadranteActivo; // En cual de los cuadrantes esta la señal que se va a medir
		private boolean waitingAnswer=false; //Si se esta esperando la rta.
		
		
		/**
		 * Al incializar el analisis hace falta a partir del setup determinar la siguiente informacion:
		 * Cuanto es el salto inicial en termino de numero de angulos en funcion de la relacion entre el salto buscado (en grados) y la densidad de angulos del setup
		 * Armar una lista ordenada de cuales son los angulos agudos y cuales los graves para poder iterar sobre esa lista el nivel de dificultad independientemente de los valores numericos
		 *   
		 */
		public AnalisisUmbralAngulos (SetupUmbralAngulos setup, int anguloReferencia) {
			this.anguloDeReferencia = anguloReferencia;
			// Agrega 4 parametros. Cada una corresponde a la convergencia al angulo recto en cada uno de los cuadrantes. Es decir la 0 es la convergencia de angulos agudos al angulo de 90 grados en sentido antihorario y el 3 la convcergencia desde los agudos al angulo recto en sentido horario.
			int salto = saltoInicialEnGrados/setup.saltoGrande;
			Array<AnguloOrdenable> angulosCuadrante1 = new Array<AnguloOrdenable>();
			Array<AnguloOrdenable> angulosCuadrante2 = new Array<AnguloOrdenable>();
			Array<AnguloOrdenable> angulosCuadrante3 = new Array<AnguloOrdenable>();
			Array<AnguloOrdenable> angulosCuadrante4 = new Array<AnguloOrdenable>();
			
			for (int angulo: setup.angulos) {
				if (setup.cumpleCriterioDistanciaMinima(angulo, this.anguloDeReferencia)) {
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
			}
			// Ordenamos los angulos segun corresponda
			angulosCuadrante1.sort();
			angulosCuadrante1.reverse();
			for (AnguloOrdenable angulo :angulosCuadrante1) {
				angulo.nivel = angulosCuadrante1.indexOf(angulo, true);
			}
			angulosCuadrante2.sort();
			for (AnguloOrdenable angulo :angulosCuadrante2) {
				angulo.nivel = angulosCuadrante2.indexOf(angulo, true);
			}
			angulosCuadrante3.sort();
			angulosCuadrante3.reverse();
			for (AnguloOrdenable angulo :angulosCuadrante3) {
				angulo.nivel = angulosCuadrante3.indexOf(angulo, true);
			}
			angulosCuadrante4.sort();
			for (AnguloOrdenable angulo :angulosCuadrante4) {
				angulo.nivel = angulosCuadrante4.indexOf(angulo, true);
			}
			// Agregamos los datos a cada cuadrante
			Parametros cuadrante1 = new Parametros();
			cuadrante1.saltosActivos = salto;
			cuadrante1.nivelEstimulo = angulosCuadrante1.size-1;
			cuadrante1.listaEstimulos = angulosCuadrante1;
			this.cuadrantes.add(cuadrante1);
			Parametros cuadrante2 = new Parametros();
			cuadrante2.saltosActivos = salto;
			cuadrante2.nivelEstimulo = angulosCuadrante2.size-1;
			cuadrante2.listaEstimulos = angulosCuadrante2;
			this.cuadrantes.add(cuadrante2);
			Parametros cuadrante3 = new Parametros();
			cuadrante3.saltosActivos = salto;
			cuadrante3.nivelEstimulo = angulosCuadrante3.size-1;
			cuadrante3.listaEstimulos = angulosCuadrante3;
			this.cuadrantes.add(cuadrante3);
			Parametros cuadrante4 = new Parametros();
			cuadrante4.saltosActivos = salto;
			cuadrante4.nivelEstimulo = angulosCuadrante4.size-1;
			cuadrante4.listaEstimulos = angulosCuadrante4;
			this.cuadrantes.add(cuadrante4);
		}

		/**
		 * Este metodo busca un nuevo estimulo a preguntar, lo carga en la clase y devuelve su valor absoluto  
		 */
		public int askNext() {
			if (!waitingAnswer) {
				// Elije un cuadrante al azar y carga esos datos
				int cuadranteAUsar=MathUtils.random(3);
				Parametros cuadrante = this.cuadrantes.get(cuadranteAUsar);
				// pone los datos en la clase principal
				this.cuadranteActivo = cuadranteAUsar;
				this.next = cuadrante.listaEstimulos.get(cuadrante.nivelEstimulo);
				this.waitingAnswer = true;
			}
			return this.next.angulo;
		}
		
		/**
		 * Esta funcion se encarga de recibir el feedback del usuario y actualizar todas las estadisticas en consecuencia
		 * @param acerto
		 */
		
		public void answer(boolean acierto) {

			if (waitingAnswer) {
				// Seteamos que ya hay rta.
				this.waitingAnswer=false;
				// Seleccionamos el cuadrante que corresponde
				Parametros cuadrante = this.cuadrantes.get(this.cuadranteActivo);
				// Agregamos la info del ultimo toque.
				Historial historial = new Historial();
				historial.acertado=acierto;
				historial.angulo=this.next;
				cuadrante.historial.add(historial);
				
				// Elije si hay que incrementar la dificultad o no
				boolean incrementarDificultad;
				if (cuadrante.historial.size >= (this.proporcionAciertos+this.proporcionErrores)) { // Estamos en el caso en que hay que mirar el historial
					int contadorAciertos=0;
					for (int i=1; i<=(this.proporcionAciertos+this.proporcionErrores); i++){
						if (cuadrante.historial.get(cuadrante.historial.size-i).acertado==true){
							contadorAciertos++;
						}
					}
					if (contadorAciertos>= this.proporcionAciertos) {
						incrementarDificultad=true;
					} else {
						incrementarDificultad=false;
					}
				} else { // Estamos en el caso de que no haya suficiente historia
					if (cuadrante.historial.size>0) {
						if (cuadrante.historial.peek().acertado) { // Se fija si lo ultimo fue un acierto.
							incrementarDificultad=true;
						} else {
							incrementarDificultad=false;
						}
					} else { // Estamos en el caso de que no haya historial
						incrementarDificultad=true;
					}
				}
				
				// Se fija si hay que disminuir el salto entre nivel y nivel
				if (cuadrante.historial.size >= (this.proporcionAciertos+this.proporcionErrores)+1) { // Estamos en el caso en que hay que mirar el historial
					boolean incrementarDificultadUltimo;
					int contadorAciertos=0;
					for (int i=0; i<(this.proporcionAciertos+this.proporcionErrores); i++){
						if (cuadrante.historial.get(cuadrante.historial.size-(i+1)).acertado==true){
							contadorAciertos++;
						}
					}
					if (contadorAciertos>= this.proporcionAciertos) {
						incrementarDificultadUltimo=true;
					} else {
						incrementarDificultadUltimo=false;
					}
					if (incrementarDificultadUltimo!=incrementarDificultad) {
						cuadrante.saltosActivos = cuadrante.saltosActivos - 1;
						if (cuadrante.saltosActivos==0) {
							cuadrante.saltosActivos = 1;
						}
					}
				}
				
				// Aqui ya se determino si hay que incrementar o dosminuir la dificultad y por lo tanto se aplica, cuidando que no exceda los limites
				if (incrementarDificultad) {
					cuadrante.nivelEstimulo=cuadrante.nivelEstimulo-cuadrante.saltosActivos;
					if (cuadrante.nivelEstimulo<0) {cuadrante.nivelEstimulo=0;}
				} else {
					cuadrante.nivelEstimulo=cuadrante.nivelEstimulo+cuadrante.saltosActivos;
					if (cuadrante.nivelEstimulo>cuadrante.listaEstimulos.size) {cuadrante.nivelEstimulo=cuadrante.listaEstimulos.size-1;}
				}
			}
		}

		/**
		 * Esta funcion se fija se se termino el level o no
		 * @return
		 */
		public boolean complete() {
			if ((this.cuadrantes.get(0).historial.size+this.cuadrantes.get(1).historial.size+this.cuadrantes.get(2).historial.size+this.cuadrantes.get(3).historial.size)>100) {
				System.out.println("Nivel finalizado:");
				System.out.println("Cuadrante1:");
				for (Historial elemento :this.cuadrantes.get(0).historial)
				System.out.print(elemento.angulo.angulo+":"+elemento.acertado+" ");
				System.out.println("Cuadrante2:");
				for (Historial elemento :this.cuadrantes.get(1).historial)
				System.out.print(elemento.angulo.angulo+":"+elemento.acertado+" ");
				System.out.println("Cuadrante3:");
				for (Historial elemento :this.cuadrantes.get(2).historial)
				System.out.print(elemento.angulo.angulo+":"+elemento.acertado+" ");
				System.out.println("Cuadrante4:");
				for (Historial elemento :this.cuadrantes.get(3).historial)
				System.out.print(elemento.angulo.angulo+":"+elemento.acertado+" ");
			}
			return ((this.cuadrantes.get(0).historial.size+this.cuadrantes.get(1).historial.size+this.cuadrantes.get(2).historial.size+this.cuadrantes.get(3).historial.size)>100);
		}
	}
}
