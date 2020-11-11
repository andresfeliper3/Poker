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
			mensaje.setText("Inicias... ");
		}
		
		actualizarPanelMano();
		actualizarPanelApuesta();
		add(panelMano, BorderLayout.CENTER);
		add(panelApuesta, BorderLayout.EAST);		
	}
	//Actualiza el JPanel donde están las cartas del jugador 
	public void actualizarPanelMano() {
		panelMano.removeAll();
		if(mano != null) {
			for(Carta carta : mano) {
				//Añado cartas (JLabels) a panelMano (JPanel).
				panelMano.add(carta);
			}			
		}	
	}	
	
	//Actualiza el JPanel donde está la apuesta del jugadior
	public void actualizarPanelApuesta() {
		panelApuesta.removeAll();
		panelApuesta.add(apuesta);	
	}

	public List<Carta> getMano() {
		  return mano; 
	}
	
	public void setValorApuesta(int valorApuesta) {
		this.valorApuesta = valorApuesta;
		apuesta.setText("$" + String.valueOf(valorApuesta));
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
