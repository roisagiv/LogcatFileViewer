
package com.logcatfileviewer;

import com.android.ddmlib.Log;
import com.android.ddmlib.Log.ILogOutput;
import com.android.ddmlib.Log.LogLevel;
import com.android.ddmuilib.ImageLoader;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The Class UIThread.
 */
public class UIThread {

    /** The Constant APP_NAME. */
    private static final String APP_NAME = "Logcat File Viewer";

    /** The Instance. */
    private static UIThread mInstance = new UIThread();

    /** The Ddm ui lib loader. */
    private ImageLoader mDdmUiLibLoader;

    /** The Display. */
    private Display mDisplay;

    /**
     * Get singleton instance of the UI thread.
     * 
     * @return single instance of UIThread
     */
    public static UIThread getInstance() {
        return mInstance;
    }

    /**
     * Run ui.
     * 
     * @param ddmsParentLocation the ddms parent location
     */
    public void runUI(String ddmsParentLocation) {
        Display.setAppName(APP_NAME);
        mDisplay = Display.getDefault();
        final Shell shell = new Shell(mDisplay, SWT.SHELL_TRIM);

        // create the image loaders for DDMS and DDMUILIB
        mDdmUiLibLoader = ImageLoader.getDdmUiLibLoader();

        shell.setImage(ImageLoader.getLoader(this.getClass()).loadImage(mDisplay, "ddms-128.png", //$NON-NLS-1$
                100, 50, null));

        Log.setLogOutput(new ILogOutput() {
            @Override
            public void printAndPromptLog(final LogLevel logLevel, final String tag,
                    final String message) {
                Log.printLog(logLevel, tag, message);
                // dialog box only run in UI thread..
                mDisplay.asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        Shell activeShell = mDisplay.getActiveShell();
                        if (logLevel == LogLevel.ERROR) {
                            MessageDialog.openError(activeShell, tag, message);
                        } else {
                            MessageDialog.openWarning(activeShell, tag, message);
                        }
                    }
                });
            }

            @Override
            public void printLog(LogLevel logLevel, String tag, String message) {
                Log.printLog(logLevel, tag, message);
            }
        });

        // set the handler for hprof dump
        // ClientData.setHprofDumpHandler(new HProfHandler(shell));
        // ClientData.setMethodProfilingHandler(new
        // MethodProfilingHandler(shell));

        // [try to] ensure ADB is running
        // in the new SDK, adb is in the platform-tools, but when run from the
        // command line
        // in the Android source tree, then adb is next to ddms.
        // String adbLocation;
        // if (ddmsParentLocation != null && ddmsParentLocation.length() != 0) {
        // // check if there's a platform-tools folder
        // File platformTools = new File(new
        // File(ddmsParentLocation).getParent(),
        //                    "platform-tools");  //$NON-NLS-1$
        // if (platformTools.isDirectory()) {
        //                adbLocation = platformTools.getAbsolutePath() + File.separator + "adb"; //$NON-NLS-1$
        // } else {
        //                adbLocation = ddmsParentLocation + File.separator + "adb"; //$NON-NLS-1$
        // }
        // } else {
        //            adbLocation = "adb"; //$NON-NLS-1$
        // }

        // AndroidDebugBridge.init(true /* debugger support */);
        // AndroidDebugBridge.createBridge(adbLocation, true /* forceNewBridge
        // */);

        // we need to listen to client change to be notified of client status
        // (profiling) change
        // AndroidDebugBridge.addClientChangeListener(this);

        shell.setText("Dalvik Debug Monitor");

        shell.pack();
        // setSizeAndPosition(shell);
        shell.open();

        while (!shell.isDisposed()) {
            if (!mDisplay.readAndDispatch())
                mDisplay.sleep();
        }

        // for (TablePanel panel : mPanels) {
        // if (panel != null) {
        // panel.dispose();
        // }
        // }

        ImageLoader.dispose();

        mDisplay.dispose();
        Log.d("ddms", "UI is down");
    }

}
