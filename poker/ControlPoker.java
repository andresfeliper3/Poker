package poker;

import java.awt.EventQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ControlPoker {
	public static final int NUMERO_CARTAS_MANO = 5;
	public static final int TOTAL_JUGADORES = 5;
	public static final String[] NOMBRE_JUGADORES = {"Duque","Dilan", "Petrosky", "Kaku"};
	
	//Vista GUI
	private VistaGUIPoker vistaPoker;
	//Elementos del juego
	private Baraja baraja;
	private List<List<Carta>> manosJugadores;
	private int apuestaInicial = 500; 
	private int apuestaActual;

	//variables para el manejo de hilos
	private int turno; //variable de control de turno hilos
	private int[] descarte = new int[TOTAL_JUGADORES-1]; //AÚN NO SÉ PARA QUÉ ES XD
	private Lock bloqueo = new ReentrantLock(); //manejo de sincronizacion
	private Condition esperarTurno = bloqueo.newCondition(); //manejo de sincronizacion	
	
	public ControlPoker() {
		manosJugadores = new ArrayList<List<Carta>>();
		repartirCartas();
		vistaPoker = new VistaGUIPoker(NOMBRE_JUGADORES, manosJugadores, apuestaInicial, this);
	}
	//Reparte las cartas al inicio del juego
	private void repartirCartas() {
		baraja = new Baraja();
		for(int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			manosJugadores.add(seleccionarCartas());
		}
	}
	
	private ArrayList<Carta> seleccionarCartas() {
		// TODO Auto-generated method stub
		ArrayList<Carta> manoJugador = new ArrayList<Carta>();
		//se dan 5 cartas al jugador
		for(int carta = 0; carta < 5; carta++) {
			manoJugador.add(baraja.getCarta());
		}	
		return manoJugador;
	}
 	//MAIN - hilo principal
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable( ) {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				new ControlPoker();
			}	
		});
	}
}
