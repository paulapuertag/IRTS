package gui; // The package on which artifact are defined               

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//import java.util.Random;

public class ECGGraphFrame extends JFrame {

	public ECGGraphFrame() {
		setTitle("ECG Signal Graph");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 200);
        
		Container contenedor = this.getContentPane();
		contenedor.setLayout(new BorderLayout());

        ECGPanel ecgPanel = new ECGPanel();
		contenedor.add(ecgPanel);

		setContentPane(contenedor); 

        Timer timer = new Timer(400, ecgPanel);
        timer.start();

	}                      
  
class ECGPanel extends JPanel implements ActionListener {
    private int time = 0; // Simulated time
    private int[] ecgData; // Simulated ECG data

    public ECGPanel() {
        ecgData = generateECGData(); // Generate sample ECG data
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.setBackground(Color.BLACK);
		super.paintComponent(g);
        g.setColor(Color.RED);

        // Draw ECG graph
        for (int i = 0; i < ecgData.length - 1; i++) {
            int x1 = i;
            int y1 = getHeight() / 2 - ecgData[i];
            int x2 = i + 1;
            int y2 = getHeight() / 2 - ecgData[i + 1];
            g.drawLine(x1*3, 100+y1, x2*3, 100+y2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Simulate real-time ECG data
        time++;
        ecgData = generateECGData();
        repaint();
    }

    private int[] generateECGData() {
        // Simulate ECG data (replace with actual data)
        int numSamples = getWidth();
        int[] data = new int[numSamples];
		
        for (int i = 0; i < numSamples; i++) {
            data[i] =  (int) (Math.random() * 50) + 70;
			// Random ECG values
        }
        return data;
    }
}
}
