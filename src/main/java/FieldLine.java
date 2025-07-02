import javafx.geometry.Point3D;

import java.awt.Graphics;
import java.util.ArrayList;

public class FieldLine
{/// parent class for field arrows and determines trajectory and position of field arrows
    double startX;
    double startY;
    double startZ;
    ArrayList<FieldArrow> fieldArrows = new ArrayList<>();
    int fieldArrowNum = 200;///how many field arrows are computed based on the line
    int stopDrawing = -1;
    double actualCharge = 1.602176634E-19;///in coulombs
    public FieldLine(double initX, double initY, double initZ)
    {
        startX = initX;
        startY = initY;
        startZ = initZ;
        for(int i = 0; i < fieldArrowNum; i++)
        {
            fieldArrows.add(new FieldArrow(startX, startY, startZ, this));
        }
    }
    public void drawFieldArrows(Graphics g, Panel panel)
    {
        double fieldX = startX;
        double fieldY = startY;
        double fieldZ = startZ;
        stopDrawing = -1;
        for(int i = 0; i < fieldArrowNum; i++)
        {
            if(stopDrawing == -1 || stopDrawing == i + 1)
            {
                //System.out.println(fieldX + " " + fieldY);
                Point3D tempPoint = panel.getForce(fieldX, fieldY, fieldZ, false, actualCharge, null);
                FieldArrow currArrow = fieldArrows.get(i);
                currArrow.magnitude = tempPoint.getX();
                //System.out.println(i + " " + tempArrow.angle);
                currArrow.angleTheta = tempPoint.getY();
                currArrow.anglePhi = tempPoint.getZ();
                currArrow.setCoords(fieldX, fieldY, fieldZ);
                currArrow.endX = currArrow.x + panel.mpp * currArrow.length * Math.cos(currArrow.angleTheta);
                currArrow.endY = currArrow.y + panel.mpp * currArrow.length * Math.sin(currArrow.angleTheta);
                currArrow.endZ = currArrow.z;
                if (i % 24 == 12) {
                    currArrow.drawMidpointArrow = true;
                }
                currArrow.update(g, panel);
                fieldX = currArrow.endX;
                fieldY = currArrow.endY;
                fieldZ = currArrow.endZ;
            }
        }
    }
}
