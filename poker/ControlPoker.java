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
	private List<List<Carta>> manosJugadores;
	private List<Integer> apuestasJugadores;
	private List<Integer> jugadoresParaApostarMas; // Lista de posiciones de jugadores
	private ArrayList<Integer> puntajesFinales; //Aquí se guardan los puntajes obtenidos por cada jugador para determinar un ganador
	private int apuestaInicial = 500;
	private boolean humanoRetirado = false;
	private int contadorTurnos = 0;
	private int posicionJugador = 0;
	//private boolean modoIgualacion = false;
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
		colocarApuestaInicial();
		repartirCartas();
		escogerJugadorMano();
		//Inician los hilos
		iniciarJugadoresSimulados();
		vistaPoker = new VistaGUIPoker(NOMBRE_JUGADORES, manosJugadores, apuestasJugadores, this);

	}
	//Reinicia el juego si el usuario así lo desea
	public void reiniciarJuego() {
		//Quita los retirados
		humanoRetirado = false;
		for(JugadorSimulado jugador : jugadoresSimulados) {
			jugador.setRetirado(false);
		}
		//Coloca condiciones iniciales
		manosJugadores.clear();
		apuestasJugadores.clear();
		jugadoresParaApostarMas.clear();
		colocarApuestaInicial();
		repartirCartas();
		escogerJugadorMano();
		
		
	}

	// Reparte las cartas al inicio del juego
	private void repartirCartas() {
		baraja = new Baraja();
		for (int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			manosJugadores.add(seleccionarCartas());
		}
	}

	// Todos inician con la misma apuesta
	private void colocarApuestaInicial() {
		for (int jugador = 0; jugador < TOTAL_JUGADORES; jugador++) {
			apuestasJugadores.add(apuestaInicial);
		}
		System.out.println("Apuesta inicial size: " + apuestasJugadores.size());
	}

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
		jugadorManoAleatorio = random.nextInt(TOTAL_JUGADORES) + 1;
		turno = jugadorManoAleatorio;
		
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

		for (JugadorSimulado jugador : jugadoresSimulados) {
			ejecutorHilos.execute(jugador);
		}

		ejecutorHilos.shutdown();
	}

	int contadorIgualacion = 0;
	int contadorDescarte = 0;

	// Método sincronizador de turnos
	public void turnos(int idJugador, String nombreJugador, int operacion, JugadorSimulado jugadorSimulado) {
		// Si está en la ronda de apuestas
		boolean[] jugadoresRetirados = { jugador1.getRetirado(), jugador2.getRetirado(), jugador3.getRetirado(),
				jugador4.getRetirado(), humanoRetirado };
		bloqueo.lock();
		try {
			// contadorTurno permite que solo 5 personas jueguen
			if (ronda == 0 && contadorTurnos < TOTAL_JUGADORES) {
				// Mientras el jugador que entre no sea el que correponda, se duerme
				while (idJugador != turno) {
					System.out.println(
							"En apuestas jugador " + nombreJugador + " intenta entrar y es mandado a esperar turno");
					esperarTurno.await();
					// Se vuelve a llamar al método run para que el jugador simulado tome su
					// decisión con las apuestas recientes
				}

				if (contadorTurnos < TOTAL_JUGADORES) {
					int apuesta = calcularApuesta(idJugador, operacion);
					System.out.println("Apuestas: Este es el idJugador " + idJugador);
					setApuestasJugadores(idJugador - 1, apuesta);
					editarPanelJugador(idJugador - 1, apuesta);
					editarRegistros(1, nombreJugador, apuesta, operacion);
					System.out.println("Turno: " + turno);
					System.out.println("Jugador " + nombreJugador + " apostó " + apuestasJugadores.get(idJugador - 1)
							+ " en total.");
					aumentarTurno();

				}
				contadorTurnos++;
				System.out.println("ContadorTurnos aumentó a " + contadorTurnos);
				esperarTurno.signalAll();
				// Turno de usuario y no está repitiendo turno adicional ilegal
				if (turno == 5 && contadorTurnos < TOTAL_JUGADORES) {
					// humanoApuesta();
					editarRegistros(2, "", -1, -1);
				}
				System.out.println(
						"Contador de turnos es " + contadorTurnos + " y totaljugadores es" + TOTAL_JUGADORES);
				// Revisar si todos los jugadores apostaron
				if (contadorTurnos == TOTAL_JUGADORES) {
					if(revisarApuestasIguales() && contadorDescarte > TOTAL_JUGADORES) {
						//Pasamos a la ronda para definir un ganador
						editarRegistros(10,"",-1,-1);//Mensaje: el Crupier determinará el ganador
						ronda = 3;	
						determinarGanador();
					}else if (revisarApuestasIguales()&& contadorDescarte ==0) {
						// PASAMOS A RONDA DE DESCARTE
						System.out.println("pasamos a ronda de descarte");
						editarRegistros(5, "", -1, -1);
						ronda = 2;
					}else {
						// REVISAR QUIENES SON DIFERENTES Y SEGUIR UNA RONDA DE APUESTAS CON ELLOS
						// Comienza ronda igualación
						System.out.println("REVISAR EDITAR: idJugador es " + idJugador);
						editarRegistros(3, "", -1, -1);
						// Paso a ronda de igualación
						ronda = 1;
						aumentarTurnosRondaIgualacion();
					}
				}
			}
			// Si estamos en la ronda de igualación de apuestas.
			else if (ronda == 1 && jugadoresParaApostarMas.contains(idJugador -1)) {
				System.out.println("Igualacion, Turno es " + turno);
				System.out.println("igualacion, IdJugador es " + idJugador);
				while (idJugador != turno) {
					System.out.println("En igualación " + nombreJugador + " intenta entrar pero se va a dormir");
					System.out.println("IdJugador " + idJugador + ", turno " + turno);
					esperarIgualacion.await();
				}
				
					int apuesta = calcularApuesta(idJugador, operacion);
					setApuestasJugadores(idJugador - 1, apuesta);
					editarPanelJugador(idJugador - 1, apuesta);
					editarRegistros(4, nombreJugador, apuesta, operacion);
					aumentarTurnosRondaIgualacion();
					System.out.println("En igualacion, el jugador " + nombreJugador + " realiza una apuesta total de "
							+ apuesta + " y debería ser de " + Collections.max(apuestasJugadores));
					
				contadorIgualacion++;	
				esperarIgualacion.signalAll();
				
				// Mostrar en registro que le toca al usuario
				if (turno == 5 && contadorIgualacion < jugadoresParaApostarMas.size()) {
					// Avisar que puede igualar o retirarse
					editarRegistros(6, "", -1, -1);
				}
				// Si todos los que debían igualar, ya igualaron
				if (contadorIgualacion == jugadoresParaApostarMas.size()) {
					System.out.println("Contador igualacion " + contadorIgualacion + " y jugadoresParaApostarMas "
							+ jugadoresParaApostarMas.size());
					if(revisarApuestasIguales() && contadorDescarte > TOTAL_JUGADORES) {
						//Pasamos a la ronda para definir un ganador
						editarRegistros(10,"",-1,-1);//Mensaje: el Crupier determinará el ganador
						ronda = 3;	
						determinarGanador();
					}else if (revisarApuestasIguales()&& contadorDescarte ==0) {
						// PASAMOS A RONDA DE DESCARTE
						editarRegistros(5, "", -1, -1);
						turno = jugadorManoAleatorio;
						ronda = 2;		
						System.out.println("La ronda ahora es " + ronda + " y el turno es para " + turno);
					}else {
						JOptionPane.showMessageDialog(null,
								"ERROR: Las apuestas deberían estar iguales y no lo están. Reinicie");
					}
				}			
			}
			//Ronda Descartes
			else if(ronda == 2 && contadorDescarte < TOTAL_JUGADORES) {
				System.out.println("Ronda de descartes");
				System.out.println("Ronda 2: idJugador " + idJugador +" y turno " + turno);
				
					while(idJugador != turno) {
						System.out.println("En descarte jugador "+nombreJugador+" intenta entrar y es mandado a MIMIRRR");
						esperarDescarte.await(); 
					} 
					
					if(!jugadoresRetirados[idJugador-1]) {
						System.out.println("Soy "+ nombreJugador +" voy a descartar " +operacion + ", es el turno: " + turno);
						descarte[idJugador - 1] = operacion; //operación = cartas pedidas
	 
						editarRegistros(7, nombreJugador, -1, operacion);

					}	
					//Mensaje al usuario indicándole que le toca descartar
					if(turno == 4 && contadorDescarte < TOTAL_JUGADORES) {
						System.out.println("le toca a YOLAS DESCARTAR ");
						editarRegistros(8, "", -1, -1);
					}
						
				contadorDescarte++;
				System.out.println("contadorDescarte aumentó a " + contadorDescarte);
				aumentarTurno();
				System.out.println("En descarte, turno aumenta a " + turno);
				esperarDescarte.signalAll();
				
					if(contadorDescarte == TOTAL_JUGADORES) {
						System.out.println("Próximo a ejecutar darCartas");
						darCartas();
						//Regresar a turno 0 para una segunda ronda de apuestas
						turno = jugadorManoAleatorio;
						contadorTurnos =0;
						ronda=0;
						contadorDescarte++;
						contadorIgualacion=0;
						jugadoresParaApostarMas.clear();
						posicionJugador =0;
						editarRegistros(9, nombreJugador, -1, operacion);//Mensaje: Iniciar la segunda ronda de apuestas
					}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("BLOQUEO UNLOCK");
			bloqueo.unlock();
		}
	}

	private void darCartas() {
		// TODO Auto-generated method stub
		// Cartas para los jugadores Simulados
		System.out.println("Entró a darCartas");
		for (int i = 0; i < TOTAL_JUGADORES-1; i++) {

			if (descarte[i] == 5) {
				System.out.println("HOLA ENTRÉ A QUITAR 5 CARTAS y es el turno" + turno);
				manosJugadores.get(i).clear(); // Borra el mazo

			} else if (descarte[i] == 4) {
				System.out.println("HOLA ENTRÉ A QUITAR 4 CARTAS y es el turno " + turno);
				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);

			} else if (descarte[i] == 3) {
				System.out.println("HOLA ENTRÉ A QUITAR 3 CARTAS y es el turno " + turno);
				System.out.println("EL TAMAÑO DEL MAZO ES " + manosJugadores.size());
				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);

			} else if (descarte[i] == 2) {
				System.out.println("HOLA ENTRÉ A QUITAR 2 CARTAS y es el turno " + turno);
				manosJugadores.get(i).remove(0);
				manosJugadores.get(i).remove(0);

			} else if (descarte[i] == 1) {
				System.out.println("HOLA ENTRÉ A QUITAR 1 CARTAS y es el turno " + turno);
				manosJugadores.get(i).remove(0);
				;
			}
			asignarCartas(manosJugadores.get(i));
		}

		// Cartas para el jugador humano
		asignarCartas(manosJugadores.get(4));

		vistaPoker.actualizarVistaPoker(manosJugadores, 0);
	}

	// Funcion que determina el ganador
	private void determinarGanador() {
		// TODO Auto-generated method stub
		crupier = new Crupier();
		
		for(int i=0;i<TOTAL_JUGADORES;i++) {
			System.out.println("el mazo de la posición: " + i + ", tiene una tolta de cartas: " + manosJugadores.get(i).size());
			int puntaje = crupier.ejecutar((ArrayList<Carta>) manosJugadores.get(i));
			puntajesFinales.add(puntaje);
			System.out.println("En la posición: "+i+ ", el jugador tiene un puntaje de: " +puntajesFinales.get(i));
		}
		int mayorpuntaje = Collections.min(puntajesFinales);
		int posicionGanador = puntajesFinales.indexOf(Collections.min(puntajesFinales));
		
		for(int i=0;i<puntajesFinales.size();i++) {
			System.out.println("Puntos del jugador: "+ i +", " + puntajesFinales.get(i) + "\n");
		}
		
		editarRegistros(11,"",posicionGanador,mayorpuntaje);
		
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
	// participamn los jugadores que deben igualar o retirarse.
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
	private void editarRegistros(int fase, String nombre, int apuesta, int operacion) {
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
		for(int apuesta: apuestasJugadores) {
			System.out.println("Apuesta en lista " + apuesta);
		}
		for(int jugadorIndex = 0; jugadorIndex < apuestasJugadores.size(); jugadorIndex++) {
			// Si el jugador está retirado no se toma en cuenta
			System.out.println("1. El jugador " + jugadorIndex + " con apuesta " + apuestasJugadores.get(jugadorIndex)
			+ " y apuesta max es " + Collections.max(apuestasJugadores));
			System.out.println("Su retirado es " + jugadoresRetirados[jugadorIndex]);
			if (!jugadoresRetirados[jugadorIndex]) {
				System.out.println("2. El jugador " + jugadorIndex + " con apuesta " + apuestasJugadores.get(jugadorIndex)
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

	private void aumentarTurno() {
		// Si turno es 4 o múltiplo de 4, se convierte en 5. Si turno tiene otro valor,
		// aumenta en 1 pero sin sobrepasar al 5.
		turno = (turno % 4 != 0) ? (turno + 1) % 5 : 5;
		System.out.println("Turno aumentó a " + turno);
	}
	
	/*
	 * private void aumentarTurno() { // Si turno es 4 o múltiplo de 4, se convierte
	 * en 5. Si turno tiene otro valor, // aumenta en 1 pero sin sobrepasar al 5.
	 * turno = (turno % 4 != 0) ? (turno + 1) % 5 : 5; if(turno !=5) { for(int
	 * i=turno-1;i<TOTAL_JUGADORES-1;i++) { if(!jugadoresSimulados[i].getRetirado())
	 * { turno=i+1; break; } } }
	 * System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA Turno aumentó a " +
	 * turno); }
	 */

	// Cambia la mano del jugador humano
	public void descarteJugadorHumano(List<Carta> manoJugadorHumano) {
		manosJugadores.remove(4);
		manosJugadores.add(manoJugadorHumano); // se agrega la mano nueva

	}

	public void setApuestasJugadores(int indexJugador, int apuesta) {
		apuestasJugadores.set(indexJugador, apuesta);
	}

	public List<Integer> getApuestasJugadores() {
		return apuestasJugadores;
	}

	public List<List<Carta>> getManosJugadores() {
		return manosJugadores;
	}
	public int getMaximaApuesta() {
		return Collections.max(apuestasJugadores);
	}

	public int getIdJugadorMano() {
		return jugadorManoAleatorio;
	}

	public void setRonda (int ronda) {
		this.ronda = ronda;
	}
	public int getRonda() {
		return ronda;
	}

	public int getTurno() {
		return turno;
	}

	public boolean isHumanoRetirado() {
		return humanoRetirado;
	}

	public void setHumanoRetirado(boolean humanoRetirado) {
		this.humanoRetirado = humanoRetirado;
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
