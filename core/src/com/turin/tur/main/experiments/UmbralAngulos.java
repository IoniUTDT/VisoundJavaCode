package com.turin.tur.main.experiments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.diseno.Trial.ResourceId;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeLEVEL;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.builder.Builder;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.Textos;
import com.turin.tur.main.util.builder.Imagenes.Linea;

public class UmbralAngulos {

	public static class Info {
		
		public Setup setup = new Setup();
		public Indexs indexs = new Indexs();
		public LevelAdvance advance = new LevelAdvance();
		public String nombre;
		
		public static class Setup {
			public int saltoGrande; // Salto que hay entre angulo no critico y angulo no critico
			public int saltoChico; // Salto que hay entre angulo dos angulos consecutivos alrededor de los angulos criticos
			public Array<Integer> angulosCriticos = new Array<Integer>(); // Nota: tiene que estar entre los angulo pertenecientes al salto grande para que los considere
			public Array<Integer> angulosNoDetalle = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos de al salto grande
			public Array<Integer> angulosDetalle = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos de al salto chico
			public Array<Integer> angulos = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos
			public int numeroDeReferenciasConjuntas; // Es el numero de angulos de referencia distintos que se intercalan en un mismo nivel para evitar feedback 
			public int saltoInicialEnGrados; // Esta cantidad representa el salto inicial en terminos absolutos. Sirve para configurar el numero de saltos iniciales en funcion del setup experimental					
		}
		
		public static class Indexs {
			public Array<Array<Integer>> idsResourcesByAngle = new Array<Array<Integer>>(); // Lista de arrays con los ids de los recursos que tienen cada angulo. Esto se crea en tiempo de ejecucion de creacion de recursos porque es infinitamente mas lento hacer una busqueda despues al trabajar con volumenes grandes de recursos. El tag de cada entrada es el angulo con el mismo indice de la lista de angulos	
		}

		public static class LevelAdvance {
			public Array<ConvergenciaInfo> convergencias = new Array<ConvergenciaInfo>();
		}

	}

	/**
	 * Clase para almacenar la info conceptual relacionada a los recursos de angulos
	 * @author ionatan
	 *
	 */
	public static class ResourceInfo {
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

	public static class AnguloOrdenable implements Comparable<AnguloOrdenable> {
		public int angulo;
		public int anguloRef;
		public int nivel;
		public ResourceId idResource;
		public int idTrial;
		public boolean acertado;
		
		public AnguloOrdenable () {
			
		}
		
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
	public static class ConvergenciaInfo {
		private int nivelEstimulo; // nivel de señal enviada
		private int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
		private boolean convergenciaAlcanzada=false;
		private Array<AnguloOrdenable> historial = new Array<AnguloOrdenable>(); // Se almacena la info de lo que va pasando
		private Array<AnguloOrdenable> listaEstimulos = new Array<AnguloOrdenable>(); // Lista de estimulos ordenados de menor a mayor dificultad
		private String nombreDelCuadrante;
		private int anguloDeReferencia;
	}
	
	





	public Info info = new Info();
	private int anguloDeReferencia; // Angulo correspondiente al lado que se deja quieto. 

	
	
	
	
	
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
	public class Analisis {		
		
		// Los siguientes dos numeros representan la relacion entre errores y aciertos que se espera para regular el umbral 
		private int proporcionAciertos=2;
		private int proporcionTotal=3;
		private float sdEsperada = 1f;
		private int numeroMaximoDeTrialsXLevel=60;
		private static int tamanoVentanaAnalisisConvergencia=6;
		
		
		// Variables que regulan en intercambio de datos con el levelcontroller.
		public AnguloOrdenable next; //Proximo valor a medir (en terminos absolutos)
		private int cuadranteActivo; // En cual de los cuadrantes esta la señal que se va a medir
		private boolean waitingAnswer=false; //Si se esta esperando la rta.
		public boolean completed;
		
		
		public AnalisisUmbralAngulos () {	
		}
		
		/**
		 * Al incializar el analisis hace falta a partir del setup determinar la siguiente informacion:
		 * Cuanto es el salto inicial en termino de numero de angulos en funcion de la relacion entre el salto buscado (en grados) y la densidad de angulos del setup
		 * Armar una lista ordenada de cuales son los angulos agudos y cuales los graves para poder iterar sobre esa lista el nivel de dificultad independientemente de los valores numericos
		 * @return 
		 *   
		 */
		public void init (SetupUmbralAngulos setup, int anguloReferencia, Level level) {
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
			cuadrante1.nombre = "Cuadrante1";
			cuadrante1.referencia = anguloReferencia;
			cuadrante1.saltosActivos = salto;
			cuadrante1.nivelEstimulo = angulosCuadrante1.size-1;
			cuadrante1.listaEstimulos = angulosCuadrante1;
			this.cuadrantes.add(cuadrante1);
			CuadranteInfo cuadrante2 = new CuadranteInfo();
			cuadrante2.nombre = "Cuadrante2";
			cuadrante2.referencia = anguloReferencia;
			cuadrante2.saltosActivos = salto;
			cuadrante2.nivelEstimulo = angulosCuadrante2.size-1;
			cuadrante2.listaEstimulos = angulosCuadrante2;
			this.cuadrantes.add(cuadrante2);
			CuadranteInfo cuadrante3 = new CuadranteInfo();
			cuadrante3.nombre = "Cuadrante3";
			cuadrante3.referencia = anguloReferencia;
			cuadrante3.saltosActivos = salto;
			cuadrante3.nivelEstimulo = angulosCuadrante3.size-1;
			cuadrante3.listaEstimulos = angulosCuadrante3;
			this.cuadrantes.add(cuadrante3);
			CuadranteInfo cuadrante4 = new CuadranteInfo();
			cuadrante4.nombre = "Cuadrante4";
			cuadrante4.referencia = anguloReferencia;
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
				JsonTrial jsonTrial = Trial.JsonTrial.LoadTrial(idTrialaMirar, level.Id);
				if ((jsonTrial.jsonEstimulo.infoConceptualAngulos.direccionLado1 == this.anguloDeReferencia) || (jsonTrial.jsonEstimulo.infoConceptualAngulos.direccionLado2 == this.anguloDeReferencia)) {
					boolean encontrado = false;
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
								encontrado = true;
								break; // Ojo que es importante que el loop continue en el proximo cuadrante, porque los angulos rectos aparecen en mas de un cuadrante!
							}
						}
					}
					if (!encontrado) {
						Gdx.app.debug(Experiments.TAG, "Trial que no se asigno a cuadrante: " + jsonTrial.Id);
					}
				}
			}
			// Mensajes de warning / error
			if (numeroDeTrialsNoEncontrados!=0) {
				Gdx.app.debug(Experiments.TAG, "Warning: Hay " + numeroDeTrialsNoEncontrados + " trials que no estan asociados a ningun cuadrante en el nivel");
			}
			for (CuadranteInfo cuadrante : cuadrantes) {
				for (AnguloOrdenable angulo : cuadrante.listaEstimulos) {
					if (angulo.idResource==null) {
						Gdx.app.error(Experiments.TAG, "Error: La figura con angulos: " + angulo.angulo + " y " + this.anguloDeReferencia + " no pudo ser identificada en ningun trial!");
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
	
	
	
	
	
	
	
	
	public UmbralAngulos () {
		
	}
	
	
	
	
	public void makeLevels() {
		// Hacemos tareas de revision y limpieza
		
		Builder.verifyLevelVersion();
		Builder.verifyResources();
		Builder.cleanAssets();
		
		// Cargamos los datos del setup
		String path = Resources.Paths.currentVersionPath+"extras/jsonSetupUmbralAngulos.meta";
		String savedData = FileHelper.readLocalFile(path);
		Json json = new Json();
		json.setUsePrototypes(false);
		this.info = json.fromJson(UmbralAngulos.Info.class, savedData);

		// Hacemos tareas de revision y limpieza
		
		Array<Integer> angulosdeReferencia = new Array<Integer>(); 
		
		for (int i = 0; i<=(90/this.info.setup.saltoGrande); i++) { // Hacemos solo para el primer cuadrante
			angulosdeReferencia.add(this.info.setup.saltoGrande*i);
		}

		while (angulosdeReferencia.size>0) { // Seleccionamos angulos en forma random segun parametros del setup
			Array<Integer> angulosElegidos = new Array<Integer>();
			// Los quita de la lista general y lo pasa a la de los que se van a incluir en el proximo nivel
			if (angulosdeReferencia.size>=this.info.setup.numeroDeReferenciasConjuntas) { //OJO!!! Esto solo va a funcionar bien si numeroDeRefrenciasConjuntas es 2, porque sino se puede cortar antes la lista al buscar mas adelante! 
				for (int i=0; i<this.info.setup.numeroDeReferenciasConjuntas; i++) {
					angulosElegidos.add(angulosdeReferencia.removeIndex(i));
					//angulosElegidos.add(angulosdeReferencia.removeIndex(MathUtils.random(angulosdeReferencia.size-1)));
				}	
				if (angulosdeReferencia.size==1) { // Agregamos el ultimo que queda al grupo anterior si queda uno solo...
					angulosElegidos.addAll(angulosdeReferencia);
					angulosdeReferencia.clear();
				}
			} else {
				angulosElegidos.addAll(angulosdeReferencia);
				angulosdeReferencia.clear();
			}
			// Ahora creamos el nivel
			JsonLevel level = Builder.crearLevel();
			level.tipoDeLevel = TIPOdeLEVEL.UMBRALANGULO;
			level.angulosReferencia = angulosElegidos;
			level.levelTitle = "";
			for (int i=0; i<angulosElegidos.size; i++) {
				level.levelTitle = level.levelTitle + " R: "+angulosElegidos.get(i);
			}
			level.randomTrialSort=false;
			level.show = true;

			// agregamos un trial por recurso. 
			for (int anguloRef:angulosElegidos) {
				for (int recurso:this.info.indexs.idsResourcesByAngle.get(this.info.setup.angulos.indexOf(anguloRef, false))) {
					JsonTrial trial = Builder.crearTrial("Selecciones a que categoria pertenece el angulo", "", DISTRIBUCIONESenPANTALLA.LINEALx3,
							new int[] {Constants.Resources.Categorias.Grave.ID,Constants.Resources.Categorias.Recto.ID,Constants.Resources.Categorias.Agudo.ID}, TIPOdeTRIAL.TEST, recurso, false, true, false);
					savedData = FileHelper.readFile(Resources.Paths.fullCurrentVersionPath + recurso + ".meta");
					json = new Json();
					json.setUsePrototypes(false);
					trial.jsonEstimulo =  json.fromJson(JsonResourcesMetaData.class, savedData);
					level.jsonTrials.add(trial); 
				}
			}
			level.infoExpAngulos = this.info;
			Builder.extract(level);
			Builder.buildJsons(level);
		}
	}

	
	/**
	 * Este metodo busca todos lo angulos en los que debe haber lineas segun los parametros con que se configure el setup
	 */
	public void searchAngles() {
		
		for (int i=0; i<360; i=i+this.info.setup.saltoGrande) {
			this.info.setup.angulosNoDetalle.add(i);
			if (this.info.setup.angulosCriticos.contains(i,false)){ // Si es un angulo critico
				int numeroDeAngulosExtra = this.info.setup.saltoGrande/this.info.setup.saltoChico;
				for (int j=1; j<numeroDeAngulosExtra; j++) {
					int value = j*this.info.setup.saltoChico+i;
					if (value < 0) {
						value = 360 + value; 
					}
					this.info.setup.angulosDetalle.add(value);
					value = -j*this.info.setup.saltoChico+i;
					if (value < 0) {
						value = 360 + value; 
					}
					this.info.setup.angulosDetalle.add(value);
				}
			}
		}
		
		this.info.setup.angulos.addAll(this.info.setup.angulosDetalle);
		this.info.setup.angulos.addAll(this.info.setup.angulosNoDetalle);
		for (@SuppressWarnings("unused") int i:this.info.setup.angulos) {
			this.info.indexs.idsResourcesByAngle.add(new Array<Integer>());
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
		if (this.info.setup.angulosDetalle.contains(angulo1, false) && this.info.setup.angulosDetalle.contains(angulo2, false)) {
			return (deltaAngulo >= this.info.setup.saltoGrande*2);
		} else {
			return (deltaAngulo >= this.info.setup.saltoGrande);
		}
	}

	public void generalBuilding () {
		this.loadSetup();
		Builder.verifyResourcesVersion();
		Textos.crearTextos();
		this.searchAngles(); // Hace que el setup busque todos los angulos
		this.resourcesBuild();
	}
	
	public void loadSetup () {
		this.info.nombre="SetupAngulosUmbral";
		this.info.setup.saltoChico = 1;
		this.info.setup.saltoChico=1;
		this.info.setup.saltoGrande=5;
		this.info.setup.angulosCriticos.add(0);
		this.info.setup.angulosCriticos.add(90);
		this.info.setup.angulosCriticos.add(180);
		this.info.setup.angulosCriticos.add(270);
		this.info.setup.numeroDeReferenciasConjuntas = 2;
		this.info.setup.saltoInicialEnGrados = 20;
	}
	
	/**
	 *  Este metodo se encarga de contruir los recursos necesarios para el experimento
	 */
	public void resourcesBuild () {
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (Resources.Display.width>Resources.Display.height) {
			tamano = Resources.Display.height;
		} else {
			tamano = Resources.Display.width;
		}
		// Hacemos dos indices que recorran todos los angulos de manera que el segundo indice solo llegue hasta el primero para evitar duplicar los recursos
		for (int indice1 = 0; indice1<this.info.setup.angulos.size ; indice1++) {
			for (int indice2 = indice1 +1; indice2<this.info.setup.angulos.size; indice2++) {
				int angulo1 = this.info.setup.angulos.get(indice1);
				int angulo2 = this.info.setup.angulos.get(indice2);
				if (this.cumpleCriterioDistanciaMinima(angulo1, angulo2)) {
					int deltaAngulo = angulo2-angulo1;
					if (deltaAngulo < 0) {deltaAngulo=-deltaAngulo;}  // Hacemos que sean todos los numeros positivos
					if (deltaAngulo >= 180) {deltaAngulo = 360 - deltaAngulo;} // Hacemos que los angulos sean considerados siempre del lado "concavo")

					// Creamos la imagen correspondiente
					Imagenes imagen = new Imagenes();
					
					float Xcenter = Resources.Display.width/2;
					float Ycenter = Resources.Display.height/2;
					imagen.infoConceptualAngulos.direccionLado1 = angulo1;
					imagen.infoConceptualAngulos.direccionLado2 = angulo2;
					
					// Agregamos al setup que el objeto creado tiene los angulos dados para facilitar la busqueda posterior
					int indice = this.info.setup.angulos.indexOf(angulo1, false);
					this.info.indexs.idsResourcesByAngle.get(indice).add(imagen.resourceId.id);
					indice = this.info.setup.angulos.indexOf(angulo2, false);
					this.info.indexs.idsResourcesByAngle.get(indice).add(imagen.resourceId.id);

					// Clasificamos el angulo segun sea agudo recto o grave
					imagen.infoConceptualAngulos.separacionAngular = deltaAngulo;
					if (deltaAngulo < 90) {
						imagen.infoConceptualAngulos.categoriaAngulo = CategoriaAngulo.Agudo;
						imagen.categories.add(Categorias.Agudo);
					} else {
						if (deltaAngulo > 90) {
							imagen.infoConceptualAngulos.categoriaAngulo = CategoriaAngulo.Grave;
							imagen.categories.add(Categorias.Grave);
						} else {
							imagen.infoConceptualAngulos.categoriaAngulo = CategoriaAngulo.Recto;
							imagen.categories.add(Categorias.Recto);
						}
					}
				
					// agrega la primer linea (notas de sistema de coordenadas:
					// El SVG considera el y positivo hacia abajo, pero eso se compensa al crear el archivo. Todo el codigo considera
					// El eje x positivo hacia la derecha y el y positivo hacia arriba
					Linea infoLinea = new Linea();
					infoLinea.radial.angulo=angulo1;
					infoLinea.radial.largo=tamano/2;
					infoLinea.radial.Xcenter = (float) (Xcenter + tamano/4 * MathUtils.cosDeg(angulo1));
					infoLinea.radial.Ycenter = (float) (Ycenter + tamano/4 * MathUtils.sinDeg(angulo1));
					infoLinea.lineaFromRadial();
					imagen.lineas.add(infoLinea);
					// agrega la segunda linea
					infoLinea = new Linea();
					infoLinea.radial.angulo=angulo2;
					infoLinea.radial.largo=tamano/2;
					infoLinea.radial.Xcenter = (float) (Xcenter + tamano/4 * MathUtils.cosDeg(angulo2));
					infoLinea.radial.Ycenter = (float) (Ycenter + tamano/4 * MathUtils.sinDeg(angulo2));
					infoLinea.lineaFromRadial();
					imagen.lineas.addAll(infoLinea);
		
					// agregamos la info a la imagen
					imagen.comments = "Imagen generada por secuencia automatica 'recursosAnguloAnalisisUmbral'.";
					imagen.name = "Imagen de angulos generada automaticamente";
					imagen.idVinculo = "";
					imagen.categories.add(Categorias.Angulo);
					
					imagen.toSVG();
					// objetos.add(imagen); // aca hay que hacer que cree la imagen
				}
			}
		}
		// Guardamos el setup 
		String path = Resources.Paths.currentVersionPath+"/extras/jsonSetupUmbralAngulos.meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path, json.toJson(this.info));
	}
}
