package com.turin.tur.main.util.builder;

import java.io.File;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.experiments.Experiments.CategoriaAngulo;
import com.turin.tur.main.experiments.Experiments.SetupUmbralAngulos;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.builder.Imagenes.Linea;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.Categorias;

public class ResourcesMaker {

	private static final String TAG = ResourcesMaker.class.getName();
	// public static int contadorDeRecursos = Constants.Resources.Reservados;
	// public static int contadorDeReferenciasUmbral = 0;
	
	public ResourcesMaker() {
		this.verifyResourcesVersion();
		Textos.crearTextos();
		if (Builder.AppVersion == "UmbralCompletoAngulos") {
			SetupUmbralAngulos setup = new SetupUmbralAngulos();
			setup.nombre="SetupAngulosUmbral";
			setup.saltoChico=1;
			setup.saltoGrande=5;
			setup.angulosCriticos.add(0);
			setup.angulosCriticos.add(90);
			setup.angulosCriticos.add(180);
			setup.angulosCriticos.add(270);
			setup.searchAngles(); // Hace que el setup busque todos los angulos
			this.constructUmbralAngulos(setup);
		}
		if (Builder.AppVersion=="Prueba") {
			Imagenes imagen = new Imagenes();
			Linea infoLinea = imagen.new Linea();
			infoLinea.radial.angulo=30;
			infoLinea.radial.largo=50;
			infoLinea.radial.Xcenter = 70;
			infoLinea.radial.Ycenter = 70;
			infoLinea.lineaFromRadial();
			//infoLinea.x1=50;
			//infoLinea.y1=50;
			//infoLinea.x2=75;
			//infoLinea.y2=75;
			imagen.lineas.add(infoLinea);
			imagen.toSVG();
		}
	}
	
	
	/**
	 * Funcion que se encarga de construir todos los recursos asociados un experimento de umbral de angulos
	 */
	private void constructUmbralAngulos (SetupUmbralAngulos setup){
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (Resources.Display.width>Resources.Display.height) {
			tamano = Resources.Display.height;
		} else {
			tamano = Resources.Display.width;
		}
		// Hacemos dos indices que recorran todos los angulos de manera que el segundo indice solo llegue hasta el primero para evitar duplicar los recursos
		for (int indice1 = 0; indice1<setup.angulos.size ; indice1++) {
			for (int indice2 = indice1 +1; indice2<setup.angulos.size; indice2++) {
				int angulo1 = setup.angulos.get(indice1);
				int angulo2 = setup.angulos.get(indice2);
				if (setup.cumpleCriterioDistanciaMinima(angulo1, angulo2)) {
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
					int indice = setup.angulos.indexOf(angulo1, false);
					setup.idsResourcesByAngle.get(indice).add(imagen.resourceId.id);
					indice = setup.angulos.indexOf(angulo2, false);
					setup.idsResourcesByAngle.get(indice).add(imagen.resourceId.id);

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
		FileHelper.writeFile(path, json.toJson(setup));
	}
	
	/**
	 * Funcion que verifica que no haya recursos creados con la misma version que la que se quiere crear
	 */
	private void verifyResourcesVersion() {
		// Verifica que no haya recursos ya numerados con la version marcada
		File file = new File(Resources.Paths.fullCurrentVersionPath);
		if (file.exists()) {
			System.out.println("Modifique la version de los recursos porque ya existe una carpeta con la version actual");
			System.exit(0);
		}
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
	

}
