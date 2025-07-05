package star.e2e.util;

import java.util.ArrayList;
import java.util.List;

public final class GeoGridUtil {

    public static List<Double> generateRange(double min, double max, int count) {
        List<Double> result = new ArrayList<>();
        double step = (max - min) / (count - 1);

        for (int i = 0; i < count; i++) {
            result.add(min + i * step);
        }
        return result;
    }

}
