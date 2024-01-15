package lab3;

import fuzzy.*;

import java.util.EnumMap;
import java.util.Map;

public class InversorExemple {
    public static TwoXTwoTable createInversor() {

//construct tabela1 FLRS for inversor, first output

        Map<FuzzyValue, Map<FuzzyValue, FuzzyValue>> ruleTable1 =
                new EnumMap<>(FuzzyValue.class);
        Map<FuzzyValue, FuzzyValue> nlLine =
                new EnumMap<>(FuzzyValue.class);
        ruleTable1.put(FuzzyValue.NL, nlLine);
        nlLine.put(FuzzyValue.NL, FuzzyValue.PL);
        nlLine.put(FuzzyValue.NM, FuzzyValue.PL);
        nlLine.put(FuzzyValue.ZR, FuzzyValue.PL);
        nlLine.put(FuzzyValue.PM, FuzzyValue.PL);
        nlLine.put(FuzzyValue.PL, FuzzyValue.PL);

        Map<FuzzyValue, FuzzyValue> nmLine =
                new EnumMap<>(FuzzyValue.class);
        ruleTable1.put(FuzzyValue.NM, nmLine);
        nmLine.put(FuzzyValue.NL, FuzzyValue.PM);
        nmLine.put(FuzzyValue.NM, FuzzyValue.PM);
        nmLine.put(FuzzyValue.ZR, FuzzyValue.PM);
        nmLine.put(FuzzyValue.PM, FuzzyValue.PM);
        nmLine.put(FuzzyValue.PL, FuzzyValue.PM);

        Map<FuzzyValue, FuzzyValue> zrLine =
                new EnumMap<>(FuzzyValue.class);
        ruleTable1.put(FuzzyValue.ZR, zrLine);
        zrLine.put(FuzzyValue.NL, FuzzyValue.ZR);
        zrLine.put(FuzzyValue.NM, FuzzyValue.ZR);
        zrLine.put(FuzzyValue.ZR, FuzzyValue.ZR);
        zrLine.put(FuzzyValue.PM, FuzzyValue.ZR);
        zrLine.put(FuzzyValue.PL, FuzzyValue.ZR);

        Map<FuzzyValue, FuzzyValue> pmLine =
                new EnumMap<>(FuzzyValue.class);
        ruleTable1.put(FuzzyValue.PM, pmLine);
        pmLine.put(FuzzyValue.NL, FuzzyValue.NM);
        pmLine.put(FuzzyValue.NM, FuzzyValue.NM);
        pmLine.put(FuzzyValue.ZR, FuzzyValue.NM);
        pmLine.put(FuzzyValue.PM, FuzzyValue.NM);
        pmLine.put(FuzzyValue.PL, FuzzyValue.NM);

        Map<FuzzyValue, FuzzyValue> plLine =
                new EnumMap<>(FuzzyValue.class);
        ruleTable1.put(FuzzyValue.PL, plLine);
        plLine.put(FuzzyValue.NL, FuzzyValue.NL);
        plLine.put(FuzzyValue.NM, FuzzyValue.NL);
        plLine.put(FuzzyValue.ZR, FuzzyValue.NL);
        plLine.put(FuzzyValue.PM, FuzzyValue.NL);
        plLine.put(FuzzyValue.PL, FuzzyValue.NL);

//construct tabela2 FLRS for inversor, the second output

        Map<FuzzyValue, Map<FuzzyValue, FuzzyValue>> ruleTable2 =
                new EnumMap<>(FuzzyValue.class);
        Map<FuzzyValue, FuzzyValue> generalLine =
                new EnumMap<>(FuzzyValue.class);
        generalLine.put(FuzzyValue.NL, FuzzyValue.PL);
        generalLine.put(FuzzyValue.NM, FuzzyValue.PM);
        generalLine.put(FuzzyValue.ZR, FuzzyValue.ZR);
        generalLine.put(FuzzyValue.PM, FuzzyValue.NM);
        generalLine.put(FuzzyValue.PL, FuzzyValue.NL);
        ruleTable2.put(FuzzyValue.PL, generalLine);
        ruleTable2.put(FuzzyValue.PM, generalLine);
        ruleTable2.put(FuzzyValue.ZR, generalLine);
        ruleTable2.put(FuzzyValue.NM, generalLine);
        ruleTable2.put(FuzzyValue.NL, generalLine);

//returning FLRS table with two inputs and two outputs
        return new TwoXTwoTable(ruleTable1, ruleTable2);
    }

    public static void main(String[] args) {
        double w1 = 0.33;
        double w2 = 1.5;

//specifying the limits for fuzzyfication, defuzzyfication		
        FuzzyDefuzzy fuzDefuz =
                new FuzzyDefuzzy(-1.0, -0.5, 0.0, 0.5, 1.0);

//creating FLRS table for tow inputs and two outputs
        TwoXTwoTable inversor =  createInversor();

//giving the two inputs		
        double x1 = -0.33;
        double x2 = 0.12;

//multiplying the inputs with the amplification and fuzzyfication factors  	
        FuzzyToken inpToken1 = fuzDefuz.fuzzyfie(x1 * w1);
        FuzzyToken inpToken2 = fuzDefuz.fuzzyfie(x2 * w2);

//executing the FLRS table   

        FuzzyToken[] out = inversor.execute(inpToken1, inpToken2);

//displaying the defuzzyfication results

        System.out.println("x3 :: " + fuzDefuz.defuzzify(out[0]));
        System.out.println("x4 :: " + fuzDefuz.defuzzify(out[1]));

    }
}

