import java.awt.*;
import java.awt.geom.Point2D;
import java.math.*;
public class FieldArrow
{
    double angle;
    double x;
    double y;
    double length = 30;
    double endX;
    double endY;
    Color color;
    double magnitude;
    boolean drawMidpointArrow = false;
    FieldLine parent;
    public FieldArrow(double initX, double initY, FieldLine parentLine)
    {
        x = initX;
        y = initY;
        parent = parentLine;
    }
    public void setCoords(double userX, double userY)
    {
        x = userX;
        y = userY;
    }
    public void update(Graphics g, Panel panel)
    {
        //panel.vector_to_rgb(this);
        /*endX = (x + 2*length*Math.cos(angle));
        endY = (y + 2*length*Math.sin(angle));*/
        /*int leftX = endX - (int)(length*Math.sin(angle));
        int leftY = endY + (int)(length*Math.cos(angle));
        int rightX = endX + (int)(length*Math.sin(angle));
        int rightY = endY - (int)(length*Math.cos(angle));
        int topX = endX + (int)(length*Math.cos(angle));
        int topY = endY + (int)(length*Math.sin(angle));*/
        g.setColor(new Color(255, 255, 255));
        boolean inElectron = false;
        for(Electron e: panel.electrons)
        {
            if(e.getBounds().contains((int)endX, (int)endY))
            {
                inElectron = true;
            }
        }
        if(!inElectron)
        {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine((int) x, (int) y, (int) endX, (int) endY);

            if (drawMidpointArrow)
            {
                double midpointX = (x + endX) / 2;
                double midpointY = (y + endY) / 2;
                double leftX = midpointX - (20 * Math.sin(angle));
                double leftY = midpointY + (20 * Math.cos(angle));
                double rightX = midpointX + (20 * Math.sin(angle));
                double rightY = midpointY - (20 * Math.cos(angle));
                double topX = midpointX + (20 * Math.cos(angle));
                double topY = midpointY + (20 * Math.sin(angle));
                g.drawLine((int) leftX, (int) leftY, (int) topX, (int) topY);
                g.drawLine((int) rightX, (int) rightY, (int) topX, (int) topY);
            }
        }
        else
        {
            parent.stopDrawing = parent.fieldArrows.indexOf(this);
        }
        //g.drawPolygon(new int[] {leftX, rightX, topX},new int[] {leftY, rightY, topY}, 3);
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
}
