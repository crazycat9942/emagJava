import javafx.geometry.Point3D;

import java.awt.Graphics;
import java.util.ArrayList;

public class FieldLine
{
    double startX;
    double startY;
    double startZ;
    ArrayList<FieldArrow> fieldArrows = new ArrayList<>();
    int fieldArrowNum = 200;
    int stopDrawing = -1;
    double actualCharge = 1.602176634E-19;
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
                Point3D tempPoint = panel.getForce(fieldX, fieldY, fieldZ, false, actualCharge);
                FieldArrow currArrow = fieldArrows.get(i);
                currArrow.magnitude = tempPoint.getX();
                //System.out.println(i + " " + tempArrow.angle);
                currArrow.angleTheta = tempPoint.getY();
                currArrow.anglePhi = tempPoint.getZ();
                currArrow.setCoords(fieldX, fieldY, fieldZ);
                //currArrow.endX = currArrow.x + panel.mpp * currArrow.length * Math.cos(currArrow.angleTheta) * Math.sin(currArrow.anglePhi) / 3;
                //currArrow.endY = currArrow.y + panel.mpp * currArrow.length * Math.sin(currArrow.angleTheta) * Math.sin(currArrow.anglePhi) / 3;
                //currArrow.endZ = currArrow.z + panel.mpp * currArrow.length * Math.cos(currArrow.anglePhi) / 3;
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
