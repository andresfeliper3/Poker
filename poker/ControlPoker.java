package poker;

import java.awt.EventQueue;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

public class ControlPoker {
	public static final int NUMERO_CARTAS_MANO = 5;
	public static final int TOTAL_JUGADORES = 5;
	public static final String[] NOMBRE_JUGADORES = {"Duque","Dilan", "Petrosky", "Kaku"};
	
	//Vista GUI
	private VistaGUIPoker vistaPoker;
	//Elementos del juego
	private Baraja baraja;
	private List<List<Carta>> manosJugadores;
	private List<Integer> apuestasJugadores;
	private int apuestaInicial = 500; 
	/*Ronda
	 * 0: ronda de apuestas
	 * 1: ronda de descarte
	 * 2: ronda de definición*/
	private int ronda = 0;
	private int jugadorManoAleatorio; 
	//variables para el manejo de hilos
	private int turno; //variable de control de turno hilos, 1-5
	private int[] descarte = new int[TOTAL_JUGADORES-1]; //AÚN NO SÉ PARA QUÉ ES XD
	private Lock bloqueo = new ReentrantLock(); //manejo de sincronizacion
	private Condition esperarTurno = bloqueo.newCondition(); //manejo de sincronizacion	
	
	private Random random;
	public ControlPoker() {
		manosJugadores = new ArrayList<List<Carta>>();
		apuestasJugadores = new ArrayList<Integer>();
		repartirCartas();
		colocarApuestaInicial();
		escogerJugadorMano();
		vistaPoker = new VistaGUIPoker(NOMBRE_JUGADORES, manosJugadores, apuestasJugadores, this);
	}
	//Reparte las cartas al inicio del juego
	private void repartirCartas() {
		baraja = new Baraja();
		for(int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			manosJugadores.add(seleccionarCartas());
		}
	}
	//Todos inician con la misma apuesta
	private void colocarApuestaInicial() {
		for(int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			apuestasJugadores.add(apuestaInicial);
		}
		System.out.println("Apuesta inicial size: " +  apuestasJugadores.size());
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
	//Escoge al azar al jugador mano (inicial) escogiendo el turno
 	private void escogerJugadorMano() {
 		random = new Random();
		jugadorManoAleatorio = random.nextInt(TOTAL_JUGADORES) + 1;
		turno = jugadorManoAleatorio;
		iniciarJugadoresSimulados();
	}
 	//Inicia los hilos de los jugadores simulados
 	private void iniciarJugadoresSimulados() {
 		JugadorSimulado jugador1 = new JugadorSimulado(NOMBRE_JUGADORES[0], 1, this);
 		JugadorSimulado jugador2 = new JugadorSimulado(NOMBRE_JUGADORES[1], 2, this);
 		JugadorSimulado jugador3 = new JugadorSimulado(NOMBRE_JUGADORES[2], 3, this);
 		JugadorSimulado jugador4 = new JugadorSimulado(NOMBRE_JUGADORES[3], 4, this);
 		
 		ExecutorService ejecutorHilos = Executors.newCachedThreadPool(); //PROBAR OTRO
 		ejecutorHilos.execute(jugador1);
 		ejecutorHilos.execute(jugador2);	
 		ejecutorHilos.execute(jugador3);
 		ejecutorHilos.execute(jugador4);
 		ejecutorHilos.shutdown();
 	}
 	//Método sincronizador de turnos
 	public void turnos(int idJugador, List<Integer> descarte, String nombreJugador) {
 		//Si está en la ronda de descarte
 		if(ronda == 1) {
 			bloqueo.lock();
 	 		try {
 	 			
 	 		} 
 	 		catch(InterruptedException e) {
 	 			e.printStackTrace();
 	 		}
 	 		finally {
 	 		bloqueo.unlock();
 	 		}
 		}
 		
 	}
 	int contadorTurnos = 0;
	//Método sincronizador de turnos
 	public void turnos(int idJugador, String nombreJugador, int apuesta, int operacion) {
 		//Si está en la ronda de apuestas
 		//contadorTurno permite que solo 5 personas jueguen
 		if(ronda == 0 && contadorTurnos < 5) {
 			bloqueo.lock();
 	 		try {
 	 			//Mientras el jugador que entre no sea el que correponda, se duerme
 	 			while(idJugador != turno) {		
 	 				System.out.println("Jugador " + nombreJugador + " intenta entrar y es mandado a esperar turno");
 	 				esperarTurno.await();
 	 			}
 	 			setApuestasJugadores(idJugador - 1, apuesta);
 	 			editarRegistros(1, nombreJugador, apuesta, operacion);
 	 			contadorTurnos++;
 	 			aumentarTurno();
 	 			System.out.println("Turno: " + turno);
 	 			System.out.println("Jugador " + nombreJugador + " apostó " + apuestasJugadores.get(idJugador - 1) + " en total.");
 	 			esperarTurno.signalAll();
 	 		} 
 	 		catch(InterruptedException e) {
 	 			e.printStackTrace();
 	 		}
 	 		finally {
 	 			bloqueo.unlock();
 	 			if(turno == 5) {
 	 				//humanoApuesta();
 	 				editarRegistros(2, "", -1, -1);
 	 			}
 	 		}
 	 	}
 	}
 	//Método que sincroniza los cambios en componente gráficos con el hilo manejador de eventos
 	private void editarRegistros(int fase, String nombre, int apuesta, int operacion) {
 		//Sincronizar con hilo manejador de eventos
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			// TODO Auto-generated method stub
				vistaPoker.editarRegistros(fase, nombre, apuesta, operacion);
			}
		});
 	}
 	
 	private void aumentarTurno() {
 		//Si turno es 4 o múltiplo de 4, se convierte en 5. Si turno tiene otro valor, aumenta en 1 pero sin sobrepasar al 5. 
 		turno = (turno % 4 != 0) ? (turno + 1) % 5 : 5;
 		System.out.println("Turno aumentó a " + turno);
 	}
 	public void setApuestasJugadores(int jugador, int apuesta) {
 		apuestasJugadores.set(jugador, apuesta);
 	}
 	public List<Integer> getApuestasJugadores() {
		return apuestasJugadores;
	}
	public int getMaximaApuesta() {
 		return Collections.max(apuestasJugadores);
 	}
 	public int getIdJugadorMano() {
 		return jugadorManoAleatorio;
 	}
	public int getRonda() {
		return ronda;
	}
	public int getTurno() {
		return turno;
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
