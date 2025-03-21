import java.awt.*;
import java.awt.geom.Point2D;
import java.math.*;
public class Arrow
{
    double angle;
    double x;
    double y;
    double length = 10;
    double endX;
    double endY;
    Color color;
    double magnitude;
    public Arrow(double initX, double initY)
    {
        x = initX;
        y = initY;
    }
    public void update(Graphics g, Panel panel)
    {
        Point2D tempPoint = panel.getForce(x, y, true, 1);
        magnitude = tempPoint.getX();
        angle = tempPoint.getY();
        panel.vector_to_rgb(this);
        int endX = (int)(x + 2*length*Math.cos(angle));
        int endY = (int)(y + 2*length*Math.sin(angle));
        int leftX = endX - (int)(length*Math.sin(angle));
        int leftY = endY + (int)(length*Math.cos(angle));
        int rightX = endX + (int)(length*Math.sin(angle));
        int rightY = endY - (int)(length*Math.cos(angle));
        int topX = endX + (int)(length*Math.cos(angle));
        int topY = endY + (int)(length*Math.sin(angle));
        g.setColor(color);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine((int)x, (int)y, endX, endY);
        g2d.drawPolygon(new int[] {leftX, rightX, topX},new int[] {leftY, rightY, topY}, 3);
        /*
        endX
        length = Math.sqrt((end[0]-start[0])**2+(end[1]-start[1])**2)
        rotation =np.atan2(end[0]-start[0],end[1]-start[1])
        //end = (start[0] + length*np.cos(rotation), start[1] + length*np.sin(rotation))
        color ='white'
        //print(start)
        pygame.draw.line(window,color,(start[0],start[1]),(end[0],end[1]),5)
        leftSide =(end[0]-0.2*length*np.sin(rotation),end[1]+0.2*length*np.cos(rotation))
        rightSide =(end[0]+0.2*length*np.sin(rotation),end[1]-0.2*length*np.cos(rotation))
        top =(end[0]+0.2*length*np.cos(rotation),end[1]+0.2*length*np.sin(rotation))
        pygame.draw.polygon(window,

                vector_to_rgb(np.pi*rotation/180, length), (leftSide,rightSide,top),0)*/
    }
    public void draw(Graphics g, Panel panel, Point userMouse)
    {
        Point2D tempPoint = panel.getForce(userMouse.x, userMouse.y, true, 1);
        magnitude = tempPoint.getX();
        angle = tempPoint.getY();
        panel.vector_to_rgb(this);
        int endX = (int)(x + 2*length*Math.cos(angle));
        int endY = (int)(y + 2*length*Math.sin(angle));
        int leftX = endX - (int)(length*Math.sin(angle));
        int leftY = endY + (int)(length*Math.cos(angle));
        int rightX = endX + (int)(length*Math.sin(angle));
        int rightY = endY - (int)(length*Math.cos(angle));
        int topX = endX + (int)(length*Math.cos(angle));
        int topY = endY + (int)(length*Math.sin(angle));
        g.setColor(color);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine((int)x, (int)y, endX, endY);
        g2d.drawPolygon(new int[] {leftX, rightX, topX},new int[] {leftY, rightY, topY}, 3);
    }
}
