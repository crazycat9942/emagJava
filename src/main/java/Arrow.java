import javafx.geometry.Point3D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.BasicStroke;

public class Arrow
{
    double angleTheta;
    double anglePhi;
    double x;
    double y;
    double length = 10;
    double actualCharge = 1.602176634E-19;
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
        Point3D posPoint = panel.rotate(panel.STCX(x),panel.STCY(y),0);
        posPoint = new Point3D(panel.STCX(x), panel.STCY(y),panel.centerOfMass().getZ());
        Point3D tempPoint = panel.getForce(posPoint.getX(), posPoint.getY(), posPoint.getZ(), true, actualCharge, null);
        //Point3D tempPoint = panel.getForce(x,y,0,true,1);
        if(tempPoint.getX() != 0)
        {
            magnitude = tempPoint.getX();
            angleTheta = -tempPoint.getY();
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
            if(endX + endY != 0) {
                g2d.drawLine((int) x, (int) y, endX, endY);
                g2d.drawPolygon(new int[]{leftX, rightX, topX}, new int[]{leftY, rightY, topY}, 3);
            }
        }
    }
    public void draw(Graphics g, Panel panel, Point userMouse)
    {
        //Point3D tempPoint = panel.getForce2(panel.screenToCoordsX(userMouse.x), panel.screenToCoordsY(userMouse.y), 0, true, 1);
        Point3D tempPoint = panel.getForce(panel.STCX(userMouse.x), panel.STCY(userMouse.y), panel.centerOfMass().getZ(), true, actualCharge, null);
        if(tempPoint.getX() != 0)
        {
            //System.out.println(tempPoint);
            magnitude = tempPoint.getX();
            angleTheta = -tempPoint.getY();
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
