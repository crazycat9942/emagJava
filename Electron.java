import java.awt.*;

public class Electron
{
    int charge = -1;
    double x;
    double y;
    double vx;
    double vy;

    public Electron(double initX, double initY)
    {
        x = initX;
        y = initY;
    }
    public void update(Graphics g)
    {
        g.setColor(new Color(255,255,0));
        g.fillOval((int)x, (int)y, 50, 50);
    }
}
