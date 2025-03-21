import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Proton
{
    int charge = 1;
    double x;
    double y;
    double fX;
    double fY;
    double mass = 1.67262192 * Math.pow(10, -27);
    double centerX;
    double centerY;
    int fieldArrowNum = 50;
    int fieldLineNum = 6;
    ArrayList<FieldLine> fieldLines = new ArrayList<>();
    ArrayList<Double[]> movements = new ArrayList<>();
    ArrayList<Double> times = new ArrayList<>();
    public Proton(double initX, double initY, double time)
    {
        x = initX - 25;
        y = initY - 25;
        centerX = initX;
        centerY = initY;
        for(int j = 0; j < fieldLineNum; j++)
        {
            fieldLines.add(new FieldLine(centerX + 5*Math.cos(j*2*Math.PI/fieldLineNum), centerY + 5*Math.sin(j*2*Math.PI/fieldLineNum)));
        }
        movements.add(new Double[]{x, y, time});
        times.add(time);
    }
    public void update(Graphics g, Panel panel)
    {
        Point2D.Double tempPoint = panel.getForce(x, y, true, charge);
        fX += tempPoint.x * Math.cos(tempPoint.y);
        fY += tempPoint.x * Math.sin(tempPoint.y);
        if(panel.moveObjects && !(panel.parentSim.userPressed && getBounds().contains(panel.parentSim.lastPoint)))
        {
            x += fX * panel.timeStep * Math.pow(10, -21) / mass;
            y += fY * panel.timeStep * Math.pow(10, -21) / mass;
        }
        else if(panel.parentSim.userPressed && getBounds().contains(panel.parentSim.lastPoint))
        {
            fX = 0;
            fY = 0;
        }
        g.setColor(new Color(255,0,0));
        //System.out.println(x + " " + y);
        g.fillOval((int)x - 25, (int)y - 25, 50, 50);
    }
    public void updateCenter()
    {
        centerX = x;
        centerY = y;
        for(int j = 0; j < fieldLineNum; j++)
        {
            fieldLines.get(j).startX = centerX + 35*Math.cos(j*2*Math.PI/fieldLineNum);
            fieldLines.get(j).startY = centerY + 35*Math.sin(j*2*Math.PI/fieldLineNum);
        }
    }
    public void addCoords(double addX, double addY, double time)
    {
        x += addX;
        y += addY;
        centerX += addX;
        centerY += addY;
        movements.add(new Double[]{x,y,time});
        times.add(time);
    }
    public Rectangle getBounds()
    {
        return new Rectangle((int)x, (int)y, 50, 50);
    }
    public void drawFieldLines(Graphics g, Panel panel)
    {
        for(FieldLine i: fieldLines)
        {
            i.drawFieldArrows(g, panel);
        }
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
