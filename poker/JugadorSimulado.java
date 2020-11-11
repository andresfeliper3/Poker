package poker;

import java.util.ArrayList;
import java.util.Random;


//Simula un jugador, de acuerdo a su tipo indica si es arriesgado (elimina las 2 cartas) o mesurado (elimina 1 carta)
//los hilos deben ejecutarse cuando les corresponda el turno

public class JugadorSimulado implements Runnable {
	
	private String nombre;
	private int turnoId;
	private int tipo;
	private int cantidadDescarte, cantidadApuesta;
	private ArrayList<Integer> descarte;
	private Random random;
	private ControlPoker controlPoker; //recurso compartido
	/*Operacion
	 * 0: igualar
	 * 1: aumentar
	 * 2: retirarse
	 * */
	private int operacion;
	
	public JugadorSimulado(String nombre, int turnoId, ControlPoker controlPoker) {
		this.nombre = nombre;
		this.turnoId = turnoId;
		this.controlPoker = controlPoker;
		this.descarte = new ArrayList<Integer>();
		random = new Random();
		
	}

	//Se encarga de asegurarse de que las posiciones aleatorias de las cartas a descartar de la mano de un jugador no se repitan.
	private void escogerDescarte(int aleatoriosNecesarios) {
		while(descarte.size() < aleatoriosNecesarios) {
			int numeroAleatorio = random.nextInt(ControlPoker.NUMERO_CARTAS_MANO); //0-4
			if(descarte.indexOf(numeroAleatorio) == -1) {
				descarte.add(numeroAleatorio);
			} 
		}
	}
 	@Override
	//Acci�n que realiza al ejecutarse el hilo
	public void run() {
		// TODO Auto-generated method stub
 		//Si est�n en ronda de apuestas
 		if(controlPoker.getRonda() == 0) {
 			
 			int factorAumento = 1;
 			//Probabilidad de aumentar: 25%
 			//Probabilidad de igualar: 50%	
 			//Probabilidad de retirarse: 25%
 			int probabilidad = random.nextInt(100) + 1;
 			//igualar
 			if(probabilidad <= 50) {
 				cantidadApuesta = controlPoker.getMaximaApuesta();
 				operacion = 0;
 				//AVISAR A CONTROL
 			}
 			//aumentar
 			else if(probabilidad <= 75) {
 				cantidadApuesta = controlPoker.getApuestasJugadores().get(turnoId - 1) + (factorAumento * 500); //turnos 1-5
 				operacion = 1;
 			} 
 			//retirarse
 			else {
 				//SE RETIRA
 				cantidadApuesta = -1;
 				operacion = 2;
 			}
 			controlPoker.turnos(turnoId, nombre, cantidadApuesta, operacion);
 		}
 		//Si est�n en ronda de descarte
 		else if(controlPoker.getRonda() == 1) {
 			//Escoge la cantidad de cartas que va a descartar
 	 		cantidadDescarte = random.nextInt(ControlPoker.NUMERO_CARTAS_MANO + 1); //0-5
 	 		//Decarta aleatoriamente y sin repetir la cantidad de cartas escogida
 			escogerDescarte(cantidadDescarte);
 			controlPoker.turnos(turnoId, descarte, nombre);
 			System.out.println("Hilo "+ nombre +" termina "+descarte);
 		}
 		
	}
	

}
