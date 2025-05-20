import javafx.geometry.Point3D;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Electron
{
    double actualCharge = -1.602176634E-19;
    double x;
    double y;
    double z;
    double fX;
    double fY;
    double fZ;
    double coordX;
    double coordY;
    double coordZ;
    double mass = 9.1093897E-31;
    int frames = 0;
    ArrayList<Double[]> movements = new ArrayList<>();
    ArrayList<Double> times = new ArrayList<>();
    public Electron(Point3D initPos, double time, Panel panel)
    {
        x = initPos.getX() - 25 + 800;//because the only time the point version of the constructor is called is when it doesn't account for the mid of the screen
        y = initPos.getY() - 25 + 450;
        z = initPos.getZ() - 25;
        coordX = initPos.getX();
        coordY = initPos.getY();
        coordZ = initPos.getZ();
        movements.add(new Double[]{coordX, coordY, coordZ, time});
        times.add(time);
        frames++;
    }
    public void updateForces(Panel panel)
    {
        Point3D tempPoint = panel.getForce(coordX, coordY, coordZ, false, actualCharge);
        if(!Double.isNaN(tempPoint.getY() + tempPoint.getZ()))
        {
            fX += tempPoint.getX() * Math.sin(tempPoint.getZ()) * Math.cos(tempPoint.getY());
            fY += tempPoint.getX() * Math.sin(tempPoint.getZ()) * Math.sin(tempPoint.getY());
            fZ += tempPoint.getX() * Math.cos(tempPoint.getZ());
        }
    }
    public void update(Graphics g, double time, Panel panel)
    {
        if(panel.moveObjects && !(panel.parentSim.userPressed && getBounds(panel).contains(panel.parentSim.lastPoint)))
        {
            coordX += fX * panel.timeStep * Math.pow(10, -21) / mass;
            coordY += fY * panel.timeStep * Math.pow(10, -21) / mass;
            coordZ += fZ * panel.timeStep * Math.pow(10, -21) / mass;
        }
        else if(panel.parentSim.userPressed && getBounds(panel).contains(panel.parentSim.lastPoint))
        {
            fX = 0;
            fY = 0;
            fZ = 0;
        }
        g.setColor(new Color(255,255,0));
        //System.out.println(x + " " + y);
        //int tempRadius = Math.max(5,(int)(25*Math.pow(new Point3D(panel.camCoordX, panel.camCoordY, panel.camCoordZ).distance(getPos())/ panel.mpp, 0.5)/800));
        int tempRadius = 25;
        g.fillOval((int)panel.CTSX(coordX) - tempRadius, (int)panel.CTSY(coordY) - tempRadius, 2*tempRadius, 2*tempRadius);
        if(frames % 50 == 49 && movements.size() >= 20)//clear list of previous locations once it gets to a certain size
        {
            movements = new ArrayList<>(movements.subList(movements.size() - 20, movements.size()));
            times = new ArrayList<Double>(times.subList(movements.size() - 20, movements.size()));
        }
        movements.add(new Double[]{coordX, coordY, coordZ, time});
        times.add(time);
        frames++;
    }
    public void addCoords(double addX, double addY, double addZ, double time)
    {
        coordX += addX;
        coordY += addY;
        coordZ += addZ;
        movements.add(new Double[]{coordX,coordY,coordZ,time});
        times.add(time);
        frames++;
        //System.out.println(x + " " + y + " " + time);
    }
    public Rectangle getBounds(Panel panel)
    {
        return new Rectangle((int)panel.CTSX(coordX) - 25, (int)panel.CTSY(coordY), 50, 50);
    }
    public Point3D getPos()
    {
        return new Point3D(coordX, coordY, coordZ);
    }
    public Double[] getPosAtTime(double time)
    {
        for(int i = 1; i < times.size(); i++)
        {
            if(times.get(i) > time)
            {
                return movements.get(i-1);
            }
        }
        return movements.getLast();
    }
}
