package com.turin.tur.main.experiments;

import com.badlogic.gdx.utils.Array;

public class UmbralAngulos {

	public static class Info {
		public String nombre; // Nombre del setup
		public int saltoGrande; // Salto que hay entre angulo no critico y angulo no critico
		public int saltoChico; // Salto que hay entre angulo dos angulos consecutivos alrededor de los angulos criticos
		public Array<Integer> angulosCriticos = new Array<Integer>(); // Nota: tiene que estar entre los angulo pertenecientes al salto grande para que los considere
		public Array<Integer> angulosNoDetalle = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos de al salto grande
		public Array<Integer> angulosDetalle = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos de al salto chico
		public Array<Integer> angulos = new Array<Integer>(); // Genera un array con todos los angulos en los que se debe dibujar angulos
		public int numeroDeReferenciasConjuntas = 2; // Es el numero de angulos de referencia distintos que se intercalan en un mismo nivel para evitar feedback 
		public Array<Array<Integer>> idsResourcesByAngle = new Array<Array<Integer>>(); // Lista de arrays con los ids de los recursos que tienen cada angulo. Esto se crea en tiempo de ejecucion de creacion de recursos porque es infinitamente mas lento hacer una busqueda despues al trabajar con volumenes grandes de recursos. El tag de cada entrada es el angulo con el mismo indice de la lista de angulos 
	}
	
	public UmbralAngulos () {
		
	}
	
	
}
