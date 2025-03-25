import javafx.geometry.Point3D;

import java.awt.*;
import java.util.ArrayList;

public class Electron
{
    int charge = -1;
    double x;
    double y;
    double z;
    double fX;
    double fY;
    double fZ;
    double mass = 9.1093897 * Math.pow(10, -31);
    ArrayList<Double[]> movements = new ArrayList<>();
    ArrayList<Double> times = new ArrayList<>();
    public Electron(double initX, double initY, double initZ, double time)
    {
        x = initX - 25;
        y = initY - 25;
        z = initZ - 25;
        movements.add(new Double[]{x, y, z, time});
        times.add(time);
    }
    public Electron(Point3D initPos, double time)
    {
        x = initPos.getX() - 25 + 800;//because the only time the point version of the constructor is called is when it doesn't account for the mid of the screen
        y = initPos.getY() - 25 + 450;
        z = initPos.getZ() - 25;
        movements.add(new Double[]{x, y, z, time});
        times.add(time);
    }
    public void updateForces(Panel panel)
    {
        Point3D tempPoint = panel.getForce(x, y, z, false, charge);
        if(!Double.isNaN(tempPoint.getY() + tempPoint.getZ()))
        {
            fX += tempPoint.getX() * Math.cos(tempPoint.getY());
            fY += tempPoint.getX() * Math.sin(tempPoint.getY());
            fZ += tempPoint.getZ() * Math.sin(tempPoint.getZ());
        }
    }
    public void update(Graphics g, Panel panel)
    {
        Point3D tempPoint = panel.getForce(x, y, z, false, charge);
        if(!Double.isNaN(tempPoint.getY() + tempPoint.getZ()))
        {
            fX += tempPoint.getX() * Math.cos(tempPoint.getY());
            fY += tempPoint.getX() * Math.sin(tempPoint.getY());
            fZ += tempPoint.getZ() * Math.sin(tempPoint.getZ());
        }
        if(panel.moveObjects && !(panel.parentSim.userPressed && getBounds().contains(panel.parentSim.lastPoint)))
        {
            x += fX * panel.timeStep * Math.pow(10, -21) / mass;
            y += fY * panel.timeStep * Math.pow(10, -21) / mass;
            z += fZ * panel.timeStep * Math.pow(10, -21) / mass;
        }
        else if(panel.parentSim.userPressed && getBounds().contains(panel.parentSim.lastPoint))
        {
            fX = 0;
            fY = 0;
            fZ = 0;
        }
        movements.add(new Double[]{x,y,z,panel.time});
        times.add(panel.time);
        g.setColor(new Color(255,255,0));
        //System.out.println(x + " " + y);
        g.fillOval((int)x - 25, (int)y - 25, 50, 50);
    }
    public void addCoords(double addX, double addY, double addZ, double time)
    {
        x += addX;
        y += addY;
        z += addZ;
        movements.add(new Double[]{x,y,z,time});
        times.add(time);
        //System.out.println(x + " " + y + " " + time);
    }
    public Rectangle getBounds()
    {
        return new Rectangle((int)x - 25, (int)y, 50, 50);
    }
    public Point3D getPos()
    {
        return new Point3D(x, y, z);
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
