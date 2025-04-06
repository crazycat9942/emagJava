import javafx.geometry.Point3D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.Point;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
        Panel panel = new Panel(this);
        frame.add(panel);
        frame.pack();
        frame.setBackground(Color.black);
        frame.add(panel.menu.getContentPane(), BorderLayout.EAST);
        frame.pack();
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
                            e.addCoords(m.getPoint().x - lastPoint.x, m.getPoint().y - lastPoint.y, 0, panel.time);
                            frame.repaint();
                        }
                    }
                    for(Proton p: panel.protons)
                    {
                        if(p.getBounds().contains(m.getPoint()) || (p.getBounds().contains(lastPoint) && userPressed))
                        {
                            p.addCoords(m.getPoint().x - lastPoint.x, m.getPoint().y - lastPoint.y, 0, panel.time);
                            frame.repaint();
                        }
                    }
                    for(Neutron n: panel.neutrons)
                    {
                        if(n.getBounds().contains(m.getPoint()) || (n.getBounds().contains(lastPoint) && userPressed))
                        {
                            n.addCoords(m.getPoint().x - lastPoint.x, m.getPoint().y - lastPoint.y, 0, panel.time);
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
    ArrayList<FieldArrow> fieldArrows = new ArrayList<>();
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
    double timeStep = .00000000005;
    Menu menu = new Menu(this);
    double mpp = 0.2*Math.pow(10, -17);
    //double mpp = Math.pow(10, -17)/menu.zoomSlider.getValue();//meters per pixel (1 femtometer is 10^-15 meters)
    //double prevmpp = mpp;
    emagSim parentSim;
    Point userMouse;
    int frames;
    Point3D tempCOM;//center of mass temp for testing
    DecimalFormat decFormat = new DecimalFormat("0.#####E0");
    //CONVERSION RATE: 1 pixel per 2*10^-18 meters
    Panel(emagSim parent)
    {
        setBackground(Color.BLACK);

        setForeground(Color.WHITE);
        parentSim = parent;
        refreshScreen();
        setSize(window_x, window_y);//0.005
        //stable orbit is when neutrons and protons are 1.3 fm (10^-15 m) away from each other
        //1.3fm in terms of pixels is 1.3 * 10^-15 / mpp
        //Point3D[] points = fibonacci_sphere(20);
        Point3D[] points = fibonacci_sphere(238);
        double scaling = 0.55 * Math.pow(10, -15)/(mpp);
        //double scaling = 7 * Math.pow(10, -15)/(mpp);
        for(int i = 0; i < 238; i++)
        {
            if(i < 92)
            {
                protons.add(new Proton(points[i].multiply(scaling), time, this));
            }
            else
            {
                neutrons.add(new Neutron(points[i].multiply(scaling), time));
            }
        }
        /*neutrons.add(new Neutron(points[0].multiply(scaling),  time));
        protons.add(new Proton(points[1].multiply(scaling), time, this));
        neutrons.add(new Neutron(points[2].multiply(scaling), time));
        protons.add(new Proton(points[3].multiply(scaling), time, this));
        //electrons.add(new Electron(points[1].multiply(scaling), time));
        tempCOM = centerOfMass();
        points = fibonacci_sphere(2);
        scaling = 0.5 * Math.pow(10, -10)/(mpp);
        electrons.add(new Electron(points[0].multiply(scaling), time));
        electrons.add(new Electron(points[1].multiply(scaling), time));*/
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
    public void updateElectronForce()
    {
        for(Electron e: electrons)
        {
            e.updateForces(this);
        }
    }
    public void updateElectrons()
    {
        for(Electron e: electrons)
        {
            e.update(g, time,this);
        }
    }
    public void updateProtonForce()
    {
        for(Proton p: protons)
        {
            p.updateForces(this);
        }
    }
    public void updateProtons()
    {
        for(Proton p: protons)
        {
            p.update(g, this, time);
            p.updateCenter();
            //p.drawFieldLines(g, this);
        }
    }
    public void updateNeutronForce()
    {
        for(Neutron n: neutrons)
        {
            n.updateForces(this);
        }
    }
    public void updateNeutrons()
    {
        for(Neutron n: neutrons)
        {
            n.update(g, this, time);
            n.updateCenter();
        }
    }
    public Point3D[] fibonacci_sphere(int numPoints)
    {
        Point3D[] points = new Point3D[numPoints];
        for(int i = 0; i < numPoints; i++)
        {
            double phi = Math.acos(1 -2.0 * i/numPoints);
            double theta = Math.PI * (1 + Math.sqrt(5)) * i;

            double x = Math.cos(theta) * Math.sin(phi);
            double y = Math.sin(theta) * Math.sin(phi);
            double z = Math.cos(phi);
            points[i] = new Point3D(x, y, z);
            //System.out.println(new Point3D(x, y, z).distance(new Point3D(0,0,0)));
        }
        return points;
    }
    public Point3D[] getSphereVolPoints(int numPoints)
    {
        Point3D[] tempPoints = new Point3D[numPoints];
        for(int i = 0; i < numPoints; i++)
        {
            double phi = Math.random() * 2 * Math.PI;
            double cosTheta = Math.random() * 2 - 1;
            double u = Math.random();
            double theta = Math.acos(cosTheta);
            double r = Math.pow(u, 1. / 3.);
            tempPoints[i] = new Point3D(r * Math.sin(theta) * Math.cos(phi), r * Math.sin(theta) * Math.sin(phi), r * Math.cos(theta));
        }
        return tempPoints;
    }
    public Point3D getForce(double x, double y, double z, boolean strongForce, int charge)//x and y are in pixels
    {
        double fX = 0;
        double fY = 0;
        double fZ = 0;
        for(Electron e: electrons)
        {
            if(e.x != x || e.y != y || e.z != z && !Double.isNaN(e.x + e.y + e.z))
            {
                if(menu.addEmagForce.isSelected())
                {
                    double lightZepto = Math.pow((Math.pow(e.x - x, 2) + Math.pow(e.y - y, 2)), 0.5) * mpp / 299792458;//time it takes in zs (10^-21) for light to travel r pixels
                    //System.out.println(lightZepto);
                    Double[] posAtTime = e.getPosAtTime(time - lightZepto);
                    double r21X = posAtTime[0] - x;
                    double r21Y = posAtTime[1] - y;
                    double r21Z = posAtTime[2] - z;
                    double r212 = Math.pow(r21X, 2) + Math.pow(r21Y, 2) + Math.pow(r21Z, 2);
                    fX += charge * r21X / r212;
                    fY += charge * r21Y / r212;
                    fZ += charge * r21Z / r212;
                }
            }
        }
        for(Proton p: protons)
        {
            //System.out.println(p.x + " " + p.y + " " + p.z);
            if((p.x != x || p.y != y || p.z != z) && !Double.isNaN(p.x + p.y + p.z))//not adding force of the same proton
            {
                if(menu.addEmagForce.isSelected())
                {
                    double lightZepto = Math.pow((Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)), 0.5) * mpp / 299792458;
                    Double[] posAtTime = p.getPosAtTime(time - lightZepto);
                    double r21X = posAtTime[0] - x;
                    double r21Y = posAtTime[1] - y;
                    double r21Z = posAtTime[2] - z;
                    double r212 = Math.pow(r21X, 2) + Math.pow(r21Y, 2) + Math.pow(r21Z, 2);
                    fX -= charge * r21X / r212;
                    fY -= charge * r21Y / r212;
                    fZ -= charge * r21Z / r212;
                }
                //yukawa force from the strong interaction between nucleons
                //double yukawaForce = getYukawaForce(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //if(Math.random() < 0.0005) {
                //System.out.println(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //}
                if(menu.addStrongForce.isSelected())
                {
                    if (strongForce) {
                        double angBetweenTheta = Math.signum(p.y - y) * Math.acos((p.x - x) / Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)));
                        double angBetweenPhi = Math.acos((p.z - z) / Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2) + Math.pow(p.z - z, 2)));
                        double reidForce = getReidForce(Math.sqrt(Math.pow(mpp * (p.x - x), 2) + Math.pow(mpp * (p.y - y), 2) + Math.pow(mpp * (p.z - z), 2)));
                        //System.out.println(reidForce);
                        fX -= reidForce * Math.sin(angBetweenPhi) * Math.cos(angBetweenTheta);
                        fY -= reidForce * Math.sin(angBetweenPhi) * Math.sin(angBetweenTheta);
                        fZ -= reidForce * Math.cos(angBetweenPhi);
                    }
                }
            }
        }
        for(Neutron n: neutrons)
        {
            if((n.x != x || n.y != y || n.z != z) && !Double.isNaN(n.x + n.y + n.z))//not adding force of the same proton
            {
                double lightZepto = Math.pow((Math.pow(n.x - x, 2) + Math.pow(n.y - y, 2)), 0.5)*mpp/299792458;
                Double[] posAtTime = n.getPosAtTime(time - lightZepto);
                //yukawa force from the strong interaction between nucleons
                //double yukawaForce = getYukawaForce(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //if(Math.random() < 0.0005) {
                //System.out.println(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //}
                if(menu.addStrongForce.isSelected())
                {
                    if (strongForce) {
                        double angBetweenTheta = Math.signum(n.y - y) * Math.acos((n.x - x) / Math.sqrt(Math.pow(n.x - x, 2) + Math.pow(n.y - y, 2)));
                        double angBetweenPhi = Math.acos((n.z - z) / Math.sqrt(Math.pow(n.x - x, 2) + Math.pow(n.y - y, 2) + Math.pow(n.z - z, 2)));
                        double reidForce = getReidForce(Math.sqrt(Math.pow(mpp * (n.x - x), 2) + Math.pow(mpp * (n.y - y), 2) + Math.pow(mpp * (n.z - z), 2)));
                        //System.out.println(x + " ajd");
                        //System.out.println(reidForce);
                        fX -= reidForce * Math.sin(angBetweenPhi) * Math.cos(angBetweenTheta);
                        fY -= reidForce * Math.sin(angBetweenPhi) * Math.sin(angBetweenTheta);
                        fZ -= reidForce * Math.cos(angBetweenPhi);
                    }
                }
            }
        }
        double magnitude = Math.sqrt(Math.pow(fX, 2) + Math.pow(fY, 2) + Math.pow(fZ, 2));//20*Math.random() + 1;
        double angleTheta = Math.signum(fY) * Math.acos(fX/Math.sqrt(Math.pow(fX, 2) + Math.pow(fY, 2)));
        double anglePhi = Math.acos(fZ/Math.sqrt(Math.pow(fX, 2) + Math.pow(fY, 2) + Math.pow(fZ, 2)));
        //System.out.println("?" + fX + " " + fY + " " + fZ);
        //System.out.println("aodiwjsharter: " + magnitude + " " + angleTheta + " " + anglePhi);
        return new Point3D(magnitude, angleTheta, anglePhi);
    }
    public Point3D getForce2(double x, double y, double z, boolean strongForce, int charge)//x and y are in pixels
    {
        double fX = 0;
        double fY = 0;
        double fZ = 0;
        for(Electron e: electrons)
        {
            if(e.x != x || e.y != y || e.z != z && !Double.isNaN(e.x + e.y + e.z))
            {
                if(menu.addEmagForce.isSelected())
                {
                    double lightZepto = Math.pow(10, -21) * Math.sqrt((Math.pow(e.x - x, 2) + Math.pow(e.y - y, 2) + Math.pow(e.z - z, 2)))/299792458;//time it takes in zs for light to travel r pixels
                    //System.out.println(lightZepto);
                    Double[] posAtTime = e.getPosAtTime(time - lightZepto);
                    double r21X = posAtTime[0] - x;
                    double r21Y = posAtTime[1] - y;
                    double r21Z = posAtTime[2] - z;
                    double r212 = Math.pow(r21X, 2) + Math.pow(r21Y, 2) + Math.pow(r21Z, 2);
                    fX += charge * r21X / r212;
                    fY += charge * r21Y / r212;
                    fZ += charge * r21Z / r212;
                }
            }
        }
        for(Proton p: protons)
        {
            //System.out.println(p.x + " " + p.y + " " + p.z);
            if((p.x != x || p.y != y || p.z != z) && !Double.isNaN(p.x + p.y + p.z))//not adding force of the same proton
            {
                if(menu.addEmagForce.isSelected())
                {
                    double lightZepto = Math.pow(10, -21) * Math.sqrt((Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2) + Math.pow(p.z - z, 2)))/ 299792458;
                    Double[] posAtTime = p.getPosAtTime(time - lightZepto);
                    double r21X = posAtTime[0] - x;
                    double r21Y = posAtTime[1] - y;
                    double r21Z = posAtTime[2] - z;
                    double r212 = Math.pow(r21X, 2) + Math.pow(r21Y, 2) + Math.pow(r21Z, 2);
                    fX -= charge * r21X / r212;
                    fY -= charge * r21Y / r212;
                    fZ -= charge * r21Z / r212;
                }
                //yukawa force from the strong interaction between nucleons
                //double yukawaForce = getYukawaForce(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //if(Math.random() < 0.0005) {
                //System.out.println(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //}
                if(menu.addStrongForce.isSelected())
                {
                    if (strongForce) {
                        double angBetweenTheta = Math.signum(p.y - y) * Math.acos((p.x - x) / Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2)));
                        double angBetweenPhi = Math.acos((p.z - z) / Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2) + Math.pow(p.z - z, 2)));
                        double reidForce = getReidForce(Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2) + Math.pow(p.z - z, 2)));
                        //System.out.println(reidForce);
                        fX -= reidForce * Math.sin(angBetweenPhi) * Math.cos(angBetweenTheta);
                        fY -= reidForce * Math.sin(angBetweenPhi) * Math.sin(angBetweenTheta);
                        fZ -= reidForce * Math.cos(angBetweenPhi);
                    }
                }
            }
        }
        for(Neutron n: neutrons)
        {
            if((n.x != x || n.y != y || n.z != z) && !Double.isNaN(n.x + n.y + n.z))//not adding force of the same proton
            {
                double lightZepto = Math.pow((Math.pow(n.x - x, 2) + Math.pow(n.y - y, 2)), 0.5)*mpp/299792458;
                Double[] posAtTime = n.getPosAtTime(time - lightZepto);
                //yukawa force from the strong interaction between nucleons
                //double yukawaForce = getYukawaForce(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //if(Math.random() < 0.0005) {
                //System.out.println(Math.pow(10, -17) * Math.sqrt(Math.pow((p.x - x), 2) + Math.pow((p.y - y), 2)));
                //}
                if(menu.addStrongForce.isSelected())
                {
                    if (strongForce) {
                        double angBetweenTheta = Math.signum(n.y - y) * Math.acos((n.x - x) / Math.sqrt(Math.pow(n.x - x, 2) + Math.pow(n.y - y, 2)));
                        double angBetweenPhi = Math.acos((n.z - z) / Math.sqrt(Math.pow(n.x - x, 2) + Math.pow(n.y - y, 2) + Math.pow(n.z - z, 2)));
                        double reidForce = getReidForce(Math.sqrt(Math.pow(n.x - x, 2) + Math.pow(n.y - y, 2) + Math.pow(n.z - z, 2)));
                        //System.out.println(x + " ajd");
                        //System.out.println(reidForce);
                        fX -= reidForce * Math.sin(angBetweenPhi) * Math.cos(angBetweenTheta);
                        fY -= reidForce * Math.sin(angBetweenPhi) * Math.sin(angBetweenTheta);
                        fZ -= reidForce * Math.cos(angBetweenPhi);
                    }
                }
            }
        }
        double magnitude = Math.sqrt(Math.pow(fX, 2) + Math.pow(fY, 2) + Math.pow(fZ, 2));//20*Math.random() + 1;
        double angleTheta = Math.signum(fY) * Math.acos(fX/Math.sqrt(Math.pow(fX, 2) + Math.pow(fY, 2)));
        double anglePhi = Math.acos(fZ/Math.sqrt(Math.pow(fX, 2) + Math.pow(fY, 2) + Math.pow(fZ, 2)));
        //System.out.println("?" + fX + " " + fY + " " + fZ);
        //System.out.println("aodiwjsharter: " + magnitude + " " + angleTheta + " " + anglePhi);
        return new Point3D(magnitude, angleTheta, anglePhi);
    }
    public double getYukawaForce(double radius)
    {
        double constantVal = 8.331318496 * Math.pow(10, -12);
        return -constantVal * Math.exp(-radius/(1.4135170512 * Math.pow(10, -15))) * ((1.4135170512 * Math.pow(10, -15)/Math.pow(radius, 2)) + 1/radius);
    }
    public double getReidForce(double radius)
    {
        //System.out.println(radius);
        radius = radius * Math.pow(10, 15);
        double firstTerm = (1/radius) * (-10.463 * Math.exp(-radius) - 1650.6 * Math.exp(-4 * radius) + 6484.2 * Math.exp(-7 * radius));
        double secondTerm = -(10.463 * Math.exp(-radius) + 6602.4 * Math.exp(-4 * radius) - 45389.4 * Math.exp(-7 * radius));
        return (1/radius) * (firstTerm + secondTerm);
    }
    public void vector_to_rgb(Arrow i)
    {
        double maxMag = maxMagnitude();
        i.angleTheta = i.angleTheta % (2 * Math.PI);
        if (i.angleTheta < 0)
        {
            i.angleTheta += 2 * Math.PI;
        }
        i.color = hslToRGB((float)(180f*i.angleTheta/Math.PI), (float) Math.pow(i.magnitude/maxMag, 0.25), (float) Math.pow(i.magnitude/maxMag, 0.25));
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
    public Point3D centerOfMass()
    {
        double pMass = 1.67262192 * Math.pow(10, -27);
        double nMass = 1.674927471 * Math.pow(10, -27);
        double eMass = 9.1093897 * Math.pow(10, -31);
        Point3D allMasses = new Point3D(0,0,0);
        for(Proton p: protons)
        {
            allMasses = allMasses.add(p.getPos().multiply(pMass));
        }
        for(Neutron n: neutrons)
        {
            allMasses = allMasses.add(n.getPos().multiply(nMass));
        }
        for(Electron e: electrons)
        {
            allMasses = allMasses.add(e.getPos().multiply(eMass));
        }
        allMasses = allMasses.multiply(1/(protons.size()*pMass + neutrons.size()*nMass + electrons.size()*eMass));
        return allMasses;
    }
    public void printPoint3D(Point3D point)
    {
        System.out.println("(" + point.getX() + ", " + point.getY() + ", " + point.getZ() + ")");
    }
    /*public void updateZoom()
    {
        double scale = prevmpp/mpp;
        for(Proton p: protons)
        {
            p.x = scale * (p.x - 775) + 775;
            p.y = scale * (p.y - 425) + 425;
            p.z = scale * (p.z + 25) - 25;
        }
        for(Neutron n: neutrons)
        {
            n.x = scale * (n.x - 775) + 775;
            n.y = scale * (n.y - 425) + 425;
            n.z = scale * (n.z + 25) - 25;
        }
        for(Electron e: electrons)
        {
            e.x = scale * (e.x - 775) + 775;
            e.y = scale * (e.y - 425) + 425;
            e.z = scale * (e.z + 25) - 25;
        }
    }*/
    @Override
    protected void paintComponent(Graphics gInit) {
        super.paintComponent(gInit);
        g = gInit;
        menu.update(g);
        //prevmpp = mpp;
        //mpp = Math.pow(10, -17)/(menu.zoomSlider.getValue());
        //System.out.println(menu.zoomSlider.getValue());
        //updateZoom();
        updateElectronForce();
        updateProtonForce();
        updateNeutronForce();
        updateElectrons();
        updateProtons();
        updateNeutrons();
        updateArrows();
        //printPoint3D(electrons.get(0).getPos());

        //printPoint3D(centerOfMass().subtract(tempCOM));
        tempCOM = centerOfMass();
        g.setFont(new Font("Calibri", Font.PLAIN, 30));
        g.setColor(new Color(120, 0, 200));
        userMouse = MouseInfo.getPointerInfo().getLocation();
        Point3D forceAtMouse = getForce(userMouse.getX(), userMouse.getY(), 0, true, 1);
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
        g.drawString(decFormat.format(100*mpp) + " m", 1425, 870);
        g.drawString(time + " zeptoseconds (10^-21)       " + frames + " frames", 50, 870);
        time += timeStep;
        frames++;
        //System.out.println(new Point3D(protons.get(0).x, protons.get(0).y, protons.get(0).z).distance(new Point3D(neutrons.get(0).x, neutrons.get(0).y, neutrons.get(0).z)));
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
        //timer.setDelay(17);
        timer.start();
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(window_x, window_y);
    }
    public double screenToCoordsX(double screenX)
    {
        return (0.5 * window_x * mpp) * (screenX - 0.5 * window_x)/(0.5 * window_x);
    }
    public double screenToCoordsY(double screenY)
    {
        return (0.5 * -window_y * mpp) * (screenY - 0.5 * window_y)/(0.5 * window_y);
    }
    public double coordsToScreenX(double coordX)
    {
        return coordX * 0.5 * window_x/(0.5 * window_x * mpp) + 0.5 * window_x;
    }
    public double coordsToScreenY(double coordY)
    {
        return -0.5 * coordY * window_y / (0.5 * window_y * mpp) + 0.5 * window_y;
    }
}
