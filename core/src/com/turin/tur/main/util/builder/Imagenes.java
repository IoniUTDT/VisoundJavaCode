package com.turin.tur.main.util.builder;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.diseno.Trial.ResourceId;
import com.turin.tur.main.experiments.Experiments.InfoConceptualParalelismo;
import com.turin.tur.main.experiments.UmbralAngulos.ResourceInfo;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Resources;


public class Imagenes {

	private static int contadorDeRecursos=Resources.Reservados;
	
	public ResourceId resourceId = new ResourceId();
	public String name;
	public String comments;
	public Array<Constants.Resources.Categorias> categories = new Array<Constants.Resources.Categorias>();
	public Array<Linea> lineas = new Array<Linea>();
	// Obsoleto?
	public String idVinculo; // Sirve para identificar cuando varias imagenes pertenecen a un mismo subgrupo
	// Obsoleto?
	public int nivelDificultad = -1; // Define un nivel de dificultad, 1 es el mas facil. -1 implica que no esta catalogado por dificultad y 0 que es compatible con cualquier dificultad (en gral para usar en las referencias, por ej rectas paralelas con las que se compara)
	// Obsoleto?
	public InfoConceptualParalelismo infoConceptualParalelismo = new InfoConceptualParalelismo(); // Esto deberia estar obsoleto
	
	public ResourceInfo infoConceptualAngulos = new ResourceInfo();
	public Object infoConceptual; 
	String contenido = "";
	
	public Imagenes () {
		contadorDeRecursos += 1;
		this.resourceId.id = contadorDeRecursos;
		this.resourceId.resourceVersion = Builder.ResourceVersion;
	}
	
	public void toSVG () {
		add("<!-- Este archivo es creado automaticamente por el generador de contenido del programa contornos version "
				+ Constants.VERSION
				+ ". Este elementos es el numero "
				+ this.resourceId.id
				+ " de la serie " + this.resourceId.resourceVersion + " -->"); // Comentario inicial
		add("<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"" + Resources.Display.height
				+ "\" width=\"" + Resources.Display.width + "\">"); // Inicializa el SVG
		add("<rect stroke-width=\"5\" stroke=\"#ffffff\" fill=\"#ffffff\" height=\"100\" width=\"100\" y=\"0\" x=\"0\"/>"); // crea el fondo blanco
		for (Linea linea : this.lineas) {
			add("<line x1=\"" + linea.x1 + "\" y1=\"" + (Resources.Display.height - linea.y1) + "\" x2=\""
					+ linea.x2 + "\" y2=\"" + (Resources.Display.height - linea.y2)
					+ "\" stroke-width=\"2\" stroke=\"black\" />"); // Agrega
																	// cada
																	// linea
			// Nota, el formato SVG cuanta como positivo hacia abajo, por eso se invierte en el eje y el valor, porque en todo el resto del programa positivo es hacia arriba
		}
		add("</svg>"); // Finaliza el SVG
		this.createSVGFile();
		this.createMetadata();
	}
	
	private void add(String string){
		contenido = contenido + string + "\r\n";
	}
	
	private void createSVGFile() {
		FileHelper.writeFile(Resources.Paths.currentVersionPath + this.resourceId.id + ".svg", contenido);
	}
	
	private void createMetadata() {
		JsonResourcesMetaData jsonMetaData = new JsonResourcesMetaData();
		jsonMetaData.resourceId = this.resourceId;
		jsonMetaData.name = this.name;
		jsonMetaData.comments = this.comments;
		jsonMetaData.categories = this.categories;
		jsonMetaData.noSound = false;
		jsonMetaData.idVinculo = this.idVinculo;
		jsonMetaData.infoLineas = this.lineas;
		jsonMetaData.infoConceptualParalelismo = this.infoConceptualParalelismo;
		jsonMetaData.infoConceptualAngulos = this.infoConceptualAngulos;
		ExperimentalObject.JsonResourcesMetaData.CreateJsonMetaData(jsonMetaData, Resources.Paths.currentVersionPath);
	}

	
	
	public static class Linea {

		public Radial radial = new Radial();
		float x1;
		float x2;
		float y1;
		float y2;

		public static class Radial {
			public float Xcenter;
			public float Ycenter;
			public float angulo;
			public float largo;
		}
		
		public void lineaFromRadial () {
			/*
			 * Para encontrar el origen y el fin de la linea deseada utilizo las funcionalidades que tienen los Vector2. Para eso creo dos vectores en el origen
			 * (cada uno con la mitad del largo, uno angulo 0 y otro 180) Luego los roto lo necesario y los traslado a las coordenadas del centro
			 */

			Vector2 V1 = new Vector2(1, 1);
			Vector2 V2 = new Vector2(1, 1);
			V1.setLength(radial.largo / 2);
			V2.setLength(radial.largo / 2);
			V1.setAngle(0);
			V2.setAngle(180);
			V1.rotate(radial.angulo);
			V2.rotate(radial.angulo);
			V1.sub(-radial.Xcenter, -radial.Ycenter); // Por alguna razon Vector2 no tiene la
										// opcion de sumar pero side restar. Por
										// eso le resto el negativo del punto del centro
			V2.sub(-radial.Xcenter, -radial.Ycenter);
			this.x1 = V1.x;
			this.y1 = V1.y;
			this.x2 = V2.x;
			this.y2 = V2.y;
		}
	}
}
