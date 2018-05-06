package curso_intensivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

// Gabriel Pinto Pineda - 201515275
// Gregorio Osorio - 201631760


/**
 * Clase que modela un algoritmo para encontrar los dias optimos en los que se debe dictar un curso y donde todos los interesados lo puedan tomar
 * @author Gabriel Pinto
 */
public class ExploracionDeGrafosCursoIntensivoAlgoritmo {

	
	private Dia [] diasCurso;

	private Persona [] personasCurso;

	private boolean [] [] diasPersona;

	private int totalDias;


	/**
	 * Calcula las fechas optimas en las cuales se debe dictar el curso atendiendo a todas las personas interesadas
	 * @param pDias Dias en los que se dicta el curso
	 * @param personas Pesonas interesadas en el curso
	 * @param limite Limite de dias en los que el curso se puede dictar
	 * @param relaciones Relacion entre los dias en los que se dicta el curso y las personas que pueden asistir a este
	 * @return Lista de dias en los que se debe dictar el curso
	 */
	public Dia[] calculateOptimalTripStatePath (Dia [] pDias, Persona [] personas, int limite, boolean [] [] relaciones) {

		this.diasCurso = pDias;
		this.personasCurso = personas;
		this.diasPersona = relaciones;
		this.totalDias = limite;

		// Se llaman a las  posibles soluciones
		List<DiasDictarState> solutions = findFeasibleSolutions();

		DiasDictarState opt = null;
		int costoOpt = 0;

		// Se recorren las posibles soluciones para ver cual es la mas optima
		for(DiasDictarState state: solutions) {
			int actualDias = state.getTotalDias();
			if(opt == null || actualDias <= costoOpt) {
				opt = state;
				costoOpt = actualDias;
			}
		}

		// Si no hay soluciones, se imprime un mensaje y se retorna una lista de dias vacia
		if(opt == null) {
			opt = new DiasDictarState(new Dia[0]);
			System.out.println("No existen fechas en las que todos los asistentes puedan ir al curso y/o cumplan las condiciones minimas");
		}

		return opt.getDias();
	}



	/**
	 * Busca las soluciones que satisfacen las condiciones definidas
	 * @return La lista de estados que cumplen con las condiciones
	 */
	private List<DiasDictarState> findFeasibleSolutions() {
		List<DiasDictarState> answer = new ArrayList<>();
		//Estado inicial
		Dia[] initial = new Dia[0];
		DiasDictarState inicial = new DiasDictarState(initial);
		//Agenda
		Queue<DiasDictarState> agenda = new LinkedList<>();
		agenda.add(inicial);
		while(agenda.size() > 0) {
			//Selecciona el estado siguiente de la agenda
			DiasDictarState actualState = agenda.poll();
			//Verifica si el estado actual es viable
			if(isViable(actualState)) {
				//Verifica si el estado actual es solucion
				if(isSolution(actualState)) {
					answer.add(actualState);
				}
				//Busca y agrega los sucesores a la agenda
				List<DiasDictarState> succesors = getSuccesors(actualState);
				agenda.addAll(succesors);
			}
		}

		return answer;
	}

	/**
	 * Mira si el estado es viable
	 * @param state Estado a verificar
	 * @return Verdadero si no se tienen fechas repetidas en el estado y si las personas que pueden tomar el curso en dicho estado son una cantidad menor o igual a la deseada.
	 * False de lo contrario
	 */
	private boolean isViable(DiasDictarState state) {
		return noRepetidos(state) && getTotalValue(state) <= personasCurso.length;
	}

	/**
	 * Verifica que no hayan fechas repetidas en el estado
	 * @param state Estado a verificar
	 * @return True si no hay fechas repetidas, False de lo contrario
	 */
	private boolean noRepetidos (DiasDictarState state) {
		Dia[] dias = state.getDias();
		for (int i = 0; i < dias.length; i++) {
			for (int j = i+1; j < dias.length; j++) {
				if(i != j && dias[i].getId() >= dias[j].getId()) {
					return false;
				}
			}
		}
		return true;
	}


	/**
	 * Obtiene el valor total de personas que pueden tomar el curso en dicho estado
	 * @param state Estado a verificar
	 * @return Numero de personas que pueden tomar el curso en dicho estado
	 */
	private int getTotalValue(DiasDictarState state) {
		Dia[] diasActuales = state.getDias();
		Set<Persona> personas = new HashSet<Persona>();
		for (int i = 0; i < personasCurso.length; i++) {
			for (int j = 0; j < diasActuales.length; j++) {
				if(diasPersona[diasActuales[j].getId()][i]) {
					personas.add(personasCurso[i]);
				}
			}
		} 

		return personas.size();
	}

	/**
	 * Verifica que el estado actual sea solucion. Implementa el predicado de satisfacibilidad
	 * @param state Estado a verificar
	 * @return True si todas las personas pueden tomar el curso en dicho estado y si el numero de fechas en las que se dicta es menor o igual al limite.
	 * False de lo contrario
	 */
	private boolean isSolution(DiasDictarState state) {
		return getTotalValue(state) == personasCurso.length && state.getTotalDias() <= totalDias;
	}


	/**
	 * Obtiene los sucesores del estado actual. Los sucesores se calculan al agregar una nueva fecha en la que se debe dictar el curso
	 * @param state Estado del que se encontraran los sucesores
	 * @return Lista de sucesores
	 */
	private List<DiasDictarState> getSuccesors(DiasDictarState state){
		ArrayList<DiasDictarState> succesors = new ArrayList<>();
		Dia[] diasActuales = state.getDias();

		Dia[] sucessorBase = new Dia[diasActuales.length+1];
		System.arraycopy(diasActuales, 0,sucessorBase, 0, diasActuales.length);

		for(int i = 0; i < diasCurso.length; i++) {
			sucessorBase[sucessorBase.length-1] = diasCurso[i];
			DiasDictarState newState = new DiasDictarState(sucessorBase);
			succesors.add(newState);
		}

		return succesors;

	}

	/**
	 * Clase que modela un dia en el que se dictara el curso
	 * @author Gabriel Pinto
	 */
	public class Dia {
		private String fecha;
		private int id;

		public Dia(String pFecha, int pId){
			fecha = pFecha;
			id = pId;
		}

		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return fecha;
		}

	}

	/**
	 * Clase que modela una persona interesada en tomar el curso
	 * @author Gabriel Pinto
	 */
	public class Persona {

		private int id;

		private String nombre;

		public Persona (int pId, String pNombre){
			id = pId;
			nombre = pNombre;
		}

		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return nombre;
		}

	}

	/**
	 * Clase que representa un estado de solucion
	 * @author Gabriel Pinto
	 */
	class DiasDictarState {

		Dia[] dias;

		public DiasDictarState (Dia[] pDias) {
			dias = Arrays.copyOf(pDias, pDias.length);
		}

		public Dia[] getDias() {
			return dias;
		}

		public int getTotalDias() {
			return dias.length;
		}

		@Override
		public String toString() {
			return Arrays.toString(dias);
		}

	}

	public static void main(String[] args) {
		
		// Se crea un objeto de la clase
		ExploracionDeGrafosCursoIntensivoAlgoritmo cursoIntensivo = new ExploracionDeGrafosCursoIntensivoAlgoritmo();

		// Se crea un grupo de personas interesadas en tomar el curso
		Persona p0 = cursoIntensivo.new Persona(0, "a");
		Persona p1 = cursoIntensivo.new Persona(1, "b"); 
		Persona p2 = cursoIntensivo.new Persona(2, "c"); 
		Persona p3 = cursoIntensivo.new Persona(3, "d"); 
		Persona p4 = cursoIntensivo.new Persona(4, "e"); 
		Persona p5 = cursoIntensivo.new Persona(5, "f"); 
		Persona p6 = cursoIntensivo.new Persona(6, "g"); 
		Persona p7 = cursoIntensivo.new Persona(7, "h"); 
		Persona p8 = cursoIntensivo.new Persona(8, "i"); 

		// Se guardan las personas en un arreglo
		Persona[] pC = {p0,p1,p2,p3,p4,p5,p6,p7,p8};

		// Se crea un grupo de dias en los que se dictara el curso
		Dia d0 = cursoIntensivo.new Dia("Fecha 0", 0);
		Dia d1 = cursoIntensivo.new Dia("Fecha 1", 1);
		Dia d2 = cursoIntensivo.new Dia("Fecha 2", 2);
		Dia d3 = cursoIntensivo.new Dia("Fecha 3", 3);
		Dia d4 = cursoIntensivo.new Dia("Fecha 4", 4);
		Dia d5 = cursoIntensivo.new Dia("Fecha 5", 5);
		Dia d6 = cursoIntensivo.new Dia("Fecha 6", 6);
		Dia d7 = cursoIntensivo.new Dia("Fecha 7", 7);

		// Se guardan los dias en un arreglo
		Dia [] dias = {d0,d1,d2,d3,d4,d5,d6, d7};

		// Se definen las relaciones entre cursos y personas que lo van a tomar
		boolean[][] relaciones = new boolean[dias.length][pC.length];

		// Soluciones fecha 1, fecha 5 y fecha 6

		relaciones[0][0] = true;
		relaciones[0][1] = true;
		relaciones[0][3] = true;
		relaciones[0][5] = true;
		relaciones[0][7] = true;
		relaciones[1][0] = true;
		relaciones[1][1] = true;
		relaciones[1][3] = true;
		relaciones[1][5] = true;
		relaciones[1][7] = true;
		relaciones[2][2] = true;
		relaciones[2][4] = true;
		relaciones[2][6] = true;
		relaciones[3][2] = true;
		relaciones[3][4] = true;
		relaciones[4][8] = true;
		relaciones[5][0] = true;
		relaciones[5][1] = true;
		relaciones[5][2] = true;
		relaciones[5][3] = true;
		relaciones[5][4] = true;
		relaciones[5][5] = true;
		relaciones[6][6] = true;
		relaciones[6][8] = true;
		relaciones[7][8] = true;


		// Descomentar para Soluciones fecha 2 y fecha 4

		//		relaciones[2][0] = true;
		//		relaciones[4][1] = true;
		//		relaciones[4][3] = true;
		//		relaciones[4][5] = true;
		//		relaciones[4][7] = true;


		// Descomentar para Soluciones fecha 7

		//		relaciones[7][0] = true;
		//		relaciones[7][1] = true;
		//		relaciones[7][2] = true;
		//		relaciones[7][3] = true;
		//		relaciones[7][4] = true;
		//		relaciones[7][5] = true;
		//		relaciones[7][6] = true;
		//		relaciones[7][7] = true;


		// Se obtienen las fechas optimas para dictar el curso
		Dia[] diasCursoDictar = cursoIntensivo.calculateOptimalTripStatePath(dias, pC, 4, relaciones);

		System.out.println(Arrays.toString(diasCursoDictar));

	}
}
