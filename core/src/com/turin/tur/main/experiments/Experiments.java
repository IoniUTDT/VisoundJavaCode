package com.turin.tur.main.experiments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.diseno.Trial.ResourceId;

/**
 * Este paquete esta pensado para almacenar todo lo que tiene que ver con los diseños experimentales.
 * @author ionatan
 *
 */
public class Experiments {
	
	private static final String TAG = Experiments.class.getName();

	
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
		public int cantidadDeltas; // Cantidad de delta titas que se generan en cada condicion de angulo de referencia y de separacion	
		public String tagRefPos="+"; // Guarda el tag de la ref positiva
		public String tagRefNeg="-"; // Guarda el tag de la ref negativa
	}		
		
	/**
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
		public int numeroDeRefrenciasConjuntas = 2; // Es el numero de angulos de referencia distintos que se intercalan en un mismo nivel para evitar feedback 
		public Array<Array<Integer>> idsResourcesByAngle = new Array<Array<Integer>>(); // Lista de arrays con los ids de los recursos que tienen cada angulo. Esto se crea en tiempo de ejecucion de creacion de recursos porque es infinitamente mas lento hacer una busqueda despues al trabajar con volumenes grandes de recursos. El tag de cada entrada es el angulo con el mismo indice de la lista de angulos 
		
		
		/**
		 * Este metodo busca todos lo angulos en los que debe haber lineas segun los parametros con que se configure el setup
		 */
		public void searchAngles() {
			
			for (int i=0; i<360; i=i+this.saltoGrande) {
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
			for (@SuppressWarnings("unused") int i:this.angulos) {
				this.idsResourcesByAngle.add(new Array<Integer>());
			}
		}

		/**
		 * Este metodo indica si dos lados estan demaciado proximos angularmente y por ende si se debe incluir el grafico en la lista de recursos a usar o no. 
		 * @param angulo1
		 * @param angulo2
		 * @return
		 */
		public boolean cumpleCriterioDistanciaMinima (int angulo1, int angulo2) {
			int deltaAngulo = angulo2-angulo1;
			if (deltaAngulo < 0) {deltaAngulo=-deltaAngulo;}  // Hacemos que sean todos los numeros positivos
			if (deltaAngulo >= 180) {deltaAngulo = 360 - deltaAngulo;} // Hacemos que los angulos sean considerados siempre del lado "concavo")
			if (this.angulosDetalle.contains(angulo1, false) && this.angulosDetalle.contains(angulo2, false)) {
				return (deltaAngulo >= this.saltoGrande*2);
			} else {
				return (deltaAngulo >= this.saltoGrande);
			}
		}

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
	 * Clase para almacenar la info conceptual relacionada a los recursos de angulos
	 * @author ionatan
	 *
	 */
	public static class InfoConceptualAngulos {
		public float direccionLado1;
		public float direccionLado2;
		public float separacionAngular;
		public CategoriaAngulo categoriaAngulo;
		public boolean critico;
		public String DescripcionParametros = "Se almacena (todo en grados) la direccion de ambos lados, el angulo formado entre ambos lados, si el angulo es agudo recto o grave, y si e critico, o sea, alguno de los lados esta sobre un eje.";
		
	}
	
	public static enum CategoriaAngulo {
		Agudo, Recto, Grave;
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
	public class AnalisisUmbralAngulos {
		
		/**
		 * Clase armada para poder ordenar los angulos en funcion de su angulo "corregido"
		 * @author ionatan
		 *
		 */
		public class AnguloOrdenable implements Comparable<AnguloOrdenable> {
			public int angulo;
			public int anguloRef;
			public int nivel;
			public ResourceId idResource;
			public int idTrial;
			
			public AnguloOrdenable(int angulo, int anguloRef) {
				this.angulo = angulo;
			    this.anguloRef = anguloRef;
			}
			 
			@Override
			public int compareTo(AnguloOrdenable o) {
				return Integer.valueOf(anguloRef).compareTo(o.anguloRef);
			}

			public int nivel() {
				return this.nivel;
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
		public class CuadranteInfo{
			private int nivelEstimulo; // nivel de señal enviada
			private int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
			private boolean convergenciaAlcanzada=false;
			private Array<Historial> historial = new Array<Historial>(); // Se almacena la info de lo que va pasando
			private Array<AnguloOrdenable> listaEstimulos = new Array<AnguloOrdenable>(); // Lista de estimulos ordenados de menor a mayor dificultad
			private WindowedMean ventana = new WindowedMean(tamanoVentanaAnalisisConvergencia);
		}
		
		private Array<CuadranteInfo> cuadrantes = new Array<CuadranteInfo>();
		private int anguloDeReferencia; // Angulo correspondiente al lado que se deja quieto.
		private int saltoInicialEnGrados=20; // Esta cantidad representa el salto inicial en terminos absolutos. Sirve para configurar el numero de saltos iniciales en funcion del setup experimental
		
		// Los siguientes dos numeros representan la relacion entre errores y aciertos que se espera para regular el umbral 
		private int proporcionAciertos=2;
		private int proporcionTotal=3;
		private float sdEsperada = 1f;
		private int numeroMaximoDeTrialsXLevel=60;
		private int tamanoVentanaAnalisisConvergencia=6;
		
		
		// Variables que regulan en intercambio de datos con el levelcontroller.
		public AnguloOrdenable next; //Proximo valor a medir (en terminos absolutos)
		private int cuadranteActivo; // En cual de los cuadrantes esta la señal que se va a medir
		private boolean waitingAnswer=false; //Si se esta esperando la rta.
		public boolean completed;
		
		
		/**
		 * Al incializar el analisis hace falta a partir del setup determinar la siguiente informacion:
		 * Cuanto es el salto inicial en termino de numero de angulos en funcion de la relacion entre el salto buscado (en grados) y la densidad de angulos del setup
		 * Armar una lista ordenada de cuales son los angulos agudos y cuales los graves para poder iterar sobre esa lista el nivel de dificultad independientemente de los valores numericos
		 *   
		 */
		public AnalisisUmbralAngulos (SetupUmbralAngulos setup, int anguloReferencia, Level level) {
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
			CuadranteInfo cuadrante1 = new CuadranteInfo();
			cuadrante1.saltosActivos = salto;
			cuadrante1.nivelEstimulo = angulosCuadrante1.size-1;
			cuadrante1.listaEstimulos = angulosCuadrante1;
			this.cuadrantes.add(cuadrante1);
			CuadranteInfo cuadrante2 = new CuadranteInfo();
			cuadrante2.saltosActivos = salto;
			cuadrante2.nivelEstimulo = angulosCuadrante2.size-1;
			cuadrante2.listaEstimulos = angulosCuadrante2;
			this.cuadrantes.add(cuadrante2);
			CuadranteInfo cuadrante3 = new CuadranteInfo();
			cuadrante3.saltosActivos = salto;
			cuadrante3.nivelEstimulo = angulosCuadrante3.size-1;
			cuadrante3.listaEstimulos = angulosCuadrante3;
			this.cuadrantes.add(cuadrante3);
			CuadranteInfo cuadrante4 = new CuadranteInfo();
			cuadrante4.saltosActivos = salto;
			cuadrante4.nivelEstimulo = angulosCuadrante4.size-1;
			cuadrante4.listaEstimulos = angulosCuadrante4;
			this.cuadrantes.add(cuadrante4);
			this.buscarInfoTrialsIds(level);
		}

		/**
		 * Esta funcion busca la info del id del trial y de recurso que corresponde a cada angulo
		 * @param angulo
		 */
		public void buscarInfoTrialsIds (Level level) {
			int numeroDeTrialsNoEncontrados=0; // Esto es para debug, porque se supone q todos tienen que ser encontrados
			for (int idTrialaMirar : level.secuenciaTrailsId) {
				JsonTrial jsonTrial = Trial.JsonTrial.LoadTrial(idTrialaMirar);
				if ((jsonTrial.jsonEstimulo.infoConceptualAngulos.direccionLado1 == this.anguloDeReferencia) || (jsonTrial.jsonEstimulo.infoConceptualAngulos.direccionLado2 == this.anguloDeReferencia)) {
					// Seleccionamos el angulo de interes
					int anguloNoReferencia;
					if (jsonTrial.jsonEstimulo.infoConceptualAngulos.direccionLado1 == this.anguloDeReferencia) {
						anguloNoReferencia = (int) jsonTrial.jsonEstimulo.infoConceptualAngulos.direccionLado2;
					} else {
						anguloNoReferencia = (int) jsonTrial.jsonEstimulo.infoConceptualAngulos.direccionLado1;
					}
					// Buscamos en todos los cuadrantes a ver si efectivamente esta el angulo y le asignamos el id del trial y del recurso
					for (CuadranteInfo cuadrante : this.cuadrantes) {
						for (AnguloOrdenable angulo : cuadrante.listaEstimulos) {
							if (angulo.angulo == anguloNoReferencia) {
								angulo.idTrial = jsonTrial.Id;
								angulo.idResource = jsonTrial.jsonEstimulo.resourceId;
								break; // Ojo que es importante que el loop continue en el proximo cuadrante, porque los angulos rectos aparecen en mas de un cuadrante!
							}
						}
					}
					numeroDeTrialsNoEncontrados ++;
				}
			}
			// Mensajes de warning / error
			if (numeroDeTrialsNoEncontrados!=0) {
				Gdx.app.debug(TAG, "Warning: Hay " + numeroDeTrialsNoEncontrados + " trials que no estan asociados a ningun cuadrante en el nivel");
			}
			for (CuadranteInfo cuadrante : cuadrantes) {
				for (AnguloOrdenable angulo : cuadrante.listaEstimulos) {
					if (angulo.idResource==null) {
						Gdx.app.error(TAG, "Error: La figura con angulos: " + angulo.angulo + " y " + this.anguloDeReferencia + " no pudo ser identificada en ningun trial!");
					}
				}
			}
		}
		
		/**
		 * Este metodo busca un nuevo estimulo a preguntar, lo carga en la clase y devuelve su valor absoluto  
		 */
		public void askNext() {
			if (!waitingAnswer) {
				// Elije un cuadrante al azar y carga esos datos
				Array<Integer> listaCuadrantesAnalizar = new Array<Integer>();
				for (int i=0; i<4; i++) { // agrega los cuadrantes que todavia no convergieron
					if (!cuadrantes.get(i).convergenciaAlcanzada) {
						listaCuadrantesAnalizar.add(i);
					}
				}
				if (listaCuadrantesAnalizar.size!=0) {
					int cuadranteAUsar=listaCuadrantesAnalizar.random();
					CuadranteInfo cuadrante = this.cuadrantes.get(cuadranteAUsar);
					// pone los datos en la clase principal
					this.cuadranteActivo = cuadranteAUsar;
					this.next = cuadrante.listaEstimulos.get(cuadrante.nivelEstimulo);
					this.waitingAnswer = true;
				}
			}
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
				CuadranteInfo cuadrante = this.cuadrantes.get(this.cuadranteActivo);
				// Agregamos la info del ultimo toque.
				Historial historial = new Historial();
				historial.acertado=acierto;
				historial.angulo=this.next;
				cuadrante.historial.add(historial);
				
				// Elije si hay que incrementar la dificultad, disminuirla o no hacer nada.
				boolean incrementarDificultad=false;
				boolean disminuirDificultad=false;
				if (cuadrante.historial.peek().acertado) { // Si se acerto y no hay suficiente historial se debe disminuir la dificultad, sino hay que revisar si la proporcion de aciertos requeridos esta cumpleida
					if (cuadrante.historial.size >= this.proporcionTotal) { // Estamos en el caso en que hay que mirar el historial
						// Nos fijamos si hay suficientes aciertos en el ultimo tramo como para aumentar la dificultad
						int contadorAciertos=0;
						for (int i=1; i<=(this.proporcionTotal); i++){
							if (cuadrante.historial.get(cuadrante.historial.size-i).acertado==true){
								contadorAciertos++;
							}
						}
						if (contadorAciertos>= this.proporcionAciertos) {
							incrementarDificultad=true;
						}
					} else { // Si no hay historial suficiente
						incrementarDificultad=true;
					}
				} else { // Significa q hubo un desacierto en este caso siempre se disminuye la dificultad
					disminuirDificultad = true;
				}
				 
				
				// Se fija si hay que disminuir el salto entre nivel y nivel. Para simplicar solo se considera que disminuye cuando hay un rebote "hacia arriba"
				
				if (cuadrante.historial.size >1) { // Verifica q haya al menos dos datos
					if (!cuadrante.historial.peek().acertado) { // Se se erro el ultimo 
						if (cuadrante.historial.get(cuadrante.historial.size-2).acertado) { // Si se acerto el anterior (hay rebote) 
							cuadrante.saltosActivos = cuadrante.saltosActivos - 1;
							// Verificamos que no llegue a cero el salto
							if (cuadrante.saltosActivos==0) {
								cuadrante.saltosActivos = 1;
							}
						}
					}
				}
				
				// Aqui ya se determino si hay que incrementar o dosminuir la dificultad y por lo tanto se aplica, cuidando que no exceda los limites
				if (incrementarDificultad) {
					cuadrante.nivelEstimulo=cuadrante.nivelEstimulo-cuadrante.saltosActivos;
					if (cuadrante.nivelEstimulo<0) {cuadrante.nivelEstimulo=0;}
				}
				if (disminuirDificultad) {
					cuadrante.nivelEstimulo=cuadrante.nivelEstimulo+cuadrante.saltosActivos;
					if (cuadrante.nivelEstimulo>cuadrante.listaEstimulos.size-1) {cuadrante.nivelEstimulo=cuadrante.listaEstimulos.size-1;}
				}
				
				// Nos fijamos si se alcanzo la convergencia
				cuadrante.ventana.addValue(historial.angulo.nivel);
				if (cuadrante.ventana.hasEnoughData()) {
					if (cuadrante.ventana.standardDeviation() < this.sdEsperada) { // Como la señal medida es el nivel, cada paso tiene valor uno y si se estabiliza no deberia fluctuar mas de uno alrededor de la media
						cuadrante.convergenciaAlcanzada=true;
						System.out.println("Convergencia alcanzada en el valor "+cuadrante.historial.peek().angulo.anguloRef );
					}
				}
			}
		}

		/**
		 * Esta funcion se fija se se termino el level o no
		 * @return
		 */
		public boolean complete() {
			boolean completado=false;
			if ((this.cuadrantes.get(0).historial.size+this.cuadrantes.get(1).historial.size+this.cuadrantes.get(2).historial.size+this.cuadrantes.get(3).historial.size)>=this.numeroMaximoDeTrialsXLevel) {
				completado=true;
			}
			boolean completadosTodos = true;
			for (CuadranteInfo cuadrante:cuadrantes) {
				if (!cuadrante.convergenciaAlcanzada) {
					completadosTodos=false;
				}
			}
			if (completadosTodos) {completado=true;} 
			return completado;
		}
		
		public void chkCompleted() {
			boolean completado=false;
			if ((this.cuadrantes.get(0).historial.size+this.cuadrantes.get(1).historial.size+this.cuadrantes.get(2).historial.size+this.cuadrantes.get(3).historial.size)>=this.numeroMaximoDeTrialsXLevel) {
				completado=true;
			}
			boolean completadosTodos = true;
			for (CuadranteInfo cuadrante:cuadrantes) {
				if (!cuadrante.convergenciaAlcanzada) {
					completadosTodos=false;
				}
			}
			if (completadosTodos) {completado=true;}
			this.completed=completado;
		}
		
		public int referencia(){
			return this.anguloDeReferencia;
		}
		
		public boolean convergencia (int cuadrante) {
			return this.cuadrantes.get(cuadrante).convergenciaAlcanzada;
		}
		
		public int trialsRestantes () {
			return this.numeroMaximoDeTrialsXLevel - (this.cuadrantes.get(0).historial.size + this.cuadrantes.get(1).historial.size + this.cuadrantes.get(2).historial.size + this.cuadrantes.get(3).historial.size);
		}
	}
}
