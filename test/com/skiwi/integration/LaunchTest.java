
package com.skiwi.integration;

import com.skiwi.javaluainteroptest.JavaLuaInteropTest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Frank van Heeswijk
 */
public class LaunchTest {
    @Test(timeout = 5000)
    public void testLaunch() throws Throwable {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<?> future = service.submit(() -> JavaLuaInteropTest.main(new String[0]));
        try {
            future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException ex) {
            assertTrue(true);
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }
}
