package poker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class PanelJugador extends JPanel {
	//Dimensiones del panel seg�n el tama�o de las cartas
	public static final int FONT_SIZE = 18;
	public static final int FONT_SIZE_MENSAJE = 14;
	public static final String FONT_TYPE = Font.DIALOG;
	public static final int FONT_STYLE = Font.BOLD;
	private static final int WIDTH = Baraja.CARD_WIDTH * 4;
	private static final int HEIGHT = Baraja.CARD_HEIGHT + FONT_SIZE * 7;
	
	
	private List<Carta> mano = new ArrayList<Carta>();
	private JLabel nombre, mensaje, apuesta;
	private JPanel panelMano, panelApuesta;
	private int valorApuesta = 0;
	private boolean isHuman;
	
	private Escucha escucha;
	
	public PanelJugador(String nombre, List<Carta> cartas, boolean isHuman) {
		//this.setBorder(new TitledBorder(nombre));
		setLayout(new BorderLayout());
		this.setBackground(Color.GREEN);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		System.out.println(nombre + "se crea con: width " + WIDTH + "y, height" + HEIGHT);
		
		this.nombre = new JLabel(nombre);
		this.nombre.setFont(new Font(FONT_TYPE, FONT_STYLE, FONT_SIZE));
		add(this.nombre, BorderLayout.NORTH);
		mensaje = new JLabel();
		mensaje.setFont(new Font(FONT_TYPE,FONT_STYLE,FONT_SIZE_MENSAJE));
		add(mensaje, BorderLayout.SOUTH);
		
		panelMano = new JPanel();
		panelMano.setBackground(Color.GREEN);
		panelApuesta = new JPanel();
		panelApuesta.setBackground(Color.GREEN);
		apuesta = new JLabel(String.valueOf(valorApuesta));
		this.isHuman = isHuman;
		
		//Recibe las cartas 
		mano = cartas;
		//Si es humano, sus cartas tienen escuchas		
		if(isHuman) {
			
			//A cada carta que est� en la mano
			
			mensaje.setText("Inicias... ");
		}
		escucha = new Escucha();
		for(Carta carta : mano) {
			carta.addMouseListener(escucha);
		}
		actualizarPanelMano();
		actualizarPanelApuesta();
		add(panelMano, BorderLayout.CENTER);
		add(panelApuesta, BorderLayout.EAST);
		System.out.println("A�adiendo cartas a " + nombre + ": width " + getWidth() + ", height: " + getHeight());
		
	}
	//Actualiza el JPanel donde est�n las cartas del jugador 
	public void actualizarPanelMano() {
		panelMano.removeAll();
		if(mano != null) {
			for(Carta carta : mano) {
				//A�ado cartas (JLabels) a panelMano (JPanel).
				panelMano.add(carta);
			}			
		}	
	}	
	
	//Actualiza el JPanel donde est� la apuesta del jugadior
	public void actualizarPanelApuesta() {
		panelApuesta.removeAll();
		panelApuesta.add(apuesta);	
	}
	
	public List<Carta> getMano() {
		  return mano; 
	}
	
 	private class Escucha extends MouseAdapter {
 		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			Carta carta = (Carta)e.getSource();
			System.out.println("Presionaste la carta: " + carta.toString());
			System.out.println("Las dimensiones de " + nombre.getText() + " son: width " + getWidth() + ", height " + getHeight());
		}	
	}
}
