/* Autores: Jose David Barona Hernández - 1727590
 *                  Andrés Felipe Rincón    - 1922840
 * Correos: jose.david.barona@correounivalle.edu.co 
 *             andres.rincon.lopez@correounivalle.edu.co
 * Mini proyecto 3: Poker
 * Fecha: 25/11/2020
 * 
 * */
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

// TODO: Auto-generated Javadoc
/**
 * The Class ControlPoker.
 */
public class ControlPoker {
	
	/** The Constant NUMERO_CARTAS_MANO. */
	public static final int NUMERO_CARTAS_MANO = 5;
	
	/** The Constant TOTAL_JUGADORES. */
	public static final int TOTAL_JUGADORES = 5;
	
	/** The Constant NOMBRE_JUGADORES. */
	public static final String[] NOMBRE_JUGADORES = { "Camila", "Laura", "Fernando","Andrés"};
	private JugadorSimulado jugador1, jugador2, jugador3, jugador4;
	private JugadorSimulado[] jugadoresSimulados = new JugadorSimulado[TOTAL_JUGADORES - 1];
	// Vista GUI
	private VistaGUIPoker vistaPoker;
	
	/** The baraja. */
	// Elementos del juego
	private Baraja baraja;
	private Crupier crupier;
	private List<Integer> jugadoresADesempatar = new ArrayList<Integer>();//Serán agregados las posiciones de los jugadores que estén empatados por la victoria
	private List<List<Carta>> manosJugadores;
	private List<Integer> dineroJugadores;
	private List<Integer> apuestasJugadores;
	private List<Integer> valoresAComparar;
	private List<Integer> jugadoresParaApostarMas; // Lista de posiciones de jugadores
	private ArrayList<Integer> puntajesFinales; // Aquí se guardan los puntajes obtenidos por cada jugador para
												// determinar un ganador
	private ArrayList<Integer> valoresJugadas; //Lista que almacena los valores de las cartas máximas del valor de las jugadas que tengan en su mano de cartas
	private int apuestaInicial = 500;
	private int dineroInicial = 10000;
	private boolean humanoRetirado = false;
	private int contadorTurnos = 0;
	private int posicionJugador = 0;
	private boolean variablePrueba = true;
	private boolean[] jugadoresRetiradosAuxiliar = new boolean[TOTAL_JUGADORES];
	private int apuestaTotal=2500;
	private boolean reiniciado=false;
	
	// private boolean modoIgualacion = false;
	/*
	 * Ronda 0: ronda de apuestas 1: ronda de igualación 2: ronda de descarte 3:
	 * ronda de apuestas 2 4: ronda de definición 3: segunda ronda de apuestas 4:
	 * segunda de igualación 5: ronda de definición
	 */
	private int ronda = 0;
	private int jugadorManoAleatorio;
	// variables para el manejo de hilos
	private int turno; // variable de control de turno hilos, 1-5
	private int[] descarte = new int[TOTAL_JUGADORES];
	private Lock bloqueo = new ReentrantLock(); // manejo de sincronizacion
	private Condition esperarTurno = bloqueo.newCondition(); // manejo de sincronizacion
	private Condition esperarIgualacion = bloqueo.newCondition();
	private Condition esperarDescarte = bloqueo.newCondition();
	private ExecutorService ejecutorHilos = Executors.newCachedThreadPool(); // PROBAR OTRO
	private Random random;

	/**
	 * Instantiates a new control poker.
	 */
	public ControlPoker() {
		manosJugadores = new ArrayList<List<Carta>>();
		apuestasJugadores = new ArrayList<Integer>();
		jugadoresParaApostarMas = new ArrayList<Integer>();
		puntajesFinales = new ArrayList<Integer>();
		valoresJugadas = new ArrayList<Integer>();
		valoresAComparar = new ArrayList<Integer>();
		dineroJugadores = new ArrayList<Integer>();
		iniciarJuego();

		vistaPoker = new VistaGUIPoker(NOMBRE_JUGADORES, manosJugadores, apuestasJugadores, this);

	}	
	/**
	 * Método encargado de iniciar el juego
	 */
	private void iniciarJuego() {
		// TODO Auto-generated method stub
		colocarApuestaInicial();
		repartirCartas();
		escogerJugadorMano();
		// Inician los hilos
		iniciarJugadoresSimulados();
		
	}

	/**
	 * Reinicia el juego si el usuario así lo desea
	 */
	public void reiniciarJuego() {
		// Quita los retirados
		humanoRetirado = false;
		for (JugadorSimulado jugador : jugadoresSimulados) {
			jugador.setRetirado(false);
		}
		// Coloca condiciones iniciales
		manosJugadores.clear();
		apuestasJugadores.clear();
		jugadoresParaApostarMas.clear();
		contadorTurnos=0;
		posicionJugador=0;
		contadorIgualacion=0;
		contadorDescarte=0;
		apuestaTotal=2500;
		puntajesFinales.clear();
		valoresAComparar.clear();
		jugadoresADesempatar.clear();
		valoresJugadas.clear();
		humanoRetirado = false;

		colocarApuestaInicial();
		repartirCartas();
		escogerJugadorMano();
		activarRonda0();
		
		//Reestablecer los valores predeterminados de cada hilo 
		jugador1.reiniciarHilo();
		jugador2.reiniciarHilo();
		jugador3.reiniciarHilo();
		jugador4.reiniciarHilo();
		reiniciado = false;
	}
	
	/**
	 * Actualizar apuesta en juego.
	 * Método encargado de totalizar el valor de la apuesta en juego
	 * @return the int. Apuesta total en juego
	 */
	public int actualizarApuestaEnJuego() {
		apuestaTotal = 0;
		for(Integer integer : apuestasJugadores) {
			apuestaTotal += integer;
		}
		return apuestaTotal;
	}
	/**
	 * repartir Cartas.
	 * Reparte las cartas al inicio del juego
	 */ 
	private void repartirCartas() {
		baraja = new Baraja();
		for (int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			manosJugadores.add(seleccionarCartas());
		}
	}
	/**
	 * inicializar Dinero.
	 * Todos los jugadores inician con la misma cantidad de dinero (sin apostar)
	 */ 
	private void inicializarDinero() {
		for (int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			dineroJugadores.add(dineroInicial);
		}
	}
	/**
	 *  Colocar Apuesta Inicial.
	 *  Todos inician con la misma apuesta
	 */ 
	private void colocarApuestaInicial() {
		//Llenar el dinero de los jugadores
		inicializarDinero();
		//Todos apuestan lo mismo
		for (int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			apuestasJugadores.add(apuestaInicial);
			dineroJugadores.set(jugador, dineroJugadores.get(jugador) - apuestaInicial);

		}
	}
	/**
	 *  Seleccionar Cartas
	 *  Método que se encarga de seleccionar 5 cartas del mazo y pasarselas al jugador
	 *  @return ArrayList. Retorna la mano del jugador
	 */ 
	private ArrayList<Carta> seleccionarCartas() {
		// TODO Auto-generated method stub
		ArrayList<Carta> manoJugador = new ArrayList<Carta>();
		// se dan 5 cartas al jugador
		for (int carta = 0; carta < 5; carta++) {
			manoJugador.add(baraja.getCarta());
		}
		return manoJugador;
	}
	/**
	 * Escoger Jugador Mano
	 *  Escoge al azar al jugador mano (inicial) escogiendo el turno
	 */ 
	private void escogerJugadorMano() {
		random = new Random();
		jugadorManoAleatorio = random.nextInt(TOTAL_JUGADORES) + 1;
		turno = jugadorManoAleatorio;
		editarRegistros(0, "", -1, -1);

		// Decirle al jugador lo que debe hacer si es el jugador mano
		if (turno == 5) {
			editarRegistros(2, "", -1, -1);
		}
	}
	/**
	 *  Iniciar jugadores simulados
	 *  Inicia los hilos de los jugadores simulados
	 */ 
	private void iniciarJugadoresSimulados() {
		// Jugadores simulados
		jugador1 = new JugadorSimulado(NOMBRE_JUGADORES[0], 1, this);
		jugador2 = new JugadorSimulado(NOMBRE_JUGADORES[1], 2, this);
		jugador3 = new JugadorSimulado(NOMBRE_JUGADORES[2], 3, this);
		jugador4 = new JugadorSimulado(NOMBRE_JUGADORES[3], 4, this);
		jugadoresSimulados[0] = jugador1;
		jugadoresSimulados[1] = jugador2;
		jugadoresSimulados[2] = jugador3;
		jugadoresSimulados[3] = jugador4;
		actualizarRetiradosAuxiliar();
		for (JugadorSimulado jugador : jugadoresSimulados) {
			ejecutorHilos.execute(jugador);
		}

		ejecutorHilos.shutdown();
	}

	/** The contador igualacion. */
	int contadorIgualacion = 0;
	
	/** The contador descarte. */
	int contadorDescarte = 0;
	/**
	 * Turnos.
	 *	Método sincronizador de turnos, se encarga de mover el motor del juego, a partir de los turnos del juego
	 * @param idJugador the id jugador
	 * @param nombreJugador the nombre jugador
	 * @param operacion the operacion, acción que va a realizar el jugador
	 * @param jugadorSimulado the jugador simulado
	 * @param estadoJugador the estado jugador, true si el jugador se ha retirado, false si no.
	 */
	public void turnos(int idJugador, String nombreJugador, int operacion, boolean estadoJugador) {
		// Si está en la ronda de apuestas

		boolean[] jugadoresRetirados = { jugador1.getRetirado(), jugador2.getRetirado(), jugador3.getRetirado(),
				jugador4.getRetirado(), humanoRetirado };
		bloqueo.lock();
		try {
			// contadorTurno permite que solo 5 personas jueguen
			if (ronda == 0 && contadorTurnos < TOTAL_JUGADORES) {
				if(turno==5 && reiniciado) {
					aumentarTurno();
					contadorTurnos++;
					esperarTurno.signalAll();
				}
				// Mientras el jugador que entre no sea el que correponda, se duerme
				while (idJugador != turno && !reiniciado) {
					esperarTurno.await();
					// Se vuelve a llamar al método run para que el jugador simulado tome su
					// decisión con las apuestas recientes
				}
				if(estadoJugador && !reiniciado) {
					editarRegistros(1,nombreJugador,0,operacion);
				}

				if (contadorTurnos < TOTAL_JUGADORES && !reiniciado) {


					if (!estadoJugador) {
						int apuesta = calcularApuesta(idJugador, operacion);
						apuestaTotal +=apuesta;
						setApuestasJugadores(idJugador - 1, apuesta);
						editarPanelJugador(idJugador - 1, apuesta);
						editarRegistros(1, nombreJugador, apuesta, operacion);
					}
					aumentarTurno();

				}

				// Turno de usuario y no está repitiendo turno adicional ilegal
				if (turno == 5 && contadorTurnos < TOTAL_JUGADORES && !reiniciado) {
					editarRegistros(2, "", -1, -1);
				}

				contadorTurnos++;
				esperarTurno.signalAll();
				// Revisar si todos los jugadores apostaron
				if (contadorTurnos == TOTAL_JUGADORES && !reiniciado) {
					if(revisarApuestasIguales() && contadorDescarte > TOTAL_JUGADORES && variablePrueba) {
						//Pasamos a la ronda para definir un ganador
						editarRegistros(10,"",-1,-1);//Mensaje: el Crupier determinará el ganador
						variablePrueba = false;
						activarRonda3();

						determinarGanador();
					} else if (revisarApuestasIguales() && contadorDescarte == 0) {
						// PASAMOS A RONDA DE DESCARTE
						editarRegistros(5, "", -1, -1);
						activarRonda2();
					} else {
						// REVISAR QUIENES SON DIFERENTES Y SEGUIR UNA RONDA DE APUESTAS CON ELLOS
						// Comienza ronda igualación
						editarRegistros(3, "", -1, -1);
						// Paso a ronda de igualación
						activarRonda1();
				
						// Mostrar en registro que le toca al usuario
						if (turno == 5 && contadorIgualacion < jugadoresParaApostarMas.size()) {
							// Avisar que puede igualar o retirarse
							editarRegistros(6, "", -1, -1);
						}
					}
				}
			}
			// Si estamos en la ronda de igualación de apuestas.
			else if (ronda == 1 && contadorIgualacion < TOTAL_JUGADORES) {
				while (idJugador != turno && !reiniciado) {
					esperarIgualacion.await();
				}
				if (!estadoJugador && !reiniciado) {
					int apuesta = calcularApuesta(idJugador, operacion);
					apuestaTotal +=apuesta;
					setApuestasJugadores(idJugador - 1, apuesta);
					editarPanelJugador(idJugador - 1, apuesta);
					editarRegistros(4, nombreJugador, apuesta, operacion);
				}
				aumentarTurno();
				contadorIgualacion++;
				esperarIgualacion.signalAll();

				// Mostrar en registro que le toca al usuario
				if (turno == 5 && contadorIgualacion < TOTAL_JUGADORES) {
					// Avisar que puede igualar o retirarse
					editarRegistros(6, "", -1, -1);
				}
				// Si todos los que debían igualar, ya igualaron
				if (contadorIgualacion == TOTAL_JUGADORES && !reiniciado) {
					if (revisarApuestasIguales() && contadorDescarte > TOTAL_JUGADORES && variablePrueba) {
						// Pasamos a la ronda para definir un ganador
						editarRegistros(10, "", -1, -1);// Mensaje: el Crupier determinará el ganador
						variablePrueba = false;
						activarRonda3();
						determinarGanador();
					} else if (revisarApuestasIguales() && contadorDescarte == 0) {
						// PASAMOS A RONDA DE DESCARTE
						editarRegistros(5, "", -1, -1);
						// Mensaje al usuario indicándole que le toca descartar
						if (turno == 5) {
							editarRegistros(8, "", -1, -1);
						}
						activarRonda2();
					} else {
						JOptionPane.showMessageDialog(null,
								"ERROR: Las apuestas deberían estar iguales y no lo están. Reinicie");
					}
				}
			}
			// Ronda Descartes
			else if (ronda == 2 && contadorDescarte < TOTAL_JUGADORES && !reiniciado) {
				while (idJugador != turno) {
					esperarDescarte.await();
				}

				if (!estadoJugador) {
					descarte[idJugador - 1] = operacion; // operación = cartas pedidas
					editarRegistros(7, nombreJugador, -1, operacion);
				}
				contadorDescarte++;
				aumentarTurno();
				esperarDescarte.signalAll();

				// Mensaje al usuario indicándole que le toca descartar
				if (turno == 5 && contadorDescarte < TOTAL_JUGADORES) {
					editarRegistros(8, "", -1, -1);
				}
				if (contadorDescarte == TOTAL_JUGADORES) {
					actualizarRetiradosAuxiliar();
					darCartas();
					// Regresar a turno 0 para una segunda ronda de apuestas
					turno = jugadorManoAleatorio;
					contadorTurnos = 0;
					activarRonda0();
					contadorDescarte++;
					contadorIgualacion = 0;
					posicionJugador = 0;
					editarRegistros(9, nombreJugador, -1, operacion);// Mensaje: Iniciar la segunda ronda de apuestas
					// Mensaje para el usuario
					if (turno == 5) {
						editarRegistros(2, "", -1, -1);
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bloqueo.unlock();
		}
	}
	/**
	 * Dar cartas.
	 * Método que se encarga de repartir las cartas que les hace falta a cada uno de los mazos de los jugadores y actualizar la vista de la mesa con las cartas
	 *	
	 */
	private void darCartas() {
		// TODO Auto-generated method stub
		// Cartas para los jugadores Simulados
		for (int i = 0; i < TOTAL_JUGADORES - 1; i++) {

			if (descarte[i] == 5) {

				manosJugadores.get(i).clear(); // Borra el mazo

			} else if (descarte[i] == 4) {

				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);

			} else if (descarte[i] == 3) {

				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);

			} else if (descarte[i] == 2) {
	
				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);

			} else if (descarte[i] == 1) {
	
				manosJugadores.get(i).remove(0);
				;
			}
			asignarCartas(manosJugadores.get(i));
		}

		// Cartas para el jugador humano
		asignarCartas(manosJugadores.get(4));
		//Actualizar la vista Poker
		vistaPoker.actualizarVistaPoker(manosJugadores, -1,ronda);

	}
	/**
	 * Actualizar retirados auxiliar.
	 * Método encargado de actualizar la lista de los jugadores que están retirados
	 */
	private void actualizarRetiradosAuxiliar() {
		for (int i = 0; i < jugadoresSimulados.length; i++) {
			jugadoresRetiradosAuxiliar[i] = jugadoresSimulados[i].getRetirado();
		}
		jugadoresRetiradosAuxiliar[4] = humanoRetirado;
	}
	/**
	 * Determinar ganador
	 * Funcion que determina el ganador del juego
	 */
	private void determinarGanador() {
		// TODO Auto-generated method stub
		crupier = new Crupier();
		int posicionGanador=0;
		int mayorPuntaje=0;
		int puntaje=0;
		
		for(int i=0;i<TOTAL_JUGADORES;i++) {
			
			puntaje = crupier.ejecutar(manosJugadores.get(i));
			puntajesFinales.add(puntaje);
			//guardar los valores de las cartas que le dieron el juego
			valoresJugadas.add(crupier.getValorMaxJugada());
		}
		
		//Determina si hay que definir el ganador por puntaje de la carta más alta
		boolean decisionPorCartaMasAlta = true;
		for(int i=0;i<puntajesFinales.size();i++) {
			if(puntajesFinales.get(i)>=10) {
				decisionPorCartaMasAlta = false;
				break;
			}
		}
		//Analizar el ganador por puntaje mínimo no por carta más alta
		if(!decisionPorCartaMasAlta) {
			 mayorPuntaje = Collections.min(puntajesFinales);
			 if(Collections.frequency(puntajesFinales, mayorPuntaje) == 1) {
				 posicionGanador = puntajesFinales.indexOf(Collections.min(puntajesFinales));
			 }
			 else {
				 	//puntajesFinales -> Tiene los valores de las jugadas de cada uno
				 	//valoresJugadas ->Tiene los valores máximos de las jugadas (para desempate)
				 	//jugadoresADesempatar ->Guarda los index de los jugadores que deben desempatar
					
					for(int i =0;i<puntajesFinales.size();i++) {
						if(puntajesFinales.get(i)==mayorPuntaje) {
							jugadoresADesempatar.add(i);
						}
					}
					for(int i=0;i<jugadoresADesempatar.size();i++) {
						
						valoresAComparar.add(valoresJugadas.get(jugadoresADesempatar.get(i)));
					}
					posicionGanador = valoresJugadas.indexOf(Collections.max(valoresAComparar));
					mayorPuntaje = puntajesFinales.get(posicionGanador);
			 }
		}
		
		//Analizar quien tenga mejor juego
		else if(decisionPorCartaMasAlta) {
			puntajesFinales.clear();
			
			for(int i=0;i<TOTAL_JUGADORES;i++) {
				//Obtener los valores numéricos de los mazos de la posición i
				ArrayList<Integer> valoresCartasJugadores = new ArrayList<Integer>();
				for (Carta carta : manosJugadores.get(i)) {
					valoresCartasJugadores.add(carta.getValorNumerico());
				}
				//agregar a la lista  de puntajesFinales los valores máximos de cada mazo de cada jugador
				for(int j=0;j<TOTAL_JUGADORES;j++) {
					puntaje = Collections.max(valoresCartasJugadores);
					puntajesFinales.add(puntaje);
				}
			}
			
			mayorPuntaje = Collections.max(puntajesFinales);
			posicionGanador = puntajesFinales.indexOf(mayorPuntaje);
		}
		
		//Editar registro
		if(decisionPorCartaMasAlta) {
			editarRegistros(11,NOMBRE_JUGADORES[posicionGanador],-1,mayorPuntaje);
			vistaPoker.actualizarVistaPoker(manosJugadores, posicionGanador,ronda);
		}else if(posicionGanador == 4) {
			editarRegistros(11,"USUARIO",posicionGanador+1,mayorPuntaje);
			vistaPoker.actualizarVistaPoker(manosJugadores, posicionGanador,ronda);
		}else {
			editarRegistros(11,NOMBRE_JUGADORES[posicionGanador],posicionGanador+1,mayorPuntaje);
			vistaPoker.actualizarVistaPoker(manosJugadores, posicionGanador,ronda);
		}
	}
	/**
	 * Asignar Cartas
	 * Calcula cuántas cartas debe darle a cada jugador luego del descarte
	 * @param manoJugador
	 */
	private void asignarCartas(List<Carta> manoJugador) {
		// TODO Auto-generated method stub
		// Si le faltan cartad
		if (manoJugador.size() < 5) {
			int numeroCartas = ControlPoker.NUMERO_CARTAS_MANO - manoJugador.size(); // Número de cartas que debe pedir
			for (int i = 0; i < numeroCartas; i++) {
				manoJugador.add(baraja.getCarta());
			}
		}
	}
	/**
	 * Calcular apuesta
	 * Calcula cuántas cartas debe darle a cada jugador luego del descarte
	 * @param idJugador
	 * @param operacion
	 */
	private int calcularApuesta(int idJugador, int operacion) {
		// igualar
		if (operacion == 0) {
				return getMaximaApuesta();	
		}
		// aumentar
		else if (operacion == 1) {
		
				return getMaximaApuesta() + 500;
		}
		// retirarse
		else if (operacion == 2) {
			return apuestasJugadores.get(idJugador - 1);
		}
		// Error
		return -1;
	}
	/**
	 *  Editar registros.
	 *  Método que sincroniza los cambios en componente gráficos con el hilo
	 *  manejador de eventos
	 * @param fase the fase
	 * @param nombre the nombre
	 * @param apuesta the apuesta
	 * @param operacion the operacion
	 */
	public void editarRegistros(int fase, String nombre, int apuesta, int operacion) {
		// Sincronizar con hilo manejador de eventos
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				vistaPoker.editarRegistros(fase, nombre, apuesta, operacion);
			}
		});
	}
	/**
	 *  Editar panel del jugador.
	 *  Método encargado de enviar la orden de actualizar el panel del jugador cuando decide aumentar apuesta
	 *  manejador de eventos
	 *  @param jugador
	 *  @param apuesta
	 */
	private void editarPanelJugador(int jugador, int apuesta) {
		// Sincronizar con hilo manejador de eventos
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				vistaPoker.editarPanelJugador(jugador, apuesta);
			}
		});
	}
	/**
	 *  Revisar apuestas iguales.
	 *  Revisar que todos los jugadores tengan las mismas apuestas. Retorna true si
	 *	todas son iguales, false en caso contrario.
	 */
	private boolean revisarApuestasIguales() {
		jugadoresParaApostarMas.clear();
		boolean[] jugadoresRetirados = { jugador1.getRetirado(), jugador2.getRetirado(), jugador3.getRetirado(),
				jugador4.getRetirado(), humanoRetirado };
		boolean iguales = true;
		for (int jugadorIndex = 0; jugadorIndex < apuestasJugadores.size(); jugadorIndex++) {
			// Si el jugador está retirado no se toma en cuenta
			if (!jugadoresRetirados[jugadorIndex]) {
				if (!apuestasJugadores.get(jugadorIndex).equals(Collections.max(apuestasJugadores))) {
					// Se añade el índice (número de jugador) de la apuesta en apuestasJugadores que es diferente
					jugadoresParaApostarMas.add(jugadorIndex);
					iguales = false;
				}
			}
		}
		// Si cantidadJugadores nunca aumentó, todas las apuestas son iguales
		return iguales;
	}
	/**
	 *  Aumentar turno
	 *  Aumenta el turno, si el turno actual es 5, el siguiente será el turno 1
	 */
	private void aumentarTurno() {
		// Si turno es 4 o múltiplo de 4, se convierte en 5. Si turno tiene otro valor,
		// aumenta en 1 pero sin sobrepasar al 5.
		turno = (turno % 4 != 0) ? (turno + 1) % 5 : 5;

	}
	/**
	 * Descarte jugador humano.
	 *	Cambia la mano del jugador humano
	 * @param manoJugadorHumano the mano jugador humano
	 */ 
	public void descarteJugadorHumano(List<Carta> manoJugadorHumano) {
		manosJugadores.remove(4);
		manosJugadores.add(manoJugadorHumano); // se agrega la mano nueva

	}
	/**
	 *  Set Apuestas Jugadores.
	 *	Establece las apuestas de los jugadores
	 * @param indexJugador
	 * @param apuesta
	 */ 
	private void setApuestasJugadores(int indexJugador, int apuesta) {
		apuestasJugadores.set(indexJugador, apuesta);
		dineroJugadores.set(indexJugador, dineroJugadores.get(indexJugador) - apuesta);
	}
	
	/**
	 * Gets the dinero jugadores.
	 *
	 * @return the dinero jugadores
	 */
	public List<Integer> getDineroJugadores() {
		return dineroJugadores;
	}
	
	/**
	 * Gets the apuestas jugadores.
	 * retorna una lista con las apuestas de todos los jugadores
	 * @return the apuestas jugadores
	 */
	public List<Integer> getApuestasJugadores() {
		return apuestasJugadores;
	}
	/**
	 * Gets the manos jugadores.
	 * retorna una lista de listas con las manos de todos los jugadores
	 * @return the manos jugadores
	 */
	public List<List<Carta>> getManosJugadores() {
		return manosJugadores;
	}
	/**
	 * Gets the maxima apuesta.
	 *
	 * @return the maxima apuesta. retorna la máxima apuesta actual
	 */
	//
	public int getMaximaApuesta() {
		return Collections.max(apuestasJugadores);
	}
	/**
	 * Gets the id jugador mano.
	 *
	 * @return the id jugador mano. Retorna el ID del jugador mano
	 */
	public int getIdJugadorMano() {
		return jugadorManoAleatorio;
	}
	/**
	 * Sets the ronda.
	 * Establece la ronda actual
	 * @param ronda the new ronda
	 */
	public void setRonda(int ronda) {
		this.ronda = ronda;
	}
	/**
	 * Gets the ronda.
	 * Retorna la ronda actual
	 * @return the ronda
	 */
	public int getRonda() {
		return ronda;
	}
	/**
	 * Gets the turno.
	 * Retorna el turno actual
	 * @return the turno
	 */
	public int getTurno() {
		return turno;
	}
	/**
	 * Checks if is humano retirado.
	 * Retorna el turno actual
	 * @return true, if is humano retirado
	 */
	public boolean isHumanoRetirado() {
		return humanoRetirado;
	}
	/**
	 * Sets the humano retirado.
	 * Método encargado de cambiar el estado activo del jugador humano
	 * @param humanoRetirado the new humano retirado
	 */
	public void setHumanoRetirado(boolean humanoRetirado) {
		this.humanoRetirado = humanoRetirado;
	}
	/**
	 * Activar ronda 0.
	 * Activa la ronda 0 de apuestas
	 */
	public void activarRonda0() {
		jugador1.activarRonda0();
		jugador2.activarRonda0();
		jugador3.activarRonda0();
		jugador4.activarRonda0();
		ronda=0;
	}
	
	/**
	 * Activar ronda 1.
	 * Activa la ronda 1 de igualacion
	 */
	public void activarRonda1() {
		jugador1.activarRonda1();
		jugador2.activarRonda1();
		jugador3.activarRonda1();
		jugador4.activarRonda1();
		ronda=1;
	}
	
	/**
	 * Activar ronda 2.
	 * Activa la ronda 2 de Descartes
	 */
	public void activarRonda2() {

		jugador1.activarRonda2();
		jugador2.activarRonda2();
		jugador3.activarRonda2();
		jugador4.activarRonda2();
		ronda=2;
	}
	
	/**
	 * Activar ronda 3.
	 * Activa la ronda 2 de determinar Ganador
	 */
	public void activarRonda3() {
		jugador1.activarRonda3();
		jugador2.activarRonda3();
		jugador3.activarRonda3();
		jugador4.activarRonda3();
		ronda=3;
	}
	
	/**
	 * Sets the reinicio.
	 */
	public void setReinicio() {	
		reiniciado=true;
	}

	/**
	 * The main method.
	 *MAIN - hilo principal
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				new ControlPoker();
			}
		});
	}
}
