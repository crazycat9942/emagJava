import com.opencsv.exceptions.CsvValidationException;
import com.univocity.parsers.common.processor.ColumnProcessor;
import com.univocity.parsers.csv.CsvParser;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import javafx.geometry.Point3D;
import com.opencsv.CSVReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import com.univocity.parsers.csv.CsvParserSettings;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

//theta is polar and phi is azimuthal (going from z axis to plane made by x and y axes)
public class WaveFunction extends Electron
{
    FieldMatrix<Complex> sigma_1 = new Array2DRowFieldMatrix<>(new Complex[][]{{Complex.ZERO, Complex.ONE}, {Complex.ONE, Complex.ZERO}});
    FieldMatrix<Complex> sigma_2 = new Array2DRowFieldMatrix<>(new Complex[][]{{Complex.ZERO, Complex.I.negate()}, {Complex.I, Complex.ZERO}});
    FieldMatrix<Complex> sigma_3 = new Array2DRowFieldMatrix<>(new Complex[][]{{Complex.ONE, Complex.ZERO}, {Complex.ZERO, Complex.ONE.negate()}});
    static double q_n = 4;
    static double q_l = 2;//azimuthal quantum number
    static double temp_l = q_l;
    static double q_m = 0;//magnetic quantum number
    final static double a_0 = 5.29177210544 * Math.pow(10, -11);//bohr radius (in meters)
    static double tempTheta = 0;
    static double tempPhi = 0;
    static double tempR = 0;
    static double maxR = 30 * a_0;
    static double sqrtPart = -1;
    static double tempExp = Math.exp(-1/(q_n * a_0));
    static double tempFrame = 0;
    static double tempTime = System.nanoTime();
    static int subsections = 50;
    static double dS = maxR/subsections;
    public static void main(String[] args)
    {
        try {
            ICsvListWriter listWriter = null;
            try
            {
                listWriter = new CsvListWriter(new FileWriter("C:/Users/crazy/IdeaProjects/Testing/src/main/java/cDistInfo.csv"), CsvPreference.STANDARD_PREFERENCE);
                final String[] header = new String[] {"rStart","rEnd","thetaStart","thetaEnd","phiStart","phiEnd","cumulativeProb"};
                listWriter.writeHeader(header);

                double cumulativeProb = 0;
                int sectionsR = 150;
                int sectionsTheta = 50;
                int sectionsPhi = 25;
                for(double r = 0; r < maxR; r += maxR/sectionsR)
                {
                    for(double theta = 0; theta < 2 * Math.PI; theta += 2 * Math.PI / sectionsTheta)
                    {
                        for(double phi = 0; phi < Math.PI; phi += Math.PI/sectionsPhi)
                        {
                            //System.out.println(tempPoint);
                            //Complex tempPsi = psi(q_n, q_l, q_m, tempPoint.getX(), tempPoint.getY(), tempPoint.getZ());
                            //double tempProb = (tempPsi.conjugate().multiply(tempPsi)).getReal() * Math.pow(dS, 3);
                            double tempProb = integrateTheta(r, r + maxR/sectionsR, theta, theta + 2 * Math.PI/sectionsTheta, phi, phi + Math.PI/sectionsPhi);
                            //System.out.println(tempProb);
                            cumulativeProb += tempProb;
                            final Double[] tempList = new Double[] {r, r + maxR/sectionsR, theta, theta + 2 * Math.PI/sectionsTheta, phi, phi + Math.PI/sectionsPhi, cumulativeProb};
                            listWriter.write(tempList);
                        }
                    }
                    System.out.println(100 * r/maxR + "% done creating orbitals");
                }
            }
            finally
            {
                if(listWriter != null)
                {
                    listWriter.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static int indexBinarySearch(double[] probCol, int low, int high, double rand)
    {//https://www.baeldung.com/java-binary-search
        int index = Integer.MAX_VALUE;

        while (low <= high) {
            int mid = low  + ((high - low) / 2);
            if (probCol[mid] < rand) {
                low = mid + 1;
            } else if (probCol[mid] > rand) {
                high = mid - 1;
            } else if ((mid != probCol.length - 1 && rand <= probCol[mid + 1] && rand >= probCol[mid]) || (rand <= probCol[mid])) {
                index = mid;
                break;
            }
        }
        return index;
    }
    public static double[] getColumn(int columnNum)
    {
        ArrayList<Double> tempColumn = new ArrayList<>();
        CSVReader reader = null;
        try
        {
            reader = new CSVReader(new FileReader("C:/Users/crazy/IdeaProjects/Testing/src/main/java/cDistInfo.csv"));
            String[] nextLine;
            reader.readNext();
            while ((nextLine = reader.readNext()) != null)
            {
                tempColumn.add(Double.parseDouble(nextLine[columnNum]));
            }
        } catch (IOException | CsvValidationException e) {throw new RuntimeException(e);}
        return ArrayUtils.toPrimitive(tempColumn.toArray(new Double[0]));
    }
    public static double[] getRow(int rowNum)
    {
        CsvParserSettings settings = new CsvParserSettings();
        ColumnProcessor rowProcessor = new ColumnProcessor();
        CsvParser parser = new CsvParser(settings);
        settings.setProcessor(rowProcessor);
        String[] nextLine = new String[8];
        try
        {
            java.util.List<String[]> parsedRows = parser.parseAll(new FileReader("C:/Users/crazy/IdeaProjects/Testing/src/main/java/cDistInfo.csv"));
            //java.util.List<String[]> parsedRows = parser.parseAll();
            String temp = Arrays.toString(parsedRows.get(rowNum));
            temp = temp.substring(1, temp.length() - 1);
            nextLine = temp.split(",");
            return Arrays.stream(nextLine).mapToDouble(Double::parseDouble).toArray();
        } catch (IOException e) {throw new RuntimeException(e);}
    }
    public static double[][] getRow(int[] rowNums)
    {
        CsvParserSettings settings = new CsvParserSettings();
        ColumnProcessor rowProcessor = new ColumnProcessor();
        CsvParser parser = new CsvParser(settings);
        settings.setProcessor(rowProcessor);
        double[][] output = new double[rowNums.length][7];
        try
        {
            java.util.List<String[]> parsedRows = parser.parseAll(new FileReader("C:/Users/crazy/IdeaProjects/Testing/src/main/java/cDistInfo.csv"));
            for(int i = 0; i < rowNums.length; i++)
            {
                String temp = Arrays.toString(parsedRows.get(rowNums[i]));
                temp = temp.substring(1, temp.length() - 1);
                output[i] = Arrays.stream(temp.split(",")).mapToDouble(Double::parseDouble).toArray();
            }
            return output;
        } catch (IOException e) {throw new RuntimeException(e);}
    }
    public static double probLessThanABC(double a, double b, double c)
    {
        return probLessThanA(0, a) * probLessThanA(1, b) * probLessThanA(2, c);
    }
    public static double probLessThanA(int columnNum, double a)
    {
        double[] col1 = getColumn(columnNum);
        double[] col2 = getColumn(3);
        double tempProb = 0;
        for(int i = 0; i < col1.length; i++)
        {
            if(col1[i] <= a)
            {
                tempProb += col2[i];
            }
        }
        return tempProb;
    }
    public WaveFunction(double n, double l, double m, double time)
    {
        super(simulateUntilPoint(maxR), time);
        q_n = n;
        q_l = l;
        q_m = m;
    }
    @Override
    public void update(Graphics g, double time, Panel panel)
    {
        //System.out.println(x + " " + y);
        double[] cProbCol = getColumn(6);
        double max = cProbCol[cProbCol.length - 1];
        cProbCol = Arrays.stream(cProbCol).map(i -> i/max).toArray();
        g.setColor(new Color(255,255,0));
        Point3D[] points = new Point3D[100000];
        long startTime = System.nanoTime();
        int[] indices = new int[points.length];
        for(int i = 0; i < points.length; i++)
        {
            double rand = Math.random();
            indices[i] = modifiedBinary(cProbCol, rand);
        }
        double[][] rowsArr = getRow(indices);
        for(int i = 0; i < points.length; i++)
        {
            Point3D tempPoint = sphericalToCartesian(new Point3D(rowsArr[i][0], rowsArr[i][2], rowsArr[i][4]));
            Point3D tempPoint2 = sphericalToCartesian(new Point3D(rowsArr[i][1], rowsArr[i][3], rowsArr[i][5]));
            Point3D tempPoint3 = tempPoint2.subtract(tempPoint);
            tempPoint = tempPoint.add(new Point3D(Math.random() * tempPoint3.getX(), Math.random() * tempPoint3.getY(), Math.random() * tempPoint3.getZ()));
            points[i] = tempPoint;
        }
        points = panel.rotate(points);
        for(int i = 0; i < points.length; i++)
        {
            g.fillOval((int) (panel.coordsToScreenX(points[i].getX())), (int) (panel.coordsToScreenY(points[i].getY())), 2, 2);
        }
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(5));
        Point3D tempPoint = panel.rotate(Math.pow(10, -9), 0, 0);
        g.setColor(new Color(100,100,200));
        g.drawLine((int)(panel.window_x/2.0), (int)(panel.window_y/2.0), (int)(panel.coordsToScreenX(tempPoint.getX())), (int)(panel.coordsToScreenY(tempPoint.getY())));
        tempPoint = panel.rotate(0, Math.pow(10, -9), 0);
        g.setColor(new Color(200, 100, 100));
        g.drawLine((int)(panel.window_x/2.0), (int)(panel.window_y/2.0), (int)(panel.coordsToScreenX(tempPoint.getX())), (int)(panel.coordsToScreenY(tempPoint.getY())));
        tempPoint = panel.rotate(0, 0, Math.pow(10, -9));
        g.setColor(new Color(100, 200, 100));
        g.drawLine((int)(panel.window_x/2.0), (int)(panel.window_y/2.0), (int)(panel.coordsToScreenX(tempPoint.getX())), (int)(panel.coordsToScreenY(tempPoint.getY())));
        //System.out.println((int)(panel.coordsToScreenX(tempPoint.getX())) + " :" + (int)(panel.coordsToScreenY(tempPoint.getY())));
        if(frames % 50 == 49 && movements.size() >= 20)
        {
            movements = new ArrayList<>(movements.subList(movements.size() - 20, movements.size()));
            times = new ArrayList<Double>(times.subList(movements.size() - 20, movements.size()));
        }
        movements.add(new Double[]{x, y, z, time});
        times.add(time);
        frames++;
    }
    public static int modifiedBinary(double[] probCol, double randProb)
    {
        int low = 0;
        int high = probCol.length;
        int pivot = probCol.length/2;
        while(high >= low)
        {
            if(probCol[pivot] > randProb)
            {
                high = pivot - 1;
                pivot = (high + low)/2;
            }
            else if(probCol[pivot] <= randProb)
            {
                low = pivot + 1;
                pivot = (high + low)/2;
            }
        }
        return pivot + 1;
    }
    public static ArrayList<Point3D> simulateProbability(int num_points, double max_r)//uses monte carlo hit-and-miss method
    {//https://compphys.notes.dmaitre.phyip3.dur.ac.uk/lectures/lecture-5/probability-distributions/
        ArrayList<Point3D> points = new ArrayList<>();

        //double maxProbDensity = integrateTheta(0, max_r, 0, 2 * Math.PI, 0, Math.PI);
        double dS = a_0/1000;
        for(int i = 0; i < num_points; i++)
        {
            //NormalDistribution normal = new NormalDistribution()
            double x = Math.random() * max_r - max_r/2;
            double y = Math.random() * max_r - max_r/2;
            double z = Math.random() * max_r - max_r/2;
            Point3D p1 = cartesianToSpherical(new Point3D(x,y,z));
            Point3D p2 = cartesianToSpherical(new Point3D(x + dS,y + dS,z + dS));
            double rand = Math.random();
            Complex tempPsi = psi(q_n, q_l, q_m, p1.getX(), p1.getY(), p1.getZ());
            double tempProbDensity = (tempPsi.conjugate().multiply(tempPsi)).getReal() * Math.pow(dS, 3);
            System.out.println((p1.getX()*Math.pow(10, 11) + "," + p1.getY() + "," + p1.getZ() + "," + tempProbDensity));
            points.add(new Point3D(x + dS/2, y + dS/2, z + dS/2));
        }
        return points;
    }
    public static Point3D simulateUntilPoint(double max_r)
    {
        while(true)
        {
            ArrayList<Point3D> temp = simulateProbability(1, max_r);
            if(temp.size() == 1)
            {
                return temp.get(0);
            }
        }
    }
    public static Point3D cartesianToSpherical(Point3D initPoint)
    {
        double r = initPoint.distance(0, 0, 0);
        double theta = Math.atan(initPoint.getY() / initPoint.getX());
        double phi = Math.acos(initPoint.getZ() / r);
        return new Point3D(r, theta, phi);
    }
    public static Point3D sphericalToCartesian(Point3D initPoint)
    {
        double x = initPoint.getX() * Math.sin(initPoint.getZ()) * Math.cos(initPoint.getY());
        double y = initPoint.getX() * Math.sin(initPoint.getZ()) * Math.sin(initPoint.getY());
        double z = initPoint.getX() * Math.cos(initPoint.getZ());
        return new Point3D(x,y,z);
    }
    private static double integrateR(double min_r, double max_r, double theta, double phi)
    {
        int subsectionsR = 5;
        double dr = (max_r - min_r)/subsectionsR;
        double total = 0;
        for(double r = min_r; r < max_r; r += dr)
        {
            Complex tempPsi = psi(q_n, q_l, q_m, r, theta, phi);
            total += r * r *(tempPsi.conjugate().multiply(tempPsi)).getReal();
        }
        return total * dr;
    }
    private static double integratePhi(double min_r, double max_r, double theta, double min_phi, double max_phi)
    {
        int subsectionsPhi = 5;
        double dP = (Math.max(max_phi, min_phi) - Math.min(min_phi, max_phi))/subsectionsPhi;
        double total = 0;
        for(double phi = Math.min(min_phi, max_phi); phi < Math.max(max_phi, min_phi); phi += dP)
        {
            total += Math.sin(phi) * integrateR(min_r, max_r, theta, phi);
        }
        return total * dP;
    }
    public static double integrateTheta(double min_r, double max_r, double min_theta, double max_theta, double min_phi, double max_phi)
    {
        int subsectionsTheta = 5;
        double dT = (Math.max(max_theta, min_theta) - Math.min(min_theta, max_theta))/subsectionsTheta;
        double total = 0;
        for(double theta = Math.min(min_theta, max_theta); theta < Math.max(max_theta, min_theta); theta += dT)
        {
            total += integratePhi(min_r, max_r, theta, min_phi, max_phi);
        }
        return total * dT;
    }
    public static Complex psi(double n, double l, double m, double r, double theta, double phi)
    {//https://en.wikipedia.org/wiki/Schr%C3%B6dinger_equation when defining psi_{n,l,m} around hyperlink [25]
        Complex sphHarmTerm = sphericalHarmonics(theta, phi);
        if(sqrtPart == -1) //removing redundant calculation
        {
            sqrtPart = sqrtPart(n, l);
        }
        double assocLagTerm = assocLaguerre(2 * r / (n * a_0), (int)(n - l - 1), (int)(2*l + 1));
        double expTerm = Math.exp(-r/(n * a_0));
        double powTerm = Math.pow(2 * r / (n * a_0), l);
        return sphHarmTerm.multiply(sqrtPart * assocLagTerm * expTerm * powTerm);
    }
    public static double sqrtPart(double n, double l)
    {
        if(sqrtPart == -1)
        {
            double term1 = Math.pow(2.0 / (n * a_0), 3);
            double term2 = factorial(n - l - 1);
            double term3 = 2 * n * factorial(n + l);
            sqrtPart = Math.sqrt(term1 * term2 / term3);
        }
        return sqrtPart;
    }
    public static Complex sphericalHarmonics(double theta, double phi)
    {//https://chem.libretexts.org/Bookshelves/Physical_and_Theoretical_Chemistry_Textbook_Maps/Supplemental_Modules_(Physical_and_Theoretical_Chemistry)/Quantum_Mechanics/07._Angular_Momentum/Spherical_Harmonics eq (1)
        double sqrtPart = Math.sqrt(((2*q_l + 1) * factorial(q_l - Math.abs(q_m))) / (4 * Math.PI * factorial(q_l + Math.abs(q_m))));
        double tempAngle = q_m * theta;
        Complex expPart = Complex.valueOf(Math.cos(tempAngle)).add(Complex.I.multiply(Complex.valueOf(Math.sin(tempAngle))));
        return Complex.valueOf(sqrtPart).multiply(expPart).multiply(Complex.valueOf(assocLegendre(Math.cos(phi), Math.abs(q_m), q_l)));
    }
    public static double assocLegendre(double x, double m, double l)
    {//https://en.wikipedia.org/wiki/Associated_Legendre_polynomials clarification in section titled "negative m and/or negative l
        FiniteDifferencesDifferentiator differentiator = new FiniteDifferencesDifferentiator(20, 0.001);
        UnivariateDifferentiableFunction completeF = differentiator.differentiate(legendreF);
        DerivativeStructure x_d = new DerivativeStructure(1, (int)Math.abs(m), 0, x);
        DerivativeStructure y_d = completeF.value(x_d);
        //return y_d.getPartialDerivative(0);
        if(m > -1 && l > -1)
        {
            double multPart = Math.pow(-1, (int)m) * Math.pow(1 - Math.pow(x, 2), m/2);
            return multPart * y_d.getPartialDerivative((int)m);
        }
        if(m < 0 && l < 0)
        {
            double multPart = Math.pow(-1, -(int)m) * Math.pow(1 - Math.pow(x, 2), -m/2);
            return multPart * Math.pow(-1, -(int)(m)) * factorial(1 - l + m) * y_d.getPartialDerivative((int)-m) / factorial(1 - l - m);
        }
        if(m < 0)
        {
            double multPart = Math.pow(-1, -(int)m) * Math.pow(1 - Math.pow(x, 2), -m/2);
            //return Math.pow(-1, -(int)q_m) * Math.pow(1 - Math.pow(x, 2), -q_m/2) * Math.pow(-1, (int)(q_m)) * factorial(q_l + q_m) * assocLegendreNthDer(x, (int)-q_m, q_l) / factorial(q_l - q_m);
            return multPart * Math.pow(-1, -(int)(m)) * factorial(l + m) * y_d.getPartialDerivative((int)-m) / factorial(l - m);
        }
        //only l < 0
        //return assocLegendreNthDer(x, (int) q_m, 1 - q_l);
        return y_d.getPartialDerivative((int)m);
    }
    static UnivariateFunction legendreF = new UnivariateFunction() {
        @Override
        public double value(double x)
        {//https://mathworld.wolfram.com/LegendrePolynomial.html (31)
            double l = temp_l;
            double temp = 0;
            for(int k = 0; k <= Math.floor(l/2); k++)
            {
                temp += Math.pow(-1, k) * factorial(2*l - 2*k) * Math.pow(x, l - 2*k) / (factorial(k) * factorial(l - k) * factorial(l - 2*k));
            }
            return Math.pow(0.5, l)*temp;
        }
    };
    public static double assocLaguerre(double x, int n, int alpha)//degree of associated laguerre polynomial is n, order of it is alpha
    {//https://mathworld.wolfram.com/AssociatedLaguerrePolynomial.html
        double temp = 0;
        for(int q = 0; q <= n; q++)
        {
            temp += Math.pow(x, q) * Math.pow(-1, q) * factorial(n + alpha) / ((factorial(n - q) * factorial(alpha + q) * factorial(q)));
        }
        return temp;//associated laguerre polynomials are used for psi
    }
    public static double factorial(double a)
    {
        if(a <= 2)
        {
            if(a == 0)
            {
                return 1;
            }
            return a;
        }
        return a * factorial(a - 1);
    }
    public static BigDecimal BV(double a)
    {
        return BigDecimal.valueOf(a);
    }
}
