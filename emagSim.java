import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class emagSim extends JComponent
{
    int window_x = 1600;
    int window_y = 900;
    boolean scaleVectors = false;
    static boolean running = true;
    private static JFrame frame = new JFrame("shitter");

    public emagSim()
    {
        frame.setSize(1600, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Panel());
        frame.pack();
        frame.setBackground(Color.black);
        frame.setVisible(true);
    }
/*
    //number of arrows: (1500-100 % 50)*(800-100 % 50)
    def getLengths(U, V):
    chargepos =(800,450)
    r21 =((chargepos[0]-U)**2+(chargepos[1]-V)**2)
    r21[r21 ==0]=1
            return np.divide(5000,r21)
    X =np.arange(100,1500,50)
    Y =np.arange(100,800,50)
//angles = np.arctan2(Y, X)
//lengths = getLengths(X, Y)
    max_abs =10000

    print(max_abs)

    def vector_to_rgb(angle, absolute)://taken from https://stackoverflow.com/a/67147320
    global max_abs
    // normalize angle
    angle =angle %(2*np.pi)
            if angle< 0:
    angle +=2*np.pi

    return mpl.colors.hsv_to_rgb((angle /2/np.pi,
    absolute /max_abs,
    absolute /max_abs))

    def drawArrow(start, end):
    length =np.sqrt((end[0]-start[0])**2+(end[1]-start[1])**2)
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

    /*def draw_arrow(start, rotation, length):
        end = (start[0] + length*np.cos(np.pi*rotation/180), start[1] + length*np.sin(np.pi*rotation/180))
        color = 'white'
        pygame.draw.line(window,color,start,end,5)
        radrotate = np.deg2rad(rotation)
        leftSide = (end[0] - 0.1*length*np.sin(radrotate), end[1] + 0.1*length*np.cos(radrotate))
        rightSide = (end[0] + 0.1*length*np.sin(radrotate), end[1] - 0.1*length*np.cos(radrotate))
        top = (end[0] + 0.1*length*np.cos(radrotate), end[1] + 0.1*length*np.sin(radrotate))
        pygame.draw.polygon(window, (255, 255, 255), (leftSide, rightSide, top), 5)
        //pygame.draw.polygon(window, (255, 255, 255), ((end[0] + -0.1*length*np.sin(np.deg2rad(rotation)), end[1] + 0.1*)))
        //pygame.draw.polygon(window, (255, 255, 255), ((end[0]+20*math.sin(math.radians(rotation)), end[1]+20*math.cos(math.radians(rotation))), (end[0]+20*math.sin(math.radians(rotation-120)), end[1]+20*math.cos(math.radians(rotation-120))), (end[0]+20*math.sin(math.radians(rotation+120)), end[1]+20*math.cos(math.radians(rotation+120)))))*/
    /*def force(startX, startY, charge):
    chargepos =(800,400)
            //r21 = (chargepos[0]-startX, chargepos[1]-startY)
            //r212 = (r21[0])**2 + (r21[1])**2
            //scaling = lengths
            //drawArrow(start, (400, 400))
            for
    i in

    range(1,len(startX)):
            for
    k in

    range(1,len(startY)):

    drawArrow((startX[i], startY[k]), (startX[i]+10,startY[k]+20))

    //drawArrow((startX, startY), (startX + 10, startY + 20))//r21[0]*scaling))
    def update():
            pygame.draw.circle(window,'yellow',(800,450),35,0)

    force(X, Y, -1)
    pygame.display.update()
            while running:
            clock.tick(20)

    update()
    for
    event in pygame.event.get():
            if event.type ==pygame.QUIT:
    running =False
    //pygame.display.flip()
    //clock.tick(20)
    //pygame.draw.line(window, 'white', (100, 100), (400, 450), 10)
    //drawArrow(100, 75, 500, 500)
//pygame.draw.polygon(window, 'white', (0, 0, 0), ((0, 100), (0, 200), (200, 200), (200, 300), (300, 150), (200, 0), (200, 100)))
*/}
class Panel extends JPanel {
    Timer timer;
    Graphics g;
    Electron e;
    Panel() {
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        refreshScreen();
        e = new Electron(800, 450);
    }
    @Override
    protected void paintComponent(Graphics gInit) {
        super.paintComponent(gInit);
        g = gInit;
        g.setFont(new Font("arial", Font.PLAIN, 24));
        e.update(g);
        //g.setColor(new Color((int)(255*Math.random()), (int)(255*Math.random()), (int)(255*Math.random())));
        //g.drawString("java2s.com", 200, 200);

    }
    public void refreshScreen() {
        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.setRepeats(true);
        // Aprox. 60 FPS
        timer.setDelay(17);
        timer.start();
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1600, 900);
    }
}