package gui;  // The package on which artifact are defined 

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;    
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import java.awt.*;
import java.awt.event.*;

                                                                             
public class MyBotiquin extends JInternalFrame {    // The First AId GUI Defined as a JInternalFrame  
    private JTextField[] medicinaField;
    private JTextField[] cantidadField;
    private JTextField[] pautaField;
    private JTextField[] fechaCaducidadField;
    private JCheckBox[] tomarCheckBox;

    JButton submitButton;

	//private String[] labels = {"Tomar","Medicina","Cantidad","Pauta","Caducidad"}
    private int numMedicinas;
    
    public JButton getSubmitButton() {
        return submitButton;
    }

    public JTextField[] getMedicines() {
        return medicinaField;
    }

    public JTextField[] getQtd() {
        return cantidadField;
    }

    public JTextField[] getPeriod() {
        return pautaField;
    }

    public JTextField[] getCaducity() {
        return fechaCaducidadField;
    }

    public JCheckBox[] getTake() {
        return tomarCheckBox;
    }

    public MyBotiquin(int numMedicinas) {

        this.numMedicinas = numMedicinas;
        setTitle("Información del Botiquín");
        setSize(400, 200 + 40 * numMedicinas-1);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 5));

        panel.add(new JLabel("Tomar"));
        panel.add(new JLabel("Medicina"));
        panel.add(new JLabel("Cantidad"));
        panel.add(new JLabel("Pauta"));
        panel.add(new JLabel("Caducidad"));
		//panel.add(new JSeparator());

        tomarCheckBox = new JCheckBox[numMedicinas];
        medicinaField = new JTextField[numMedicinas];
        cantidadField = new JTextField[numMedicinas];
        pautaField = new JTextField[numMedicinas];
        fechaCaducidadField = new JTextField[numMedicinas];

        for (int i = 0; i < numMedicinas; i++) {
            tomarCheckBox[i] = new JCheckBox();
            panel.add(tomarCheckBox[i]);

            medicinaField[i] = new JTextField();
            panel.add(medicinaField[i]);

            cantidadField[i] = new JTextField();
            panel.add(cantidadField[i]);

            pautaField[i] = new JTextField();
            panel.add(pautaField[i]);

            fechaCaducidadField[i] = new JTextField();
            panel.add(fechaCaducidadField[i]);
			
			//panel.add(new JSeparator());

        }
		//panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
        panel.add(new JSeparator());     
        
		this.submitButton = new JButton("Guardar");
        panel.add(submitButton);

        add(panel);
    }

    public int getNumMedicinas(){
        return this.numMedicinas;
    }
	
}
          



