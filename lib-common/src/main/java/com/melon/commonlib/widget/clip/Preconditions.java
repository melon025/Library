package info.emm.commonlib.widget.clip;

/**
 * Created by Z on 2018/7/9.
 */

public class Preconditions {
    private Preconditions() {
        throw new AssertionError("No instances");
    }

    public static <T> T checkNotNull(T value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        } else {
            return value;
        }
    }

    public static void checkArgument(boolean check, String message) {
        if (!check) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkState(boolean check, String message) {
        if (!check) {
            throw new IllegalStateException(message);
        }
    }
}
