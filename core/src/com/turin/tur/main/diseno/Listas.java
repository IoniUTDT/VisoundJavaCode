package com.turin.tur.main.diseno;

public class Listas {

	public enum LISTAdeNIVELES {
		Tutorial(LISTAdeRECURSOS.ImagenesTutorial, TIPOdeNivel.Tutorial);
		
		public LISTAdeRECURSOS listaDeRecursos;
		public TIPOdeNivel tipoDeNivel;
		
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
		ImagenesTutorial, Paralelismo, Angulos
	}
	
	public enum FASEdeEXPERIMENTO {
		Intro, Tutorial, TestInicial, Entrenamiento1, TestFinal, ExperimentoCompleto;
		
		private FASEdeEXPERIMENTO etapaSiguiente;
		
		static {
			Intro.etapaSiguiente = Tutorial;
			Tutorial.etapaSiguiente = TestInicial;
			TestInicial.etapaSiguiente = Entrenamiento1;
			Entrenamiento1.etapaSiguiente = TestFinal;
			TestFinal.etapaSiguiente = ExperimentoCompleto;
		}
		
		public FASEdeEXPERIMENTO etapaSiguiente() {
			return this.etapaSiguiente;
		}
	}
}
