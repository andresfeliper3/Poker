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
	private JugadorSimulado jugador1, jugador2, jugador3, jugador4;
	private JugadorSimulado[] jugadoresSimulados = {jugador1, jugador2, jugador3, jugador4};
	//Vista GUI
	private VistaGUIPoker vistaPoker;
	//Elementos del juego
	private Baraja baraja;
	private List<List<Carta>> manosJugadores;
	private List<Integer> apuestasJugadores;
	private List<Integer> jugadoresParaApostarMas; //Lista de posiciones de jugadores 	
	private int apuestaInicial = 500; 
	private boolean humanoRetirado = false;
	/*Ronda
	 * 0: ronda de apuestas
	 * 1: ronda de revisión
	 * 2: ronda de descarte
	 * 3: ronda de definición*/
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
		jugadoresParaApostarMas = new ArrayList<Integer>();
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
 		jugador1 = new JugadorSimulado(NOMBRE_JUGADORES[0], 1, this);
 		jugador2 = new JugadorSimulado(NOMBRE_JUGADORES[1], 2, this);
 		jugador3 = new JugadorSimulado(NOMBRE_JUGADORES[2], 3, this);
 		jugador4 = new JugadorSimulado(NOMBRE_JUGADORES[3], 4, this);
 		
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
 		if(ronda == 2) {
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
 	public void turnos(int idJugador, String nombreJugador, int apuesta, int operacion, JugadorSimulado jugadorSimulado) {
 		//Si está en la ronda de apuestas
 		//contadorTurno permite que solo 5 personas jueguen
 		if(ronda == 0 && contadorTurnos < TOTAL_JUGADORES) {
 			bloqueo.lock();
 	 		try {
 	 			//Mientras el jugador que entre no sea el que correponda, se duerme
 	 			while(idJugador != turno) {		
 	 				System.out.println("Jugador " + nombreJugador + " intenta entrar y es mandado a esperar turno");
 	 				esperarTurno.await();
 	 				//Se vuelve a llamar al método run para que el jugador simulado tome su decisión con las apuestas recientes
 	 				jugadorSimulado.run();
 	 				//return;
 	 			}
 	 			System.out.println("Este es el idJugador " + idJugador);
 	 			setApuestasJugadores(idJugador - 1, apuesta);
 	 			editarPanelJugador(idJugador - 1, apuesta);
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
 	 			//Revisar si todos los jugadores apostaron
 	 			if(contadorTurnos == TOTAL_JUGADORES) {
 	 				if(revisarApuestasIguales()) {
 	 					//PASAMOS A RONDA DE DESCARTE
 	 					ronda = 2;
 	 					editarRegistros(4, "", -1, -1);
 	 				}
 	 				else {
	 					//REVISAR QUIENES SON DIFERENTES Y 	SEGUIR UNA RONDA DE APUESTAS CON ELLOS
 	 					editarRegistros(3, "", -1, -1);
 	 					ronda = 1;
 	 				}
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
				System.out.println("Editar Registros");
				vistaPoker.editarRegistros(fase, nombre, apuesta, operacion);
			}
		});
 	}
 	
 	private void editarPanelJugador(int jugador, int apuesta) {
 		//Sincronizar con hilo manejador de eventos
 		SwingUtilities.invokeLater(new Runnable() {
 			@Override
 			public void run() {
 			// TODO Auto-generated method stub
 				System.out.println("Editar PanelJugador");
 				vistaPoker.editarPanelJugador(jugador, apuesta);
 			}
 		});
 	}
 	
 	//Revisar que todos los jugadores tengan las mismas apuestas. Retorna true si todas son iguales, false en caso contrario.
 	private boolean revisarApuestasIguales() {
 		jugadoresParaApostarMas.clear();
 		boolean[] jugadoresRetirados = {jugador1.getRetirado(), jugador2.getRetirado(), jugador3.getRetirado(), jugador4.getRetirado(), humanoRetirado};
 		int cantidadJugadores = 0;
 		for(int jugadorIndex = 0; jugadorIndex < apuestasJugadores.size(); jugadorIndex++) {
 			//Si el jugador apuesta 0 quiere decir que se retiró, es decir, no se va a tomar en cuenta en el juego
 			if(!jugadoresRetirados[jugadorIndex]) {
 				if(!(apuestasJugadores.get(jugadorIndex) == Collections.max(apuestasJugadores))) {
 	 				//Se añade el índice (número de jugador) de la apuesta en apuestasJugadores que es diferente
 	 				jugadoresParaApostarMas.add(jugadorIndex);
 	 				cantidadJugadores++;
 	 				}
 	 			}
 			}
 			
 		//Si cantidadJugadores nunca aumentó, todas las apuestas son iguales
 		if(cantidadJugadores == 0) {
 			return true;
 		}
 		return false;
 	}
 	
 	private void aumentarTurno() {
 		//Si turno es 4 o múltiplo de 4, se convierte en 5. Si turno tiene otro valor, aumenta en 1 pero sin sobrepasar al 5. 
 		turno = (turno % 4 != 0) ? (turno + 1) % 5 : 5;
 		System.out.println("Turno aumentó a " + turno);
 	}
 	
 	public void setApuestasJugadores(int indexJugador, int apuesta) {
 		apuestasJugadores.set(indexJugador, apuesta);
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
