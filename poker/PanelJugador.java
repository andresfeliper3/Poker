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
	private static final int WIDTH = Baraja.CARD_WIDTH * 3;
	private static final int HEIGHT = Baraja.CARD_HEIGHT + FONT_SIZE * 3;
	
	
	private List<Carta> mano = new ArrayList<Carta>();
	private JLabel nombre, mensaje;
	private int apuesta;
	private boolean isHuman;
	
	private Escucha escucha;
	
	public PanelJugador(String nombre, List<Carta> cartas, boolean isHuman) {
		this.setBorder(new TitledBorder(nombre));
		setLayout(new BorderLayout());
		this.setBackground(Color.GREEN);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		this.nombre = new JLabel(nombre);
		this.nombre.setFont(new Font(FONT_TYPE, FONT_STYLE, FONT_SIZE));
		add(this.nombre,BorderLayout.NORTH);
		mensaje = new JLabel();
		mensaje.setFont(new Font(FONT_TYPE,FONT_STYLE,FONT_SIZE_MENSAJE));
		add(mensaje,BorderLayout.SOUTH);
		
		this.isHuman = isHuman;
		
	}
	
	private class Escucha extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		
	}
}
