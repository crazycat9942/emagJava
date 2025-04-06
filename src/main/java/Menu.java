import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import net.miginfocom.swing.MigLayout;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.EventQueue;
import java.text.DecimalFormat;
import java.util.ArrayList;
class Menu extends JFrame
{
    static Panel panel;
    ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
    JCheckBox addStrongForce;
    JCheckBox addEmagForce;
    JTextArea timeArea;
    JTextArea fpsArea;
    double lastTime;
    //JTextArea curlArea;
    //JTextArea divArea;
    //JTextArea magArea;
    //JSlider zoomSlider;
    //JTextArea jacobArea;
    //JTextArea zoomArea;
    public Menu(Panel panelInit)
    {
        panel = panelInit;
        checkBoxes.add(addStrongForce);
        checkBoxes.add(addEmagForce);
        init();
    }

    public void init()
    {
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(300, 900));
        MigLayout ml = new MigLayout();
        setLayout(ml);
        FlatMacDarkLaf.setup();
        FlatMacDarkLaf.updateUI();
        addStrongForce = new JCheckBox("Strong force", true);
        addEmagForce = new JCheckBox("Electromagnetic force", true);
        //zoomSlider = new JSlider(1, 10);
        //System.out.println(colorWithCurl.isSelected());
        ml.setLayoutConstraints("width 200px!, wrap");
        this.add(addStrongForce);
        //gbc.gridy = GridBagConstraints.FIRST_LINE_END;
        this.add(addEmagForce);
        timeArea = new JTextArea("time: " + panel.time + " zs (10^-21 s)");
        fpsArea = new JTextArea();
        /*curlArea = new JTextArea();
        divArea = new JTextArea();
        magArea = new JTextArea();
        jacobArea = new JTextArea();*/
        //zoomArea = new JTextArea();
        this.add(fpsArea);
        this.add(timeArea, "bottom");
        /*this.add(curlArea);
        this.add(divArea);
        this.add(magArea);*/
        //this.add(zoomArea);
        //this.add(zoomSlider);
        //this.add(new JTextArea("Legend"));
        //PaintScaleLegend
        //jacobArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        //jacobArea.setSize(180, 180);
        //this.add(jacobArea);
        //panel.add(this.getContentPane(), BorderLayout.EAST);
        //ChartPanel chartPanel = new ChartPanel(createChart(createDataset()));
        //this.add(chartPanel);
    }
    public static void main(String[] args)
    {
        EventQueue.invokeLater(() -> new Menu(panel).setVisible(true));
    }

    public void update(Graphics g)
    {
        //g.setColor(Color.white);
        /*g.drawLine(1550, 30, 1575, 30);
        g.drawLine(1550, 40, 1575, 40);
        g.drawLine(1550, 50, 1575, 50);*/
        DecimalFormat df = new DecimalFormat("#.###");
        /*for(JCheckBox i: checkBoxes)
        {
            if(i.isSelected())
            {
                for(JCheckBox j: checkBoxes)
                {
                    if(i != j)
                    {
                        j.setSelected(false);
                    }
                }
            }
        }*/
        timeArea.setText("time: " + panel.time + " zs (10^-21 s)");
        fpsArea.setText("FPS: " + df.format((1000.0/(System.currentTimeMillis() - lastTime))));
        lastTime = System.currentTimeMillis();
        //zoomArea.setText("zoom: " + zoomSlider.getValue() + "x");
        //Arrow tempArrow = new Arrow(panel.axes.screenToCoordsX(panel.userMouse.x), panel.axes.screenToCoordsY(panel.userMouse.y), panel);
        //jacobArea.setText("Jacobian:\n   x  y  t\nP   " + tempArrow.DPDX + "  " + tempArrow.DPDY + "  3\nQ   4  5  6");
    }
    /*private static JFreeChart createChart(XYDataset dataset) {
        NumberAxis xAxis = new NumberAxis("x Axis");
        NumberAxis yAxis = new NumberAxis("y Axis");
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        XYBlockRenderer r = new XYBlockRenderer();
        r.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
        test.SpectrumPaintScale ps = new test.SpectrumPaintScale(0, 1);
        r.setPaintScale(ps);
        plot.setRenderer(r);
        JFreeChart chart = new JFreeChart("Title",
                JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        NumberAxis scaleAxis = new NumberAxis("Scale");
        scaleAxis.setAxisLinePaint(Color.white);
        scaleAxis.setTickMarkPaint(Color.white);
        PaintScaleLegend legend = new PaintScaleLegend(ps, scaleAxis);
        legend.setSubdivisionCount(10);
        legend.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
        legend.setPadding(new RectangleInsets(25, 10, 50, 10));
        legend.setStripWidth(20);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setBackgroundPaint(Color.WHITE);
        chart.addSubtitle(legend);
        chart.setBackgroundPaint(Color.white);
        return chart;
    }
    private static XYZDataset createDataset() {
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        double[][] data = new double[3][128 * 128];
        for (int i = 0; i < 128 * 128; i++) {
            var x = i % 128;
            var y = i / 128;
            data[0][i] = x;
            data[1][i] = y;
            data[2][i] = x * y;
        }
        dataset.addSeries("Series", data);
        return dataset;
    }
}
class SpectrumPaintScale implements PaintScale {

    private static final float H1 = 0f;
    private static final float H2 = 1f;
    private final double lowerBound;
    private final double upperBound;

    public SpectrumPaintScale(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public double getLowerBound() {
        return lowerBound;
    }

    @Override
    public double getUpperBound() {
        return upperBound;
    }

    @Override
    public Paint getPaint(double value) {
        float scaledValue = (float) (value / (getUpperBound() - getLowerBound()));
        float scaledH = H1 + scaledValue * (H2 - H1);
        return Color.getHSBColor(scaledH, 1f, 1f);
    }*/
}
