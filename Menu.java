import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
class Menu extends JFrame
{
    static Panel panel;
    ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
    JCheckBox addStrongForce;
    JCheckBox addEmagForce;
    JTextArea timeArea;
    //JTextArea curlArea;
    //JTextArea divArea;
    //JTextArea magArea;
    //JSlider zoomSlider;
    //JTextArea jacobArea;
    //JTextArea zoomArea;
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
        //zoomSlider = new JSlider(1, 10);
        //System.out.println(colorWithCurl.isSelected());
        ml.setLayoutConstraints("width 200px!, wrap");
        this.add(addStrongForce);
        //gbc.gridy = GridBagConstraints.FIRST_LINE_END;
        this.add(addEmagForce);
        timeArea = new JTextArea("time: " + panel.time + " zs (10^-21 s)");
        /*curlArea = new JTextArea();
        divArea = new JTextArea();
        magArea = new JTextArea();
        jacobArea = new JTextArea();*/
        //zoomArea = new JTextArea();
        this.add(timeArea, "bottom");
        /*this.add(curlArea);
        this.add(divArea);
        this.add(magArea);*/
        //this.add(zoomArea);
        //this.add(zoomSlider);
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
        //zoomArea.setText("zoom: " + zoomSlider.getValue() + "x");
    }
}
