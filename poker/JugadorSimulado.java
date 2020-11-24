package poker;

import java.util.ArrayList;
import java.util.Random;


//Simula un jugador, de acuerdo a su tipo indica si es arriesgado (elimina las 2 cartas) o mesurado (elimina 1 carta)
//los hilos deben ejecutarse cuando les corresponda el turno

public class JugadorSimulado implements Runnable {
	
	private String nombre;
	private int turnoId;
	private boolean retirado = false;
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
	private boolean jugar = true;
	private boolean enRondaDeApuestas = true; //Controla que un jugador no entre dos veces seguidas a turnos.
	private boolean enRondaDeIgualacion = true; //Controla que un jugador no entre dos veces seguidas a turnos.
	private boolean enRondaDeDescarte = true;
	
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
	
	public boolean getRetirado() {
		return retirado;
	}
	
	public void setRetirado(boolean retirado) {
		this.retirado = retirado;
	}
	
 	public void setEnRondaDeApuestas(boolean enRondaDeApuestas) {
		this.enRondaDeApuestas = enRondaDeApuestas;
	}

	public void setEnRondaDeIgualacion(boolean enRondaDeIgualacion) {
		this.enRondaDeIgualacion = enRondaDeIgualacion;
	}

	public void setEnRondaDeDescarte(boolean enRondaDeDescarte) {
		this.enRondaDeDescarte = enRondaDeDescarte;
	}

	@Override
	//Acción que realiza al ejecutarse el hilo
	public void run() {
		// TODO Auto-generated method stub
 		
 		//Mientras la ronda de descarte no haya iniciado, esto sirve para hacer pruebas. Debe ir "mientras el jugador no se haya retirado".
 		while(jugar) {
 			//System.out.println("En ronda " + controlPoker.getRonda() + " entra el jugador simulado " + nombre);
 			//Si están en ronda de apuestas
 	 		//Probabilidad de aumentar: 25%
 			//Probabilidad de igualar: 50%	
 			//Probabilidad de retirarse: 25%
 			int probabilidad = random.nextInt(100) + 1;

 	 		//Ronda de apuestas
 	 		if(controlPoker.getRonda() == 0 && enRondaDeApuestas) {	
 	 			//igualar
 	 			
 	 			if(probabilidad <= 50) {
 	 				//cantidadApuesta = controlPoker.getMaximaApuesta();
 	 				operacion = 0;
 	 				//AVISAR A CONTROL
 	 			}
 	 			//aumentar
 	 			else if(probabilidad <= 90) {
 	 				//cantidadApuesta = controlPoker.getMaximaApuesta() + (factorAumento * 500); //turnos 1-5
 	 				operacion = 1;
 	 			} 
 	 			//retirarse
 	 			else{
 	 				//SE RETIRA
 	 				//cantidadApuesta = controlPoker.getApuestasJugadores().get(turnoId - 1);
 	 				operacion = 2;
 	 				retirado = true;
 	 				controlPoker.setJugadoresEnjuego();
 	 				System.out.println("Run: Jugador " + nombre + " se retira");
 	 			}
 	 			enRondaDeApuestas = false;
 	 			enRondaDeIgualacion=true;
 	 			System.out.println("Antes de ejecutar turnos, jugador " + nombre);
 	 			controlPoker.turnos(turnoId, nombre, operacion, this);
 	 			System.out.println("Después de ejecutar turnos, jugador" + nombre);
 	
 	 		}
 	 		//Ronda de igualacion
 	 		else if(controlPoker.getRonda() == 1 && enRondaDeIgualacion) {
 	 			System.out.println("Run: Entra a igualación del jugador " + nombre);
 	 			System.out.println("ronda " + controlPoker.getRonda());
 	 			//igualar
 	 			if(probabilidad <= 90) {
 	 				//cantidadApuesta = controlPoker.getMaximaApuesta();
 	 				operacion = 0;
 	 			} 
 	 			//retirarse
 	 			else {
 	 				//cantidadApuesta = controlPoker.getApuestasJugadores().get(turnoId - 1);
 	 				operacion = 2;
 	 				retirado = true;
 	 				controlPoker.setJugadoresEnjuego();
 	 			}
 	 			enRondaDeIgualacion = false;
 	 			controlPoker.turnos(turnoId, nombre, operacion, this);
 	 			
 	 		}
 	 		//Ronda de descartes
 	 		else if(controlPoker.getRonda() == 2 && enRondaDeDescarte) {
 	 			//Escoge la cantidad de cartas que va a descartar
 	 			System.out.println("Run: Ronda 2 de jugador simulado " + nombre);
 	 			cantidadDescarte = random.nextInt(ControlPoker.NUMERO_CARTAS_MANO + 1); //0-5
 	 			//Decarta aleatoriamente y sin repetir la cantidad de cartas escogida
 	 			//escogerDescarte(cantidadDescarte);
 	 			enRondaDeDescarte = false;
 	 			System.out.println("Antes de llamar a turnos en RUN ronda 2 el jugador " + nombre);
 	 			controlPoker.turnos(turnoId, nombre, cantidadDescarte, this);
 	 			System.out.println("Después de llamar a turnos en RUN ronda 2 el jugador " + nombre);
 	 			enRondaDeApuestas=true;
 	 			enRondaDeDescarte=false;
 	 			
 	 		}
 	 		else if(controlPoker.getRonda() == 3) {
 	 			System.out.println("MATANDOOOOO AL HILO " + nombre);
 	 			jugar = false;
 	 		}
 	 		
 	 		
 		} 
 	}	

}



	
