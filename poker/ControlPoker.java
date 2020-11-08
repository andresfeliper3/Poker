package poker;

import java.awt.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ControlPoker {
	public static final int NUMERO_CARTAS_MANO = 5;
	public static final int TOTAL_JUGADORES = 5;
	public static final String[] NOMBRE_JUGADORES = {"Javier","Valentina", "Mario", "Camila"};
	
	//Vista GUI
	
	//Elementos del juego
	private Baraja baraja;
	private List<List<Carta>> manosJugadores;

	//variables para el manejo de hilos
	private int turno; //variable de control de turno hilos
	private int[] descarte = new int[TOTAL_JUGADORES-1]; //AÚN NO SÉ PARA QUÉ ES XD
	private Lock bloqueo = new ReentrantLock(); //manejo de sincronizacion
	private Condition esperarTurno = bloqueo.newCondition(); //manejo de sincronizacion	
	
	public ControlPoker() {
		
	}
}
