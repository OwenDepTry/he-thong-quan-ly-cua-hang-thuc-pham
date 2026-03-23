package ui;

public final class PanelSortUtils {

    private PanelSortUtils() {
    }

    public static int compareText(Object left, Object right) {
        String a = normalize(left);
        String b = normalize(right);
        return a.compareToIgnoreCase(b);
    }

    public static int compareNumber(Object left, Object right) {
        return Double.compare(toDouble(left), toDouble(right));
    }

    public static int compareCode(Object left, Object right) {
        CodePart a = parseCode(normalize(left));
        CodePart b = parseCode(normalize(right));

        int byPrefix = a.prefix.compareToIgnoreCase(b.prefix);
        if (byPrefix != 0) {
            return byPrefix;
        }
        return Integer.compare(a.number, b.number);
    }

    private static String normalize(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static double toDouble(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        String text = String.valueOf(value).trim().replace(",", "");
        try {
            return Double.parseDouble(text);
        } catch (Exception ex) {
            return 0;
        }
    }

    private static CodePart parseCode(String value) {
        if (value.isEmpty()) {
            return new CodePart("", 0);
        }

        int split = value.length();
        while (split > 0 && Character.isDigit(value.charAt(split - 1))) {
            split--;
        }

        String prefix = value.substring(0, split);
        String numberPart = value.substring(split);

        int number;
        try {
            number = numberPart.isEmpty() ? 0 : Integer.parseInt(numberPart);
        } catch (Exception ex) {
            number = 0;
        }

        return new CodePart(prefix, number);
    }

    private static class CodePart {
        final String prefix;
        final int number;

        CodePart(String prefix, int number) {
            this.prefix = prefix;
            this.number = number;
        }
    }
}