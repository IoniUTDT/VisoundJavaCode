package com.turin.tur.main.levelsDesign;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Listas.LISTAdeRECURSOS;
import com.turin.tur.main.diseno.Listas.TIPOSdeRECURSOS;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Resources.CategoriasImagenes;
import com.turin.tur.main.util.Constants.Resources.Paths;
import com.turin.tur.main.util.builder.Imagenes;
import com.turin.tur.main.util.builder.Imagenes.Linea;

public class Resources {

	/**
	 * Esta clase guarda el setup con el que se crean los recursos para que luego sean usados por el creador de niveles
	 * @author ionatan
	 *
	 */
	public static class SetupResources {
		Array<Double> angulosReferencia = new Array<Double>(); // Referencias del experimento
		Array<Float> fluctuacionesLocalesReferenciaSeries = new Array<Float>(); // Fluctuaciones dentro de cada referencia, en terminos relativos
		Array<Double> desviacionesAngulares = new Array<Double>(); // Variaciones del lado movil o del angulo respecto a la referencia
		Array<Double> fluctuacionesLocalesReferenciaEstimuloCero = new Array<Double>(); // angulos en los cuales se muestra a señal recta.
		Array<Estimulo> estimulos = new Array<Estimulo>(); // Lista de estimulos que se arman en la fase de generacion de recursos.
		public int numeroDeEstimulosPorSerie;
		double desvMin;
		double desvMax;
		boolean logscale = true;
	}
	
	/**
	 * Esta clase representa cada uno de los estinulos creados. Se utiliza a lo largo de todo el programa
	 * @author ionatan
	 *
	 */
	static class Estimulo implements Comparable<Estimulo> {
		int idResource; // Id del archivo con el recurso
		int idTrial; // Id del trial en que se evalua al recurso (esto es porque 
		double anguloFijo; // Angulo de inclinacion de las rectas paralelas de
							// referencia
		double desviacion; // Desviacion respecto a la referencia
		int nivelSenal; // Nivel de intensidad de la señal. Cero representa el angulo recto o las rectas paralelas. Y despues representa una escala lineal que mapea las estimulos ordenados segun la intensidad del estimulo a medir (mas facil mayor intencidad)
		@Override
		public int compareTo(Estimulo o) {
			return Integer.valueOf(nivelSenal).compareTo(o.nivelSenal);
		}
	}
	
	/**
	 * Esta clase crea los recursos a partir de la informacion del tipo de 
	 * recurso que se desea crear y guarda los archivos en la carpeta tenporal 
	 * donde se almacenan los recursos y la info del setup 
	 * @param identificadorNivel
	 */
	public static void makeResources (LISTAdeRECURSOS identificador) {
		SetupResources setup = makeSetup (identificador);
		buildResources (setup, identificador);
	}

	private static void buildResources(SetupResources setup, LISTAdeRECURSOS identificador) {
		// Creamos un recurso para cada imagen necesaria en las series de estimulo variable
		for (double referencia : setup.angulosReferencia) { // Asume que en esta variable estan los angulos de referencia
			for (double ladoFijo : setup.fluctuacionesLocalesReferenciaSeries) {
				ladoFijo = ladoFijo + referencia;
				for (double desviacion : setup.desviacionesAngulares) { // Asume que en esta variable estan los angulos a formar para cada referencia, siempre positivos
					setup.estimulos.add(makeResource(ladoFijo, desviacion, identificador));
				}
			}
		}
		// Creamos los recursos correspondientes a cada estimulo de nivel cero
		float desviacionCero = 0;
		if (identificador.tipoDeRecursos == TIPOSdeRECURSOS.Angulos) {
			desviacionCero = 90;
		}
		for (double referencia : setup.angulosReferencia) { // Asume que en esta variable estan los angulos de referencia
			for (double ladoFijo : setup.fluctuacionesLocalesReferenciaEstimuloCero) {
				ladoFijo = ladoFijo + referencia;
				setup.estimulos.add(makeResource(ladoFijo, desviacionCero, identificador));
			}
		}
		saveSetup(setup, identificador);
	}

	private static void saveSetup(SetupResources setup, LISTAdeRECURSOS identificador) {
		// Guardamos el setup en la carpeta temporal
		String path = Paths.ResourcesBuilder + Paths.ExtraFldr + identificador.toString() + Paths.ResourcesSetupExt;
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeLocalFile(path, json.toJson(setup));
	}

	private static Estimulo makeResource(double ladoFijo, double desviacion, LISTAdeRECURSOS identificador) {
		if (identificador.tipoDeRecursos == TIPOSdeRECURSOS.Angulos) {
			return makeResourceAngulo(ladoFijo, desviacion);
		}
		if (identificador.tipoDeRecursos == TIPOSdeRECURSOS.Paralelismo) {
			return makeResourceParalelismo();
		}
		return null;
	}

	private static Estimulo makeResourceParalelismo() {
		// TODO Auto-generated method stub
		return null;
	}

	private static Estimulo makeResourceAngulo(double ladoFijo, double anguloAFormar) {
		// buscamos el tamaño del lienzo a dibujar
		float tamano;
		if (Constants.Resources.Display.width > Constants.Resources.Display.height) {
			tamano = Constants.Resources.Display.height;
		} else {
			tamano = Constants.Resources.Display.width;
		}

		// Creamos la info conceptual de la imagen
		ImageInfoAngulo info = new ImageInfoAngulo();

		float largoLados = tamano * 0.4f;

		float Xcenter = Constants.Resources.Display.width / 2;
		float Ycenter = Constants.Resources.Display.height / 2;

		// A partir del angulo a formar, lo orientamos segun haga falta. En este experimento el angulo se forma hacia la izq si es mayor que noventa el lado fijo y hacia derecha si es menor.
		if (ladoFijo < 90) {anguloAFormar = -anguloAFormar;}
		double anguloLadoMovil = (ladoFijo + anguloAFormar);
		
		// Nota: los ejes son cartesianos y hacia abajo, x hacia la derecha
		info.linea1.radial.Xcenter = Xcenter + largoLados / 2 * MathUtils.cosDeg((float) ladoFijo);
		info.linea1.radial.Ycenter = Ycenter + largoLados / 2 * MathUtils.sinDeg((float) ladoFijo);
		info.linea2.radial.Xcenter = Xcenter + largoLados / 2 * MathUtils.cosDeg((float) anguloLadoMovil);
		info.linea2.radial.Ycenter = Ycenter + largoLados / 2 * MathUtils.sinDeg((float) anguloLadoMovil);
		
		
		info.linea1.radial.angulo = ladoFijo;
		info.linea2.radial.angulo = anguloLadoMovil;

		info.linea1.radial.largo = largoLados;
		info.linea2.radial.largo = largoLados;

		info.anguloLadoMovil = anguloLadoMovil;
		info.referencia = ladoFijo;
		info.desviacion = anguloAFormar%360; //El angulo tiene signo, y esta entre -360 y 360 
		
		// Pasamos la info a formato cartesiano
		info.linea1.lineaFromRadial();
		info.linea2.lineaFromRadial();

		// Creamos la imagen correspondiente
		Imagenes imagen = new Imagenes();

		// Cargamos la info conceptual (que varia segun el tipo de experimento)
		imagen.infoConceptual = info;

		
		// Creamos las categorias correspondientes (para angulos de -360 a 360)
		if ((info.desviacion < -270) & (info.desviacion >= -360)){
			imagen.categories.add(CategoriasImagenes.Agudo);
			imagen.categories.add(CategoriasImagenes.NoRecto);
		}
		if (info.desviacion == -270){
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion < -90) & (info.desviacion > -270)){
			imagen.categories.add(CategoriasImagenes.Obtuso);
			imagen.categories.add(CategoriasImagenes.NoRecto);
		}
		if (info.desviacion == -90) {
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion > -90) & (info.desviacion < 90)){
			imagen.categories.add(CategoriasImagenes.Agudo);
			imagen.categories.add(CategoriasImagenes.NoRecto);
		}
		if (info.desviacion == 90) {
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion < 270) & (info.desviacion > 90)){
			imagen.categories.add(CategoriasImagenes.Obtuso);
			imagen.categories.add(CategoriasImagenes.NoRecto);
		}
		if (info.desviacion == 270) {
			imagen.categories.add(CategoriasImagenes.Recto);
		}
		if ((info.desviacion > 270) & (info.desviacion <= 360)){
			imagen.categories.add(CategoriasImagenes.Agudo);
			imagen.categories.add(CategoriasImagenes.NoRecto);
		}
		// Agregamos las dos lineas para que se dibujen
		imagen.lineas.add(info.linea1);
		imagen.lineas.add(info.linea2);

		// Hacemos de la info de la imagen el SVG
		imagen.toSVG();

		// Agregamos al setup el recurso
		EstimuloAngulo estimulo = new EstimuloAngulo();
		estimulo.idResource = imagen.resourceId.id;
		estimulo.anguloLadoMovil = info.anguloLadoMovil;
		estimulo.anguloFijo = info.referencia;
		estimulo.desviacion = info.desviacion;
		return estimulo;
	}

	private static SetupResources makeSetup(LISTAdeRECURSOS identificador) {
		SetupResources setup = new SetupResources();
		
		if (identificador == LISTAdeRECURSOS.UmbralAngulosTutorial) {
			setup.angulosReferencia.add(180d);
			setup.fluctuacionesLocalesReferenciaSeries.add(0f);
			setup.fluctuacionesLocalesReferenciaEstimuloCero.addAll(0d,2.5d,-2.5d,5d,-5d,7.5d,-7.5d,10d,-10d);
			setup.numeroDeEstimulosPorSerie = 10;
			setup.desvMax = 80;
			setup.desvMin = 1;
		}
		
		generarDesviaciones(setup);
		if (identificador.tipoDeRecursos == TIPOSdeRECURSOS.Angulos) {
			for (int i=0 ; i < setup.desviacionesAngulares.size; i++) {
				setup.desviacionesAngulares.set(i, setup.desviacionesAngulares.get(i)+90);
			}
		}
		return setup;
	}

	private static void generarDesviaciones(SetupResources setup) {
		// Generamos los lados moviles
		double desvMinLog = Math.log(setup.desvMin);
		double desvMaxLog = Math.log(setup.desvMax);
		Array<Double> desviaciones = new Array<Double>();
		// Creamos la serie de desviaciones en abstracto
		if (setup.logscale) {
			double paso = (desvMaxLog - desvMinLog) / (setup.numeroDeEstimulosPorSerie - 1);
			for (int i = 0; i < setup.numeroDeEstimulosPorSerie; i++) {
				desviaciones.add(desvMinLog + paso * i);
			}
			for (int i = 0; i < setup.numeroDeEstimulosPorSerie; i++) {
				desviaciones.set(i, Math.exp(desviaciones.get(i)));
			}
		} else {
			double paso = (setup.desvMax - setup.desvMin) / setup.numeroDeEstimulosPorSerie;
			for (int i = 0; i < setup.numeroDeEstimulosPorSerie; i++) {
				desviaciones.add(setup.desvMin + paso * i);
			}
		}
		// Armamos la serie completa
		desviaciones.reverse();
		for (double desviacion : desviaciones) {
			setup.desviacionesAngulares.add(desviacion);
		}
		desviaciones.reverse();
		for (double desviacion : desviaciones) {
			setup.desviacionesAngulares.add(-desviacion);
		}
	}
	
	private static class ImageInfoAngulo extends ImageInfo {
		double anguloLadoMovil;
	}
	
	private static class ImageInfo {
		Linea linea1 = new Linea();
		Linea linea2 = new Linea();
		double referencia;
		double desviacion;
	}
	
	private static class EstimuloAngulo extends Estimulo implements Comparable<Estimulo> {
		private double anguloLadoMovil; // Angulo absoluto del lado movil
		
		public int compareTo(EstimuloAngulo o) {
			return Double.valueOf(this.desviacion).compareTo(o.desviacion);
		}
		
	}
}
