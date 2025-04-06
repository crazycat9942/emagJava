import javafx.geometry.Point3D;

import java.awt.*;

public class Arrow
{
    double angleTheta;
    double anglePhi;
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
        Point3D tempPoint = panel.getForce(x, y, 0, true, 1);
        if(tempPoint.getX() != 0)
        {
            magnitude = tempPoint.getX();
            angleTheta = tempPoint.getY();
            anglePhi = tempPoint.getZ();
            panel.vector_to_rgb(this);
            int endX = (int) (x + 2 * length * Math.cos(angleTheta));
            int endY = (int) (y + 2 * length * Math.sin(angleTheta));
            int leftX = endX - (int) (length * Math.sin(angleTheta));
            int leftY = endY + (int) (length * Math.cos(angleTheta));
            int rightX = endX + (int) (length * Math.sin(angleTheta));
            int rightY = endY - (int) (length * Math.cos(angleTheta));
            int topX = endX + (int) (length * Math.cos(angleTheta));
            int topY = endY + (int) (length * Math.sin(angleTheta));
            g.setColor(color);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine((int) x, (int) y, endX, endY);
            g2d.drawPolygon(new int[]{leftX, rightX, topX}, new int[]{leftY, rightY, topY}, 3);
        }
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
        //Point3D tempPoint = panel.getForce2(panel.screenToCoordsX(userMouse.x), panel.screenToCoordsY(userMouse.y), 0, true, 1);
        Point3D tempPoint = panel.getForce(userMouse.x, userMouse.y, 0, true, 1);
        if(tempPoint.getX() != 0)
        {
            //System.out.println(tempPoint);
            magnitude = tempPoint.getX();
            angleTheta = tempPoint.getY();
            anglePhi = tempPoint.getZ();
            panel.vector_to_rgb(this);
            int endX = (int) (x + 2 * length * Math.cos(angleTheta));
            int endY = (int) (y + 2 * length * Math.sin(angleTheta));
            int leftX = endX - (int) (length * Math.sin(angleTheta));
            int leftY = endY + (int) (length * Math.cos(angleTheta));
            int rightX = endX + (int) (length * Math.sin(angleTheta));
            int rightY = endY - (int) (length * Math.cos(angleTheta));
            int topX = endX + (int) (length * Math.cos(angleTheta));
            int topY = endY + (int) (length * Math.sin(angleTheta));
            g.setColor(color);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine((int) x, (int) y, endX, endY);
            g2d.drawPolygon(new int[]{leftX, rightX, topX}, new int[]{leftY, rightY, topY}, 3);
        }
    }
}
