/*
 * 
 */
package poker;

import java.util.ArrayList;
import java.util.Random;


//Simula un jugador, de acuerdo a su tipo indica si es arriesgado (elimina las 2 cartas) o mesurado (elimina 1 carta)
//los hilos deben ejecutarse cuando les corresponda el turno

public class JugadorSimulado implements Runnable {
	
	private String nombre;
	private int turnoId;
	private boolean retirado = false;
	private int cantidadDescarte;
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
	private int ronda=0;
	public JugadorSimulado(String nombre, int turnoId, ControlPoker controlPoker) {
		this.nombre = nombre;
		this.turnoId = turnoId;
		this.controlPoker = controlPoker;
		this.descarte = new ArrayList<Integer>();
		random = new Random();
		
	}
	public void reiniciarHilo() {
		this.jugar=true;
		this.enRondaDeApuestas=true;
		this.enRondaDeDescarte=true;
		this.operacion=0;
		this.cantidadDescarte=0;
		this.retirado=false;
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
	//Método encargado de anunciar retiro
	public String estadoJugador() {
		if(retirado) {
			return "Jugador retirado";
		}else {
			return "Jugador en juego";
		}
	}
	//Retorna el estado del jugador si está retirado o no
	public boolean getRetirado() {
		return retirado;
	}
	//Activa la ronda 0 de apuestas
	public void activarRonda0() {
		
		ronda=0;
	}
	//Activa la ronda 1 de igualacion
	public void activarRonda1() {
		
		ronda=1;
	}
	//Activa la ronda 2 de Descartes
	public void activarRonda2() {
		
		ronda=2;
	}
	//Activa la ronda 2 de determinar Ganador
	public void activarRonda3() {
		
		ronda=3;
	}
	//Establece si un jugador se retira o no del juego
	public void setRetirado(boolean retirado) {
		this.retirado = retirado;
	}
 	@Override
	//Acción que realiza al ejecutarse el hilo
	public void run() {
		// TODO Auto-generated method stub
 		
 		//Mientras la ronda de descarte no haya iniciado, esto sirve para hacer pruebas. Debe ir "mientras el jugador no se haya retirado".
 		while(jugar) {
 			
 			switch(ronda) {
 			case 0://Ronda de apuestas
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
 	 	 				System.out.println("Run: Jugador " + nombre + " se retira");
 	 	 			}
 	 	 			
 	 	 			enRondaDeApuestas = false;
 	 	 			enRondaDeIgualacion=true;
 	 	 			System.out.println("Antes de ejecutar turnos, jugador " + nombre);
 	 	 			controlPoker.turnos(turnoId, nombre, operacion, this,retirado);
 	 	 			System.out.println("Después de ejecutar turnos, jugador" + nombre);
 	 	
 	 	 		}
 				break;
 			case 1://Ronda de Igualacion
 				int probabilidad1 = random.nextInt(100) + 1;
 				if(controlPoker.getRonda() == 1 && enRondaDeIgualacion) {
 	 	 			System.out.println("Run: Entra a igualación del jugador " + nombre);
 	 	 			System.out.println("ronda " + controlPoker.getRonda());
 	 	 			//igualar
 	 	 			if(probabilidad1 <= 90) {
 	 	 				//cantidadApuesta = controlPoker.getMaximaApuesta();
 	 	 				operacion = 0;
 	 	 			} 
 	 	 			//retirarse
 	 	 			else {
 	 	 				//cantidadApuesta = controlPoker.getApuestasJugadores().get(turnoId - 1);
 	 	 				operacion = 2;
 	 	 				retirado = true;
 	 	 			}
 	 	 			enRondaDeIgualacion = false;
 	 	 			controlPoker.turnos(turnoId, nombre, operacion, this,retirado);
 	 	 			
 	 	 		}
 				break;	
 			case 2://Ronda de Descartes
 	 	 		if(controlPoker.getRonda() == 2 && enRondaDeDescarte) {
 	 	 			//Escoge la cantidad de cartas que va a descartar
 	 	 			System.out.println("Run: Ronda 2 de jugador simulado " + nombre);
 	 	 			cantidadDescarte = random.nextInt(ControlPoker.NUMERO_CARTAS_MANO + 1); //0-5
 	 	 			//Decarta aleatoriamente y sin repetir la cantidad de cartas escogida
 	 	 			enRondaDeDescarte = false;
 	 	 			System.out.println("Antes de llamar a turnos en RUN ronda 2 el jugador " + nombre);
 	 	 			controlPoker.turnos(turnoId, nombre, cantidadDescarte, this,retirado);
 	 	 			System.out.println("Después de llamar a turnos en RUN ronda 2 el jugador " + nombre);
 	 	 			enRondaDeApuestas=true;
 	 	 			enRondaDeDescarte=false;
 	 	 		}
 				break;
 			case 3://Ronda de determinar ganador
 	 	 		if(controlPoker.getRonda() == 3) {
 	 	 			jugar = false;
 	 	 		}
 				
 			}
	
 		} 
 	}

}



	
