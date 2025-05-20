import javafx.geometry.Point3D;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Proton
{
    double x;
    double y;
    double z;
    double coordX;
    double coordY;
    double coordZ;
    double fX;
    double fY;
    double fZ;
    double actualCharge = 1.602176634E-19;
    double mass = 1.67262192 * Math.pow(10, -27);
    double centerX;
    double centerY;
    double centerZ;
    int fieldArrowNum = 50;
    int fieldLineNum = 12;
    int frames = 0;
    ArrayList<FieldLine> fieldLines = new ArrayList<>();
    ArrayList<Double[]> movements = new ArrayList<>();
    ArrayList<Double> times = new ArrayList<>();
    public Proton(Point3D initPos, double time, Panel panel)
    {
        x = initPos.getX() - 25 + 800;//because the only time the point version of the constructor is called is when it doesn't account for the mid of the screen
        y = initPos.getY() - 25 + 450;
        z = initPos.getZ() - 25;
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
        movements.add(new Double[]{coordX, coordY, coordZ, time});
        times.add(time);
        frames++;
    }
    public void updateForces(Panel panel)
    {
        //Point3D tempPoint = panel.getForce(x, y, z,true, charge);
        Point3D tempPoint = panel.getForce(coordX, coordY, coordZ, true, actualCharge);
        //panel.printPoint3D(tempPoint);
        if(!Double.isNaN(tempPoint.getY() + tempPoint.getZ()))
        {
            fX += tempPoint.getX() * Math.sin(tempPoint.getZ()) * Math.cos(tempPoint.getY());
            fY += tempPoint.getX() * Math.sin(tempPoint.getZ()) * Math.sin(tempPoint.getY());
            fZ += tempPoint.getX() * Math.cos(tempPoint.getZ());
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
            coordX += fX * panel.timeStep * Math.pow(10, -21) / mass;
            coordY += fY * panel.timeStep * Math.pow(10, -21) / mass;
            //System.out.println(x + " " + y + " " + z + "m" + " q" + fX);
            coordZ += fZ * panel.timeStep * Math.pow(10, -21) / mass;
        }
        else if(panel.parentSim.userPressed && getBounds(panel).contains(panel.parentSim.lastPoint))
        {
            fX = 0;
            fY = 0;
            fZ = 0;
        }
        g.setColor(new Color(255,0,0));
        //System.out.println(x + " " + y);
        //int tempRadius = Math.max(5,(int)(25*Math.pow(new Point3D(panel.camCoordX, panel.camCoordY, panel.camCoordZ).distance(getPos())/ panel.mpp, 0.5)/800));
        int tempRadius = 25;
        //System.out.println(tempRadius);
        g.fillOval((int)panel.CTSX(coordX) - tempRadius, (int)panel.CTSY(coordY) - tempRadius, 2*tempRadius, 2*tempRadius);
        movements.add(new Double[]{coordX,coordY,coordZ,time});
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
        movements.add(new Double[]{coordX,coordY,coordZ,time});
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
