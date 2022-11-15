package gradle.graalvm.skeleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.management.HotSpotDiagnosticMXBean;
import org.graalvm.nativeimage.VMRuntime;

import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

class Main {
    private static List<Object> PREVENT_GC = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int numberOfObjectMappers = args.length == 0 ? 1 : Integer.parseInt(args[0]);

        for (int i = 0; i < numberOfObjectMappers; i++) {
            ObjectMapper mapper = new ObjectMapper();
            PREVENT_GC.add(mapper);
        }

        heapDump();
    }

    private static void heapDump() throws IOException {
        try {
            VMRuntime.dumpHeap("jackson-native-image-%s.hprof".formatted(Instant.now()), true);
        } catch (UnsupportedOperationException e) {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
                server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
            mxBean.dumpHeap("jackson-jvm-%s.hprof".formatted(Instant.now()), true);
        }
    }
}
