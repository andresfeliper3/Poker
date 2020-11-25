package poker;

import java.util.ArrayList;
import java.util.Random;


// TODO: Auto-generated Javadoc
//Simula un jugador, de acuerdo a su tipo indica si es arriesgado (elimina las 2 cartas) o mesurado (elimina 1 carta)
//los hilos deben ejecutarse cuando les corresponda el turno

/**
 * The Class JugadorSimulado.
 */
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
	
	/**
	 * Instantiates a new jugador simulado.
	 *
	 * @param nombre the nombre
	 * @param turnoId the turno id
	 * @param controlPoker the control poker
	 */
	public JugadorSimulado(String nombre, int turnoId, ControlPoker controlPoker) {
		this.nombre = nombre;
		this.turnoId = turnoId;
		this.controlPoker = controlPoker;
		this.descarte = new ArrayList<Integer>();
		random = new Random();
		
	}
	
	/**
	 * Reiniciar hilo.
	 * 
	 * Ajusta las condiciones iniciales para el reinicio del hilo.
	 */
	public void reiniciarHilo() {
		this.jugar=true;
		this.enRondaDeApuestas=true;
		this.enRondaDeDescarte=true;
		this.operacion=0;
		this.cantidadDescarte=0;
		this.retirado=false;
	}
	
	/**
	 * Gets the retirado.
	 *
	 * @return the retirado
	 */
	//Retorna el estado del jugador si está retirado o no
	public boolean getRetirado() {
		return retirado;
	}
	
	/**
	 * Activar ronda 0.
	 * 
	 * Activa la ronda de apuesta
	 */
	
	public void activarRonda0() {
		
		ronda=0;
	}
	
	/**
	 * Activar ronda 1.
	 * 
	 * Activa la ronda de igualación
	 * 
	 */
	public void activarRonda1() {
		
		ronda=1;
	}
	
	/**
	 * Activar ronda 2.
	 * 
	 * Activa la ronda de descartes
	 */
	
	public void activarRonda2() {
		
		ronda=2;
	}
	
	/**
	 * Activar ronda 3.
	 * 
	 * Activa la ronda de determinar ganador.
	 */
	public void activarRonda3() {
		
		ronda=3;
	}
	
	/**
	 * Sets the retirado.
	 *
	 * Establece si un jugador se retira o no del juego
	 *
	 * @param retirado the new retirado
	 */
	
	public void setRetirado(boolean retirado) {
		this.retirado = retirado;
	}
 	
	 /**
	  * Run.
	  * 
	  * Acciones que realiza al ejecutarse el hilo
	  */
	 @Override
	public void run() {
		// TODO Auto-generated method stub
 		
 		//Mientras la ronda de descarte no haya iniciado, esto sirve para hacer pruebas. Debe ir "mientras el jugador no se haya retirado".
 		while(jugar) {
 			
 			switch(ronda) {
 			case 0://Ronda de apuestas
 	 			int probabilidad = random.nextInt(100) + 1;

 	 	 		//Ronda de apuestas
 	 	 		if(controlPoker.getRonda() == 0 && enRondaDeApuestas) {	
 	 	 			//igualar: probabilidad del 50%		
 	 	 			if(probabilidad <= 50) {
 	 	 				operacion = 0;
 	 	 			}
 	 	 			//aumentar: probabilidad del 40%
 	 	 			else if(probabilidad <= 90) {
 	 	 				operacion = 1;
 	 	 			} 
 	 	 			//retirarse: probabilidad del 10%
 	 	 			else{
 	 	 				operacion = 2;
 	 	 				retirado = true;
 	 	 			}	
 	 	 			enRondaDeApuestas = false;
 	 	 			enRondaDeIgualacion=true;
 	 	 			controlPoker.turnos(turnoId, nombre, operacion, this,retirado); 	 	
 	 	 		}
 				break;
 			case 1://Ronda de Igualacion
 				int probabilidad1 = random.nextInt(100) + 1;
 				if(controlPoker.getRonda() == 1 && enRondaDeIgualacion) {
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
 	 	 			controlPoker.turnos(turnoId, nombre, operacion, this, retirado);	
 	 	 		}
 				break;	
 			case 2://Ronda de Descartes
 	 	 		if(controlPoker.getRonda() == 2 && enRondaDeDescarte) {
 	 	 			//Escoge la cantidad de cartas que va a descartar
 	 	 			cantidadDescarte = random.nextInt(ControlPoker.NUMERO_CARTAS_MANO + 1); //0-5
 	 	 			//Decarta aleatoriamente y sin repetir la cantidad de cartas escogida
 	 	 			enRondaDeDescarte = false;
 	 	 	 		controlPoker.turnos(turnoId, nombre, cantidadDescarte, this,retirado);
 	 	 			enRondaDeApuestas=true;
 	 	 			enRondaDeDescarte=false;
 	 	 		}
 				break;
 			case 3://Ronda para determinar ganador
 	 	 		if(controlPoker.getRonda() == 3) {
 	 	 			jugar = false;
 	 	 		}
 				
 			}
	
 		} 
 	}

}



	
