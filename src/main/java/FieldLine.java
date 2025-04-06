import javafx.geometry.Point3D;

import java.awt.Graphics;
import java.util.ArrayList;

public class FieldLine
{
    double startX;
    double startY;
    ArrayList<FieldArrow> fieldArrows = new ArrayList<>();
    int fieldArrowNum = 200;
    int stopDrawing = -1;
    public FieldLine(double initX, double initY)
    {
        startX = initX;
        startY = initY;
        for(int i = 0; i < fieldArrowNum; i++)
        {
            fieldArrows.add(new FieldArrow(startX, startY, this));
        }
    }
    public void drawFieldArrows(Graphics g, Panel panel)
    {
        double fieldX = startX;
        double fieldY = startY;
        stopDrawing = -1;
        for(int i = 0; i < fieldArrowNum; i++)
        {
            if(stopDrawing == -1 || stopDrawing == i + 1)
            {
                //System.out.println(fieldX + " " + fieldY);
                Point3D tempPoint = panel.getForce(fieldX, fieldY, 0, false, 1);
                FieldArrow currArrow = fieldArrows.get(i);
                currArrow.magnitude = tempPoint.getX();
                //System.out.println(i + " " + tempArrow.angle);
                currArrow.angle = tempPoint.getY();
                currArrow.setCoords(fieldX, fieldY);
                currArrow.endX = currArrow.x + currArrow.length * Math.cos(currArrow.angle) / 3;
                currArrow.endY = currArrow.y + currArrow.length * Math.sin(currArrow.angle) / 3;
                if (i % 24 == 12) {
                    currArrow.drawMidpointArrow = true;
                }
                currArrow.update(g, panel);
                fieldX = currArrow.endX;
                fieldY = currArrow.endY;
            }
        }
    }
}
