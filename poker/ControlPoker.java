package poker;


import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ControlPoker {

	public static final int TOTAL_JUGADORES = 5;
	public static final String[] NOMBRE_JUGADORES = {"Cristiano","Duque","Petro","Uribe"};
	public static final int NUMERO_CARTAS=5;
	
	//Vista
	private VistaGUIPoker vistaPoker;
	
	//Variables del juego
	private Baraja baraja;
	private List<List<Carta>> manosJugadores;
	private Carta cartaComun;
	
	//variable para el manejo de hilos
	private int turno; //Controlar el turno de los hilos
	private int[] descarte = new int[TOTAL_JUGADORES-1];
	private Lock bloqueo = new ReentrantLock(); // manejo de sincronización
	private Condition esperarTurno = bloqueo.newCondition(); // manejo de sincronización
	
	
	public ControlPoker(){
		
		
		manosJugadores = new ArrayList<List<Carta>>();
		iniciarJuego();
		vistaPoker = new VistaGUIPoker(NOMBRE_JUGADORES, manosJugadores,cartaComun,this);
	}


	private void iniciarJuego() {
		// TODO Auto-generated method stub
		baraja = new Baraja();
		for(int i=0;i<TOTAL_JUGADORES;i++) {
			manosJugadores.add(seleccionarCartas());
			
		}
		cartaComun = baraja.getCarta();
	}
	
	private ArrayList<Carta> seleccionarCartas() {
		// TODO Auto-generated method stub
		ArrayList<Carta> manoJugador = new ArrayList<Carta>();
		//se dan 5 cartas al jugador
		manoJugador.add(baraja.getCarta());
		manoJugador.add(baraja.getCarta());
		manoJugador.add(baraja.getCarta());
		manoJugador.add(baraja.getCarta());
		manoJugador.add(baraja.getCarta());
		return manoJugador;
	}
	
	public void descarteJugadorHumano(List<Carta> manoJugadorHumano) {
		//cambiar la mano del jugador humano 
		
		manosJugadores.remove(4); //se quita la mano antigua
		manosJugadores.add(manoJugadorHumano); // se agrega la mano nueva
				
		//iniciarHilos
		 iniciarJugadoresSimulados();
	}
	
	private void iniciarJugadoresSimulados() {
	    turno=1;
	  //crear los hilos e iniciarlos
	
	  JugadorSimulado jugador1 = new JugadorSimulado(1,ControlPoker.NOMBRE_JUGADORES[0],this); 
	  JugadorSimulado jugador2 = new JugadorSimulado(2, ControlPoker.NOMBRE_JUGADORES[1],this);
	  JugadorSimulado jugador3 = new JugadorSimulado(3,ControlPoker.NOMBRE_JUGADORES[2],this);
	  JugadorSimulado jugador4 = new JugadorSimulado(4,ControlPoker.NOMBRE_JUGADORES[3],this);

	  
	  ExecutorService ejecutorSubprocesos = Executors.newCachedThreadPool();
	  ejecutorSubprocesos.execute(jugador1); 
	  ejecutorSubprocesos.execute(jugador2);
	  ejecutorSubprocesos.execute(jugador3); 
	  ejecutorSubprocesos.execute(jugador4);
	  
	  ejecutorSubprocesos.shutdown();
	}
	
	//método a sincronizar - condition es el turno 
	public void turnos(int jugador, int cartasPedidas,String nombreJugador) {
		//bloquear la clase
		bloqueo.lock();
		try{
			//Validar condición de ejecución para el hilo
			while(jugador!=turno) {
				System.out.println("Jugador "+nombreJugador+" intenta entrar y es mandado a esperar turno");
				
				esperarTurno.await();
			}
			System.out.println("Soy "+ nombreJugador + " voy a descartar " +cartasPedidas + ", es el turno: " + turno);
			descarte[turno-1]=cartasPedidas;
			turno++;
			esperarTurno.signalAll();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}finally {
			bloqueo.unlock();
			if(turno==5) {
				darCartas();
			}
		}
	}

	private void darCartas() {
		// Cartas para los jugadores Simulados
		for(int i=0;i<4;i++) { 

				 if(descarte[i]==5) {
					 
					manosJugadores.get(i).clear(); //Borra el mazo
					 
				 }else if(descarte[i]==4) {
					 
					 manosJugadores.get(i).remove(0);
					 manosJugadores.get(i).remove(0);
					 manosJugadores.get(i).remove(0);
					 manosJugadores.get(i).remove(0);
					 
				 }else if(descarte[i]==3) {
					 manosJugadores.get(i).remove(0);
					 manosJugadores.get(i).remove(0);
					 manosJugadores.get(i).remove(0);
					 
				 }else if(descarte[i]==2) {
					 manosJugadores.get(i).remove(0);
					 manosJugadores.get(i).remove(0);

				 }else if(descarte[i]==1) {
					 manosJugadores.get(i).remove(0);;
				 }
				 asignarCartas(manosJugadores.get(i));
		}
		int ganador = determinarGanador();
		
		//Cartas para el jugador humano
		asignarCartas(manosJugadores.get(4));
		
		vistaPoker.actualizarVistaPoker(manosJugadores,ganador);
		
	}
	//Calcula cuántas cartas debe darle a cada jugador luego del descarte
	private void asignarCartas(List<Carta> manoJugador) {
		
		if(manoJugador.size()<5) {
			int numeroCartas = ControlPoker.NUMERO_CARTAS - manoJugador.size(); //Número de cartas que debe pedir
			 System.out.println(" NECESITO " + numeroCartas + "PORQUE SOLO TENGO: "+ manoJugador.size());
			for(int i=0; i < numeroCartas ;i++) {
				manoJugador.add(baraja.getCarta());

			}
		}
		
	}

	//Establecer los parametros para ganar
	private int determinarGanador() {
		// TODO Auto-generated method stub
		return 0;
	}


	//Hilo principal - main
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 EventQueue.invokeLater(new Runnable() {
	        	public void run() {
	        		new ControlPoker();
	        	}
	        });
	}
	
}
