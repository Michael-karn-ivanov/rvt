package mivanov.rev.api.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {
    public static double decorateDouble(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(3, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
}
