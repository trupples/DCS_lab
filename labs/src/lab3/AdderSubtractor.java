package lab3;

import fuzzy.FuzzyDefuzzy;
import fuzzy.FuzzyToken;
import fuzzy.FuzzyValue;
import fuzzy.TwoXTwoTable;

import java.util.EnumMap;
import java.util.Map;

public class AdderSubtractor {

    public static TwoXTwoTable createTable() {
        Map<FuzzyValue, Map<FuzzyValue, FuzzyValue>> add = new EnumMap<>(FuzzyValue.class);
        Map<FuzzyValue, Map<FuzzyValue, FuzzyValue>> sub = new EnumMap<>(FuzzyValue.class);

        for(Object[] row : new Object[][] {
                {FuzzyValue.NL, new FuzzyValue[] {FuzzyValue.NL, FuzzyValue.NL, FuzzyValue.NL, FuzzyValue.NM, FuzzyValue.ZR} },
                {FuzzyValue.NM, new FuzzyValue[] {FuzzyValue.NL, FuzzyValue.NL, FuzzyValue.NM, FuzzyValue.ZR, FuzzyValue.PM} },
                {FuzzyValue.ZR, new FuzzyValue[] {FuzzyValue.NL, FuzzyValue.NM, FuzzyValue.ZR, FuzzyValue.PM, FuzzyValue.PL} },
                {FuzzyValue.PM, new FuzzyValue[] {FuzzyValue.NM, FuzzyValue.ZR, FuzzyValue.PM, FuzzyValue.PL, FuzzyValue.PL} },
                {FuzzyValue.PL, new FuzzyValue[] {FuzzyValue.ZR, FuzzyValue.PM, FuzzyValue.PL, FuzzyValue.PL, FuzzyValue.PL} }
        }) {
            FuzzyValue rowValue = (FuzzyValue) row[0];
            FuzzyValue[] x3s = (FuzzyValue[]) row[1];

            Map<FuzzyValue, FuzzyValue> addLine = new EnumMap<>(FuzzyValue.class);
            addLine.put(FuzzyValue.NL, x3s[0]);
            addLine.put(FuzzyValue.NM, x3s[1]);
            addLine.put(FuzzyValue.ZR, x3s[2]);
            addLine.put(FuzzyValue.PM, x3s[3]);
            addLine.put(FuzzyValue.PL, x3s[4]);
            add.put(rowValue, addLine);

            Map<FuzzyValue, FuzzyValue> subLine = new EnumMap<>(FuzzyValue.class);
            subLine.put(FuzzyValue.NL, x3s[4]);
            subLine.put(FuzzyValue.NM, x3s[3]);
            subLine.put(FuzzyValue.ZR, x3s[2]);
            subLine.put(FuzzyValue.PM, x3s[1]);
            subLine.put(FuzzyValue.PL, x3s[0]);
            sub.put(rowValue, subLine);
        }

        return new TwoXTwoTable(add, sub);
    }

    public static void main(String[] args) {
        double w1 = 1;
        double w2 = 1;

//specifying the limits for fuzzyfication, defuzzyfication
        FuzzyDefuzzy fuzDefuz =
                new FuzzyDefuzzy(-1.0, -0.5, 0.0, 0.5, 1.0);

//creating FLRS table for tow inputs and two outputs
        TwoXTwoTable addsub =  createTable();

//giving the two inputs
        double x1 = 0.33;
        double x2 = 0.12;

//multiplying the inputs with the amplification and fuzzyfication factors
        FuzzyToken inpToken1 = fuzDefuz.fuzzyfie(x1 * w1);
        FuzzyToken inpToken2 = fuzDefuz.fuzzyfie(x2 * w2);

//executing the FLRS table

        FuzzyToken[] out = addsub.execute(inpToken1, inpToken2);

//displaying the defuzzyfication results

        System.out.println("x3 :: " + fuzDefuz.defuzzify(out[0]));
        System.out.println("x4 :: " + fuzDefuz.defuzzify(out[1]));
    }
}
