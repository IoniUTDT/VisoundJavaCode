package com.turin.tur.main.experiments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.turin.tur.main.diseno.Level.JsonLevel;
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

	private static final String TAG = UmbralAngulos.class.getName();
	
	public static class Info {
		
		public Setup setup = new Setup();
		public Indexs indexs = new Indexs();
		public LevelAdvance advance = new LevelAdvance();
		public String nombre;
		
		public static class Setup {
			
			// Cosas relacionadas con la generacion de recursos
			public int saltoGrande; // Salto que hay entre angulo no critico y angulo no critico
			public int saltoChico; // Salto que hay entre angulo dos angulos consecutivos alrededor de los angulos criticos
			public Array<Integer> angulosCriticos = new Array<Integer>(); // Nota: tiene que estar entre los angulo pertenecientes al salto grande para que los considere
			public Array<Integer> angulosNoDetalle = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos de al salto grande
			public Array<Integer> angulosDetalle = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos de al salto chico
			public Array<Integer> angulos = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos
	
			
			// Cosas relacionadas con la generacion de niveles
			public int numeroDeReferenciasConjuntas; // Es el numero de angulos de referencia distintos que se intercalan en un mismo nivel para evitar feedback 
			public int saltoInicialEnGrados; // Esta cantidad representa el salto inicial en terminos absolutos. Sirve para configurar el numero de saltos iniciales en funcion del setup experimental

			
			// Cosas relacionadas con la dinamica del level 
			// Los siguientes dos numeros representan la relacion entre errores y aciertos que se espera para regular el umbral 
			private int proporcionAciertos=2;
			private int proporcionTotal=3;
			private float sdEsperada = 1f;
			public int numeroMaximoDeTrialsXCuadrante=15;
			private int tamanoVentanaAnalisisConvergencia=6;

		}
		
		public static class Indexs {
			public ArrayMap<Integer, ArrayMap<Integer, Integer>> idsResourcesBySides = new ArrayMap<Integer, ArrayMap<Integer, Integer>>(); // Lista de ids de los recursos para cada angulo de referencia
			public Array<Array<Integer>> resourcesContent = new Array<Array<Integer>>();
			public Array<Array<Integer>> resourcesSide2Tag = new Array<Array<Integer>>();
			public Array<Integer> resourcesSide1Tag = new Array<Integer>();
			
			/*
			public ArrayMap<Integer, ArrayMap<Integer, ArrayMap<Integer, Integer>>> idsTrialByLevelBySides = new ArrayMap<Integer, ArrayMap<Integer, ArrayMap<Integer, Integer>>>();  // Lista de ids de los trial. El primer indice es el angulo de referencia, el segundo el angulo del lado restante
			public Array<Array<Array<Integer>>> trialsContent = new Array<Array<Array<Integer>>>();
			public Array<Array<Array<Integer>>> trialsSide2Tag = new Array<Array<Array<Integer>>>();
			public Array<Array<Integer>> trialsSide1Tag = new Array<Array<Integer>>();
			public Array<Integer> trialsLevelTag = new Array<Integer>();
			*/
			/*
			public void trialMaptoArray () {
				int levelContador=0;
				for (Entry<Integer, ArrayMap<Integer, ArrayMap<Integer, Integer>>> levelKeyEntry : this.idsTrialByLevelBySides.entries()) {
					this.trialsLevelTag.add(levelKeyEntry.key);
					this.trialsSide1Tag.add(new Array<Integer>());
					this.trialsSide2Tag.add(new Array<Array<Integer>>());
					this.trialsContent.add(new Array<Array<Integer>>());
					int side1Contador=0;
					for (Entry<Integer, ArrayMap<Integer, Integer>> side1KeyEntry : this.idsTrialByLevelBySides.get(levelKeyEntry.key).entries()) {
						this.trialsSide1Tag.get(levelContador).add(side1KeyEntry.key);
						this.trialsSide2Tag.get(levelContador).add(new Array<Integer>());
						this.trialsContent.get(levelContador).add(new Array<Integer>());
						for (Entry<Integer, Integer> side2KeyEntry : this.idsTrialByLevelBySides.get(levelKeyEntry.key).get(side1KeyEntry.key).entries()){
							this.trialsSide2Tag.get(levelContador).get(side1Contador).add(side2KeyEntry.key);
							this.trialsContent.get(levelContador).get(side1Contador).add(this.idsTrialByLevelBySides.get(levelKeyEntry.key).get(side1KeyEntry.key).get(side2KeyEntry.key));
						}
						side1Contador++;
					}
					levelContador++;
				}
				this.idsTrialByLevelBySides.clear();
			}
			*/
			public void resourcesMaptoArray () {
				int side1Contador=0;
				for (Entry<Integer, ArrayMap<Integer, Integer>> side1KeyEntry : this.idsResourcesBySides.entries()) {
					this.resourcesSide1Tag.add(side1KeyEntry.key);
					this.resourcesSide2Tag.add(new Array<Integer>());
					this.resourcesContent.add(new Array<Integer>());
					for (Entry<Integer, Integer> side2KeyEntry : this.idsResourcesBySides.get(side1KeyEntry.key).entries()) {
						this.resourcesSide2Tag.get(side1Contador).add(side2KeyEntry.key);
						this.resourcesContent.get(side1Contador).add(side2KeyEntry.value);
					}
					side1Contador++;
				}
				this.idsResourcesBySides.clear();
			}
			
			public void resourcesArraytoMap () {
				for (int side1Contador = 0; side1Contador < this.resourcesSide1Tag.size; side1Contador++) {
					this.idsResourcesBySides.put(this.resourcesSide1Tag.get(side1Contador), new ArrayMap<Integer, Integer>());
					for (int side2Contador = 0; side2Contador < this.resourcesSide2Tag.get(side1Contador).size; side2Contador++) {
						this.idsResourcesBySides.get(resourcesSide1Tag.get(side1Contador)).put(resourcesSide2Tag.get(side1Contador).get(side2Contador),resourcesContent.get(side1Contador).get(side2Contador));
					}
				}
				this.resourcesContent.clear();
				this.resourcesSide1Tag.clear();
				this.resourcesSide2Tag.clear();
			}
			/*
			public void trialArraytoMap () {
				for (Integer levelContador = 0; levelContador < this.trialsLevelTag.size; levelContador++) {
					this.idsTrialByLevelBySides.put(this.trialsLevelTag.get(levelContador), new ArrayMap <Integer, ArrayMap <Integer, Integer>>());
					for (Integer side1Contador = 0; side1Contador < this.trialsSide1Tag.get(levelContador).size; side1Contador++) {
						this.idsTrialByLevelBySides.get(this.trialsLevelTag.get(levelContador)).put(this.trialsSide1Tag.get(levelContador).get(side1Contador), new ArrayMap <Integer, Integer>());
						for (Integer side2Contador = 0; side2Contador < this.trialsSide2Tag.get(levelContador).get(side1Contador).size; side2Contador++) {
							this.idsTrialByLevelBySides.get(this.trialsLevelTag.get(levelContador)).get(this.trialsSide1Tag.get(levelContador).get(side1Contador)).put(this.trialsSide2Tag.get(levelContador).get(side1Contador).get(side2Contador), this.trialsContent.get(levelContador).get(side1Contador).get(side2Contador));
						}
					}
				}
			}
			*/
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
		public int direccionLado1;
		public int direccionLado2;
		public float separacionAngular;
		public CategoriaAngulo categoriaAngulo;
		public boolean critico;
		public String DescripcionParametros = "Se almacena (todo en grados) la direccion de ambos lados, el angulo formado entre ambos lados, si el angulo es agudo recto o grave, y si e critico, o sea, alguno de los lados esta sobre un eje.";
	}
	
	public enum CategoriaAngulo {
		Agudo, Recto, Grave;
	}

	public static class AnguloOrdenable implements Comparable<AnguloOrdenable> {
		
		public int angulo;
		public int anguloReferido;
		public int nivel;
		public ResourceId idResource = new ResourceId();
		public int idTrial;
		public boolean acertado;
		public int anguloDeReferencia;
		
		public AnguloOrdenable () {
			
		}
		 
		@Override
		public int compareTo(AnguloOrdenable o) {
			return Integer.valueOf(anguloReferido).compareTo(o.anguloReferido);
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
		public int nivelEstimulo; // nivel de señal enviada
		public int saltosActivos; // nivel del proximo salto (en numero de niveles de señal)
		public boolean convergenciaAlcanzada=false;
		public Array<AnguloOrdenable> historial = new Array<AnguloOrdenable>(); // Se almacena la info de lo que va pasando
		public Array<AnguloOrdenable> listaEstimulos = new Array<AnguloOrdenable>(); // Lista de estimulos ordenados de menor a mayor dificultad
		public String nombreDelCuadrante;
		public int anguloDeReferencia;
		public float ultimaSD;
		public float ultimoMEAN;
		public int numeroCuadrante;
		public float ultimoMEANAngulo;
	}
	


	public Info info = new Info();
	// private int anguloDeReferencia; // Angulo correspondiente al lado que se deja quieto. 

	// Variables que regulan en intercambio de datos con el levelcontroller.
	public AnguloOrdenable next; //Proximo valor a medir (en terminos absolutos)
	public ConvergenciaInfo cuadranteActivo; // En cual de los cuadrantes esta la señal que se va a medir
	public boolean waitingAnswer=false; //Si se esta esperando la rta.
	public boolean levelCompleted;
	public ArrayMap <String, WindowedMean> ventanasNivel = new ArrayMap <String, WindowedMean>();
	public ArrayMap <String, WindowedMean> ventanasAnguloReferenciado = new ArrayMap <String, WindowedMean>();
	public int numeroDeTrialsRealizados;
	
	public UmbralAngulos () {
	}
	
	/**
	 * Este metodo busca un nuevo estimulo a preguntar, lo carga en la clase y devuelve su valor absoluto  
	 */
	public void askNext() {
		if (!waitingAnswer) {
			// Elije un cuadrante al azar y carga esos datos
			Array<Integer> listaCuadrantesAnalizar = new Array<Integer>();
			for (int i=0; i<this.info.advance.convergencias.size; i++) { // agrega los cuadrantes que todavia no convergieron
				if (!this.info.advance.convergencias.get(i).convergenciaAlcanzada) {
					listaCuadrantesAnalizar.add(i);
				}
			}
			if (listaCuadrantesAnalizar.size!=0) {
				// pone los datos en la clase principal
				this.cuadranteActivo = this.info.advance.convergencias.get(listaCuadrantesAnalizar.random());
				this.next = this.cuadranteActivo.listaEstimulos.get(this.cuadranteActivo.nivelEstimulo);
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
			// Agregamos la info del ultimo toque.
			this.next.acertado=acierto;
			this.cuadranteActivo.historial.add(this.next);
			this.numeroDeTrialsRealizados ++;
			
			// Elije si hay que incrementar la dificultad, disminuirla o no hacer nada.
			boolean incrementarDificultad=false;
			boolean disminuirDificultad=false;
			if (this.cuadranteActivo.historial.peek().acertado) { // Si se acerto y no hay suficiente historial se debe disminuir la dificultad, sino hay que revisar si la proporcion de aciertos requeridos esta cumpleida
				if (this.cuadranteActivo.historial.size >= this.info.setup.proporcionTotal) { // Estamos en el caso en que hay que mirar el historial
					// Nos fijamos si hay suficientes aciertos en el ultimo tramo como para aumentar la dificultad
					int contadorAciertos=0;
					for (int i=1; i<=(this.info.setup.proporcionTotal); i++){
						if (this.cuadranteActivo.historial.get(this.cuadranteActivo.historial.size-i).acertado==true){
							contadorAciertos++;
						}
					}
					if (contadorAciertos>= this.info.setup.proporcionAciertos) {
						incrementarDificultad=true;
					}
				} else { // Si no hay historial suficiente
					incrementarDificultad=true;
				}
			} else { // Significa q hubo un desacierto en este caso siempre se disminuye la dificultad
				disminuirDificultad = true;
			}
			 
			
			// Se fija si hay que disminuir el salto entre nivel y nivel. Para simplicar solo se considera que disminuye cuando hay un rebote "hacia arriba"
			
			if (this.cuadranteActivo.historial.size >1) { // Verifica q haya al menos dos datos
				if (!this.cuadranteActivo.historial.peek().acertado) { // Se se erro el ultimo 
					if (this.cuadranteActivo.historial.get(this.cuadranteActivo.historial.size-2).acertado) { // Si se acerto el anterior (hay rebote) 
						this.cuadranteActivo.saltosActivos = this.cuadranteActivo.saltosActivos - 1;
						// Verificamos que no llegue a cero el salto
						if (this.cuadranteActivo.saltosActivos==0) {
							this.cuadranteActivo.saltosActivos = 1;
						}
					}
				}
			}
			
			// Aqui ya se determino si hay que incrementar o dosminuir la dificultad y por lo tanto se aplica, cuidando que no exceda los limites
			if (incrementarDificultad) {
				this.cuadranteActivo.nivelEstimulo=this.cuadranteActivo.nivelEstimulo-this.cuadranteActivo.saltosActivos;
				if (this.cuadranteActivo.nivelEstimulo<0) {this.cuadranteActivo.nivelEstimulo=0;}
			}
			if (disminuirDificultad) {
				this.cuadranteActivo.nivelEstimulo=this.cuadranteActivo.nivelEstimulo+this.cuadranteActivo.saltosActivos;
				if (this.cuadranteActivo.nivelEstimulo>this.cuadranteActivo.listaEstimulos.size-1) {this.cuadranteActivo.nivelEstimulo=this.cuadranteActivo.listaEstimulos.size-1;}
			}
			
			// Nos fijamos si se alcanzo la convergencia
			if (!this.ventanasNivel.containsKey(this.cuadranteActivo.nombreDelCuadrante)) { // Primero nos fijamos si existe una ventana para el cuadrante activo
				this.ventanasNivel.put(this.cuadranteActivo.nombreDelCuadrante, new WindowedMean(this.info.setup.tamanoVentanaAnalisisConvergencia));
			} 
			if (!this.ventanasAnguloReferenciado.containsKey(this.cuadranteActivo.nombreDelCuadrante)) { // Primero nos fijamos si existe una ventana para el cuadrante activo
				this.ventanasAnguloReferenciado.put(this.cuadranteActivo.nombreDelCuadrante, new WindowedMean(this.info.setup.tamanoVentanaAnalisisConvergencia));
			} 
			
			this.ventanasNivel.get(this.cuadranteActivo.nombreDelCuadrante).addValue(this.next.nivel);
			this.ventanasAnguloReferenciado.get(this.cuadranteActivo.nombreDelCuadrante).addValue(this.next.anguloReferido);
			if (this.ventanasNivel.get(this.cuadranteActivo.nombreDelCuadrante).hasEnoughData()) {
				this.cuadranteActivo.ultimaSD = this.ventanasNivel.get(this.cuadranteActivo.nombreDelCuadrante).standardDeviation();
				this.cuadranteActivo.ultimoMEAN = this.ventanasNivel.get(this.cuadranteActivo.nombreDelCuadrante).getMean();
				this.cuadranteActivo.ultimoMEANAngulo = this.ventanasAnguloReferenciado.get(this.cuadranteActivo.nombreDelCuadrante).getMean();
				if (this.cuadranteActivo.ultimaSD < this.info.setup.sdEsperada) {
					this.cuadranteActivo.convergenciaAlcanzada = true;
					Gdx.app.debug(TAG, this.cuadranteActivo.nombreDelCuadrante + " ha alcanzado la convergencia con valor " + this.cuadranteActivo.ultimoMEAN);
				}
			}
			this.levelCompleted();
		}
	}
	
	/**
	 * Esta funcion se fija se se termino el level o no
	 * @return
	 */
	
	public void levelCompleted() {
		boolean todosCompletados = true;
		for (ConvergenciaInfo cuadrante : this.info.advance.convergencias) {
			if (!cuadrante.convergenciaAlcanzada) {
				todosCompletados = false;
			}
		}
		if (this.numeroDeTrialsRealizados >= this.info.advance.convergencias.size * this.info.setup.numeroMaximoDeTrialsXCuadrante) {
			this.levelCompleted=true;
		}
		if (todosCompletados) {
			this.levelCompleted=true;
		}
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
		this.info.indexs.resourcesArraytoMap();
		// Hacemos tareas de revision y limpieza
		
		Array<Integer> angulosdeReferencia = new Array<Integer>(); 
		
		for (int i = 0; i<=(90/this.info.setup.saltoGrande); i++) { // Hacemos solo para el primer cuadrante
			angulosdeReferencia.add(this.info.setup.saltoGrande*i); // Esta lista se manipula durante el make level	
		}
		
		
		while (angulosdeReferencia.size>0) { // Seleccionamos angulos en forma random segun parametros del setup
			Array<Integer> angulosReferenciaElegidos = new Array<Integer>();
			// Los quita de la lista general y lo pasa a la de los que se van a incluir en el proximo nivel
			if (angulosdeReferencia.size>=this.info.setup.numeroDeReferenciasConjuntas) { //OJO!!! Esto solo va a funcionar bien si numeroDeRefrenciasConjuntas es 2, porque sino se puede cortar antes la lista al buscar mas adelante! 
				for (int i=0; i<this.info.setup.numeroDeReferenciasConjuntas; i++) {
					angulosReferenciaElegidos.add(angulosdeReferencia.removeIndex(i));
					//angulosElegidos.add(angulosdeReferencia.removeIndex(MathUtils.random(angulosdeReferencia.size-1)));
				}	
				if (angulosdeReferencia.size==1) { // Agregamos el ultimo que queda al grupo anterior si queda uno solo...
					angulosReferenciaElegidos.addAll(angulosdeReferencia);
					angulosdeReferencia.clear();
				}
			} else {
				angulosReferenciaElegidos.addAll(angulosdeReferencia);
				angulosdeReferencia.clear();
			}
			// Ahora creamos el nivel
			JsonLevel level = Builder.crearLevel();
			level.tipoDeLevel = TIPOdeLEVEL.UMBRALANGULO;
			// level.angulosReferencia = angulosReferenciaElegidos;
			level.levelTitle = "";
			for (int i=0; i<angulosReferenciaElegidos.size; i++) {
				level.levelTitle = level.levelTitle + " R: "+angulosReferenciaElegidos.get(i);
			}
			// level.randomTrialSort=false;
			// level.show = true;

			// Agregamos la entrada del nivel en el index de trials
			//this.info.indexs.idsTrialByLevelBySides.put(level.Id, new ArrayMap<Integer, ArrayMap<Integer, Integer>>());
			// agregamos un trial por recurso. 
			for (int anguloReferencia:angulosReferenciaElegidos) {
				// Agregamos la info relacionada al angulo de referencia en el index de trials
				// this.info.indexs.idsTrialByLevelBySides.get(level.Id).put(anguloReferencia, new ArrayMap<Integer, Integer>());
				// Creamos las convergencias correspondientes a los 4 cuadrantes de esta refrencia
				ConvergenciaInfo cuadrante1 = new ConvergenciaInfo();
				ConvergenciaInfo cuadrante2 = new ConvergenciaInfo();
				ConvergenciaInfo cuadrante3 = new ConvergenciaInfo();
				ConvergenciaInfo cuadrante4 = new ConvergenciaInfo();
				
				
				for (Entry<Integer, Integer> recurso:this.info.indexs.idsResourcesBySides.get(anguloReferencia).entries()) {
					
					// Nos fijamos si cumple con el criterio de sitancia minimo
					if (this.cumpleCriterioDistanciaMinima(anguloReferencia, recurso.key)) {
					
						// Creamos el trial
						JsonTrial trial = Builder.crearTrial("Selecciones a que categoria pertenece el angulo", "", DISTRIBUCIONESenPANTALLA.LINEALx3,
								new int[] {Constants.Resources.Categorias.Grave.ID,Constants.Resources.Categorias.Recto.ID,Constants.Resources.Categorias.Agudo.ID}, TIPOdeTRIAL.TEST, recurso.value, false, true, false);

						// agregamos el trial creado al index
						// this.info.indexs.idsTrialByLevelBySides.get(level.Id).get(anguloReferencia).put(recurso.key, trial.Id);

						// Creamos el angulo correspondiente y lo agregamos al cuadrante que toque
						int anguloReferido = recurso.key - anguloReferencia;
						if (anguloReferido < 0) {anguloReferido = anguloReferido + 360;} // corrige para que sean todos angulos en la primer vuelta
						AnguloOrdenable anguloOrdenable = new AnguloOrdenable();
						anguloOrdenable.angulo = recurso.key;
						anguloOrdenable.anguloDeReferencia = anguloReferencia;
						anguloOrdenable.anguloReferido = anguloReferido;
						anguloOrdenable.idTrial = trial.Id;
						anguloOrdenable.idResource.id = recurso.value;
						
						
						if (anguloOrdenable.anguloReferido > 0 && anguloOrdenable.anguloReferido <= 90) {
							cuadrante1.listaEstimulos.add(anguloOrdenable);
						}
						if (anguloOrdenable.anguloReferido >= 90 && anguloOrdenable.anguloReferido < 180) {
							cuadrante2.listaEstimulos.add(anguloOrdenable);
						}
						if (anguloOrdenable.anguloReferido > 180 && anguloOrdenable.anguloReferido <= 270) {
							cuadrante3.listaEstimulos.add(anguloOrdenable);
						}
						if (anguloOrdenable.anguloReferido >= 270 && anguloOrdenable.anguloReferido < 360) {
							cuadrante4.listaEstimulos.add(anguloOrdenable);
						}
						
						level.jsonTrials.add(trial); 
						
					}
					
					 
				}
				
				// Agregamos info restante y general a los cuadrantes
				int saltoInicial = this.info.setup.saltoInicialEnGrados / this.info.setup.saltoGrande;
				// Ordenamos los cuadrantes segun nivel de deificultad y agregamos el datos a la info del angulo
				cuadrante1.listaEstimulos.sort();
				cuadrante1.listaEstimulos.reverse();
				for (AnguloOrdenable angulo :cuadrante1.listaEstimulos) {
					angulo.nivel = cuadrante1.listaEstimulos.indexOf(angulo, true);
				}
				cuadrante2.listaEstimulos.sort();
				for (AnguloOrdenable angulo :cuadrante2.listaEstimulos) {
					angulo.nivel = cuadrante2.listaEstimulos.indexOf(angulo, true);
				}
				cuadrante3.listaEstimulos.sort();
				cuadrante3.listaEstimulos.reverse();
				for (AnguloOrdenable angulo :cuadrante3.listaEstimulos) {
					angulo.nivel = cuadrante3.listaEstimulos.indexOf(angulo, true);
				}
				cuadrante4.listaEstimulos.sort();
				for (AnguloOrdenable angulo :cuadrante4.listaEstimulos) {
					angulo.nivel = cuadrante4.listaEstimulos.indexOf(angulo, true);
				}
				// Agregamos la info general a cada cuadrante
				cuadrante1.nombreDelCuadrante = "Cuadrante 1" + " R: " + anguloReferencia;
				cuadrante1.anguloDeReferencia = anguloReferencia;
				cuadrante1.saltosActivos = saltoInicial;
				cuadrante1.nivelEstimulo = cuadrante1.listaEstimulos.size - 1;
				cuadrante1.numeroCuadrante=1;

				
				cuadrante2.nombreDelCuadrante = "Cuadrante 2" + " R: " + anguloReferencia;
				cuadrante2.anguloDeReferencia = anguloReferencia;
				cuadrante2.saltosActivos = saltoInicial;
				cuadrante2.nivelEstimulo = cuadrante2.listaEstimulos.size - 1;
				cuadrante2.numeroCuadrante=2;
				
				cuadrante3.nombreDelCuadrante = "Cuadrante 3" + " R: " + anguloReferencia;
				cuadrante3.anguloDeReferencia = anguloReferencia;
				cuadrante3.saltosActivos = saltoInicial;
				cuadrante3.nivelEstimulo = cuadrante3.listaEstimulos.size - 1;
				cuadrante3.numeroCuadrante=3;
				
				cuadrante4.nombreDelCuadrante = "Cuadrante 4" + " R: " + anguloReferencia;
				cuadrante4.anguloDeReferencia = anguloReferencia;
				cuadrante4.saltosActivos = saltoInicial;
				cuadrante4.nivelEstimulo = cuadrante4.listaEstimulos.size - 1;
				cuadrante4.numeroCuadrante=4;

				// Agregamos los cuadrantes a la lista de cuadrantes que de guarda en el info del nivel
				this.info.advance.convergencias.add(cuadrante1);
				this.info.advance.convergencias.add(cuadrante2);
				this.info.advance.convergencias.add(cuadrante3);
				this.info.advance.convergencias.add(cuadrante4);
			}
			
			this.info.indexs.resourcesMaptoArray();
			//this.info.indexs.trialMaptoArray();
			level.infoDinamica = this.info;
			Builder.extract(level);
			Builder.buildJsons(level);
			this.info.indexs.resourcesArraytoMap();
			this.info.advance.convergencias.clear();
			//this.info.indexs.trialArraytoMap();
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
		for (int i:this.info.setup.angulos) {
			this.info.indexs.idsResourcesBySides.put(i, new ArrayMap<Integer, Integer>());
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
					ResourceInfo infoConceptualAngulos = new ResourceInfo(); 
					infoConceptualAngulos.direccionLado1 = angulo1;
					infoConceptualAngulos.direccionLado2 = angulo2;
					
					// Agregamos al setup que el objeto creado tiene los angulos dados para facilitar la busqueda posterior
					this.info.indexs.idsResourcesBySides.get(angulo1).put(angulo2, imagen.resourceId.id);
					this.info.indexs.idsResourcesBySides.get(angulo2).put(angulo1, imagen.resourceId.id);
					
					// Clasificamos el angulo segun sea agudo recto o grave
					infoConceptualAngulos.separacionAngular = deltaAngulo;
					if (deltaAngulo < 90) {
						infoConceptualAngulos.categoriaAngulo = CategoriaAngulo.Agudo;
						imagen.categories.add(Categorias.Agudo);
					} else {
						if (deltaAngulo > 90) {
							infoConceptualAngulos.categoriaAngulo = CategoriaAngulo.Grave;
							imagen.categories.add(Categorias.Grave);
						} else {
							infoConceptualAngulos.categoriaAngulo = CategoriaAngulo.Recto;
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
					// imagen.idVinculo = "";
					imagen.categories.add(Categorias.Angulo);
					imagen.infoConceptual = infoConceptualAngulos;
					
					imagen.toSVG();
				}
			}
		}
		// Guardamos el setup 
		String path = Resources.Paths.currentVersionPath+"/extras/jsonSetupUmbralAngulos.meta";
		Json json = new Json();
		this.info.indexs.resourcesMaptoArray();
		// this.info.indexs.trialMaptoArray();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path, json.toJson(this.info));
	}
}
