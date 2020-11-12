package poker;

import java.util.ArrayList;
import java.util.Random;

public class JugadorSimulado implements Runnable {
	
	private String nombreJugador;
	private int turno;
	private int cantidadDescarte;
	private Random random;
	private ArrayList<Integer> cartasADescartar = new ArrayList<>();
	private ControlPoker controlPoker;//Recurso compartido
	
	
	public JugadorSimulado(int turno, String nombreJugador, ControlPoker controlPoker) {
		this.turno = turno;
		this.nombreJugador = nombreJugador;
		this.controlPoker = controlPoker;
		random = new Random();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
			cartasADescartar();
	}
	
	//Método que genera las cartas que van a ser descartadas
	public void cartasADescartar() {
		cantidadDescarte = random.nextInt(6); //Genera un número al azar de cartas que el jugador va a descartar entre 0 y 5
		
		if(cantidadDescarte >0) {
			for(int i=0;i<cantidadDescarte;i++) {
				cartasADescartar.add(random.nextInt(6)); //Genera un número aleatorio especificando la carta que va a ser descartada
			}
		}
		
	controlPoker.turnos(turno,cantidadDescarte,nombreJugador);
		
	}

}
