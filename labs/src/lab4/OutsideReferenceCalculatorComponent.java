package lab4;

import core.FuzzyPetriLogic.Executor.AsyncronRunnableExecutor;
import core.FuzzyPetriLogic.FuzzyDriver;
import core.FuzzyPetriLogic.FuzzyToken;
import core.FuzzyPetriLogic.PetriNet.FuzzyPetriNet;
import core.FuzzyPetriLogic.PetriNet.Recorders.FullRecorder;
import core.FuzzyPetriLogic.Tables.OneXOneTable;
import core.TableParser;

import java.util.HashMap;
import java.util.Map;

public class OutsideReferenceCalculatorComponent {
    private FuzzyPetriNet net;
    private int p1inp;
    private FuzzyDriver outsideTempDriver;
    private FuzzyDriver tankWaterTemeDriver;
    private FullRecorder rec;
    private AsyncronRunnableExecutor execcutor;



    static String reader = "" +
            "{[<ZR,NL><ZR,NM><ZR,ZR><ZR,PM><ZR,PL>]" +
            " [<ZR,NL><ZR,NM><ZR,ZR><ZR,PM><ZR,PL>]" +
            " [<ZR,NL><ZR,NM><ZR,ZR><ZR,PM><ZR,PL>]" +
            " [<ZR,NL><ZR,NM><ZR,ZR><ZR,PM><ZR,PL>]" +
            " [<ZR,NL><ZR,NM><ZR,ZR><ZR,PM><ZR,PL>]}";

    static String t2Table = "{[<PL><PM><ZR><NM><NL>]}";

    public OutsideReferenceCalculatorComponent(Plant plant, HeaterTankControllerComponent comp, long simPeriod) {

// Build the petri net here   // your homework
        net = new FuzzyPetriNet();

        int p0 = net.addPlace(); net.setInitialMarkingForPlace(p0, FuzzyToken.zeroToken());
        p1inp = net.addInputPlace();
        int p2 = net.addPlace();
        int p3out = net.addPlace();

        int t0 = net.addTransition(0, new TableParser().parseTable(reader));
        net.addArcFromPlaceToTransition(p0, t0, 1.0);
        net.addArcFromPlaceToTransition(p1inp, t0, 1.0);
        net.addArcFromTransitionToPlace(t0, p2);
        net.addArcFromTransitionToPlace(t0, p3out);

        int t1 = net.addTransition(1, OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p2, t1, 1.0);
        net.addArcFromTransitionToPlace(t1, p0);

        int t2 = net.addOuputTransition(OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p3out, t2, 1.0);

        outsideTempDriver = FuzzyDriver.createDriverFromMinMax(-30, 10);
        tankWaterTemeDriver = FuzzyDriver.createDriverFromMinMax(45, 68);
        net.addActionForOuputTransition(t2, tk -> {     //<<<<<<<<<< this is the t2 action
            comp.setWaterRefTemp(tankWaterTemeDriver.defuzzify(tk));//<<< that is connected to
            //<<< The water tank
        });
        rec = new FullRecorder();
        execcutor = new AsyncronRunnableExecutor(net, simPeriod);
        execcutor.setRecorder(rec);
    }

    public void start() {
        (new Thread(execcutor)).start();  }
    public void stop() {    execcutor.stop();  }

    public void setOutsideTemp(double waterRefTemp) {
        Map<Integer, FuzzyToken> inps = new HashMap<Integer, FuzzyToken>();
        inps.put(p1inp, outsideTempDriver.fuzzifie(waterRefTemp));
        execcutor.putTokenInInputPlace(inps);  }

    public FuzzyPetriNet getNet() {    return net;  }
    public FullRecorder getRecorder() {    return rec;  }

}
