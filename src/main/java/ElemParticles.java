import java.util.Arrays;

class ElemRunner
{
    public static void main(String[] args)
    {
        ElemParticles[] inList = new ElemParticles[]{new electron0(), new positron() };
        ElemParticles[] outList = new ElemParticles[]{new photon(), new gluon()};
        System.out.println(interactionAllowed(inList, outList));
    }
    public static boolean interactionAllowed(ElemParticles[] inList, ElemParticles[] outList)
    {
        double spin = 0;
        double charge = 0;
        double baryonNumber = 0;
        double[] leptonFlavor = new double[]{0, 0, 0};
        for(ElemParticles e: inList)
        {
            spin += e.spin;
            charge += e.charge;
            if(e instanceof RegQuark) {baryonNumber += 1.0/3.0;}
            if(e instanceof AntiQuark) {baryonNumber -= 1.0/3.0;}
            double tempFlavor = 0;
            if(e instanceof RegLepton) {tempFlavor = 1;}
            if(e instanceof AntiLepton) {tempFlavor -= 1;}
            if(e instanceof electron0 || e instanceof positron || e instanceof electronNeu || e instanceof electronAntiNeu) {leptonFlavor[0] += tempFlavor;}
            if(e instanceof muon || e instanceof antiMuon || e instanceof muonNeu || e instanceof muonAntiNeu) {leptonFlavor[1] += tempFlavor;}
            if(e instanceof tau || e instanceof antiTau || e instanceof tauNeu || e instanceof tauAntiNeu) {leptonFlavor[2] += tempFlavor;}
        }
        for(ElemParticles e: outList)
        {
            spin -= e.spin;
            charge -= e.charge;
            if(e instanceof RegQuark) {baryonNumber -= 1.0/3.0;}
            if(e instanceof AntiQuark) {baryonNumber += 1.0/3.0;}
            double tempFlavor = 0;
            if(e instanceof RegLepton) {tempFlavor = 1;}
            if(e instanceof AntiLepton) {tempFlavor -= 1;}
            if(e instanceof electron0 || e instanceof positron || e instanceof electronNeu || e instanceof electronAntiNeu) {leptonFlavor[0] -= tempFlavor;}
            if(e instanceof muon || e instanceof antiMuon || e instanceof muonNeu || e instanceof muonAntiNeu) {leptonFlavor[1] -= tempFlavor;}
            if(e instanceof tau || e instanceof antiTau || e instanceof tauNeu || e instanceof tauAntiNeu) {leptonFlavor[2] -= tempFlavor;}
        }
        if(spin != 0 || charge != 0 || baryonNumber != 0 || leptonFlavor[0] != 0 || leptonFlavor[1] != 0 || leptonFlavor[2] != 0)
        {
            System.out.println("spin: " + spin + " charge: " + charge + " baryon number: " + baryonNumber + " leptonFlavor: " + Arrays.toString(leptonFlavor));
            return false;
        }
        return true;
    }
}
public class ElemParticles
{
    double spin;
    double charge;
    boolean emField;
    boolean weakField;
    boolean strongField;
    boolean higgsField;
    double mass;
    public ElemParticles(double initMass, double initSpin, double initCharge, boolean initEM, boolean initWeak, boolean initStrong, boolean initHiggs)
    {
        mass = initMass;
        spin = initSpin;
        charge = initCharge;
        emField = initEM;
        weakField = initWeak;
        strongField = initStrong;
        higgsField = initHiggs;
    }
}
class Fermions extends ElemParticles
{
    public Fermions(double initMass, double initCharge, boolean initEM, boolean initWeak, boolean initStrong, boolean initHiggs)
    {
        super(initMass, 0.5, initCharge, initEM, initWeak, initStrong, initHiggs);
    }
}
class Quark extends Fermions
{
    public Quark(double initMass, double initCharge)
    {
        super(initMass, initCharge, true, true, true, true);
    }
}
class RegQuark extends Quark
{
    public RegQuark(double initMass, double initCharge)
    {
        super(initMass, initCharge);
    }
}
class AntiQuark extends Quark
{
    public AntiQuark(double initMass, double initCharge)
    {
        super(initMass, initCharge);
    }
}
class Leptons extends Fermions
{
    public Leptons(double initMass, double initCharge, boolean initEM, boolean initWeak, boolean initHiggs)
    {
        super(initMass, initCharge, initEM, initWeak, false, initHiggs);
    }
}
class RegLepton extends Leptons
{
    public RegLepton(double initMass, double initCharge, boolean initEM, boolean initWeak, boolean initHiggs)
    {
        super(initMass, initCharge, initEM, initWeak, initHiggs);
    }
}
class AntiLepton extends Leptons
{
    public AntiLepton(double initMass, double initCharge, boolean initEM, boolean initWeak, boolean initHiggs)
    {
        super(initMass, initCharge, initEM, initWeak, initHiggs);
    }
}
class GaugeBosons extends ElemParticles
{
    public GaugeBosons(double initMass, double initCharge, boolean initEM, boolean initWeak, boolean initStrong, boolean initHiggs)
    {
        super(initMass, 1, initCharge, initEM, initWeak, initStrong, initHiggs);
    }
}
class upQuark extends RegQuark
{
    double charge = 2.0/3.0;
    double mass = 2200000.0;//in electron volts over the speed of light squared (eV/c^2)
    double[] colorCharge;
    double baryonNum = 1.0/3.0;
    public upQuark()
    {
        super(2200000.0, 2.0/3.0);
    }
}
class downQuark extends RegQuark
{
    double charge = -1.0/3.0;
    double mass = 4700000.0;
    double baryonNum = 1.0/3.0;
    public downQuark()
    {
        super(4700000.0, -1.0/3.0);
    }
}
class charmQuark extends RegQuark
{
    double charge = 2.0/3.0;
    double mass = 1280000000.0;
    double baryonNum = 1.0/3.0;
    public charmQuark()
    {
        super(1280000000.0, 2.0/3.0);
    }
}
class strangeQuark extends RegQuark
{
    double charge = -1.0/3.0;
    double mass = 96000000.0;
    double baryonNum = 1.0/3.0;
    public strangeQuark()
    {
        super(96000000.0, -1.0/3.0);
    }
}
class topQuark extends RegQuark
{
    double charge = 2.0/3.0;
    double mass = 173100000000.0;
    double baryonNum = 1.0/3.0;
    public topQuark()
    {
        super(173100000000.0, 2.0/3.0);
    }
}
class bottomQuark extends RegQuark
{
    double charge = -1.0/3.0;
    double mass = 4180000000.0;
    double baryonNum = 1.0/3.0;
    public bottomQuark()
    {
        super(4180000000.0, -1.0/3.0);
    }
}
class antiUpQuark extends AntiQuark
{
    double charge = -2.0/3.0;
    double mass = 2200000.0;
    double baryonNum = -1.0/3.0;
    public antiUpQuark()
    {
        super(2200000.0, -2.0/3.0);
    }
}
class antiDownQuark extends AntiQuark
{
    double charge = 1.0/3.0;
    double mass = 4700000.0;
    double baryonNum = -1.0/3.0;
    public antiDownQuark()
    {
        super(4700000.0, 1.0/3.0);
    }
}
class antiCharmQuark extends AntiQuark
{
    double charge = -2.0/3.0;
    double mass = 1280000000.0;
    double baryonNum = -1.0/3.0;
    public antiCharmQuark()
    {
        super(1280000000.0, -2.0/3.0);
    }
}
class antiStrangeQuark extends AntiQuark
{
    double charge = 1.0/3.0;
    double mass = 96000000.0;
    double baryonNum = -1.0/3.0;
    public antiStrangeQuark()
    {
        super(96000000.0, 1.0/3.0);
    }
}
class antiTopQuark extends AntiQuark
{
    double charge = -2.0/3.0;
    double mass = 173100000000.0;
    double baryonNum = -1.0/3.0;
    public antiTopQuark()
    {
        super(173100000000.0, -2.0/3.0);
    }
}
class antiBottomQuark extends AntiQuark
{
    double charge = 1.0/3.0;
    double mass = 4180000000.0;
    double baryonNum = -1.0/3.0;
    public antiBottomQuark()
    {
        super(4180000000.0, 1.0/3.0);
    }
}

class electron0 extends RegLepton
{
    boolean emField = true;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = true;
    double charge = -1.0;
    double mass = 511000.0;
    public electron0()
    {
        super(511000.0, -1.0, true, true, true);
    }
}
class muon extends RegLepton
{
    boolean emField = true;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = true;
    double charge = -1.0;
    double mass = 105660000.0;
    public muon()
    {
        super(105660000.0, -1.0, true, true, true);
    }
}
class tau extends RegLepton
{
    boolean emField = true;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = true;
    double charge = -1.0;
    double mass = 1776800000.0;
    public tau()
    {
        super(1776800000.0, -1.0, true, true, true);
    }
}
class positron extends AntiLepton
{
    boolean emField = true;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = true;
    double charge = 1.0;
    double mass = 511000.0;
    public positron()
    {
        super(511000.0, 1.0, true, true, true);
    }
}
class antiMuon extends AntiLepton
{
    boolean emField = true;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = true;
    double charge = 1.0;
    double mass = 105660000.0;
    public antiMuon()
    {
        super(105660000.0, 1.0, true, true, true);
    }
}
class antiTau extends AntiLepton
{
    boolean emField = true;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = true;
    double charge = 1.0;
    double mass = 1776800000.0;
    public antiTau()
    {
        super(1776800000.0, 1.0, true, true, true);
    }
}
class electronNeu extends RegLepton
{
    boolean emField = false;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = false;
    double charge = 0.0;
    double mass = 2.2;
    public electronNeu()
    {
        super(2.2, 0.0, false, true, false);
    }
}
class muonNeu extends RegLepton
{
    boolean emField = false;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = false;
    double charge = 0.0;
    double mass = 170000.0;
    public muonNeu()
    {
        super(170000.0, 0.0, false, true, false);
    }
}
class tauNeu extends RegLepton
{
    boolean emField = false;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = false;
    double charge = 0.0;
    double mass = 18200000.0;
    public tauNeu()
    {
        super(18200000.0, 0.0, false, true, false);
    }
}
class electronAntiNeu extends AntiLepton
{
    boolean emField = false;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = false;
    double charge = 0.0;
    double mass = 2.2;
    public electronAntiNeu()
    {
        super(2.2, 0.0, false, true, false);
    }
}
class muonAntiNeu extends AntiLepton
{
    boolean emField = false;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = false;
    double charge = 0.0;
    double mass = 170000.0;
    public muonAntiNeu()
    {
        super(170000.0, 0.0, false, true, false);
    }
}
class tauAntiNeu extends AntiLepton
{
    boolean emField = false;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = false;
    double charge = 0.0;
    double mass = 18200000.0;
    public tauAntiNeu()
    {
        super(18200000.0, 0.0, false, true, false);
    }
}

class gluon extends GaugeBosons
{
    boolean emField = false;
    boolean weakField = false;
    boolean strongField = true;
    boolean higgsField = false;
    double charge = 0.0;
    double mass = 0.0;
    public gluon()
    {
        super(0.0, 0.0, false, false, true, false);
    }
}
class photon extends GaugeBosons
{
    boolean emField = false;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = false;
    double charge = 0.0;
    double mass = 0.0;
    public photon()
    {
        super(0.0, 0.0, false, true, false, false);
    }
}
class Z0Boson extends GaugeBosons
{
    boolean emField = false;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = true;
    double charge = 0.0;
    double mass = 91190000000.0;
    public Z0Boson()
    {
        super(91190000000.0, 0.0, false, true, false, true);
    }
}
class WPBoson extends GaugeBosons
{
    boolean emField = true;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = true;
    double charge = 1.0;
    double mass = 80390000000.0;
    public WPBoson()
    {
        super(80390000000.0, 1.0, true, true, false, true);
    }
}
class WNBoson extends GaugeBosons
{
    boolean emField = true;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = true;
    double charge = -1.0;
    double mass = 80390000000.0;
    public WNBoson()
    {
        super(80390000000.0, -1.0, true, true, false, true);
    }
}
class higgsBoson extends ElemParticles
{
    boolean emField = false;
    boolean weakField = true;
    boolean strongField = false;
    boolean higgsField = true;
    double charge = 0.0;
    double mass = 124970000000.0;
    double spin = 0.0;
    public higgsBoson()
    {
        super(124970000000.0, 0.0, 0.0, false, true, false, true);
    }
}

