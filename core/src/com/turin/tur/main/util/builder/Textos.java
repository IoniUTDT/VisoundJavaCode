package com.turin.tur.main.util.builder;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.diseno.Trial.ResourceId;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.ResourcesCategorias;
import com.turin.tur.main.util.Constants.ResourcesCategorias.CategoriasImagenes;

public class Textos {
	
	ResourceId resourceId = new ResourceId();
	String name;
	String comments;
	Array<CategoriasImagenes> categories = new Array<ResourcesCategorias.CategoriasImagenes>();
	String texto;
	String contenido="";
	
	public void toSVG(){
		add("<!-- Este archivo es creado automaticamente por el generador de contenido del programa contornos version "
				+ Constants.VERSION
				+ ". Este elementos es el numero "
				+ this.resourceId.id
				+ " de la serie " + this.resourceId.resourceVersion + " de textos-->"); // Comentario inicial
		add("<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"" + ResourcesCategorias.Display.height
				+ "\" width=\"" + ResourcesCategorias.Display.width + "\">"); // Inicializa el SVG
		add("<rect stroke-width=\"5\" stroke=\"#ffffff\" fill=\"#ffffff\" height=\"100\" width=\"100\" y=\"0\" x=\"0\"/>"); // crea el fondo blanco

		add("<text text-anchor=\"middle\" x=\"" + ResourcesCategorias.Display.width / 2 + "\" y=\"" + ResourcesCategorias.Display.height / 2 + "\">" + this.texto + "</text>");
		add("</svg>"); // Finaliza el SVG
		this.createSVGFile();
		this.createMetadataText();
	}
	
	private void add(String string){
		contenido = contenido + string + "\r\n";
	}
	
	private void createSVGFile() {
		FileHelper.writeLocalFile(ResourcesCategorias.Paths.ResourcesBuilder + this.resourceId.id + ".svg", contenido);
	}
	
	private void createMetadataText() {
		JsonResourcesMetaData jsonMetaData = new JsonResourcesMetaData();
		jsonMetaData.resourceId = this.resourceId;
		jsonMetaData.name = this.name;
		jsonMetaData.comments = this.comments;
		jsonMetaData.categories = this.categories;
		jsonMetaData.noSound = true;
		ExperimentalObject.JsonResourcesMetaData.CreateJsonMetaData(jsonMetaData, ResourcesCategorias.Paths.ResourcesBuilder);
	}
	
	/**
	 * Esta funcion crea los archivos SVG y metadata correspondiente a los textos de las categorias
	 */
	public static void crearTextos() {
		// Crea un recurso para cada categoria
		for (Constants.ResourcesCategorias.CategoriasImagenes categoria : Constants.ResourcesCategorias.CategoriasImagenes.values()) {
			Textos texto = new Textos();
			texto.resourceId.id = categoria.ID;
			texto.comments = "Texto correspondiente a la categoria: " + categoria.nombre;
			texto.categories.add(categoria);
			texto.categories.add(CategoriasImagenes.Texto); // Marca que son textos
			texto.name = categoria.nombre;
			texto.texto = categoria.texto;
			texto.resourceId.resourceVersion = Builder.ResourceVersion;
			texto.toSVG();
		}
	}
}
