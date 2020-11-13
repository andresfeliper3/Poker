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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ControlPoker {
	public static final int NUMERO_CARTAS_MANO = 5;
	public static final int TOTAL_JUGADORES = 5;
	public static final String[] NOMBRE_JUGADORES = {"Duque","Dilan", "Petrosky", "Kaku"};
	private JugadorSimulado jugador1, jugador2, jugador3, jugador4;
	private JugadorSimulado[] jugadoresSimulados = new JugadorSimulado[TOTAL_JUGADORES - 1];
	//Vista GUI
	private VistaGUIPoker vistaPoker;
	//Elementos del juego
	private Baraja baraja;
	private List<List<Carta>> manosJugadores;
	private List<Integer> apuestasJugadores;
	private List<Integer> jugadoresParaApostarMas; //Lista de posiciones de jugadores 	
	private int apuestaInicial = 500; 
	private boolean humanoRetirado = false;
	private int contadorTurnos = 0;
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
	private Condition esperarIgualacion = bloqueo.newCondition();
	private ExecutorService ejecutorHilos = Executors.newCachedThreadPool(); //PROBAR OTRO
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
		//Decirle al jugador lo que debe hacer si es el jugador mano
		if(turno == 5) {
			editarRegistros(2, "", -1, -1);
		}
	}
 	//Inicia los hilos de los jugadores simulados
 	private void iniciarJugadoresSimulados() {
 		//Jugadores simulados
 		jugador1 = new JugadorSimulado(NOMBRE_JUGADORES[0], 1, this);
 		jugador2 = new JugadorSimulado(NOMBRE_JUGADORES[1], 2, this);
 		jugador3 = new JugadorSimulado(NOMBRE_JUGADORES[2], 3, this);
 		jugador4 = new JugadorSimulado(NOMBRE_JUGADORES[3], 4, this);
 		jugadoresSimulados[0] = jugador1;
 		jugadoresSimulados[1] = jugador2;
 		jugadoresSimulados[2] = jugador3;
 		jugadoresSimulados[3] = jugador4;
 		
 		for(JugadorSimulado jugador : jugadoresSimulados) {
 			ejecutorHilos.execute(jugador);
 		}
 		
 		//ejecutorHilos.shutdown();
 	}
 	//Método sincronizador de turnos
 	/*
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
 		
 	}*/
 	
 	int contadorIgualacion = 0;
	//Método sincronizador de turnos
 	public void turnos(int idJugador, String nombreJugador, int operacion, JugadorSimulado jugadorSimulado) {
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
 	 				//jugadorSimulado.run();
 	 			}
 	 			int apuesta = calcularApuesta(idJugador, operacion);
 	 			System.out.println("Este es el idJugador " + idJugador);
 	 			setApuestasJugadores(idJugador - 1, apuesta);
 	 			editarPanelJugador(idJugador - 1, apuesta);
 	 			editarRegistros(1, nombreJugador, apuesta, operacion);
 	 			System.out.println("Turno: " + turno);
 	 			System.out.println("Jugador " + nombreJugador + " apostó " + apuestasJugadores.get(idJugador - 1) + " en total.");
 	 			contadorTurnos++;
 	 			aumentarTurno();		
 	 			esperarTurno.signalAll();
 	 		} 
 	 		catch(InterruptedException e) {
 	 			e.printStackTrace();
 	 		}
 	 		finally {
 	 			bloqueo.unlock();
 	 			if(turno == 5 && contadorTurnos < TOTAL_JUGADORES) {
 	 				//humanoApuesta();
 	 				editarRegistros(2, "", -1, -1);
 	 			}
 	 			System.out.println("Contador de turnos es " + contadorTurnos + " y total jugadores es " + TOTAL_JUGADORES);
 	 			//Revisar si todos los jugadores apostaron
 	 			if(contadorTurnos == TOTAL_JUGADORES) {					
 	 				if(revisarApuestasIguales()) {
 	 					//PASAMOS A RONDA DE DESCARTE
 	 					editarRegistros(5, "", -1, -1);
 	 					ronda = 2;
 	 				}
 	 				else {
	 					//REVISAR QUIENES SON DIFERENTES Y 	SEGUIR UNA RONDA DE APUESTAS CON ELLOS
 	 					//Comienza ronda igualación
 	 					editarRegistros(3, "", -1, -1);			
 	 					ronda = 1;
 	 					aumentarTurnosRondaIgualacion();
 	 					rondaIgualarApuestas();
 	 								
 	 				}
 	 			}
 	 		}
 	 	}
 		//Si estamos en la ronda de igualación de apuestas
 		else if(ronda == 1) {
 			try {
 				bloqueo.lock();
 				System.out.println("Igualacion, Turno es " + turno);
 				System.out.println("igualacion, IdJugador es " + idJugador);
 				while(idJugador != turno) {
 					System.out.println("En igualación " + nombreJugador + " intenta entrar pero se va a dormir");
 					esperarIgualacion.await();
 					//jugadorSimulado.run();
 				}
 				int apuesta = calcularApuesta(idJugador, operacion);
 				setApuestasJugadores(idJugador - 1, apuesta);
 	 			editarPanelJugador(idJugador - 1, apuesta);
 	 			editarRegistros(4, nombreJugador, apuesta, operacion);
 	 			contadorIgualacion++;
 	 			System.out.println("En igualacion, el jugador " + nombreJugador + " realiza una apuesta total de " + apuesta + " y debería ser de " + Collections.max(apuestasJugadores));
 	 			aumentarTurnosRondaIgualacion();
 	 			esperarIgualacion.signalAll();
 			} 
 			catch(InterruptedException e) {
 				e.printStackTrace();
 			} 
 			finally {
 				bloqueo.unlock();
 				if(turno == 5) {
 	 				//Avisar que puede igualar o retirarse
 	 				editarRegistros(6, "", -1, -1);
 	 			}
 				//Si todos los que debían igualar, ya igualaron
 				if(contadorIgualacion == jugadoresParaApostarMas.size()) {
 					System.out.println("Contador igualacion " + contadorIgualacion + " y jugadoresParaApostarMas " + jugadoresParaApostarMas.size());
 					if(revisarApuestasIguales()) {
 		 				//PASAMOS A RONDA DE DESCARTE
 						JOptionPane.showMessageDialog(null, "Después de igualación, las apuestas están iguales");
 		 				ronda = 2;
 		 				editarRegistros(5, "", -1, -1);
 		 			}
 	 				else {
 	 					JOptionPane.showMessageDialog(null, "Las apuestas deberían estar iguales y no lo están.");
 	 				}
 				}
 			}
 		}
 	}
 	
 	//Calcula el valor de la apuesta basándose en la operación dada por el jugador con identificado idJugador
 	private int calcularApuesta(int idJugador, int operacion) {
 		//igualar
 		if(operacion == 0) {
 			return getMaximaApuesta();
 		}
 		//aumentar
 		else if(operacion == 1) {
 			return getMaximaApuesta() + 500;
 		}
 		//retirarse
 		else if(operacion == 2) {
 			return apuestasJugadores.get(idJugador - 1);
 		}
 		//Error
		return -1;
 	}
 	int posicionJugador = 0;
 	//Función para manejar los turnos en la ronda de igualación, donde solo participamn los jugadores que deben igualar o retirarse.
 	private void aumentarTurnosRondaIgualacion() {
 		//Turno está entre 1 y los jugadores que deben igualar o retirarse
 		turno = jugadoresParaApostarMas.get(posicionJugador) + 1;
 		if(posicionJugador < jugadoresParaApostarMas.size() - 1) {
 			posicionJugador++;
 		}
 		System.out.println("turno2 está aumentado a " + turno);
 		if(turno == 5) {
 			JOptionPane.showMessageDialog(null, "Es el turno del usuario de igualar o retirarse.");
 		}
 	}
 	//Ejecutar los hilos en la ronda de igualación de apuestas
 	private void rondaIgualarApuestas() {	
 		System.out.println("Entró a igualar apuestas");
 		System.out.println("El size de jugadoresParaApostarMas es " + jugadoresParaApostarMas.size());
 		//ExecutorService ejecutorHilos = Executors.newCachedThreadPool(); //PROBAR OTRO
 		for(int i = 0; i < jugadoresSimulados.length; i++) {
 			//No activa al jugador humano, porque no es un hilo
 			
 			if(i != 4) {
 				System.out.println("Prende el hilo del jugador " + i);
 				//jugadoresSimulados[jugadoresParaApostarMas.get(i)].run();
 				ejecutorHilos.execute(jugadoresSimulados[i]/*jugadoresParaApostarMas.get(i)]*/);
 			}
 		}
 		ejecutorHilos.shutdown();
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
 		boolean iguales = true;
 		for(int jugadorIndex = 0; jugadorIndex < apuestasJugadores.size(); jugadorIndex++) {
 			//Si el jugador está retirado no se toma en cuenta
 			if(!jugadoresRetirados[jugadorIndex]) {
 				System.out.println("El jugador " + jugadorIndex + " con apuesta " + apuestasJugadores.get(jugadorIndex) + " y apuesta max es " + Collections.max(apuestasJugadores));
 				if(!apuestasJugadores.get(jugadorIndex).equals(Collections.max(apuestasJugadores))/* && jugadorIndex != 4*/) {
 	 				//Se añade el índice (número de jugador) de la apuesta en apuestasJugadores que es diferente
 	 				jugadoresParaApostarMas.add(jugadorIndex);		
 	 				iguales = false;
 	 			}
 	 		}		
 		}	
 		System.out.println("jugadoresParaApostarMas adquiere size de " + jugadoresParaApostarMas.size());
 		//Si cantidadJugadores nunca aumentó, todas las apuestas son iguales
 		if(iguales /*&& (apuestasJugadores.get(4) == Collections.max(apuestasJugadores))*/) {
 			System.out.println("Las apuestas están iguales");
 		
 		} else {
 			System.out.println("Las apuestas NO están iguales");
 		}
 		return iguales;
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
