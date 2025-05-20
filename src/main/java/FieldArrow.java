import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class FieldArrow
{
    double angleTheta;
    double anglePhi;
    double x;
    double y;
    double z;
    double length = 15;
    double endX;
    double endY;
    double endZ;
    Color color;
    double magnitude;
    boolean drawMidpointArrow = false;
    FieldLine parent;
    public FieldArrow(double initX, double initY, double initZ, FieldLine parentLine)
    {
        x = initX;
        y = initY;
        z = initZ;
        parent = parentLine;
    }
    public void setCoords(double userX, double userY, double userZ)
    {
        x = userX;
        y = userY;
        z = userZ;
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
            if(e.getBounds(panel).contains((int)panel.CTSX(endX), (int)panel.CTSY(endY)))
            {
                inElectron = true;
            }
        }
        if(!inElectron)
        {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine((int) panel.CTSX(x), (int) panel.CTSY(y), (int) panel.CTSX(endX), (int) panel.CTSY(endY));

            if (drawMidpointArrow)
            {
                angleTheta *= -1;
                double midpointX = (panel.CTSX(x) + panel.CTSX(endX)) / 2;
                double midpointY = (panel.CTSY(y) + panel.CTSY(endY)) / 2;
                double leftX = midpointX - (20 * Math.sin(angleTheta)) * Math.sin(anglePhi);
                double leftY = midpointY + (20 * Math.cos(angleTheta) * Math.sin(anglePhi));
                double rightX = midpointX + (20 * Math.sin(angleTheta) * Math.sin(anglePhi));
                double rightY = midpointY - (20 * Math.cos(angleTheta) * Math.sin(anglePhi));
                double topX = midpointX + (20 * Math.cos(angleTheta) * Math.sin(anglePhi));
                double topY = midpointY + (20 * Math.sin(angleTheta) * Math.sin(anglePhi));
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
