package com.turin.tur.obsoleto;


public class Deadcode {
	
}
	/*		
	
	/**
	 * Agrega al json del level la entrada que corresponde al analisis de significancia considerando todo el nivel
	 * @param level
	 */
/*		private static void addSignificanciaTotal(JsonLevel level) {
		Significancia significancia = new Significancia();
		significancia.tipo=TIPOdeSIGNIFICANCIA.COMPLETO;
		// Filtra los trials que son test para no procesar los que son "entrenamiento"
		Array<Integer> listaIdsSoloTests = new Array<Integer>();
		for (JsonTrial json:level.jsonTrials) {
			if (json.modo == TIPOdeTRIAL.TEST) {
				listaIdsSoloTests.add(json.Id);
			}
		}
		// Nota, aca tengo el mismo problema de siempre de acceder a las listas! Estoy haciendo mucho codigo pero no se como solucionarlo
		significancia.trialIncluidos = new Integer[listaIdsSoloTests.size];
		for (int i=0; i<listaIdsSoloTests.size;i++) {
			significancia.trialIncluidos[i]=listaIdsSoloTests.get(i);
		}
		addSignificancia (significancia, level);
	}
	
	/**
	 * Agrega al json del level la entrada que corresponde al analisis de significancia considerando los trials que son seleccion de imagen
	 * @param level
	 */
/*		private static void addSignificanciaImagen(JsonLevel level) {
		Significancia significancia = new Significancia();
		significancia.tipo = TIPOdeSIGNIFICANCIA.IMAGEN;
		// Filtra los trials que son test para no procesar los que son "entrenamiento"
		Array<Integer> listaIds = new Array<Integer>();
		for (JsonTrial json:level.jsonTrials) {
			if (json.modo == TIPOdeTRIAL.TEST){
				boolean categoria=true;
				for (int id:json.elementosId) {
					if (id > Constants.Resources.Reservados) { // Se fija si los elementos apuntan a una categoria o no en funcion de que las categorias estan asociadas a IDs reservados
						categoria=false;
						break;
					}
				}
				if (!categoria) {
					listaIds.add(json.Id);
				}
			}
		}
		// Nota, aca tengo el mismo problema de siempre de acceder a las listas! Estoy haciendo mucho codigo pero no se como solucionarlo
		significancia.trialIncluidos = new Integer[listaIds.size];
		for (int i=0; i<listaIds.size;i++) {
			significancia.trialIncluidos[i]=listaIds.get(i);
		}
		addSignificancia (significancia, level);
	}
	
	/**
	 * Agrega al json del level la entrada que corresponde al analisis de significancia considerando los trials que son seleccion de imagen
	 * @param level
	 */
/*		private static void addSignificanciaCategoria(JsonLevel level) {
		Significancia significancia = new Significancia();
		significancia.tipo = TIPOdeSIGNIFICANCIA.CATEGORIA;
		// Filtra los trials que son test para no procesar los que son "entrenamiento"
		Array<Integer> listaIds = new Array<Integer>();
		for (JsonTrial json:level.jsonTrials) {
			if (json.modo == TIPOdeTRIAL.TEST){
				boolean categoria=true;
				for (int id:json.elementosId) {
					if (id > Constants.Resources.Reservados) { // Se fija si los elementos apuntan a una categoria o no en funcion de que las categorias estan asociadas a IDs reservados
						categoria=false;
						break;
					}
				}
				if (categoria) { 
					listaIds.add(json.Id);
				}
			}
		}
		// Nota, aca tengo el mismo problema de siempre de acceder a las listas! Estoy haciendo mucho codigo pero no se como solucionarlo
		significancia.trialIncluidos = new Integer[listaIds.size];
		for (int i=0; i<listaIds.size;i++) {
			significancia.trialIncluidos[i]=listaIds.get(i);
		}
		addSignificancia (significancia, level);
	}

	/**
	 *  Ultimo paso del calculo de significancias que es independiente del subconjunto de datos elegidos
	 * @param significancia
	 * @param level
	 */
/*		private static void addSignificancia (Significancia significancia, JsonLevel level) {
		
		significancia.histogramaTrials = trialHistograma(significancia.trialIncluidos,level);
		significancia.distribucion = Stadistics.distribucion(significancia.histogramaTrials);
		Float acumulado=0f;
		significancia.exitoMinimo=0;
		for (int i=0; i<significancia.distribucion.length; i++) {
			acumulado = acumulado + significancia.distribucion[i];
			if (acumulado > 1-significancia.tipo.pValue) {
				significancia.exitoMinimo=i;
				break;
			}
		}
		level.significancias.add(significancia);
	}
	
	private static int[] trialHistograma(Integer[] trialIncluidos, JsonLevel level) {
		int[] histograma_t = new int[10]; // Nota, lo inicializamos en 10 porque asumimos que nunca va a haber ms de 10, despues se recorta
		// Contamos cuantos trials de cada numero de opciones hay 
		for (JsonTrial json:level.jsonTrials) {
			int n = json.elementosId.length;
			if (Arrays.asList(trialIncluidos).contains(json.Id)) {
				histograma_t[n]++;
			}
		}
		// Recortamos los resultados
		int longitud = histograma_t.length;
		while (true) {
			if ((histograma_t[longitud-1]!=0) || (longitud==1)){
				break;
			} else { longitud--;}
		}
		int[] histograma = new int[longitud];
		for (int i=0;i<longitud;i++) {
			histograma[i]=histograma_t[i];
		}
		return histograma;
	}
}
*/
	
	
	/**
	 * 
	 * Cosas de umbralParalelismo
	 * 
	 */
	
	/*
	private File[] loadFilelist () {
		File dir = new File(Resources.Paths.fullCurrentVersionPath);
		return dir.listFiles(new MetaFileFilter());
	}

	private void categorizeResources(File[] archivos) {
		// Ahora carga la info de cada archivo encontrado
		for (File archivo: archivos) {
			String savedData = FileHelper.readFile(archivo.getPath());
			if (!savedData.isEmpty()) {
				Json json = new Json();
				json.setUsePrototypes(false);
				JsonResourcesMetaData metaData = json.fromJson(JsonResourcesMetaData.class, savedData);

				// Clasifica los recursos por categorias. Usa los id de las categorias para armar la lista. Tambien por grupos
				for (Categorias categoria:metaData.categories) {
					listadosIdbyCategory.get(categoria.ID).add(metaData.resourceId.id);
				}
				
				// Clasifica los recursos por grupos
			
				
			} else { Gdx.app.error(TAG,"Error leyendo el archivo de metadatos"); }
		}

			if (metadata.idVinculo != null) {
				boolean nuevo=true;
				for (Agrupamientos agrupamiento:listadosGrupos) {
					if (agrupamiento.nombre.equals(metadata.idVinculo)) {
						agrupamiento.ids.add(metadata.resourceId.id);
						nuevo = false;
						break;
					}
				}
				if (nuevo) {
					Agrupamientos agrupamiento = new Agrupamientos();
					agrupamiento.nombre = metadata.idVinculo;
					agrupamiento.ids.add(metadata.resourceId.id);
					listadosGrupos.add(agrupamiento);
				}
			}
		}
		System.out.println("Recursos catalogados");
		
	}
	
*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	private static void MakeLevelParalelismoUmbral () {
		
		int nSetup = 0;
		// Se fija si encuentra el setup experimental correspondiente
		
		String path = Resources.Paths.fullCurrentVersionPath+"extras/jsonSetup"+nSetup+".meta";
		File file = new File(path);
		System.out.println("intentando:"+file.getAbsolutePath());
		while (file.exists()) { // Se hace para cada setup q exista
			// Cargamos el setup 
			SetupUmbralParalelismo setup = loadSetup(nSetup);
			// Hacemos un loop para cada referencia dentro del nivel
			for (int n=0; n<setup.cantidadReferencias; n++) {
				int R = n + nSetup; // Indice angulo de referencia
				System.out.println("Creando nivel:"+R);
				// Creamos el nivel
				JsonLevel level = crearLevel();
				level.levelTitle = setup.tag + n + "R:"+(setup.saltoTitaRef*n+setup.titaRefInicial)+"º";
				level.randomTrialSort=false;
				level.show = true;
				level.analisisUmbral.indiceAnguloRefrencia = R;
				level.analisisUmbral.anguloReferencia = setup.saltoTitaRef*n;
				level.analisisUmbral.trueRate = 0.5f;
				level.analisisUmbral.cantidadDeNivelesDeDificultad=setup.cantidadDeltas;
				level.analisisUmbral.saltoCurvaSuperior=setup.cantidadDeltas/10;
				level.analisisUmbral.proximoNivelCurvaSuperior = setup.cantidadDeltas;
				
				/*
				 * Queremos crear una lista de trials que incluya todos los trials por dificultad
				 */
/*
				for (int D=0; D<=setup.cantidadDeltas; D++) {
					String tag = "R"+R+"D"+D;
					
					Array<Integer> recursos = ResourcesSelectors.findResourceByTag(tag);
					
					for (int id: recursos) {
						JsonTrial trial = crearTrial("Seleccione a que se parece mas", "", DISTRIBUCIONESenPANTALLA.LINEALx2,
								new int[] {ResourcesSelectors.findResourceByTag("R"+R+setup.tagRefPos).first(),ResourcesSelectors.findResourceByTag("R"+R+setup.tagRefNeg).first()}, TIPOdeTRIAL.TEST, id , false, true, false);
						trial.parametrosParalelismo.D=D;
						trial.parametrosParalelismo.R=R;
						level.jsonTrials.add(trial);
					}
				}
				level.build(Resources.Paths.levelsPath);
			}
			nSetup = nSetup + setup.cantidadReferencias; 
			path = Resources.Paths.fullCurrentVersionPath+"extras/jsonSetup"+nSetup+".meta";
			file = new File(path);
		}
		
	}
	*/
	
	
	
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 	public class MetaFileFilter implements FileFilter {
		private final String[] okFileExtensions =
				new String[] { "meta" };

		@Override
		public boolean accept(File file){
			for (String extension : okFileExtensions)
			{
				if (file.getName().toLowerCase().endsWith(extension))
				{
					return true;
				}
			}
			return false;
		}
	}
	
	*/
	 








































/*
public static Array<JsonResourcesMetaData> listadoRecursos = new Array<JsonResourcesMetaData>();
public static Array<Array<Integer>> listadosId = new Array<Array<Integer>>();
public static Array<Agrupamientos> listadosGrupos = new Array<Agrupamientos>();



static Array<Integer> findResourceByTag (String tag) {
	for (Agrupamientos agrupamiento : listadosGrupos) {
		if (agrupamiento.nombre.equals(tag)) {
			return agrupamiento.ids;
		}
	}
	System.out.println("No se ha encontrado el rescurso buscado: "+tag);
	return null;
}


/**
 * Esta funcion selecciona el grupo de 6 elementos que pertenecen al agrupamiento pedido. Si no encuentra un grupo de 6 elementos devuelve un objeto vacio y manda un mensaje de error.
 * Los elementos se filtran por el nivel de dificultad seteado en la variable global de la clase, un -1 significa sin dificultad asignada y es compatible con cualquier dificultad.
 * 
 * @param agrupamientoPedido
 * 	Representa el nombre del agrupamiento pedido.
 * @return
 * 	Lista de ids de los elementos pertenecientes al agrupamiento pedido. 
 */
/*	public static int[] rsGetAllGrupo(String agrupamientoPedido, Dificultad dificultad) {
	int[] recursos = new int[] {0,0,0,0,0,0}; //Inicializa el vector con datos nulos, total solo puede tener 6 elementos
	for (Agrupamientos agrupamiento : listadosGrupos) {
		if (agrupamiento.nombre.equals(agrupamientoPedido)) {
			Agrupamientos agrupamientoFiltroDificultad = new Agrupamientos();
			agrupamientoFiltroDificultad.nombre = agrupamiento.nombre;
			if (dificultad.dificultad != -1) { // Filtra solo en caso de que haya una dificultad buscada;
				for (int i=0; i<agrupamiento.ids.size;i++) {
					// Carga la info de la metada 
					String savedData = FileHelper.readFile(Resources.Paths.fullCurrentVersionPath + agrupamiento.ids.get(i) + ".meta");
					Json json = new Json();
					json.setUsePrototypes(false);
					JsonResourcesMetaData jsonMetaData =  json.fromJson(JsonResourcesMetaData.class, savedData);
					if ((jsonMetaData.nivelDificultad == dificultad.dificultad) || (jsonMetaData.nivelDificultad==0)){ // Lo incluye sea 0 o el nivel de dificultad pedido
						agrupamientoFiltroDificultad.ids.add(agrupamiento.ids.get(i));
					}
				}
			} else {
				agrupamientoFiltroDificultad = agrupamiento;
			}
			if (agrupamientoFiltroDificultad.ids.size==6) {
				for (int i=0; i<6; i++) {
					recursos[i] = agrupamientoFiltroDificultad.ids.get(i); 
				}
				return recursos;
			} 
		}
	}
	System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+" y no se ha podido encontrar dicho agrupamiento. Se devuelve un elemento nulo.");
	return null;
}
public static int[] rsGetAllGrupo(String agrupamientoPedido) {
	Dificultad dificultad = new Dificultad(-1);
	return rsGetAllGrupo(agrupamientoPedido, dificultad);
}


/**
 * Esta funcion devuelve un elemento random pertenezca a la categoria pedida, al agrupamiento pedido, que no este en la lista de elementos omitidos y que tenga el nivel de dificultad deseado
 * La funcion se puede invocar con todos los parametros o usando algunos de los equivalentes con menos parametros. Estos invocan la funcion con parametros que anulan el filtro.
 * 
 *  Anulan el filtro el string agrupamientoPedido="SinAgrupamiento"
 *  Anulan el filtro la categoria = Categorias.Nada
 *  Anulan el filtro int[] = {}
 *  Anulan el filtro nivelDificultad = -1 
 * 
 * @param agrupamientoPedido 
 *			Indica el nombre del agrupamiento del que se quiere extraer un elemento.
 * @param categoria
 * 			Indica la categoria a la que debe pertenecer dicho elemento.
 * @param nivelDificultad
 * 			Indica el nivel de dificultad a seleccionar, -1 implica seleccionar cualquier dificultad.
 * @param omitir 
 * 			Indica que recursos se deben omitir
 * 
 * @return
 * 			Devuelve el int con el id del elemento seleccionado o 0 si no se encuetra ninguno.
 */
/*	public static int rsGet(String agrupamientoPedido, Array<Categorias> categorias, int[] omitir, Dificultad dificultad) {
	int recurso;
	recurso=0;
	Array<Integer> listadoValido = new Array<Integer>();
	
	for (JsonResourcesMetaData json: listadoRecursos) {
		if (json.idVinculo==null) { // Evita que haya un error de null
			json.idVinculo = "sin dato";
		}
		if ((json.idVinculo.equals(agrupamientoPedido)) || (agrupamientoPedido=="SinAgrupamiento")) { // Primero filtra por agrupamiento
			int elemento = json.resourceId.id;
			// Lo agrega a la lista valida si corresponde segun la dificultad buscada. Si dificultad buscada es -1 agrega todos. 
			if ((dificultad.dificultad==-1) || (json.nivelDificultad==dificultad.dificultad) || (json.nivelDificultad==0)) {
				if (categorias.contains(Categorias.Nada, false)) {
					listadoValido.add(elemento);
				} else {
					boolean contieneTodasLasCategorias = true;
					for (Categorias categoria:categorias) { 
						if (!json.categories.contains(categoria, false)){
							contieneTodasLasCategorias = false;
							break;
						}
					}
					if (contieneTodasLasCategorias) {
						listadoValido.add(elemento);
					}
				}
			}
		}
	}
	// Hasta aca deberia haber recolectado todos los recursos validos que pertenecen a la categoria, grupo y dificultad indicada
	
	// Ahora elegimos uno al azar que no este en la lista de omitidos
	if (listadoValido.size!=0) {
		listadoValido.shuffle();
		for (int i=0;i<listadoValido.size;i++) {
			recurso = listadoValido.get(i);
			boolean valido = true;
			for (int j=0; j<omitir.length; j++) {
				if (recurso==omitir[j]) {
					valido=false;
				}
			}
			if (valido) {
				System.out.print(".");
				return recurso;
			}
		}
	} else {
		System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+", de las categorias "+categorias+", de dificultad "+dificultad.dificultad+" , y que no sea alguno de los siguientes elementos "+omitir+". No se puede encontrar!");
	}
	System.out.print(".");
	return recurso;
}


/*
 * 
 * Aca van varios invocadores (o como se diga) con diferentes tipos de parametros
 * 
 */
/*	public static int rsGet (String agrupamientoPedido) {
	Array<Categorias> categorias = new Array<Categorias>();
	categorias.add(Categorias.Nada);
	int [] omitir = {};
	Dificultad dificultad = new Dificultad(-1);
	return rsGet(agrupamientoPedido, categorias, omitir, dificultad);
}
public static int rsGet (String agrupamientoPedido, Dificultad dificultad) { //Ok
	Array<Categorias> categorias = new Array<Categorias>();
	categorias.add(Categorias.Nada);
	int [] omitir = {};
	return rsGet(agrupamientoPedido, categorias, omitir, dificultad);
}
public static int rsGet (String agrupamientoPedido, Categorias categoria, Dificultad dificultad) { //Ok
	Array<Categorias> categorias = new Array<Categorias>();
	categorias.add(categoria);
	int [] omitir = {};
	return rsGet(agrupamientoPedido, categorias, omitir, dificultad);
}
public static int rsGet (String agrupamientoPedido, int omitir) {
	Array<Categorias> categorias = new Array<Categorias>();
	categorias.add(Categorias.Nada);
	int [] omitirArray = {omitir};
	Dificultad dificultad = new Dificultad(-1);
	return rsGet(agrupamientoPedido, categorias, omitirArray, dificultad);
}	
public static int rsGet (String agrupamientoPedido, int omitir, Dificultad dificultad) { //Ok
	Array<Categorias> categorias = new Array<Categorias>();
	categorias.add(Categorias.Nada);
	int [] omitirArray = {omitir};
	return rsGet(agrupamientoPedido, categorias, omitirArray, dificultad);
}
public static int rsGet (Categorias categoria, Dificultad dificultad) { //Ok
	String agrupamientoPedido = "SinAgrupamiento";
	Array<Categorias> categorias = new Array<Categorias>();
	categorias.add(categoria);
	int [] omitir = {};
	return rsGet(agrupamientoPedido, categorias, omitir, dificultad);
}
public static int rsGet (Categorias categoria, Categorias categoria2, Dificultad dificultad) { //Ok
	String agrupamientoPedido = "SinAgrupamiento";
	Array<Categorias> categorias = new Array<Categorias>();
	categorias.add(categoria);
	categorias.add(categoria2);
	int [] omitir = {};
	return rsGet(agrupamientoPedido, categorias, omitir, dificultad);
}
public static int rsGet (Categorias categoria, int omitir, Dificultad dificultad) { //ok
	String agrupamientoPedido = "SinAgrupamiento";
	Array<Categorias> categorias = new Array<Categorias>();
	categorias.add(categoria);
	int [] omitirArray = {omitir};
	return rsGet(agrupamientoPedido, categorias, omitirArray, dificultad);
}






public static class Agrupamientos {
	public String nombre;
	public Array<Integer> ids = new Array<Integer>();
}






public static Array<Integer> findAngles(int angulo) {
	Array<Integer> listaIds = new Array<Integer>();
	for (JsonResourcesMetaData json: listadoRecursos) {
		if (json.noSound!=true) {
			if ((json.infoConceptualAngulos.direccionLado1 == angulo) || (json.infoConceptualAngulos.direccionLado2 == angulo)) {
				listaIds.add(json.resourceId.id);
			}
		}
	}
	return listaIds;
}



	/*
	
	
	
	private static void extracted(Array<Imagen> objetos) {
		{
			SetupUmbralParalelismo setup = new SetupUmbralParalelismo();

			// Creamos los recursos de -6 a 6 con saltos de 3
			// Vamos a trabajar todas las cuentas en radianes
			setup.nombre = "Eje horizontal";
			setup.tag = "H";
			setup.titaRefInicial = -6;
			setup.saltoTitaRefInt = 3;
			setup.saltoTitaRef = setup.saltoTitaRefInt;
			setup.anguloMinimo = 0.5f;  
			setup.anguloMaximo = 30;
			setup.largo=80; // Largo de las lineas
			setup.separacionMinima = 15; // Separacion predeterminada
			setup.separacionIncremento = 10;
			setup.cantidadReferencias = 5;
			setup.cantidadSeparaciones = 2;
			setup.cantidadDeltas = 50;   
			objetos.addAll(recursosParalelismoAnalisisUmbral(setup));
			
			// Creamos los recursos de 10 a 80 con saltos de 10
			// Vamos a trabajar todas las cuentas en radianes
			setup.nombre = "Primer cuadrante";
			setup.tag = "1C";
			setup.titaRefInicial = 10;
			setup.saltoTitaRefInt = 10;
			setup.saltoTitaRef = setup.saltoTitaRefInt;
			setup.anguloMinimo = 1f;  
			setup.anguloMaximo = 30;
			setup.largo=80; // Largo de las lineas
			setup.separacionMinima = 15; // Separacion predeterminada
			setup.separacionIncremento = 10;
			setup.cantidadReferencias = 8;
			setup.cantidadSeparaciones = 2;
			setup.cantidadDeltas = 50;   
			objetos.addAll(recursosParalelismoAnalisisUmbral(setup));
			
			// Creamos los recursos verticales
			// Vamos a trabajar todas las cuentas en radianes
			setup.nombre = "eje vertical";
			setup.tag = "V";
			setup.titaRefInicial = 86;
			setup.saltoTitaRefInt = 2;
			setup.saltoTitaRef = setup.saltoTitaRefInt;
			setup.anguloMinimo = 0.02f;  
			setup.anguloMaximo = 10;
			setup.largo=80; // Largo de las lineas
			setup.separacionMinima = 15; // Separacion predeterminada
			setup.separacionIncremento = 10;
			setup.cantidadReferencias = 5;
			setup.cantidadSeparaciones = 2;
			setup.cantidadDeltas = 50;   
			objetos.addAll(recursosParalelismoAnalisisUmbral(setup));

			// Creamos los recursos de 100 a 170 con saltos de 10
			// Vamos a trabajar todas las cuentas en radianes
			setup.nombre = "Segundo cuadrante";
			setup.tag = "2C";
			setup.titaRefInicial = 100;
			setup.saltoTitaRefInt = 10;
			setup.saltoTitaRef = setup.saltoTitaRefInt;
			setup.anguloMinimo = 1f;  
			setup.anguloMaximo = 30;
			setup.largo=80; // Largo de las lineas
			setup.separacionMinima = 15; // Separacion predeterminada
			setup.separacionIncremento = 10;
			setup.cantidadReferencias = 8;
			setup.cantidadSeparaciones = 2;
			setup.cantidadDeltas = 50;   
			objetos.addAll(recursosParalelismoAnalisisUmbral(setup));
		}
	}
	
	
	private static Array<Imagen> recursosParalelismoAnalisisUmbral(SetupUmbralParalelismo setup) {
		
		/*
		 * Queremos mapear una escala log en una lineal, es decir que parametro [pmin-->pmax] mapee angulos que van de anguloMin --> angluloMax de manera que p = 1/A*log(1/B*angulo)
		 * ==> angulo = B * e ^ (A * parametro)
		 * Porque la idea es que haya mas densidad de angulos en los angulos chicos que grandes
		 * Si pmin = 0 y pmax=cantidadDeltas-1, queda que  
		 * 0 = 1/ A log (1/B * anguloMin) ==> B=angMin
		 * Pmax = 1/A *log (1/AngMin * AngMax) ==> A = log(AngMax/AngMin)/Pmax 
		
		float parametroB = setup.anguloMinimo;
		float parametroA = (float) ((Math.log(setup.anguloMaximo/setup.anguloMinimo))/(setup.cantidadDeltas-1));
		
		
		Array<Imagen> objetos = new Array<Imagen>();
		
		
		for (int i=0; i<setup.cantidadReferencias; i++) {
		
			boolean recursoPosCreado = false;
			boolean recursoNegCreado = false;
			
			for (int j=0; j<setup.cantidadSeparaciones ; j++) {
				
				
				float separacion = setup.separacionMinima + j * setup.separacionIncremento; // Itera para separaciones cada vez mayores
				// Esto no lo estoy usando!
				// float anguloMaximoNoInterseccion = (float) Math.toDegrees(Math.asin(separacion/setup.largo)); // Calcula el maximo angulo permitido de manera que no corten las dos rectas.
				
				// Creamos la imagen paralela
				Imagen imagen = crearImagen();
				
				float anguloReferencia = setup.titaRefInicial + i * setup.saltoTitaRef;
				// Calculamos los centros de manera que esten separados en funcion del angulo
				float Xcenter1 = width/2 - separacion/2 * MathUtils.sinDeg(anguloReferencia);
				float Xcenter2 = width/2 + separacion/2 * MathUtils.sinDeg(anguloReferencia);
				float Ycenter1 = width/2 - separacion/2 * MathUtils.cosDeg(anguloReferencia);
				float Ycenter2 = width/2 + separacion/2 * MathUtils.cosDeg(anguloReferencia);
				imagen.infoConceptualParalelismo.direccionAnguloReferencia = anguloReferencia;
				
				// agrega la primer linea
				InfoLinea infoLinea = new InfoLinea();
				infoLinea.angulo=anguloReferencia;
				infoLinea.largo=setup.largo;
				infoLinea.Xcenter = Xcenter1;
				infoLinea.Ycenter = Ycenter1;
				imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
				imagen.infoLineas.add(infoLinea);
				// Agrega la segunda linea
				infoLinea = new InfoLinea();
				infoLinea.angulo=anguloReferencia;
				infoLinea.largo=setup.largo;
				infoLinea.Xcenter = Xcenter2;
				infoLinea.Ycenter = Ycenter2;
				imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
				imagen.infoLineas.add(infoLinea);
				// Datos generales
				// Nota, aca no tiene sentido poner ni parametro linelizado ni si se juntan! ...
				imagen.infoConceptualParalelismo.deltaAnguloLinealizado=0;
				imagen.infoConceptualParalelismo.deltaAngulo=0;
				imagen.infoConceptualParalelismo.separacion=separacion;
				
				imagen.comments = "Imagen generada por secuencia automatica 'recursosParalelismoAnalisisUmbral'.";
				imagen.name = "Imagen de rectas no paralelas generada automaticamente";
				imagen.idVinculo = "R"+(contadorDeReferenciasUmbral+i)+"D0";
				imagen.categories.add(Categorias.Lineax2);
				imagen.categories.add(Categorias.Paralelas);
				imagen.nivelDificultad = -1;
				objetos.add(imagen);
				
				// Creamos las imagenes con deltas
				for (int k=1; k<=setup.cantidadDeltas; k++) {
					float anguloDelta = (float) (parametroB * Math.exp(parametroA*(k-1)));
					float anguloDeltaPos = anguloDelta/2;
					float anguloDeltaNeg = -anguloDelta/2;
					
					// Creamos la imagen con delta "positivo"
					imagen = crearImagen();
					
					// Almacenamos la data de la info de la geometria que queremos estudiar.
					imagen.infoConceptualParalelismo.deltaAngulo = anguloDelta;
					imagen.infoConceptualParalelismo.deltaAnguloLinealizado = k;
					imagen.infoConceptualParalelismo.direccionAnguloReferencia = anguloReferencia;
					imagen.infoConceptualParalelismo.seJuntan = true;
					imagen.infoConceptualParalelismo.separacion = separacion;
					
					// agrega la primer linea
					infoLinea = new InfoLinea();
					infoLinea.angulo=anguloReferencia+anguloDeltaPos;
					infoLinea.largo=setup.largo;
					infoLinea.Xcenter = Xcenter1;
					infoLinea.Ycenter = Ycenter1;
					imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
					imagen.infoLineas.add(infoLinea);
					// Agrega la segunda linea
					infoLinea = new InfoLinea();
					infoLinea.angulo=anguloReferencia+anguloDeltaNeg;
					infoLinea.largo=setup.largo;
					infoLinea.Xcenter = Xcenter2;
					infoLinea.Ycenter = Ycenter2;
					imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
					imagen.infoLineas.add(infoLinea);
					// Datos generales
					imagen.comments = "Imagen generada por secuencia automatica 'recursosParalelismoAnalisisUmbral'.";
					imagen.name = "Imagen de rectas no paralelas generada automaticamente";
					imagen.idVinculo = "R"+(contadorDeReferenciasUmbral+i)+"D"+k;
					imagen.categories.add(Categorias.Lineax2);
					imagen.categories.add(Categorias.NoParalelas);
					imagen.nivelDificultad = -1;
					
					if ((recursoNegCreado==false) & (anguloDelta>9) & (j==setup.cantidadSeparaciones-1)){
						recursoNegCreado = true;
						Imagen imagenRefNeg = crearImagen();
						
						System.out.println(imagenRefNeg.resourceId.id);
						
						// Almacenamos la data de la info de la geometria que queremos estudiar.
						imagenRefNeg.infoConceptualParalelismo.deltaAngulo = anguloDelta;
						imagenRefNeg.infoConceptualParalelismo.deltaAnguloLinealizado = k;
						imagenRefNeg.infoConceptualParalelismo.direccionAnguloReferencia = anguloReferencia;
						imagenRefNeg.infoConceptualParalelismo.seJuntan = true;
						imagenRefNeg.infoConceptualParalelismo.separacion = separacion;
						
						// agrega la primer linea
						infoLinea = new InfoLinea();
						infoLinea.angulo=anguloReferencia+anguloDeltaPos;
						infoLinea.largo=setup.largo;
						infoLinea.Xcenter = Xcenter1;
						infoLinea.Ycenter = Ycenter1;
						imagenRefNeg.parametros.addAll(ExtremosLinea.Linea(infoLinea));
						imagenRefNeg.infoLineas.add(infoLinea);
						// Agrega la segunda linea
						infoLinea = new InfoLinea();
						infoLinea.angulo=anguloReferencia+anguloDeltaNeg;
						infoLinea.largo=setup.largo;
						infoLinea.Xcenter = Xcenter2;
						infoLinea.Ycenter = Ycenter2;
						imagenRefNeg.parametros.addAll(ExtremosLinea.Linea(infoLinea));
						imagenRefNeg.infoLineas.add(infoLinea);
						// Datos generales
						imagenRefNeg.comments = "Imagen generada por secuencia automatica 'recursosParalelismoAnalisisUmbral'.";
						imagenRefNeg.name = "Imagen de rectas no paralelas generada automaticamente";
						imagenRefNeg.idVinculo = "R"+(contadorDeReferenciasUmbral+i)+setup.tagRefNeg;
						imagenRefNeg.categories.add(Categorias.Lineax2);
						imagenRefNeg.categories.add(Categorias.NoParalelas);
						imagenRefNeg.nivelDificultad = -1;
						objetos.add(imagenRefNeg);
					}
					
					objetos.add(imagen);
					
					// Creamos la imagen con delta "positivo"
					imagen = crearImagen();
					
					// Almacenamos la data de la info de la geometria que queremos estudiar.
					imagen.infoConceptualParalelismo.deltaAngulo = anguloDelta;
					imagen.infoConceptualParalelismo.deltaAnguloLinealizado = k;
					imagen.infoConceptualParalelismo.direccionAnguloReferencia = anguloReferencia;
					imagen.infoConceptualParalelismo.seJuntan = false;
					imagen.infoConceptualParalelismo.separacion = separacion;
					
					// agrega la primer linea
					infoLinea = new InfoLinea();
					infoLinea.angulo=anguloReferencia-anguloDeltaPos;
					infoLinea.largo=setup.largo;
					infoLinea.Xcenter = Xcenter1;
					infoLinea.Ycenter = Ycenter1;
					imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
					imagen.infoLineas.add(infoLinea);
					// Agrega la segunda linea
					infoLinea = new InfoLinea();
					infoLinea.angulo=anguloReferencia-anguloDeltaNeg;
					infoLinea.largo=setup.largo;
					infoLinea.Xcenter = Xcenter2;
					infoLinea.Ycenter = Ycenter2;
					imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
					imagen.infoLineas.add(infoLinea);
					// Datos generales
					imagen.comments = "Imagen generada por secuencia automatica 'recursosParalelismoAnalisisUmbral'.";
					imagen.name = "Imagen de rectas no paralelas generada automaticamente";
					imagen.idVinculo = "R"+(contadorDeReferenciasUmbral+i)+"D"+k;
					imagen.categories.add(Categorias.Lineax2);
					imagen.categories.add(Categorias.NoParalelas);
					imagen.nivelDificultad = -1;
					
					if ((recursoPosCreado==false) & (anguloDelta>9) & (j==setup.cantidadSeparaciones-1)){
						recursoPosCreado = true;
						
						// Creamos la imagen con delta "positivo"
						Imagen imagenPos = crearImagen();
						System.out.println(imagenPos.resourceId.id);
						
						// Almacenamos la data de la info de la geometria que queremos estudiar.
						imagenPos.infoConceptualParalelismo.deltaAngulo = anguloDelta;
						imagenPos.infoConceptualParalelismo.deltaAnguloLinealizado = k;
						imagenPos.infoConceptualParalelismo.direccionAnguloReferencia = anguloReferencia;
						imagenPos.infoConceptualParalelismo.seJuntan = false;
						imagenPos.infoConceptualParalelismo.separacion = separacion;
						
						// agrega la primer linea
						infoLinea = new InfoLinea();
						infoLinea.angulo=anguloReferencia-anguloDeltaPos;
						infoLinea.largo=setup.largo;
						infoLinea.Xcenter = Xcenter1;
						infoLinea.Ycenter = Ycenter1;
						imagenPos.parametros.addAll(ExtremosLinea.Linea(infoLinea));
						imagenPos.infoLineas.add(infoLinea);
						// Agrega la segunda linea
						infoLinea = new InfoLinea();
						infoLinea.angulo=anguloReferencia-anguloDeltaNeg;
						infoLinea.largo=setup.largo;
						infoLinea.Xcenter = Xcenter2;
						infoLinea.Ycenter = Ycenter2;
						imagenPos.parametros.addAll(ExtremosLinea.Linea(infoLinea));
						imagenPos.infoLineas.add(infoLinea);
						// Datos generales
						imagenPos.comments = "Imagen generada por secuencia automatica 'recursosParalelismoAnalisisUmbral'.";
						imagenPos.name = "Imagen de rectas no paralelas generada automaticamente";
						imagenPos.idVinculo = "R"+(contadorDeReferenciasUmbral+i)+setup.tagRefPos;
						imagenPos.categories.add(Categorias.Lineax2);
						imagenPos.categories.add(Categorias.NoParalelas);
						imagenPos.nivelDificultad = -1;
						objetos.add(imagenPos);
					}
					
					objetos.add(imagen);
				} // Termina el loop en deltasTita
			} // Termina el loop de separaciones
			if (recursoPosCreado == false) {
				System.out.println("Warning! : No se creo el recurso positivo de referencia!");
			}
	
			if (recursoNegCreado == false) {
				System.out.println("Warning! : No se creo el recurso negativo de referencia!");
			}
		} // Termina el loop de referencias
		saveSetupParalelismo(setup);
		contadorDeReferenciasUmbral = contadorDeReferenciasUmbral + setup.cantidadReferencias;
		return objetos;
	}
	
	
	
	private static void saveSetupParalelismo(SetupUmbralParalelismo jsonSetup) {
		String path = Resources.Paths.currentVersionPath+"/extras/jsonSetup"+contadorDeReferenciasUmbral+".meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path, json.toJson(jsonSetup));
	}

	private static void saveSetupAngulos(SetupUmbralAngulos jsonSetup) {
		String path = Resources.Paths.currentVersionPath+"/extras/jsonSetup"+contadorDeReferenciasUmbral+".meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path, json.toJson(jsonSetup));
	}

	
	
	
	
	
	
	
	
*/
