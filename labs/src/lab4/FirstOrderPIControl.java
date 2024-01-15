package lab4;

import Main.FuzzyPVizualzer;
import View.MainView;
import core.FuzzyPetriLogic.Executor.AsyncronRunnableExecutor;
import core.FuzzyPetriLogic.FuzzyDriver;
import core.FuzzyPetriLogic.FuzzyToken;
import core.FuzzyPetriLogic.PetriNet.FuzzyPetriNet;
import core.FuzzyPetriLogic.PetriNet.Recorders.FullRecorder;
import core.FuzzyPetriLogic.Tables.OneXOneTable;
import core.FuzzyPetriLogic.Tables.OneXTwoTable;
import core.TableParser;

import java.util.HashMap;
import java.util.function.Consumer;

public class FirstOrderPIControl {
    /*
     * This example implements P[k] = a*e[k] + b*e[k-1] + P[k-1]
     */

    String reader = "" + //
            "{[<NL><NM><ZR><PM><PL>]" + //
            " [<NL><NM><ZR><PM><PL>]" + //
            " [<NL><NM><ZR><PM><PL>]" + //
            " [<NL><NM><ZR><PM><PL>]" + //
            " [<NL><NM><ZR><PM><PL>]}"; //

    String doubleChannelAdder = ""//
            + "{[<NL,NL><NL,NL><NL,NL><NM,NM><ZR,ZR>]" //
            + " [<NL,NL><NL,NL><NM,NM><ZR,ZR><PM,PM>]" //
            + " [<NL,NL><NM,NM><ZR,ZR><PM,PM><PL,PL>]"//
            + " [<NM,NM><ZR,ZR><PM,PM><PL,PL><PL,PL>]"//
            + " [<ZR,ZR><PM,PM><PL,PL><PL,PL><PL,PL>]}";

    String doubleChannelDifferentiator = ""//
            + "{[<ZR,ZR><PM,PM><PL,PL><PL,PL><PL,PL>]" //
            + " [<NM,NM><ZR,ZR><PM,PM><PL,PL><PL,PL>]" //
            + " [<NL,NL><NM,NM><ZR,ZR><PM,PM><PL,PL>]"//
            + " [<NL,NL><NL,NL><NM,NM><ZR,ZR><PM,PM>]"//
            + " [<NL,NL><NL,NL><NL,NL><NM,NM><ZR,ZR>]}";

    String doubleChannelDifferentiator2 = ""//
            + "{[<ZR,ZR><nm,nm><nl,nl><nl,nl><nl,nl>]" //
            + " [<pm,pm><ZR,ZR><nm,nm><nl,nl><nl,nl>]" //
            + " [<pl,pl><pm,pm><ZR,ZR><nm,nm><nl,nl>]"//
            + " [<pl,pl><pl,pl><pm,pm><ZR,ZR><nm,nm>]"//
            + " [<pl,pl><pl,pl><pl,pl><pm,pm><ZR,ZR>]}";

    String adder = String.join("\n", //
            "{[<NL><NL><NL><NM><ZR>]", //
            " [<NL><NL><NM><ZR><PM>]", //
            " [<NL><NM><ZR><PM><PL>]", //
            " [<NM><ZR><PM><PL><PL>]", //
            " [<ZR><PM><PL><PL><PL>]}");

    public FirstOrderPIControl() {

// specify constants and period
        long period = 10;
        FirtsOrderSystemThreaded plant = new
                FirtsOrderSystemThreaded(0.5, 0.7, 0.2, 0.3, period);

// specify the exit interval of the system
        FuzzyDriver plantInDriver =
                FuzzyDriver.createDriverFromMinMax(-0.6, +0.6);
        FuzzyDriver userCommandInDriver =
                FuzzyDriver.createDriverFromMinMax(-0.6, +0.6);
        FuzzyDriver controlOutDriver =
                FuzzyDriver.createDriverFromMinMax(-1.0, 1.0);

// the Petri Net is being built
        TableParser parser = new TableParser();
        FuzzyPetriNet net = new FuzzyPetriNet();

        // PID controller, computed with MATLAB:
        // (1 - 0.5 z^-1) * u =  (0.4746 - 0.4904 z^-1 + 0.158 z^-2) * e
        //
        // u(t) = 0.5 u(t-1) + 0.476 e(t) - 0.4904 e(t-1) + 0.158 e(t-2)

        int p0 = net.addPlace(); net.setInitialMarkingForPlace(p0, FuzzyToken.zeroToken());
        int p1 = net.addPlace();
        int p2InpSys = net.addInputPlace();
        int p3 = net.addPlace();
        int p4InpCmd = net.addInputPlace();
        int p5 = net.addPlace();
        int p6 = net.addPlace();
        int p7Mem = net.addPlace(); net.setInitialMarkingForPlace(p7Mem, FuzzyToken.zeroToken());
        int p8 = net.addPlace();
        int p9 = net.addPlace();
        int p10 = net.addPlace();
        int p11Mem = net.addPlace();
        int p12 = net.addPlace(); net.setInitialMarkingForPlace(p12, FuzzyToken.zeroToken());
        int p13 = net.addPlace(); net.setInitialMarkingForPlace(p13, FuzzyToken.zeroToken());
        int p14 = net.addPlace();

        int t0 = net.addTransition(0, OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p0, t0, 1.0);
        net.addArcFromTransitionToPlace(t0, p1);

        int t1 = net.addTransition(0, parser.parseTable(reader));
        net.addArcFromPlaceToTransition(p1, t1, 1.0);
        net.addArcFromPlaceToTransition(p2InpSys, t1, 1.0);
        net.addArcFromTransitionToPlace(t1, p3);

        int t2 = net.addTransition(0,
                parser.parseTable(doubleChannelDifferentiator2));
        net.addArcFromPlaceToTransition(p4InpCmd, t2, 1.0);
        net.addArcFromPlaceToTransition(p3, t2, 1.0);
        net.addArcFromTransitionToPlace(t2, p5);
        net.addArcFromTransitionToPlace(t2, p6);


        int t4delay = net.addTransition(1,
                OneXTwoTable.defaultTable());
        net.addArcFromPlaceToTransition(p6, t4delay, 1.0);
        net.addArcFromTransitionToPlace(t4delay, p7Mem);
        net.addArcFromTransitionToPlace(t4delay, p12);

        int t3 = net.addTransition(0, parser.parseTable(adder));
        net.addArcFromPlaceToTransition(p14, t3, 1);
        net.addArcFromPlaceToTransition(p5, t3, 0.476); // CONTROLLER EQUATION, e(t)
        net.addArcFromTransitionToPlace(t3, p8);

        int t5 = net.addTransition(0,
                parser.parseTable(doubleChannelAdder));
        net.addArcFromPlaceToTransition(p8, t5, 1);
        net.addArcFromTransitionToPlace(t5, p9);
        net.addArcFromTransitionToPlace(t5, p10);

        int t6delay = net.addTransition(1,
                OneXTwoTable.defaultTable());
        net.addArcFromPlaceToTransition(p10, t6delay, 1.0);
        net.addArcFromTransitionToPlace(t6delay, p1);
        net.setInitialMarkingForPlace(p11Mem, FuzzyToken.zeroToken());
        net.addArcFromTransitionToPlace(t6delay, p11Mem);
        net.addArcFromPlaceToTransition(p11Mem, t5, 1); // CONTROLLER EQUATION, u(t-1)

        // p5 = e(k) -+-> p8       (t3)
        // p6 = e(k) -y-> p7,p12   (t4)
        // p7 = e(k-1) -+-> p14    (t9)
        // p12 = e(k-1) ---> p13   (t8)
        // p13 = e(k-2) -+-> p14   (t9)
        // p14 = e(k-1) * ... + e(k-2) * ... -+-> p8   (t3)

        int t8 = net.addTransition(1, OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p12, t8, 1);
        net.addArcFromTransitionToPlace(t8, p13);

        int t9 = net.addTransition(0, parser.parseTable(adder));
        net.addArcFromPlaceToTransition(p7Mem, t9, -0.4094); // CONTROLLER EQUATION, e(t-1)
        net.addArcFromPlaceToTransition(p13, t9, 0.158); // CONTROLLER EQUATION, e(t-2)
        net.addArcFromTransitionToPlace(t9, p14);

        int t7Out = net.addOuputTransition(OneXOneTable.defaultTable());
        net.addArcFromPlaceToTransition(p9, t7Out, 1.0);
        net.addActionForOuputTransition(t7Out, new
                Consumer<FuzzyToken>() {
                    @Override
                    public void accept(FuzzyToken t) {
                        System.out.println(
                                "Output From Transition 2: " + t.shortString());
                        plant.setCommand(controlOutDriver.defuzzify(t));
                    }
                });

        AsyncronRunnableExecutor executor = new
                AsyncronRunnableExecutor(net, period);

        FullRecorder recorder = new FullRecorder();
        executor.setRecorder(recorder);

// the threads of execution for the controller and system are launched
        (new Thread(plant)).start();
        (new Thread(executor)).start();

// a reference is inserted
        double command = 0.55;
        for (int i = 0; i < 200; i++) {
            if (i > 100) {
                command = 0.35;
            }
            HashMap<Integer, FuzzyToken> input = new HashMap<>();
            input.put(p4InpCmd,
                    userCommandInDriver.fuzzifie(command));
            input.put(p2InpSys,
                    plantInDriver.fuzzifie(plant.curentStatus()));
            executor.putTokenInInputPlace(input);
            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        plant.stop();
        executor.stop();
        MainView mainView = FuzzyPVizualzer.visualize(net, recorder);
    }

    public static void main(String args[]) {
        new FirstOrderPIControl();
    }
}