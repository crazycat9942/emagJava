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
class Menu extends JFrame
{
    static Panel panel;
    ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
    JCheckBox addStrongForce;
    JCheckBox addEmagForce;
    JCheckBox fieldLines;
    JCheckBox arrows;
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
    JComboBox addParticle = new JComboBox();
    JButton addPartButton = new JButton();
    JButton clearAll = new JButton();
    static JTextField zoomField;
    //JTextArea jacobArea;
    JTextArea zoomArea;
    public Menu(Panel panelInit)
    {
        panel = panelInit;
        checkBoxes.add(addStrongForce);
        checkBoxes.add(addEmagForce);
        init();
    }

    public void init()
    {
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(300, 900));
        MigLayout ml = new MigLayout();
        setLayout(ml);
        FlatMacDarkLaf.setup();
        FlatMacDarkLaf.updateUI();
        addStrongForce = new JCheckBox("Strong force", true);
        addEmagForce = new JCheckBox("Electromagnetic force", true);
        //System.out.println(colorWithCurl.isSelected());
        ml.setLayoutConstraints("width 200px!, wrap");
        this.add(addStrongForce);
        //gbc.gridy = GridBagConstraints.FIRST_LINE_END;
        this.add(addEmagForce);
        fieldLines = new JCheckBox("Field lines", true);
        this.add(fieldLines);
        arrows = new JCheckBox("Arrows", true);
        this.add(arrows);
        timeArea = new JTextArea("time: " + panel.time + " zs (10^-21 s)");
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
                tempPoint = tempPoint.multiply(0.3 * panel.mpp * 900);
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
        //this.add(new JTextArea("Legend"));
        //PaintScaleLegend
        //jacobArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        //jacobArea.setSize(180, 180);
        //this.add(jacobArea);
        //panel.add(this.getContentPane(), BorderLayout.EAST);
        //ChartPanel chartPanel = new ChartPanel(createChart(createDataset()));
        //this.add(chartPanel);
    }
    public static void main(String[] args)
    {
        EventQueue.invokeLater(() -> new Menu(panel).setVisible(true));
    }

    public void update(Graphics g)
    {
        //g.setColor(Color.white);
        /*g.drawLine(1550, 30, 1575, 30);
        g.drawLine(1550, 40, 1575, 40);
        g.drawLine(1550, 50, 1575, 50);*/
        DecimalFormat df = new DecimalFormat("#.###");
        /*for(JCheckBox i: checkBoxes)
        {
            if(i.isSelected())
            {
                for(JCheckBox j: checkBoxes)
                {
                    if(i != j)
                    {
                        j.setSelected(false);
                    }
                }
            }
        }*/
        timeArea.setText("time: " + panel.time + " zs (10^-21 s)");
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
        //Arrow tempArrow = new Arrow(panel.axes.screenToCoordsX(panel.userMouse.x), panel.axes.screenToCoordsY(panel.userMouse.y), panel);
        //jacobArea.setText("Jacobian:\n   x  y  t\nP   " + tempArrow.DPDX + "  " + tempArrow.DPDY + "  3\nQ   4  5  6");
    }
    public double[] getQuaternion()
    {
        return new double[]{quatW.getValue()/1000., quatI.getValue()/1000., quatJ.getValue()/1000., quatK.getValue()/1000.};
    }
}
