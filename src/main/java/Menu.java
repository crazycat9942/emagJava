import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import javafx.geometry.Point3D;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
/// menu is thing on the right with the checkboxes etc
class Menu extends JFrame
{
    static Panel panel;
    ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
    JCheckBox addStrongForce;
    JCheckBox addElecForce;
    JCheckBox addMagForce;
    JCheckBox fieldLines;
    JCheckBox arrows;
    JCheckBox fieldColoring;
    JCheckBox infoRel;
    JCheckBox posRel;
    JTextArea timeArea;
    JTextArea fpsArea;
    double lastTime;
    JTextArea quatArea;
    JSlider quatW;
    JSlider quatI;
    JSlider quatJ;
    JSlider quatK;
    JTextField pointNum;
    JTextArea pointNumArea;
    JTextArea timeStepArea;
    JTextField timeStep;
    JComboBox addParticle = new JComboBox();///dropdown
    JButton addPartButton = new JButton();
    JButton clearAll = new JButton();
    JButton addWaveFunction = new JButton();
    static JTextField zoomField;
    //JTextArea jacobArea;
    JTextArea zoomArea;
    JComboBox waveN;
    JComboBox waveL;
    JComboBox waveM;
    public Menu(Panel panelInit)
    {
        panel = panelInit;
        init();
    }

    public void init()
    {
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(300, panel.windowY));
        MigLayout ml = new MigLayout();
        setLayout(ml);
        FlatMacDarkLaf.setup();
        FlatMacDarkLaf.updateUI();
        addStrongForce = new JCheckBox("Strong force", true);
        addElecForce = new JCheckBox("Electric force", true);
        //System.out.println(colorWithCurl.isSelected());
        ml.setLayoutConstraints("width 200px!, wrap");
        this.add(addStrongForce);
        //gbc.gridy = GridBagConstraints.FIRST_LINE_END;
        this.add(addElecForce);
        addMagForce = new JCheckBox("Magnetic force", true);
        this.add(addMagForce);
        fieldLines = new JCheckBox("Field lines", true);
        this.add(fieldLines);
        arrows = new JCheckBox("Arrows", true);
        this.add(arrows);
        fieldColoring = new JCheckBox("Field Coloring", true);
        this.add(fieldColoring);
        infoRel = new JCheckBox("Information < speed of light", true);
        this.add(infoRel);
        posRel = new JCheckBox("Particle pos < speed of light", false);
        this.add(posRel);
        timeArea = new JTextArea("time: " + panel.time + " seconds");
        fpsArea = new JTextArea();
        /*curlArea = new JTextArea();
        divArea = new JTextArea();
        magArea = new JTextArea();
        jacobArea = new JTextArea();*/
        this.add(fpsArea);
        this.add(timeArea, "bottom");
        quatArea = new JTextArea();
        this.add(quatArea);
        quatW = new JSlider(0,1000,1000);
        quatW.setPreferredSize(new Dimension(150,10));
        this.add(quatW);
        quatI = new JSlider(0,1000,0);
        quatI.setPreferredSize(new Dimension(150,10));
        this.add(quatI);
        quatJ = new JSlider(0,1000,0);
        quatJ.setPreferredSize(new Dimension(150,10));
        this.add(quatJ);
        quatK = new JSlider(0,1000,0);
        quatK.setPreferredSize(new Dimension(150,10));
        this.add(quatK);
        pointNumArea = new JTextArea("Number of points:");
        this.add(pointNumArea);
        pointNum = new JTextField("1000");
        this.add(pointNum);
        timeStepArea = new JTextArea("timestep:");
        this.add(timeStepArea);
        timeStep = new JTextField(String.valueOf(panel.timeStep));
        this.add(timeStep);
        addParticle = new JComboBox(new String[]{"Add Proton", "Add Neutron", "Add Electron"});
        addParticle.setToolTipText("Add a particle at a certain position");
        this.add(addParticle);
        addPartButton = new JButton("Add");
        this.add(addPartButton);
        addPartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp;
                String text = String.valueOf(addParticle.getSelectedItem());
                Point3D tempPoint = new Point3D(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
                tempPoint = tempPoint.multiply(1/Math.sqrt(Math.pow(tempPoint.getX(), 2) + Math.pow(tempPoint.getY(), 2) + Math.pow(tempPoint.getZ(), 2)));
                tempPoint = tempPoint.multiply(0.3 * panel.mpp * panel.windowY).add(panel.parentSim.translateX, panel.parentSim.translateY, 0);
                if(text.contains("Proton"))
                {
                    temp = new Proton(tempPoint, panel.time, panel);
                    panel.protons.add((Proton)temp);
                }
                else if(text.contains("Neutron"))
                {
                    temp = new Neutron(tempPoint, panel.time);
                    panel.neutrons.add((Neutron)temp);
                }
                else
                {
                    temp = new Electron(tempPoint, panel.time, panel);
                    panel.electrons.add((Electron)temp);
                }
            }
        });
        /*this.add(curlArea);
        this.add(divArea);
        this.add(magArea);*/
        zoomArea = new JTextArea("zoom:");
        System.out.println(panel.mpp);
        zoomField = new JTextField(String.valueOf(panel.mpp));
        this.add(zoomArea);
        this.add(zoomField);
        clearAll = new JButton("Clear all charges");
        this.add(clearAll);
        clearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.electrons.clear();
                panel.protons.clear();
                panel.neutrons.clear();
            }
        });
        this.add(new JTextField("Wavefunction settings"));
        this.add(new JTextField("n, l, and m quantum numbers:"));
        waveN = new JComboBox(new String[]{"1","2","3","4","5","6","7","8","9","10"});
        this.add(waveN);
        String[] temp = new String[Integer.parseInt(String.valueOf(waveN.getSelectedItem()))];
        for(int i = 0; i < temp.length; i++)
        {
            temp[i] = String.valueOf(i);
        }
        waveL = new JComboBox(temp);
        this.add(waveL);
        temp = new String[waveL.getItemCount() * 2 - 1];
        for(int i = -waveL.getItemCount() + 1; i < waveL.getItemCount(); i++)
        {
            temp[i + waveL.getItemCount() - 1] = String.valueOf(i);
        }
        waveM = new JComboBox(temp);
        this.add(waveM);
        addWaveFunction = new JButton("Add Wave Function");
        addWaveFunction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WaveFunction temp = new WaveFunction(Integer.parseInt(String.valueOf(waveN.getSelectedItem())), Integer.parseInt(String.valueOf(waveL.getSelectedItem())), Integer.parseInt(String.valueOf(waveM.getSelectedItem())), panel.time, panel);
                temp.q_n = Integer.parseInt(String.valueOf(waveN.getSelectedItem()));
                temp.q_l = Integer.parseInt(String.valueOf(waveL.getSelectedItem()));
                temp.temp_l = temp.q_l;
                temp.q_m = Integer.parseInt(String.valueOf(waveM.getSelectedItem()));
                temp.createData();
                panel.electrons.add(temp);
            }
        });
        this.add(addWaveFunction);
        waveN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String[] temp = new String[Integer.parseInt(String.valueOf(waveN.getSelectedItem()))];
                for(int i = 0; i < temp.length; i++)
                {
                    temp[i] = String.valueOf(i);
                }
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(temp);
                waveL.setModel(model);
            }
        });
        waveL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] temp = new String[Integer.parseInt(String.valueOf(waveL.getSelectedItem())) * 2 + 1];
                int max = Integer.parseInt(String.valueOf(waveL.getSelectedItem()));
                int temp2 = 0;
                for(int i = -max; i < max + 1; i++)
                {
                    temp[temp2] = String.valueOf(i);
                    temp2++;
                }
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(temp);
                waveM.setModel(model);
            }
        });
        FlatMacDarkLaf.updateUI();
    }
    public static void main(String[] args)
    {
        EventQueue.invokeLater(() -> new Menu(panel).setVisible(true));
    }

    public void update(Graphics g)
    {
        setSize(new Dimension(300, panel.windowY));
        DecimalFormat df = new DecimalFormat("#.###");
        timeArea.setText("time: " + panel.time + " seconds");
        fpsArea.setText("FPS: " + df.format((1000.0/(System.currentTimeMillis() - lastTime))));
        lastTime = System.currentTimeMillis();
        quatArea.setText("Quaternion: " + quatW.getValue()/1000. + " + " + quatI.getValue()/1000. + "i + " + quatJ.getValue()/1000. + "j + " + quatK.getValue()/1000. + "k");
        panel.timeStep = Double.parseDouble(timeStep.getText());
        //zoomField.setText(String.valueOf(panel.mpp));
        //System.out.println(zoomField.getText());
        if(Double.parseDouble(zoomField.getText()) != 0 && panel.prevmpp != panel.mpp) {
            panel.prevmpp = panel.mpp;
            panel.mpp = Double.parseDouble(zoomField.getText());
            panel.updateZoom();
            panel.prevmpp = panel.mpp;
            zoomArea.setText("zoom: " + zoomField.getText());
        }
    }
    public double[] getQuaternion()
    {/// used for wavefunction class, just returns double[] representation of how the electron wavefunction is oriented
        return new double[]{quatW.getValue()/1000., quatI.getValue()/1000., quatJ.getValue()/1000., quatK.getValue()/1000.};
    }
}
