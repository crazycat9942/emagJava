import javafx.geometry.Point3D;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Electron
{
    double actualCharge = -1.602176634E-19;///in coulombs
    /// don't think this is used for anything besides wavefunction class
    double x;
    double y;
    double z;
    /// the momentum of the electron
    double vX;
    double vY;
    double vZ;
    /// coordinates of the electron
    double coordX;
    double coordY;
    double coordZ;
    double mass = 9.1093897E-31;///in kilograms
    int frames = 0;///how many frames the electron has existed for
    int tempRadius;
    Point2D.Double drawnCoords;
    /// stores the position of the electron with time and size of movements at time of creation (used for relativity stuff)
    ArrayList<Double[]> movements = new ArrayList<>();
    ArrayList<Double> times = new ArrayList<>();
    public Electron(Point3D initPos, double time, Panel panel)
    {
        x = initPos.getX() - 25 + panel.windowX/2.;
        y = initPos.getY() - 25 + panel.windowY/2.;
        z = initPos.getZ() - 25;
        coordX = initPos.getX();
        coordY = initPos.getY();
        coordZ = initPos.getZ();
        movements.add(new Double[]{coordX, coordY, coordZ, time, (double) movements.size()});
        times.add(time);
        frames++;
    }
    public void updateForces(Panel panel)
    {
        Point3D tempPoint = panel.getForce(coordX, coordY, coordZ, false, actualCharge, this);
        if(!Double.isNaN(tempPoint.getY() + tempPoint.getZ()))
        {
            /// extracts x, y, and z components of the force vector
            Point3D v = new Point3D(vX, vY, vZ);
            double c = 299792458;
            double c2 = Math.pow(c, 2);
            double temp1 = tempPoint.getX() * Math.sin(tempPoint.getZ()) * Math.cos(tempPoint.getY()) * panel.timeStep / mass;
            double temp2 = tempPoint.getX() * Math.sin(tempPoint.getZ()) * Math.sin(tempPoint.getY()) * panel.timeStep / mass;
            double temp3 = tempPoint.getX() * Math.cos(tempPoint.getZ()) * panel.timeStep / mass;

            if(panel.menu.posRel.isSelected()) {
                Point3D uPrime = new Point3D(temp1, temp2, temp3);

                Point3D uPrimePara = new Point3D(uPrime.getX(), 0, 0);
                Point3D uPrimePerp = new Point3D(0, uPrime.getY(), uPrime.getZ());

                Point3D uPara = (uPrimePara.add(v)).multiply(1 / (1 + (v.dotProduct(uPrimePara) / c2)));
                Point3D uPerp = uPrimePerp.multiply(Math.sqrt(1 - Math.pow(v.magnitude() / c, 2))).multiply(1 / (1 + (v.dotProduct(uPrimePara) / c2)));
                Point3D u = uPara.add(uPerp);

                vX = u.getX();
                vY = u.getY();
                vZ = u.getZ();
            }
            else
            {
                vX += temp1;
                vY += temp2;
                vZ += temp3;
            }
            //System.out.println(u.magnitude());
        }
    }
    public void update(Graphics g, double time, Panel panel)
    {
        if(panel.moveObjects && !(panel.parentSim.userPressed && getBounds(panel).contains(panel.parentSim.lastPoint)))
        {
            /// updates position based on momentum
            coordX += vX * panel.timeStep;
            coordY += vY * panel.timeStep;
            coordZ += vZ * panel.timeStep;
        }
        else if(panel.parentSim.userPressed && getBounds(panel).contains(panel.parentSim.lastPoint))
        {
            /// sets momentum vector to 0 when 'picked up' by user mouse
            vX = 0;
            vY = 0;
            vZ = 0;
        }
        /// yellow
        g.setColor(new Color(255,255,0));
        //System.out.println(x + " " + y);
        /// radius in pixels of electron
        //tempRadius = Math.max(10,(int)(25*Math.pow(new Point3D(panel.camCoordX, panel.camCoordY, panel.camCoordZ).distance(getPos())/ panel.mpp, 0.5)/800));
        tempRadius = 10;
        //int tempRadius = 25;
        Point3D shiftedPos = getPos();
        shiftedPos = panel.rotatePoint3(shiftedPos, panel.camPitch, panel.camYaw);
        Point3D temp = panel.getCamVector();
        drawnCoords = new Point2D.Double(panel.CTSX(coordX) - tempRadius, panel.CTSY(coordY) - tempRadius);//top left corner
        //System.out.println((shiftedPos.getX() - panel.camCoordX) * (temp.getX() - panel.camCoordX) + (shiftedPos.getY() - panel.camCoordY) * (temp.getY() - panel.camCoordY) + (shiftedPos.getZ() - panel.camCoordZ) * (temp.getZ() - panel.camCoordZ) > 0);
        if(true || (shiftedPos.getX() - panel.camCoordX) * (temp.getX() - panel.camCoordX) + (shiftedPos.getY() - panel.camCoordY) * (temp.getY() - panel.camCoordY) + (shiftedPos.getZ() - panel.camCoordZ) * (temp.getZ() - panel.camCoordZ) < 0) {
            g.fillOval((int)panel.CTSX(coordX) - tempRadius, (int)panel.CTSY(coordY) - tempRadius, 2*tempRadius, 2*tempRadius);
            //g.fillOval((int) panel.CTSX(shiftedPos.getX()) - tempRadius, (int) panel.CTSY(shiftedPos.getY()) - tempRadius, 2 * tempRadius, 2 * tempRadius);
        }
        if(frames % 400 == 399 && movements.size() >= 100)///clear list of previous locations once it gets to a certain size
        {
            movements = new ArrayList<>(movements.subList(movements.size() - 200, movements.size()));
            times = new ArrayList<Double>(times.subList(movements.size() - 200, movements.size()));
        }
        movements.add(new Double[]{coordX, coordY, coordZ, time, (double)movements.size()});
        times.add(time);
        frames++;
    }
    public void addCoords(double addX, double addY, double addZ, double time)
    {
        coordX += addX;
        coordY += addY;
        coordZ += addZ;
        movements.add(new Double[]{coordX,coordY,coordZ,time, (double)movements.size()});
        times.add(time);
        frames++;
        //System.out.println(x + " " + y + " " + time);
    }
    /// returns a box surrounding the coordinates where electron is drawn
    public Rectangle getBounds(Panel panel)
    {
        return new Rectangle((int)panel.CTSX(coordX), (int)panel.CTSY(coordY) + 2*tempRadius, tempRadius*2, tempRadius*2);
    }
    public Point3D getPos()
    {
        return new Point3D(coordX, coordY, coordZ);
    }
    public Point3D getPosAtTime(double time)
    {
        for(int i = 1; i < times.size(); i++)
        {
            if(times.get(i) > time)
            {
                Double[] temp = movements.get(i-1);
                return new Point3D(temp[0], temp[1], temp[2]);
            }
        }
        Double[] temp = movements.getLast();
        return new Point3D(temp[0], temp[1], temp[2]);
    }
    public double[] getPosAndIndexAtTime(double time)
    {
        for(int i = 1; i < times.size(); i++)
        {
            if(times.get(i) > time)
            {
                Double[] temp = movements.get(i-1);
                return new double[]{temp[0], temp[1], temp[2], temp[3], temp[4]};
            }
        }
        Double[] temp = movements.getLast();
        return new double[]{temp[0], temp[1], temp[2], temp[3], temp[4]};
    }
    public Point3D getVelocityAtTime(double time, Panel panel)
    {
        double[] temp1 = getPosAndIndexAtTime(time);
        double[] temp2 = getPosAndIndexAtTime(time - panel.timeStep);
        //System.out.println(movements.size());
        //System.out.println(temp1[3] + " " + temp2[3]);
        double dt = time - temp2[3];

        Point3D v1 = new Point3D(temp1[0], temp1[1], temp1[2]);
        Point3D v2 = new Point3D(temp2[0], temp2[1], temp2[2]);
        return (v1.subtract(v2)).multiply(1/dt);
    }
    public double lorentzFactor(Point3D velocity)
    {
        double vel = velocity.magnitude();
        double c = 299792458;
        return 1/(Math.sqrt(1 - Math.pow(vel/c, 2)));
    }
    public double kineticEnergy(Point3D velocity)
    {
        return (lorentzFactor(velocity) - 1) * mass * Math.pow(299792458, 2);
    }
    public double vPrime(Point3D velocity)
    {
        double K = kineticEnergy(velocity);
        double c = 299792458;
        double c2 = Math.pow(c, 2);
        return c * Math.sqrt(1 - Math.pow((mass * c2)/(K + mass * c2), 2));
    }
}
