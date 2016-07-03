package com.turin.tur.main.diseno;

import com.turin.tur.main.util.builder.Builder;

public class Listas {

	public enum LISTAdeNIVELES {
		Tutorial(LISTAdeRECURSOS.ImagenesTutorial, TIPOdeNivel.Tutorial),
		TestAngulos30(LISTAdeRECURSOS.UmbralAngulosTutorial, TIPOdeNivel.Umbral)
		;
		public LISTAdeRECURSOS listaDeRecursos;
		public TIPOdeNivel tipoDeNivel;
		public static final int levelVersion = Builder.levelVersionFinal;
		private LISTAdeNIVELES(LISTAdeRECURSOS listaDeRecursos, TIPOdeNivel tipoDeNivel) {
			this.listaDeRecursos = listaDeRecursos;
			this.tipoDeNivel = tipoDeNivel;
		}
	}
	
	public enum TIPOdeNivel {
		Tutorial, Umbral;
	}
	
	public enum LISTAdeRECURSOS {
		ImagenesTutorial (TIPOSdeRECURSOS.ImagenesTutorial),
		UmbralAngulosTutorial (TIPOSdeRECURSOS.Angulos),
		UmbralAngulosTransferencia (TIPOSdeRECURSOS.Angulos),
		UmbralParalelismoTutorial (TIPOSdeRECURSOS.Paralelismo),
		UmbralParalelismoTransferencia (TIPOSdeRECURSOS.Paralelismo);
		
		public TIPOSdeRECURSOS tipoDeRecursos;
		
		private LISTAdeRECURSOS(TIPOSdeRECURSOS tipoDeRecursos) {
			this.tipoDeRecursos = tipoDeRecursos;
		}
	}
	
	public enum TIPOSdeRECURSOS {
		ImagenesTutorial, Paralelismo, Angulos;
		public static final int ResourceVersion = Builder.ResourceVersion;
	}
}
