import java.awt.*;
import java.math.*;
public class Arrow
{
    double angle;
    double x;
    double y;
    double length;
    double endX;
    double endY;
    Color color;
    public Arrow(double initX, double initY)
    {
        x = initX;
        y = initY;
    }
    public void update(Graphics g)
    {//need to convert this to java code bc pasted it in from python
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

                vector_to_rgb(np.pi*rotation/180, length), (leftSide,rightSide,top),0)
    }
}
