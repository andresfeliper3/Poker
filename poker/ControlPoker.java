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
	public static final String[] NOMBRE_JUGADORES = { "Duque", "Uribe", "Petrosky", "Kaku" };
	private JugadorSimulado jugador1, jugador2, jugador3, jugador4;
	private JugadorSimulado[] jugadoresSimulados = new JugadorSimulado[TOTAL_JUGADORES - 1];
	// Vista GUI
	private VistaGUIPoker vistaPoker;
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
	private int dineroInicial = 1000;
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
	//Método encargado de iniciar el juego
	private void iniciarJuego() {
		// TODO Auto-generated method stub
		colocarApuestaInicial();
		repartirCartas();
		escogerJugadorMano();
		// Inician los hilos
		iniciarJugadoresSimulados();
		
	}

	// Reinicia el juego si el usuario así lo desea
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
		System.out.println("ANTES DE ACTIVAR LA RONDA 0 EN EL REINICIO");
		colocarApuestaInicial();
		repartirCartas();
		escogerJugadorMano();
		activarRonda0();
		//vistaPoker.actualizarVistaPoker(manosJugadores, -1, 2);
		System.out.println("REINICIANDO TODOS LOS HILOS Y JUGADOR MANO ES:" + turno);
		//Reestablecer los valores predeterminados de cada hilo 
		jugador1.reiniciarHilo();
		jugador2.reiniciarHilo();
		jugador3.reiniciarHilo();
		jugador4.reiniciarHilo();
		reiniciado = false;
	}
	//Método encargado de totalizar el valor de la apuesta en juego
	public int actualizarApuestaEnJuego() {
		
		return apuestaTotal;
	}

	// Reparte las cartas al inicio del juego
	private void repartirCartas() {
		baraja = new Baraja();
		for (int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			manosJugadores.add(seleccionarCartas());
		}
	}
	//Todos los jugadores inician con la misma cantidad de dinero (sin apostar)
	private void inicializarDinero() {
		for (int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			dineroJugadores.add(dineroInicial);
		}
	}

	// Todos inician con la misma apuesta
	private void colocarApuestaInicial() {
		//Llenar el dinero de los jugadores
		inicializarDinero();
		//Todos apuestan lo mismo
		for (int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			apuestasJugadores.add(apuestaInicial);
			dineroJugadores.set(jugador, dineroJugadores.get(jugador) - apuestaInicial);
			System.out.println("Jugador " + jugador + " queda con dinero " + dineroJugadores.get(jugador));
		}
		System.out.println("Apuesta inicial size: " + apuestasJugadores.size());
	}
	//Método que se encarga de seleccionar 5 cartas del mazo y pasarselas al jugador
	private ArrayList<Carta> seleccionarCartas() {
		// TODO Auto-generated method stub
		ArrayList<Carta> manoJugador = new ArrayList<Carta>();
		// se dan 5 cartas al jugador
		for (int carta = 0; carta < 5; carta++) {
			manoJugador.add(baraja.getCarta());
		}
		return manoJugador;
	}

	// Escoge al azar al jugador mano (inicial) escogiendo el turno
	private void escogerJugadorMano() {
		random = new Random();
		jugadorManoAleatorio = 5;//random.nextInt(TOTAL_JUGADORES) + 1;
		turno = jugadorManoAleatorio;
		editarRegistros(0, "", -1, -1);

		// Decirle al jugador lo que debe hacer si es el jugador mano
		if (turno == 5) {
			editarRegistros(2, "", -1, -1);
		}
	}

	// Inicia los hilos de los jugadores simulados
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

	int contadorIgualacion = 0;
	int contadorDescarte = 0;

	// Método sincronizador de turnos, se encarga de mover el motor del juego, a partir de los turnos del juego
	public void turnos(int idJugador, String nombreJugador, int operacion, JugadorSimulado jugadorSimulado,boolean estadoJugador) {
		// Si está en la ronda de apuestas
		System.out.println("TURNOS");
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
					System.out.println(
							"En apuestas jugador " + nombreJugador + " intenta entrar y es mandado a esperar turno en la ronda de apuestas");
					esperarTurno.await();
					System.out.println("ES EL TURNO: " + turno + ", "+nombreJugador+"DESPERTÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓ EN LA RONDA DE APUESTAS");
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
						System.out.println("Apuestas: Este es el idJugador " + idJugador);
						setApuestasJugadores(idJugador - 1, apuesta);
						editarPanelJugador(idJugador - 1, apuesta);
						editarRegistros(1, nombreJugador, apuesta, operacion);
						System.out.println("Jugador " + nombreJugador + " apostó " + apuestasJugadores.get(idJugador - 1)
						+ " en total.");
					}

					System.out.println("Turno: " + turno);
					aumentarTurno();

				}

				// Turno de usuario y no está repitiendo turno adicional ilegal
				if (turno == 5 && contadorTurnos < TOTAL_JUGADORES && !reiniciado) {
					// humanoApuesta();
					editarRegistros(2, "", -1, -1);
				}

				contadorTurnos++;
				System.out.println("ContadorTurnos aumentó a " + contadorTurnos);
				esperarTurno.signalAll();
				System.out
						.println("Contador de turnos es " + contadorTurnos + " y totaljugadores es" + TOTAL_JUGADORES);
				// Revisar si todos los jugadores apostaron
				if (contadorTurnos == TOTAL_JUGADORES && !reiniciado) {
					if(revisarApuestasIguales() && contadorDescarte > TOTAL_JUGADORES && variablePrueba) {
						//Pasamos a la ronda para definir un ganador
						editarRegistros(10,"",-1,-1);//Mensaje: el Crupier determinará el ganador
						variablePrueba = false;
						activarRonda3();
						//ronda = 3;

						determinarGanador();
					} else if (revisarApuestasIguales() && contadorDescarte == 0) {
						// PASAMOS A RONDA DE DESCARTE
						System.out.println("pasamos a ronda de descarte");
						editarRegistros(5, "", -1, -1);
						activarRonda2();
						//ronda = 2;
					} else {
						// REVISAR QUIENES SON DIFERENTES Y SEGUIR UNA RONDA DE APUESTAS CON ELLOS
						// Comienza ronda igualación
						System.out.println("REVISAR EDITAR: idJugador es " + idJugador);
						editarRegistros(3, "", -1, -1);
						// Paso a ronda de igualación
						activarRonda1();
						//ronda = 1;
						// aumentarTurnosRondaIgualacion();
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
				System.out.println("Igualacion, Turno es " + turno);
				System.out.println("igualacion, IdJugador es " + idJugador);

				while (idJugador != turno && !reiniciado) {
					System.out.println("En igualación " + nombreJugador + " intenta entrar pero se va a dormir en la ronda de igualación");
					System.out.println("IdJugador " + idJugador + ", turno " + turno);
					esperarIgualacion.await();
					System.out.println("ES EL TURNO: " + turno + ", "+nombreJugador+"DESPERTÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓ EN LA RONDA DE IGUALACIÓN");
				}
				if (!estadoJugador && !reiniciado) {
					int apuesta = calcularApuesta(idJugador, operacion);
					apuestaTotal +=apuesta;
					setApuestasJugadores(idJugador - 1, apuesta);
					editarPanelJugador(idJugador - 1, apuesta);
					editarRegistros(4, nombreJugador, apuesta, operacion);

					System.out.println("En igualacion, el jugador " + nombreJugador + " realiza una apuesta total de "
							+ apuesta + " y debería ser de " + Collections.max(apuestasJugadores));
				}
				System.out.println("En igualacion el jugador " + nombreJugador + " pasa de largo");
				aumentarTurno();
				contadorIgualacion++;
				System.out.println("contadorIgualacion llega a " + contadorIgualacion);
				esperarIgualacion.signalAll();

				// Mostrar en registro que le toca al usuario
				if (turno == 5 && contadorIgualacion < TOTAL_JUGADORES) {
					// Avisar que puede igualar o retirarse
					editarRegistros(6, "", -1, -1);
				}
				// Si todos los que debían igualar, ya igualaron
				if (contadorIgualacion == TOTAL_JUGADORES && !reiniciado) {
					System.out.println("Contador igualacion " + contadorIgualacion + " y jugadoresParaApostarMas "
							+ jugadoresParaApostarMas.size());
					if (revisarApuestasIguales() && contadorDescarte > TOTAL_JUGADORES && variablePrueba) {
						// Pasamos a la ronda para definir un ganador
						editarRegistros(10, "", -1, -1);// Mensaje: el Crupier determinará el ganador
						variablePrueba = false;
						activarRonda3();
						//ronda = 3;
						determinarGanador();
					} else if (revisarApuestasIguales() && contadorDescarte == 0) {
						// PASAMOS A RONDA DE DESCARTE
						editarRegistros(5, "", -1, -1);
						// turno = jugadorManoAleatorio;
						// Mensaje al usuario indicándole que le toca descartar
						if (turno == 5) {
							System.out.println("le toca a YOLAS DESCARTAR ");
							editarRegistros(8, "", -1, -1);
						}
						activarRonda2();
						//ronda = 2;
						System.out.println("La ronda ahora es " + ronda + " y el turno es para " + turno);
					} else {
						JOptionPane.showMessageDialog(null,
								"ERROR: Las apuestas deberían estar iguales y no lo están. Reinicie");
					}
				}
			}
			// Ronda Descartes
			else if (ronda == 2 && contadorDescarte < TOTAL_JUGADORES && !reiniciado) {
				System.out.println("Ronda de descartes");
				System.out.println("Ronda 2: idJugador " + idJugador + " y turno " + turno);

				while (idJugador != turno) {
					System.out
							.println("En descarte jugador " + nombreJugador + " intenta entrar y es mandado a MIMIRR en la ronda de Descartes");
					esperarDescarte.await();
					System.out.println("ES EL TURNO: " + turno + ", "+nombreJugador+"DESPERTÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓÓ EN LA RONDA DE DESCARTESSSSSSSS");
				}

				if (!estadoJugador) {
					System.out.println(
							"Soy " + nombreJugador + " voy a descartar " + operacion + ", es el turno: " + turno);
					descarte[idJugador - 1] = operacion; // operación = cartas pedidas

					editarRegistros(7, nombreJugador, -1, operacion);

				}
				contadorDescarte++;
				System.out.println("contadorDescarte aumentó a " + contadorDescarte);
				aumentarTurno();
				System.out.println("En descarte, turno aumenta a " + turno);
				esperarDescarte.signalAll();

				// Mensaje al usuario indicándole que le toca descartar
				if (turno == 5 && contadorDescarte < TOTAL_JUGADORES) {
					System.out.println("le toca a YOLAS DESCARTAR ");
					editarRegistros(8, "", -1, -1);
				}
				if (contadorDescarte == TOTAL_JUGADORES) {
					System.out.println("Próximo a ejecutar darCartas");
					actualizarRetiradosAuxiliar();
					darCartas();
					// Regresar a turno 0 para una segunda ronda de apuestas
					turno = jugadorManoAleatorio;
					contadorTurnos = 0;
					activarRonda0();
					//ronda = 0;
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
			System.out.println("BLOQUEO UNLOCK");
			bloqueo.unlock();
		}
	}
	//Método que se encarga de repartir las cartas que les hace falta a cada uno de los mazos de los jugadores
	private void darCartas() {
		// TODO Auto-generated method stub
		// Cartas para los jugadores Simulados
		System.out.println("Entró a darCartas");
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
		
		vistaPoker.actualizarVistaPoker(manosJugadores, -1,ronda);

	}
	//Método encargado de actualizar la lista de los jugadores que están retirados
	private void actualizarRetiradosAuxiliar() {
		for (int i = 0; i < jugadoresSimulados.length; i++) {
			jugadoresRetiradosAuxiliar[i] = jugadoresSimulados[i].getRetirado();
		}
		jugadoresRetiradosAuxiliar[4] = humanoRetirado;
	}

	// Funcion que determina el ganador del juego
	private void determinarGanador() {
		// TODO Auto-generated method stub
		System.out.println("Entró a determinarGanador");
		crupier = new Crupier();
		int posicionGanador=0;
		int mayorPuntaje=0;
		int puntaje=0;
		
		for(int i=0;i<TOTAL_JUGADORES;i++) {
			
			puntaje = crupier.ejecutar(manosJugadores.get(i));
			
			puntajesFinales.add(puntaje);
			//guardar los valores de las cartas que le dieron el juego?
			valoresJugadas.add(crupier.getValorMaxJugada());
			System.out.println("VALORESJUGADAS POSICION "+i+", Valor: "+crupier.getValorMaxJugada());
			System.out.println("PRIMERO, En la posición: "+i+ ", el jugador tiene un puntaje de: " +puntajesFinales.get(i));
		}

		//imprimir en consola los puntajes de los jugadores
		for(int i=0;i<puntajesFinales.size();i++) {
			System.out.println("Puntos del jugador: "+ i +", " + puntajesFinales.get(i) + "\n");
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
							System.out.println("Valor "+i+ ", de jugadoresADesempatar" + jugadoresADesempatar.size());
						}
					}
					System.out.println("tamaño jugadoresADesempatar: " + jugadoresADesempatar.size());
					System.out.println("tamaño puntajesFinales: " + puntajesFinales.size());
					System.out.println("tamaño valoresJugadas: " + valoresJugadas.size());
					
					for( Integer numero : jugadoresADesempatar) {
						System.out.print(", " + numero);
						}
					for(int i=0;i<jugadoresADesempatar.size();i++) {
						System.out.println("JUGADOR DE LA POSICIÓN: " + jugadoresADesempatar.get(i));
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
					System.out.println("SEGUNDO, En la posición: "+i+ ", el jugador tiene un puntaje de: " +puntajesFinales.get(i));
				}
			}
			
			mayorPuntaje = Collections.max(puntajesFinales);
			posicionGanador = puntajesFinales.indexOf(mayorPuntaje);
		}
		
		//Editar registro
		if(decisionPorCartaMasAlta) {
			System.out.println("Ganador por carta más alta");
			editarRegistros(11,NOMBRE_JUGADORES[posicionGanador],-1,mayorPuntaje);
			vistaPoker.actualizarVistaPoker(manosJugadores, posicionGanador,ronda);
		}else if(posicionGanador == 4) {
			System.out.println("Ganador usuario");
			editarRegistros(11,"USUARIO",posicionGanador+1,mayorPuntaje);
			vistaPoker.actualizarVistaPoker(manosJugadores, posicionGanador,ronda);
		}else {
			System.out.println("Ganador simulado");
			editarRegistros(11,NOMBRE_JUGADORES[posicionGanador],posicionGanador+1,mayorPuntaje);
			vistaPoker.actualizarVistaPoker(manosJugadores, posicionGanador,ronda);
		}
	}

	// Calcula cuántas cartas debe darle a cada jugador luego del descarte
	private void asignarCartas(List<Carta> manoJugador) {
		// TODO Auto-generated method stub
		// Si le faltan cartad
		if (manoJugador.size() < 5) {
			int numeroCartas = ControlPoker.NUMERO_CARTAS_MANO - manoJugador.size(); // Número de cartas que debe pedir
			System.out.println(" NECESITO " + numeroCartas + "PORQUE SOLO TENGO: " + manoJugador.size());
			for (int i = 0; i < numeroCartas; i++) {
				manoJugador.add(baraja.getCarta());
			}
		}
	}

	// Calcula el valor de la apuesta basándose en la operación dada por el jugador
	// con identificado idJugador
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

	// Función para manejar los turnos en la ronda de igualación, donde solo
	// participan los jugadores que deben igualar o retirarse.
	private void aumentarTurnosRondaIgualacion() {
		// Turno está entre 1 y los jugadores que deben igualar o retirarse
		turno = jugadoresParaApostarMas.get(posicionJugador) + 1;
		if (posicionJugador < jugadoresParaApostarMas.size() - 1) {
			posicionJugador++;
		}
		System.out.println("turno2 está aumentado a " + turno);
	}

	// Método que sincroniza los cambios en componente gráficos con el hilo
	// manejador de eventos
	public void editarRegistros(int fase, String nombre, int apuesta, int operacion) {
		// Sincronizar con hilo manejador de eventos
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("Editar Registros");
				vistaPoker.editarRegistros(fase, nombre, apuesta, operacion);
			}
		});
	}
	//Método encargado de enviar la orden de actualizar el panel del jugador cuando decide aumentar apuesta
	private void editarPanelJugador(int jugador, int apuesta) {
		// Sincronizar con hilo manejador de eventos
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("Editar PanelJugador");
				vistaPoker.editarPanelJugador(jugador, apuesta);
			}
		});
	}
	// Revisar que todos los jugadores tengan las mismas apuestas. Retorna true si
	// todas son iguales, false en caso contrario.
	private boolean revisarApuestasIguales() {
		jugadoresParaApostarMas.clear();
		boolean[] jugadoresRetirados = { jugador1.getRetirado(), jugador2.getRetirado(), jugador3.getRetirado(),
				jugador4.getRetirado(), humanoRetirado };
		boolean iguales = true;
		for (int apuesta : apuestasJugadores) {
			System.out.println("Apuesta en lista " + apuesta);
		}
		for (int jugadorIndex = 0; jugadorIndex < apuestasJugadores.size(); jugadorIndex++) {
			// Si el jugador está retirado no se toma en cuenta
			System.out.println("1. El jugador " + jugadorIndex + " con apuesta " + apuestasJugadores.get(jugadorIndex)
					+ " y apuesta max es " + Collections.max(apuestasJugadores));
			System.out.println("Su retirado es " + jugadoresRetirados[jugadorIndex]);
			if (!jugadoresRetirados[jugadorIndex]) {
				System.out
						.println("2. El jugador " + jugadorIndex + " con apuesta " + apuestasJugadores.get(jugadorIndex)
								+ " y apuesta max es " + Collections.max(apuestasJugadores));
				if (!apuestasJugadores.get(jugadorIndex).equals(Collections.max(apuestasJugadores))) {
					// Se añade el índice (número de jugador) de la apuesta en apuestasJugadores que
					// es diferente
					System.out.println("Se añade a jugadoresParaApostarMas a jugador " + jugadorIndex
							+ ", con apuesta de " + apuestasJugadores.get(jugadorIndex) + " y max "
							+ Collections.max(apuestasJugadores));
					jugadoresParaApostarMas.add(jugadorIndex);
					iguales = false;
				}
			}
		}
		System.out.println("jugadoresParaApostarMas adquiere size de " + jugadoresParaApostarMas.size());
		// Si cantidadJugadores nunca aumentó, todas las apuestas son iguales
		if (iguales) {
			System.out.println("Las apuestas están iguales");

		} else {
			System.out.println("Las apuestas NO están iguales");
		}
		return iguales;
	}
	//Aumenta el turno, si el turno actual es 5, el siguiente será el turno 1
	private void aumentarTurno() {
		// Si turno es 4 o múltiplo de 4, se convierte en 5. Si turno tiene otro valor,
		// aumenta en 1 pero sin sobrepasar al 5.
		turno = (turno % 4 != 0) ? (turno + 1) % 5 : 5;
		System.out.println("Turno aumentó a " + turno);
	}

	// Cambia la mano del jugador humano
	public void descarteJugadorHumano(List<Carta> manoJugadorHumano) {
		manosJugadores.remove(4);
		manosJugadores.add(manoJugadorHumano); // se agrega la mano nueva

	}
	//establece las apuestas de los jugadores
	private void setApuestasJugadores(int indexJugador, int apuesta) {
		apuestasJugadores.set(indexJugador, apuesta);
		dineroJugadores.set(indexJugador, dineroJugadores.get(indexJugador) - apuesta);
	}
	
	public List<Integer> getDineroJugadores() {
		return dineroJugadores;
	}
	//retorna una lista con las apuestas de todos los jugadores
	public List<Integer> getApuestasJugadores() {
		return apuestasJugadores;
	}
	//retorna una lista de listas con las manos de todos los jugadores
	public List<List<Carta>> getManosJugadores() {
		return manosJugadores;
	}
	//retorna la máxima apuesta actual
	public int getMaximaApuesta() {
		return Collections.max(apuestasJugadores);
	}
	//retorna el ID del jugador mano
	public int getIdJugadorMano() {
		return jugadorManoAleatorio;
	}
	//Establece la ronda actual
	public void setRonda(int ronda) {
		this.ronda = ronda;
	}
	//Retorna la ronda actual
	public int getRonda() {
		return ronda;
	}
	//Retorna el turno actual
	public int getTurno() {
		return turno;
	}
	//Retorna false si el jugador humano está retirado
	public boolean isHumanoRetirado() {
		return humanoRetirado;
	}
	//Método encargado de cambiar el estado activo del jugador humano
	public void setHumanoRetirado(boolean humanoRetirado) {
		this.humanoRetirado = humanoRetirado;
	}
	//Activa la ronda 0 de apuestas
	public void activarRonda0() {
		jugador1.activarRonda0();
		jugador2.activarRonda0();
		jugador3.activarRonda0();
		jugador4.activarRonda0();
		System.out.println("Se activa la ronda 0, Ronda de Apuestas");
		ronda=0;
	}
	//Activa la ronda 1 de igualacion
	public void activarRonda1() {
		jugador1.activarRonda1();
		jugador2.activarRonda1();
		jugador3.activarRonda1();
		jugador4.activarRonda1();
		System.out.println("Se activa la ronda 1, Ronda de Igualacion");
		ronda=1;
	}
	//Activa la ronda 2 de Descartes
	public void activarRonda2() {
		System.out.println("Se activa la ronda 2, Ronda de Descartes");
		jugador1.activarRonda2();
		jugador2.activarRonda2();
		jugador3.activarRonda2();
		jugador4.activarRonda2();
		ronda=2;
	}
	//Activa la ronda 2 de determinar Ganador
	public void activarRonda3() {
		System.out.println("Se activa la ronda 3, Ronda para determinar Ganador");
		jugador1.activarRonda3();
		jugador2.activarRonda3();
		jugador3.activarRonda3();
		jugador4.activarRonda3();
		ronda=3;
	}
	public void setReinicio() {
		
		reiniciado=true;
		System.out.println("GAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+reiniciado);
	}

	// MAIN - hilo principal
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
