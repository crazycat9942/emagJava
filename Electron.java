import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

public class Electron
{
    int charge = -1;
    double x;
    double y;
    double vx;
    double vy;
    ArrayList<Double[]> movements = new ArrayList<>();
    ArrayList<Double> times = new ArrayList<>();
    public Electron(double initX, double initY, double time)
    {
        x = initX - 25;
        y = initY - 25;
        movements.add(new Double[]{x, y, time});
        times.add(time);
    }
    public void update(Graphics g)
    {
        g.setColor(new Color(255,255,0));
        //System.out.println(x + " " + y);
        g.fillOval((int)x - 25, (int)y - 25, 50, 50);
    }
    public void addCoords(double addX, double addY, double time)
    {
        x += addX;
        y += addY;
        movements.add(new Double[]{x,y,time});
        times.add(time);
        //System.out.println(x + " " + y + " " + time);
    }
    public Rectangle getBounds()
    {
        return new Rectangle((int)x - 25, (int)y, 50, 50);
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
