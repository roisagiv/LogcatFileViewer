
package com.logcatfileviewer;

import com.android.ddmlib.Log;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * The Class Main.
 */
public class Main {

    private static final String TAG = Main.class.getSimpleName();

    /**
     * Parse args, start threads.
     */
    public static void main(String[] args) {
        // In order to have the AWT/SWT bridge work on Leopard, we do this
        // little hack.
        if (isOSX()) {
            RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
            System.setProperty(
                    "JAVA_STARTED_ON_FIRST_THREAD_" + (rt.getName().split("@"))[0], "1");
        }

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtHandler());

        // Create an initial shell display with the correct app name.
        Display.setAppName("");
        Shell shell = new Shell(Display.getDefault());

        // create the three main threads
        UIThread ui = UIThread.getInstance();

        try {
            ui.runUI(null);
        } finally {

        }

        Log.d("ddms", "Bye");

        // this is kinda bad, but on MacOS the shutdown doesn't seem to finish
        // because of
        // a thread called AWT-Shutdown. This will help while I track this down.
        System.exit(0);
    }

    /** Return true if we're running on a Mac */
    private static boolean isOSX() {
        return org.eclipse.jface.util.Util.isMac();
    }

    /**
     * The Class UncaughtHandler.
     */
    private static class UncaughtHandler implements Thread.UncaughtExceptionHandler {

        /*
         * (non-Javadoc)
         * @see
         * java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.
         * lang.Thread, java.lang.Throwable)
         */
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Log.e(TAG, "shutting down due to uncaught exception");
            Log.e(TAG, e);
            System.exit(1);
        }
    }
}
