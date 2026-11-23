
package student.mgt.util;

import java.util.Locale;
import java.util.regex.Pattern;

public final class BusNumberUtil {

    private static final Pattern BUS_NUMBER_PATTERN =
            Pattern.compile(
                    "^[A-Z]{2}\\s[0-9]{1,2}\\s[A-Z]{1,3}\\s[0-9]{4}$"
            );

    private BusNumberUtil() {
    }

    public static boolean isValid(String busNumber) {

        if (busNumber == null || busNumber.isBlank()) {
            return false;
        }

        return BUS_NUMBER_PATTERN.matcher(
                normalize(busNumber)
        ).matches();
    }

    public static String normalize(String busNumber) {

        if (busNumber == null) {
            return null;
        }

        return busNumber.trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }
}
