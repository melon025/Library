package androidx.fragment.app;



import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * melon
 * on 2021/3/5
 */
public class BackstackAccessor {
    private BackstackAccessor() {
        throw new IllegalStateException("Not instantiatable");
    }

    public static boolean isFragmentOnBackStack(Fragment fragment) {
        try {
            return fragment.isInBackStack();
        } catch (IllegalAccessError var2) {
            return isInBackStackAndroidX(fragment);
        }
    }

    private static boolean isInBackStackAndroidX(Fragment fragment) {
        StringWriter writer = new StringWriter();
        fragment.dump("", (FileDescriptor)null, new PrintWriter(writer), (String[])null);
        String dump = writer.toString();
        return !dump.contains("mBackStackNesting=0");
    }
}
