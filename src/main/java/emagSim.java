import javafx.geometry.Point3D;
import org.apache.commons.math3.complex.Quaternion;

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
import java.awt.event.*;
import java.awt.geom.Point2D;
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
    Point2D lastCenter = new Point2D.Double(0,0);
    double centerX = 0;
    double centerY = 0;
    double maxCoordX;
    double minCoordX;
    double maxCoordY;
    double minCoordY;
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

        maxCoordX = 800 * panel.mpp;
        minCoordX = -maxCoordX;
        maxCoordY = 450 * panel.mpp;
        minCoordY = -maxCoordY;

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
                boolean temp = false;
                if (lastPoint != null)//if lastpoint is defined
                {
                    for(Electron e: panel.electrons)
                    {
                        if (e.getBounds(panel).contains(m.getPoint()) || (e.getBounds(panel).contains(lastPoint) && userPressed))
                        {
                            e.addCoords(panel.STCX(m.getPoint().x) - panel.STCX(lastPoint.x), panel.STCY(m.getPoint().y) - panel.STCY(lastPoint.y), 0, panel.time);
                            frame.repaint();
                            temp = true;
                        }
                    }
                    for(Proton p: panel.protons)
                    {
                        if(p.getBounds(panel).contains(m.getPoint()) || (p.getBounds(panel).contains(lastPoint) && userPressed))
                        {
                            p.addCoords(panel.STCX(m.getPoint().x) - panel.STCX(lastPoint.x), panel.STCY(m.getPoint().y) - panel.STCY(lastPoint.y), 0, panel.time);
                            frame.repaint();
                            temp = true;
                        }
                    }
                    for(Neutron n: panel.neutrons)
                    {
                        if(n.getBounds(panel).contains(m.getPoint()) || (n.getBounds(panel).contains(lastPoint) && userPressed))
                        {
                            n.addCoords(panel.STCX(m.getPoint().x) - panel.STCX(lastPoint.x), panel.STCY(m.getPoint().y) - panel.STCY(lastPoint.y), 0, panel.time);
                            frame.repaint();
                            temp = true;
                        }
                    }
                }
                if(!temp)
                {
                    panel.panChanged = true;
                    lastCenter = new Point2D.Double(centerX, centerY);
                    double addX = -(panel.screenToCoordsX(m.getX())-panel.screenToCoordsX(lastPoint.x));
                    double addY = -(panel.screenToCoordsY(m.getY())-panel.screenToCoordsY(lastPoint.y));
                    centerX += addX;
                    minCoordX += addX;
                    maxCoordX += addX;
                    centerY += addY;
                    minCoordY += addY;
                    maxCoordY += addY;
                    for(Electron e: panel.electrons)
                    {
                        e.addCoords(-addX, -addY, 0, panel.time);
                    }
                    for(Neutron n: panel.neutrons)
                    {
                        n.addCoords(-addX, -addY, 0, panel.time);
                    }
                    for(Proton p: panel.protons)
                    {
                        p.addCoords(-addX, -addY, 0, panel.time);
                    }
                    //lastPoint = m.getPoint();
                    //System.out.println(minCoordX + " " + minCoordY + " " + maxCoordX + " " + maxCoordY);
                }
                lastPoint = m.getPoint();
            }
        });
        frame.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                panel.prevmpp = panel.mpp;
                panel.mpp *= Math.pow(1.2,e.getPreciseWheelRotation());
                panel.updateZoom();
                panel.prevmpp = panel.mpp;
                //System.out.println(1/Math.pow(2,e.getPreciseWheelRotation()));
            }
        });
    }
}
class Panel extends JPanel {
    ArrayList<Arrow> arrows = new ArrayList<>();
    ArrayList<Electron> electrons = new ArrayList<>();
    ArrayList<Proton> protons = new ArrayList<>();
    ArrayList<Neutron> neutrons = new ArrayList<>();
    Timer timer;
    Graphics g;
    double usefulConstant = .00000000000000000000000000023070775393;//equals (1 elementary charge)^2*(4*pi*epsilon_0)^-1 in units N*m^2
    double moreUsefulConstant = 2.3070775393;//equals (1 elementary charge)^2*(4*pi*epsilon_0)^-1*(1*10^-14m)^-2 in units N
    double coulombConstant = 8.9875517862E9;//coulombs constant in units N*m^2*C^-2
    double time = 0;
    int windowX = 1600;
    int windowY = 900;
    boolean moveObjects = true;
    double timeStep = 4*.0000001;
    Menu menu = new Menu(this);
    static double mpp = 2*Math.pow(10, -13);
    //double mpp = Math.pow(10, -17)/menu.zoomSlider.getValue();//meters per pixel (1 femtometer is 10^-15 meters)
    double prevmpp = mpp;
    emagSim parentSim;
    Point userMouse;
    int frames;
    Point3D tempCOM;//center of mass temp for testing
    DecimalFormat decFormat = new DecimalFormat("0.#####E0");
    double scaling = 0.55 * Math.pow(10, -15);
    //CAMERA
    double camCoordX = 0;
    double camCoordY = 0;
    double camCoordZ = 1000*mpp;
    double camTheta = 0;
    double camPhi = Math.PI;
    //CONVERSION RATE: 1 pixel per 2*10^-18 meters
    boolean panChanged = true;
    Panel(emagSim parent)
    {
        setBackground(Color.BLACK);

        setForeground(Color.WHITE);
        parentSim = parent;
        refreshScreen();
        setSize(windowX, windowY);//0.005
        //stable orbit is when neutrons and protons are 1.3 fm (10^-15 m) away from each other
        //1.3fm in terms of pixels is 1.3 * 10^-15 / mpp
        Point3D[] points = fibonacci_sphere(4);
        tempCOM = centerOfMass();
        points = fibonacci_sphere(2);
        for(int i = 50; i < windowX; i = i + 50)
        {
            for(int j = 50; j < windowY; j = j + 50)
            {
                arrows.add(new Arrow(i + 0.0001, j+0.0001));
            }
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
    public Point3D getForce(double coordX, double coordY, double coordZ, boolean strongForce, double actualCharge)
    {
        double pX = 0;
        double pY = 0;
        double pZ = 0;
        for(Electron e: electrons)
        {
            if(e.coordX != coordX || e.coordY != coordY || e.coordZ != coordZ && !Double.isNaN(e.coordX + e.coordY + e.coordZ))
            {
                if(menu.addEmagForce.isSelected())
                {
                    double lightZepto = Math.sqrt((Math.pow(e.coordX - coordX, 2) + Math.pow(e.coordY - coordY, 2) + Math.pow(e.coordZ - coordZ, 2))) / 299792458;//time it takes in zs (10^-21) for light to travel r pixels
                    //System.out.println(lightZepto);
                    Double[] posAtTime = e.getPosAtTime(time);
                    double r12X = coordX - posAtTime[0];
                    double r12Y = coordY - posAtTime[1];
                    double r12Z = coordZ - posAtTime[2];
                    double r212 = Math.pow(r12X, 2) + Math.pow(r12Y, 2) + Math.pow(r12Z, 2);
                    pX += coulombConstant * actualCharge * e.actualCharge * r12X / r212;
                    pY += coulombConstant * actualCharge * e.actualCharge * r12Y / r212;
                    pZ += coulombConstant * actualCharge * e.actualCharge * r12Z / r212;
                }
            }
        }
        for(Proton p: protons) {
            //System.out.println(p.x + " " + p.y + " " + p.z);
            if ((p.coordX != coordX || p.coordY != coordY || p.coordZ != coordZ) && !Double.isNaN(p.coordX + p.coordY + p.coordZ))//not adding force of the same proton
            {
                if (menu.addEmagForce.isSelected()) {
                    double lightZepto = Math.pow((Math.pow(p.coordX - coordX, 2) + Math.pow(p.coordY - coordY, 2)), 0.5) / 299792458;
                    Double[] posAtTime = p.getPosAtTime(time - lightZepto);
                    double r12X = coordX - posAtTime[0];
                    double r12Y = coordY - posAtTime[1];
                    double r12Z = coordZ - posAtTime[2];
                    double r212 = Math.pow(r12X, 2) + Math.pow(r12Y, 2) + Math.pow(r12Z, 2);
                    pX += coulombConstant * actualCharge * p.actualCharge * r12X / r212;
                    pY += coulombConstant * actualCharge * p.actualCharge * r12Y / r212;
                    pZ += coulombConstant * actualCharge * p.actualCharge * r12Z / r212;
                }
                if(menu.addStrongForce.isSelected())
                {
                    if (strongForce) {
                        double angBetweenTheta = Math.signum(p.coordY - coordY) * Math.acos((p.coordX - coordX) / Math.sqrt(Math.pow(p.coordX - coordX, 2) + Math.pow(p.coordY - coordY, 2)));
                        double angBetweenPhi = Math.acos((p.coordZ - coordZ) / Math.sqrt(Math.pow(p.coordX - coordX, 2) + Math.pow(p.coordY - coordY, 2) + Math.pow(p.coordZ - coordZ, 2)));
                        double reidForce = getReidForce(Math.sqrt(Math.pow(p.coordX - coordX, 2) + Math.pow(p.coordY - coordY, 2) + Math.pow(p.coordZ - coordZ, 2)));
                        //System.out.println(reidForce);
                        pX += reidForce * Math.sin(angBetweenPhi) * Math.cos(angBetweenTheta);
                        pY += reidForce * Math.sin(angBetweenPhi) * Math.sin(angBetweenTheta);
                        pZ += reidForce * Math.cos(angBetweenPhi);
                    }
                }
            }
        }
        for(Neutron n: neutrons) {
            //System.out.println(p.x + " " + p.y + " " + p.z);
            if ((n.coordX != coordX || n.coordY != coordY || n.coordZ != coordZ) && !Double.isNaN(n.coordX + n.coordY + n.coordZ))//not adding force of the same proton
            {
                if(menu.addStrongForce.isSelected())
                {
                    if (strongForce) {
                        double angBetweenTheta = Math.signum(n.coordY - coordY) * Math.acos((n.coordX - coordX) / Math.sqrt(Math.pow(n.coordX - coordX, 2) + Math.pow(n.coordY - coordY, 2)));
                        double angBetweenPhi = Math.acos((n.coordZ - coordZ) / Math.sqrt(Math.pow(n.coordX - coordX, 2) + Math.pow(n.coordY - coordY, 2) + Math.pow(n.coordZ - coordZ, 2)));
                        double reidForce = getReidForce(Math.sqrt(Math.pow(n.coordX - coordX, 2) + Math.pow(n.coordY - coordY, 2) + Math.pow(n.coordZ - coordZ, 2)));
                        //System.out.println(reidForce);
                        pX += reidForce * Math.sin(angBetweenPhi) * Math.cos(angBetweenTheta);
                        pY += reidForce * Math.sin(angBetweenPhi) * Math.sin(angBetweenTheta);
                        pZ += reidForce * Math.cos(angBetweenPhi);
                    }
                }
            }
        }
        double magnitude = Math.sqrt(Math.pow(pX, 2) + Math.pow(pY, 2) + Math.pow(pZ, 2));//20*Math.random() + 1;
        double angleTheta = Math.signum(pY) * Math.acos(pX/Math.sqrt(Math.pow(pX, 2) + Math.pow(pY, 2)));
        double anglePhi = Math.acos(pZ/Math.sqrt(Math.pow(pX, 2) + Math.pow(pY, 2) + Math.pow(pZ, 2)));
        return new Point3D(magnitude, angleTheta, anglePhi);
    }

    public double getReidForce(double radius)
    {
        //System.out.println(radius);
        radius = radius * Math.pow(10, 15);
        double firstTerm = (1/radius) * (-10.463 * Math.exp(-radius) - 1650.6 * Math.exp(-4 * radius) + 6484.2 * Math.exp(-7 * radius));
        double secondTerm = -(10.463 * Math.exp(-radius) + 6602.4 * Math.exp(-4 * radius) - 45389.4 * Math.exp(-7 * radius));
        return (1/radius) * (firstTerm + secondTerm);
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
    @Override
    protected void paintComponent(Graphics gInit) {
        super.paintComponent(gInit);
        g = gInit;
        menu.update(g);
        updateElectronForce();
        updateProtonForce();
        updateNeutronForce();
        updateElectrons();
        updateProtons();
        updateNeutrons();
        if(menu.arrows.isSelected()) {
            updateArrows();
        }
        tempCOM = centerOfMass();
        g.setFont(new Font("Calibri", Font.PLAIN, 30));
        g.setColor(new Color(120, 0, 200));
        userMouse = MouseInfo.getPointerInfo().getLocation();
        Point3D forceAtMouse = getForce(STCX(userMouse.getX()), STCY(userMouse.getY()), centerOfMass().getZ(), true, 1.602176634E-19);
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
        Mesh mesh = new Mesh();
        mesh.tris.add(new Triangle(new Point3D[]{new Point3D(0,0,0), new Point3D(0, 1, 0), new Point3D(1, 1, 0)}));

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
        panChanged = false;
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
        return new Dimension(windowX, windowY);
    }
    //CONVERSION AND TRANSFORMATION METHODS
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
    public Point3D rotate(double x, double y, double z, double alpha, double beta, double gamma)
    {//alpha is euler angle for rotating around x axis, beta for y, gamma for z
        //does in order of x rotation matrix, then y, then z (non-commutative)
        double[] tempQuat = menu.getQuaternion();
        Quaternion q = new Quaternion(tempQuat[0], tempQuat[1], tempQuat[2], tempQuat[3]).normalize();
        tempQuat = ((q.getInverse()).multiply(new Quaternion(0, x, y, z)).multiply(q)).getVectorPart();
        return new Point3D(tempQuat[0], tempQuat[1], tempQuat[2]);
    }
    public Point3D[] rotate(Point3D[] initPoints)
    {
        double[] tempQuat = menu.getQuaternion();
        Quaternion q = new Quaternion(tempQuat[0], tempQuat[1], tempQuat[2], tempQuat[3]).normalize();
        Quaternion invQ = q.getInverse();
        for(int i = 0; i < initPoints.length; i++)
        {
            Quaternion tempQuat2 = new Quaternion(0, initPoints[i].getX(), initPoints[i].getY(), initPoints[i].getZ());
            double[] temp = invQ.multiply(tempQuat2).multiply(q).getVectorPart();
            initPoints[i] = new Point3D(temp[0], temp[1], temp[2]);
        }
        return initPoints;
    }
    public double screenToCoordsX(double screenX)
    {
        return (parentSim.maxCoordX - parentSim.minCoordX) *(screenX - 800)/1600;
        //return (0.5 * windowX * mpp) * (screenX - 0.5 * windowX)/(0.5 * windowX);
    }
    public double screenToCoordsY(double screenY)
    {
        return -(parentSim.maxCoordY - parentSim.minCoordY) *(screenY - 450)/900;
        //return (0.5 * -windowY * mpp) * (screenY - 0.5 * windowY)/(0.5 * windowY);
    }
    public double coordsToScreenX(double coordX)
    {
        return coordX*1600/(parentSim.maxCoordX - parentSim.minCoordX) + 800;
        //return (coordX * 0.5 * windowX/(0.5 * windowX * mpp)) + (0.5 * windowX);
    }
    public double coordsToScreenY(double coordY)
    {
        return -coordY*900/(parentSim.maxCoordY - parentSim.minCoordY) + 450;
        //return (-0.5 * coordY * windowY / (0.5 * windowY * mpp)) + (0.5 * windowY);
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

    //UPDATING METHODS

    public void updateZoom()
    {
        double scale = mpp/prevmpp;
        parentSim.maxCoordX = parentSim.centerX + scale * (parentSim.maxCoordX - parentSim.centerX);
        parentSim.minCoordX = parentSim.centerX - scale * (parentSim.centerX - parentSim.minCoordX);
        parentSim.maxCoordY = parentSim.centerY + scale * (parentSim.maxCoordY - parentSim.centerY);
        parentSim.minCoordY = parentSim.centerY - scale * (parentSim.centerY - parentSim.minCoordY);
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
            if(!(e instanceof WaveFunction))
            {
                e.updateForces(this);
            }
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
            p.updateCenter(this);
            if(menu.fieldLines.isSelected())
            {
                p.drawFieldLines(g, this);
            }
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

    //ALIAS METHODS
    public double cos(double input)
    {
        return Math.cos(input);
    }
    public double sin(double input)
    {
        return Math.sin(input);
    }
    public Point3D rotate(double x, double y, double z)
    {
        return rotate(x, y, z,0,0,0);// Math.toRadians(menu.rotateX.getValue()), Math.toRadians(menu.rotateY.getValue()), Math.toRadians(menu.rotateZ.getValue()));
    }
    public void printPoint3D(Point3D point)
    {
        System.out.println("(" + point.getX() + ", " + point.getY() + ", " + point.getZ() + ")");
    }
    public double CTSX(double coordX)
    {
        return coordsToScreenX(coordX);
    }
    public double CTSY(double coordY)
    {
        return coordsToScreenY(coordY);
    }
    public double STCX(double screenX)
    {
        return screenToCoordsX(screenX);
    }public double STCY(double screenY)
    {
        return screenToCoordsY(screenY);
    }
}
class Triangle
{
    Point3D[] p = new Point3D[3];
    public Triangle()
    {

    }
    public Triangle(Point3D[] pInit)
    {
        p = pInit;
    }
}
class Mesh
{
    java.util.ArrayList<Triangle> tris = new ArrayList<>();
    public Mesh()
    {

    }
    public Mesh(ArrayList<Triangle> initTris)
    {
        tris = initTris;
    }
}
