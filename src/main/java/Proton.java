import javafx.geometry.Point3D;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Proton
{/// method and variable explanations in electron class
    double coordX;
    double coordY;
    double coordZ;
    double vX;
    double vY;
    double vZ;
    double actualCharge = 1.602176634E-19;
    double mass = 1.67262192 * Math.pow(10, -27);
    double centerX;
    double centerY;
    double centerZ;
    int fieldArrowNum = 50;
    int fieldLineNum = 12;
    int frames = 0;
    Point2D.Double drawnCoords;
    ArrayList<FieldLine> fieldLines = new ArrayList<>();
    ArrayList<Double[]> movements = new ArrayList<>();
    ArrayList<Double> times = new ArrayList<>();
    public Proton(Point3D initPos, double time, Panel panel)
    {
        coordX = initPos.getX();
        coordY = initPos.getY();
        coordZ = initPos.getZ();
        centerX = initPos.getX();
        centerY = initPos.getY();
        centerZ = initPos.getZ();
        for(int j = 0; j < fieldLineNum; j++)
        {
            fieldLines.add(new FieldLine(centerX + 25*panel.mpp*Math.cos(0.001 + j*2*Math.PI/fieldLineNum), centerY + panel.mpp * 25*panel.mpp*Math.sin(0.001 + j*2*Math.PI/fieldLineNum), centerZ + 0*5*Math.sin(0.001 + j*2*Math.PI/fieldLineNum)));
        }
        if(frames % 50 == 49 && movements.size() >= 20)
        {
            movements = new ArrayList<>(movements.subList(movements.size() - 20, movements.size()));
            times = new ArrayList<Double>(times.subList(movements.size() - 20, movements.size()));
        }
        movements.add(new Double[]{coordX, coordY, coordZ, time, (double)movements.size()});
        times.add(time);
        frames++;
    }
    public void updateForces(Panel panel)
    {
        Point3D tempPoint = panel.getForce(coordX, coordY, coordZ, true, actualCharge, this);
        //panel.printPoint3D(tempPoint);
        if(!Double.isNaN(tempPoint.getY() + tempPoint.getZ()))
        {
            Point3D v = new Point3D(vX, vY, vZ);
            double c = 299792458;
            double c2 = Math.pow(c, 2);

            double temp1 = tempPoint.getX() * Math.sin(tempPoint.getZ()) * Math.cos(tempPoint.getY());
            double temp2 = tempPoint.getX() * Math.sin(tempPoint.getZ()) * Math.sin(tempPoint.getY());
            double temp3 = tempPoint.getX() * Math.cos(tempPoint.getZ());

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
        }
    }
    public void update(Graphics g, Panel panel, double time)
    {
        //fX *= 0.999;
        //fY *= 0.999;
        //fZ *= 0.999;
        //System.out.println(fX);
        if(panel.moveObjects && !(panel.parentSim.userPressed && getBounds(panel).contains(panel.parentSim.lastPoint)))
        {
            coordX += vX * panel.timeStep;
            coordY += vY * panel.timeStep;
            coordZ += vZ * panel.timeStep;
        }
        else if(panel.parentSim.userPressed && getBounds(panel).contains(panel.parentSim.lastPoint))
        {
            vX = 0;
            vY = 0;
            vZ = 0;
        }
        g.setColor(new Color(255,0,0));
        //System.out.println(x + " " + y);
        //int tempRadius = Math.max(5,(int)(25*Math.pow(new Point3D(panel.camCoordX, panel.camCoordY, panel.camCoordZ).distance(getPos())/ panel.mpp, 0.5)/800));
        Point3D shiftedPos = getPos();
        shiftedPos = panel.rotatePoint3(shiftedPos, panel.camPitch, panel.camYaw);
        int tempRadius = 25;
        //System.out.println(tempRadius);
        //g.fillOval((int)panel.CTSX(coordX) - tempRadius, (int)panel.CTSY(coordY) - tempRadius, 2*tempRadius, 2*tempRadius);
        drawnCoords = new Point2D.Double(panel.mpp*panel.CTSX(shiftedPos.getX()) - tempRadius, panel.mpp*panel.CTSY(shiftedPos.getY()) - tempRadius);
        g.fillOval((int) panel.CTSX(shiftedPos.getX()) - tempRadius, (int) panel.CTSY(shiftedPos.getY()) - tempRadius, 2 * tempRadius, 2 * tempRadius);
        movements.add(new Double[]{coordX,coordY,coordZ,time, (double)movements.size()});
        times.add(time);
        frames++;
        //System.out.println(fieldLines);
    }
    public void updateCenter(Panel panel)
    {
        centerX = coordX;
        centerY = coordY;
        centerZ = coordZ;
        for(int j = 0; j < fieldLineNum; j++)
        {
            fieldLines.get(j).startX = centerX + panel.mpp*25*Math.cos(0.001 + j*2*Math.PI/fieldLineNum);
            fieldLines.get(j).startY = centerY + panel.mpp*25*Math.sin(0.001 + j*2*Math.PI/fieldLineNum);
            fieldLines.get(j).startZ = centerZ;
        }
    }
    public void addCoords(double addX, double addY, double addZ, double time)
    {
        coordX += addX;
        coordY += addY;
        coordZ += addZ;
        centerX += addX;
        centerY += addY;
        centerZ += addZ;
        movements.add(new Double[]{coordX,coordY,coordZ,time, (double)movements.size()});
        times.add(time);
        frames++;
    }
    public Rectangle getBounds(Panel panel)
    {
        return new Rectangle((int)panel.CTSX(coordX), (int)panel.CTSY(coordY), 50, 50);
    }
    public void drawFieldLines(Graphics g, Panel panel)
    {
        for(FieldLine i: fieldLines)
        {
            i.drawFieldArrows(g, panel);
        }
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
}
