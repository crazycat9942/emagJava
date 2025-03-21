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
    private static JFrame frame = new JFrame("shitter");
    boolean userPressed = false;
    Point lastPoint;

    public emagSim()
    {
        frame.setSize(window_x, window_y);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Panel panel = new Panel(this);
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
                    for(Neutron n: panel.neutrons)
                    {
                        if(n.getBounds().contains(m.getPoint()) || (n.getBounds().contains(lastPoint) && userPressed))
                        {
                            n.addCoords(m.getPoint().x - lastPoint.x, m.getPoint().y - lastPoint.y, panel.time);
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
    ArrayList<Neutron> neutrons = new ArrayList<>();
    Timer timer;
    Graphics g;
    double usefulConstant = .00000000000000000000000000023070775393;//equals (1 elementary charge)^2*(4*pi*epsilon_0)^-1 in units N*m^2
    double moreUsefulConstant = 2.3070775393;//equals (1 elementary charge)^2*(4*pi*epsilon_0)^-1*(1*10^-14m)^-2 in units N
    double time = 0;
    int window_x = 1600;
    int window_y = 900;
    boolean moveObjects = true;
    double timeStep = 0.0000000000001;
    double mpp = 2 * Math.pow(10, -18);//meters per pixel (1 femtometer is 10^-15 meters)
    emagSim parentSim;
    Point userMouse;
    //CONVERSION RATE: 1 pixel per 1*10^-18 meters
    Panel(emagSim parent)
    {
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);

        parentSim = parent;
        refreshScreen();
        setSize(window_x, window_y);//0.005
        //stable orbit is when neutrons and protons are 1.3 fm (10^-15 m) away from each other
        //1.3fm in terms of pixels is 1.3 * 10^-15 / mpp
        double ang = 2*Math.PI/3;
        neutrons.add(new Neutron(800 + 1.3 * Math.pow(10, -15)/(2 * mpp) * rotate(0).x, 450 + 1.3 * Math.pow(10, -15)/(2 * mpp) * rotate(0).y, time));
        //neutrons.add(new Neutron(1200, 450, time));
        protons.add(new Proton(800 + 1.3 * Math.pow(10, -15)/(2 * mpp) * rotate(ang).x , 450 + 1.3 * Math.pow(10, -15)/(2 * mpp) * rotate(ang).y, time));
        protons.add(new Proton(800 + 1.3 * Math.pow(10, -15)/(2 * mpp) * rotate(2 * ang).x, 450 + 1.3 * Math.pow(10, -15)/(2 * mpp) * rotate(2 * ang).y, time));
        electrons.add(new Electron(800 + 5.29177 * Math.pow(10, -11)/mpp, 700, time));
        //electrons.add(new Electron(300, 900, time));
        //protons.add(new Proton(400, 400, time));
        //  protons.add(new Proton(600, 900, time));
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
            e.update(g, this);
        }
    }
    public void updateProtons()
    {
        for(Proton p: protons)
        {
            p.update(g, this);
            p.updateCenter();
            //p.drawFieldLines(g, this);
        }
    }
    public void updateNeutrons()
    {
        for(Neutron n: neutrons)
        {
            n.update(g, this);
            n.updateCenter();
        }
    }
    public Point2D.Double rotate(double angle)
    {
        return new Point2D.Double(Math.cos(angle), Math.sin(angle));
    }
    public Point2D.Double getForce(double x, double y, boolean strongForce, int charge)//x and y are in pixels
    {
        double fX = 0;
        double fY = 0;
        for(Electron e: electrons)
        {
            if(e.x != x && e.y != y)
            {
                double lightZepto = Math.pow((Math.pow(e.x - x, 2) + Math.pow(e.y - y, 2)), 0.5) * mpp / 299792458;//time it takes in zs (10^-21) for light to travel r pixels
                //System.out.println(lightZepto);
                Double[] posAtTime = e.getPosAtTime(time - lightZepto);
                double r21X = posAtTime[0] - x;
                double r21Y = posAtTime[1] - y;
                double r212 = Math.pow(r21X, 2) + Math.pow(r21Y, 2);
                fX += charge * r21X / r212;
                fY += charge * r21Y / r212;
            }
        }
        for(Proton p: protons)
        {
            if(p.x != x && p.y != y)//not adding force of the same proton
            {
                double lightZepto = Math.pow((Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)), 0.5)*mpp/299792458;
                Double[] posAtTime = p.getPosAtTime(time - lightZepto);
                double r21X = posAtTime[0] - x;
                double r21Y = posAtTime[1] - y;
                double r212 = Math.pow(r21X, 2) + Math.pow(r21Y, 2);
                fX -= charge * r21X / r212;
                fY -= charge * r21Y / r212;
                //yukawa force from the strong interaction between nucleons
                //double yukawaForce = getYukawaForce(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //if(Math.random() < 0.0005) {
                //    System.out.println(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //}
                if(strongForce)
                {
                    double angBetween = Math.atan2((p.y - y), (p.x - x));
                    double reidForce = getReidForce(Math.sqrt(Math.pow(mpp * (p.x - x), 2) + Math.pow(mpp * (p.y - y), 2)));
                    fX -= reidForce * Math.cos(angBetween);
                    fY -= reidForce * Math.sin(angBetween);
                }
            }
        }
        for(Neutron n: neutrons)
        {
            if(n.x != x && n.y != y)//not adding force of the same proton
            {
                double lightZepto = Math.pow((Math.pow(n.x - x, 2) + Math.pow(n.y - y, 2)), 0.5)*mpp/299792458;
                Double[] posAtTime = n.getPosAtTime(time - lightZepto);
                //yukawa force from the strong interaction between nucleons
                //double yukawaForce = getYukawaForce(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //if(Math.random() < 0.0005) {
                //    System.out.println(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //}
                if(strongForce)
                {
                    double angBetween = Math.atan2((n.y - y), (n.x - x));
                    double reidForce = getReidForce(Math.sqrt(Math.pow(mpp * (n.x - x), 2) + Math.pow(mpp * (n.y - y), 2)));
                    fX -= reidForce * Math.cos(angBetween);
                    fY -= reidForce * Math.sin(angBetween);
                }
            }
        }
        double magnitude = Math.pow(fX, 2) + Math.pow(fY, 2);//20*Math.random() + 1;
        double angle = Math.atan2(fY, fX);//2*Math.PI*Math.random();
        return new Point2D.Double(magnitude, angle);
    }
    public double getYukawaForce(double radius)
    {
        double constantVal = 8.331318496 * Math.pow(10, -12);
        return -constantVal * Math.exp(-radius/(1.4135170512 * Math.pow(10, -15))) * ((1.4135170512 * Math.pow(10, -15)/Math.pow(radius, 2)) + 1/radius);
    }
    public double getReidForce(double radius)
    {
        radius = radius * Math.pow(10, 15);
        double firstTerm = (1/radius) * (-10.463 * Math.exp(-radius) - 1650.6 * Math.exp(-4 * radius) + 6484.2 * Math.exp(-7 * radius));
        double secondTerm = -(10.463 * Math.exp(-radius) + 6602.4 * Math.exp(-4 * radius) - 45389.4 * Math.exp(-7 * radius));
        return (1/radius) * (firstTerm + secondTerm);
    }
    public void vector_to_rgb(Arrow i)
    {
        double maxMag = maxMagnitude();
        i.angle = i.angle % (2 * Math.PI);
        if (i.angle < 0)
        {
            i.angle += 2 * Math.PI;
        }
        i.color = hslToRGB((float)(180f*i.angle/Math.PI), (float) Math.pow(i.magnitude/maxMag, 0.25), (float) Math.pow(i.magnitude/maxMag, 0.25));
    }
    public Color hslToRGB(float h, float s, float l)
    {//h is [0,360) (deg), s is [0, 1] l is [0, 1]
        l = 0.8f*l + 0.2f;
        s = 0.6f*s + 0.4f;
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
        try {
            return new Color(R1 + m, G1 + m, B1 + m);
        } catch (IllegalArgumentException e) {
            return new Color(255, 255, 255);
        }
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
        updateNeutrons();
        g.setFont(new Font("Calibri", Font.PLAIN, 30));
        g.setColor(new Color(120, 0, 200));
        userMouse = MouseInfo.getPointerInfo().getLocation();
        Point2D.Double forceAtMouse = getForce(userMouse.getX(), userMouse.getY(), true, 1);
        if(!Double.isNaN(forceAtMouse.getX()))
        {
            double shortenedForce = BigDecimal.valueOf(forceAtMouse.getX()).round(new MathContext(5)).doubleValue();
            g.drawString("Force at cursor: " + shortenedForce + " N (angle : " + (Math.toDegrees(forceAtMouse.getY()) % 360) + ")", 30, 50);
            Arrow tempArrow = new Arrow(userMouse.getX(), userMouse.getY());
            tempArrow.x = 780;
            tempArrow.y = 50;
            tempArrow.draw(g, this, userMouse);
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
        g.drawString("10^-16 m (0.2 fm)", 1425, 870);
        g.drawString(time + " zeptoseconds (10^-21)", 50, 870);
        time += timeStep;
        System.out.println(new Point2D.Double(protons.get(0).x, protons.get(0).y).distance(new Point2D.Double(neutrons.get(0).x, neutrons.get(0).y)));
        repaint();

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
