/* Autores: Jose David Barona Hernández - 1727590
 *                  Andrés Felipe Rincón    - 1922840
 * Correos: jose.david.barona@correounivalle.edu.co 
 *             andres.rincon.lopez@correounivalle.edu.co
 * Mini proyecto 3: Poker
 * Fecha: 25/11/2020
 * 
 * */
package poker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

// TODO: Auto-generated Javadoc
/**
 * The Class PanelJugador.
 */
public class PanelJugador extends JPanel {
	//Dimensiones del panel según el tamaño de las cartas
	public static final int FONT_SIZE = 18;
	public static final int FONT_SIZE_MENSAJE = 14;
	public static final String FONT_TYPE = Font.DIALOG;
	public static final int FONT_STYLE = Font.BOLD;
	private static final int WIDTH = Baraja.CARD_WIDTH * 5;
	private static final int HEIGHT = Baraja.CARD_HEIGHT + FONT_SIZE * 7;
	
	private List<Carta> mano = new ArrayList<Carta>();
	private JLabel nombre, mensaje, apuesta;
	private JPanel panelMano, panelApuesta;
	private int valorApuesta;
	private boolean isHuman;
	
	private Escucha escucha;
	
	/**
	 * Instantiates a new panel jugador.
	 *
	 * @param nombre the nombre
	 * @param cartas the cartas
	 * @param valorApuesta the valor apuesta
	 * @param isHuman the is human
	 */
	public PanelJugador(String nombre, List<Carta> cartas, Integer valorApuesta, boolean isHuman) {
		//this.setBorder(new TitledBorder(nombre));
		setLayout(new BorderLayout());
		this.setBackground(Color.GREEN);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		this.nombre = new JLabel(nombre);
		this.nombre.setFont(new Font(FONT_TYPE, FONT_STYLE, FONT_SIZE));
		add(this.nombre, BorderLayout.NORTH);
		mensaje = new JLabel();
		mensaje.setFont(new Font(FONT_TYPE,FONT_STYLE,FONT_SIZE_MENSAJE));
		add(mensaje, BorderLayout.SOUTH);
		
		//apuesta
		this.valorApuesta = valorApuesta;
		panelMano = new JPanel();
		panelMano.setBackground(Color.GREEN);
		panelApuesta = new JPanel();
		panelApuesta.setBackground(Color.GREEN);
		apuesta = new JLabel("$" + String.valueOf(valorApuesta));
		this.isHuman = isHuman;
		
		//Recibe las cartas 
		mano = cartas;
		//Si es humano, sus cartas tienen escuchas		
		if(isHuman) {
			escucha = new Escucha();
			//A cada carta que está en la mano
			for(Carta carta : mano) {
				carta.addMouseListener(escucha);
			}	
			desactivarEscuchas();
			mensaje.setText("Inicias... ");
		}
		
		actualizarPanelMano();
		actualizarPanelApuesta();
		add(panelMano, BorderLayout.CENTER);
		add(panelApuesta, BorderLayout.EAST);		
	}
	
	/**
	 * Actualizar panel mano.
	 * Actualiza el JPanel donde están las cartas del jugador 
	 */
	public void actualizarPanelMano() {
		panelMano.removeAll();
		if(mano != null) {
			for(Carta carta : mano) {
				//Añado cartas (JLabels) a panelMano (JPanel).
				panelMano.add(carta);
			}			
		}	
	}	
	
	/**
	 * Actualizar panel apuesta.
	 * Actualiza el JPanel donde está la apuesta del jugador
	 */
	public void actualizarPanelApuesta() {
		panelApuesta.removeAll();
		panelApuesta.add(apuesta);	
	}
	
	/**
	 * Recibir cartas.
	 * recibe unas nuevas cartas y las pinta en el panel del jugador
	 * @param nuevasCartas the nuevas cartas
	 * @param texto the texto
	 */
	  public void recibirCartas(List<Carta> nuevasCartas, String texto) {
		  SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mano=nuevasCartas;
				actualizarPanelMano();
				mensaje.setText(texto);
				panelMano.revalidate();	
			    panelMano.repaint();
			}
			   
		   });
	   }
	 
 	/**
 	 * Gets the mano.
 	 * retorna la mano del jugador
 	 * @return the mano
 	 */
	public List<Carta> getMano() {
		  return mano; 
	}
	/**
	 * Desactivar escuchas.
	 * Desactiva las escuchas de las cartas del jugador humano, para no descartar
	 */
	public void desactivarEscuchas() {
		   SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(Carta carta : mano) {
					carta.removeMouseListener(escucha);
				}
			}		   
		   });
	   }
	  
  	/**
  	 * Activar escuchas.
  	 * Activa las escuchas de las cartas del jugador humano, para descartar
	 */
	 public void activarEscuchas() {
		   SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(Carta carta : mano) {
					carta.addMouseListener(escucha);
				}
			}		   
		   });   
	   }
	
	/**
	 * Gets the nombre.
	 * retorna el nombre del jugador
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre.getText();
	}
	
	/**
	 * Sets the valor apuesta.
	 * establece el valor de la apuesta en el panel del jugador
	 * @param valorApuesta the new valor apuesta
	 */
	public void setValorApuesta(int valorApuesta) {
		this.valorApuesta = valorApuesta;
		apuesta.setText("$" + String.valueOf(valorApuesta));
	}
	
	/**
	 * Eliminar carta.
	 * elimina de la mesa de juego, la carta seleccionada
	 * @param cartaEliminar the carta eliminar
	 */
	   private void eliminarCarta(Carta cartaEliminar) { 
		   mano = Collections.synchronizedList(mano);
		   synchronized (mano){
			   for(int i=0;i<mano.size();i++) {
				 if(cartaEliminar.getValor()==mano.get(i).getValor() && cartaEliminar.getPalo()==mano.get(i).getPalo() ) {
					 mano.remove(i);
				 }   
			   }
			   
		   }
	   }
 	
	 /**
	  * The Class Escucha.
	  */
	 private class Escucha extends MouseAdapter {
 		
		/**
		 * Mouse clicked.
		 *
		 * @param e the e
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			Carta cartaEliminar = (Carta)e.getSource();
			
			eliminarCarta(cartaEliminar);
			actualizarPanelMano();
			panelMano.revalidate();
			panelMano.repaint();
			
			System.out.println("Presionaste la carta: " + cartaEliminar.toString());
			System.out.println("Descartó " + nombre.getText() + " son: width " + getWidth() + ", height " + getHeight());
		}	
	}
}
