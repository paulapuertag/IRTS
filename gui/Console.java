package gui;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Insets;
//import java.awt.event.ActionListener;

//import java.awt.Container;                                                                               
import cartago.*;
import cartago.tools.*;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

public class Console extends GUIArtifact {

    // I decide to program the JFrames out in order to be reuse by several 
    // artifacts. All class must be defined on the same package => gui
    private MyFrame frame;
    //I could define the first aid as a JFrame also
    //private MyBotiquin botiquin; 

    private ECGGraphFrame ekgFrame;

    private String botName;  				// The name of the chatbot
    private String botMasterName = "Ivan"; 	// The default owner name of the chatbot
    private int ecgTime = 0;

    public void setup() {
        frame = new MyFrame();
        frame.setVisible(true);

        // If we decide to use the first aid as a new JFrame
        //botiquin = new MyBotiquin(5);
        //botiquin.setVisible(true);
        ekgFrame = new ECGGraphFrame();
        ekgFrame.setVisible(true);

        // The Code to link the events from the GUI to agents   
        // I protect the use of operations by using INTERNAL OPERATIONS
        // such operations could not be invoked from agents
        linkActionEventToOp(frame.getBotiquin().getSubmitButton(), "save");
        linkActionEventToOp(frame.getButton(), "send");  // INTERNAL OPERATION send
        linkKeyStrokeToOp(frame.getTextField(), "ENTER", "send");
        linkWindowClosingEventToOp(frame, "closed");    // INTERNAL OPERATION close 
        linkMouseEventToOp(frame, "mouseDragged", "mouseDraggedOp");

    }

    @INTERNAL_OPERATION
    void save(ActionEvent ev) {

        int n_medicines = frame.getNumMed();
        MyBotiquin botiquin = frame.getBotiquin();
        JCheckBox[] take = botiquin.getTake();
        JTextField[] name = botiquin.getMedicines();
        JTextField[] qtd = botiquin.getQtd();
        JTextField[] period = botiquin.getPeriod();
        JTextField[] caducity = botiquin.getCaducity();
        signal("medUpdate", n_medicines);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        for (int i = 0; i < n_medicines; i++) {
            if (take[i].isSelected()) {
                signal("say", " medication prescription");

                signal("newMedication",
                        Literal.parseLiteral(name[i].getText()),
                        Integer.parseInt(qtd[i].getText()),
                        Integer.parseInt(period[i].getText()),
                        Literal.parseLiteral("C"));//Literal.parseLiteral(caducity[i].getText()));
            }
        }
    }

    @INTERNAL_OPERATION
    void send(ActionEvent ev) {
        String texto = frame.getTextField().getText();
        //getObsProperty("say").updateValue(texto);
        signal("say", texto);

        frame.getTextField().setText("");

        frame.appendToPane(frame.getTextArea(), botMasterName, Color.DARK_GRAY);
        frame.appendToPane(frame.getTextArea(), " dice: ", Color.DARK_GRAY);
        frame.appendToPane(frame.getTextArea(), texto, Color.DARK_GRAY);
        String salto = System.lineSeparator();
        frame.appendToPane(frame.getTextArea(), salto, Color.DARK_GRAY);
    }

    @INTERNAL_OPERATION
    void closed(WindowEvent ev) {
        signal("closed");
    }

    // This operation will update the text on the GUI console
    @INTERNAL_OPERATION
    void updateText(ActionEvent ev) {
        String texto = frame.getText();

        // A first way to send information to agent is sending a new perception
        //getObsProperty("say").updateValue(texto); 
        // A second way is to send a signal with the information
        // signals are not saved on agents
        signal("say", texto);

        frame.getTextField().setText("");

        frame.appendToPane(frame.getTextArea(), botMasterName, Color.DARK_GRAY);
        frame.appendToPane(frame.getTextArea(), " pregunta: ", Color.DARK_GRAY);
        frame.appendToPane(frame.getTextArea(), texto, Color.DARK_GRAY);
        String salto = System.lineSeparator();
        frame.appendToPane(frame.getTextArea(), salto, Color.DARK_GRAY);
    }

    // This OPERATION is used to update the text with information provided 
    // by the agent
    @OPERATION
    void show(String texto) {
        frame.appendToPane(frame.getTextArea(), botName, Color.RED);
        frame.appendToPane(frame.getTextArea(), " dice: ", Color.RED);
        frame.appendToPane(frame.getTextArea(), texto, Color.RED);
        String salto = System.lineSeparator();
        frame.appendToPane(frame.getTextArea(), salto, Color.RED);
    }

    // This OPERATION could be used to set the name of the chatbot
    @OPERATION
    void setBotName(String name) {
        botName = name;
    }

    // This OPERATION could be used to set the name of the Owner of the chatbot
    @OPERATION
    void setBotMasterName(String name) {
        botMasterName = name;
    }

    @OPERATION
    void getVitals(String name) {
        // This operation will send a signal to the agent with the vital signs
        int time = ekgFrame.getTime();
        if (time>ecgTime) {
            this.ecgTime = time;
            int avgVitals = ekgFrame.getMeanEcgData();
            signal("updateVitals",time,avgVitals);
        }
        
    }

}
