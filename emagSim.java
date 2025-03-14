import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.awt.geom.Point2D;

public class emagSim extends JComponent
{
    int window_x = 1600;
    int window_y = 900;
    boolean scaleVectors = false;
    static boolean running = true;
    private static JFrame frame = new JFrame("frame");
    boolean userPressed = false;
    Point lastPoint;

    public emagSim()
    {
        frame.setSize(window_x, window_y);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Panel panel = new Panel();
        frame.add(panel);
        frame.pack();
        frame.setBackground(Color.black);
        frame.setVisible(true);
        frame.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent m)
            {
                lastPoint = m.getPoint();
                userPressed = true;
            }
            public void mouseReleased(MouseEvent m)
            {
                userPressed = false;
            }
        });
        frame.addMouseMotionListener(new MouseAdapter()
        {//panning
            public void mouseDragged(MouseEvent m)//when mouse dragged
            {
                if (lastPoint != null)//if lastpoint is defined
                {
                    for(Electron e: panel.electrons)
                    {
                        if (e.getBounds().contains(m.getPoint()) || (e.getBounds().contains(lastPoint) && userPressed))
                        {
                            e.addCoords(m.getPoint().x - lastPoint.x, m.getPoint().y - lastPoint.y, panel.time);
                            frame.repaint();
                        }
                    }
                    for(Proton p: panel.protons)
                    {
                        if(p.getBounds().contains(m.getPoint()) || (p.getBounds().contains(lastPoint) && userPressed))
                        {
                            p.addCoords(m.getPoint().x - lastPoint.x, m.getPoint().y - lastPoint.y, panel.time);
                            frame.repaint();
                        }
                    }
                }
                lastPoint = m.getPoint();
            }
        });
    }

}
class Panel extends JPanel {
    ArrayList<Arrow> arrows = new ArrayList<>();
    //ArrayList<FieldArrow> fieldArrows = new ArrayList<>();
    ArrayList<Electron> electrons = new ArrayList<>();
    ArrayList<Proton> protons = new ArrayList<>();
    Timer timer;
    Graphics g;
    double usefulConstant = .00000000000000000000000000023070775393;//equals (1 elementary charge)^2*(4*pi*epsilon_0)^-1 in units N*m^2
    double moreUsefulConstant = 2.3070775393;//equals (1 elementary charge)^2*(4*pi*epsilon_0)^-1*(1*10^-14m)^-2 in units N
    double time = 0;
    int window_x = 1600;
    int window_y = 900;
    //CONVERSION RATE: 1 pixel per 1*10^-14 meters
    Panel()
    {
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);

        refreshScreen();
        setSize(window_x, window_y);
        electrons.add(new Electron(800, 450, time));
        electrons.add(new Electron(300, 900, time));
        protons.add(new Proton(400, 400, time));
        protons.add(new Proton(600, 900, time));
        for(int i = 50; i < window_x; i = i + 50)
        {
            for(int j = 50; j < window_y; j = j + 50)
            {
                arrows.add(new Arrow(i, j));
            }
        }
    }
    public void updateArrows()
    {
        for(Arrow i: arrows)
        {
            i.update(g, this);
        }
    }
    public void updateElectrons()
    {
        for(Electron e: electrons)
        {
            e.update(g);
        }
    }
    public void updateProtons()
    {
        for(Proton p: protons)
        {
            p.update(g);
            p.updateCenter();
            p.drawFieldLines(g, this);
        }
    }
    public Point2D.Double getForce(double x, double y)
    {
        double fX = 0;
        double fY = 0;
        for(Electron e: electrons)
        {
            double lightZepto = Math.pow((Math.pow(e.x - x, 2) + Math.pow(e.y - y, 2)), 0.5)*0.0333564095198;//time it takes in zs (10^-21) for light to travel r pixels
            //System.out.println(lightZepto);
            Double[] posAtTime = e.getPosAtTime(time - lightZepto);
            double r21X = posAtTime[0] - x;
            double r21Y = posAtTime[1] - y;
            double r212 = Math.pow(r21X, 2) + Math.pow(r21Y, 2);
            fX += r21X / r212;
            fY += r21Y / r212;
        }
        for(Proton p: protons)
        {
            double lightZepto = Math.pow((Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)), 0.5)*0.0333564095198;
            Double[] posAtTime = p.getPosAtTime(time - lightZepto);
            double r21X = posAtTime[0] - x;
            double r21Y = posAtTime[1] - y;
            double r212 = Math.pow(r21X, 2) + Math.pow(r21Y, 2);
            fX -= r21X / r212;
            fY -= r21Y / r212;
        }
        double magnitude = Math.pow(fX, 2) + Math.pow(fY, 2);//20*Math.random() + 1;
        double angle = Math.atan2(fY, fX);//2*Math.PI*Math.random();
        return new Point2D.Double(magnitude, angle);
    }
    public void vector_to_rgb(Arrow i)
    {
        double maxMag = maxMagnitude();
        i.angle = i.angle % (2 * Math.PI);
        if (i.angle < 0)
        {
            i.angle += 2 * Math.PI;
        }
        i.color = hslToRGB((float)(180f*i.angle/Math.PI), (float) (i.magnitude / maxMag), (float) (i.magnitude / maxMag));
    }
    public Color hslToRGB(float h, float s, float l)
    {//h is [0,360) (deg), s is [0, 1] l is [0, 1]
        l = 0.8f*l + 0.2f;
        s = 0.7f*s + 0.3f;
        float C = (1-Math.abs(2*l - 1f))*s;
        float H_ = h/60;
        float X = C*(1-Math.abs(H_%2f - 1));
        float m = l - (C/2f);
        float R1;
        float G1;
        float B1;

        if(0f <= H_ && H_ < 1f){R1 = C; G1 = X; B1 = 0f;}
        else if(1f <= H_ && H_ < 2f){R1 = X; G1 = C; B1 = 0f;}
        else if(2f <= H_ && H_ < 3f){R1 = 0f; G1 = C; B1 = X;}
        else if(3f <= H_ && H_ < 4f){R1 = 0f; G1 = X; B1 = C;}
        else if(4f <= H_ && H_ < 5f){R1 = X; G1 = 0f; B1 = C;}
        else{R1 = C; G1 = 0f; B1 = X;}//(5f <= H_ && H_ < 6f)
        return new Color(R1 + m, G1 + m, B1 + m);
    }
    public double maxMagnitude()
    {
        double max = 0;
        for(Arrow i: arrows)
        {
            if(i.magnitude > max)
            {
                max = i.magnitude;
            }
        }
        return max;
    }
    @Override
    protected void paintComponent(Graphics gInit) {
        super.paintComponent(gInit);
        g = gInit;
        updateElectrons();
        updateProtons();
        updateArrows();
        g.setFont(new Font("Calibri", Font.PLAIN, 30));
        g.setColor(new Color(120, 0, 200));
        Point userMouse = MouseInfo.getPointerInfo().getLocation();
        Point2D.Double forceAtMouse = getForce(userMouse.getX(), userMouse.getY());
        if(!Double.isNaN(forceAtMouse.getX()))
        {
            double shortenedForce = BigDecimal.valueOf(forceAtMouse.getX()).round(new MathContext(5)).doubleValue();
            g.drawString("Force at cursor: " + shortenedForce + " N", 30, 50);
        }
        else
        {
            g.drawString("Force at cursor: Infinity N", 30, 50);
        }

        //BUTTONS
        //g.drawRect(1400, 10, 175, 40);
        //g.drawString("Add electron", 1410, 40);

        g.setColor(Color.white);
        g.drawLine(1400, 850, 1500, 850);
        g.drawLine(1400, 850, 1400, 830);
        g.drawLine(1500, 850, 1500, 830);
        g.setFont(new Font("Calibri", Font.PLAIN, 15));
        g.drawString("10^-12 m", 1425, 870);
        g.drawString(time + " zeptoseconds (10^-21)", 50, 870);
        time += 0.5;

    }
    public void refreshScreen() {
        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.setRepeats(true);
        // Aprox. 60 FPS
        timer.setDelay(17);
        timer.start();
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(window_x, window_y);
    }
}
