package org.server.scrcpy;

import android.graphics.Rect;
import android.hardware.display.VirtualDisplay;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;

import org.server.scrcpy.wrappers.ServiceManager;
import org.server.scrcpy.wrappers.SurfaceControl;


public class ScreenCapture {

    // Android VirtualDisplay 用フラグ (android.hardware.display.DisplayManager 定義値)
    private static final int VIRTUAL_DISPLAY_FLAG_PUBLIC = 1;
    private static final int VIRTUAL_DISPLAY_FLAG_PRESENTATION = 1 << 1;
    private static final int VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY = 1 << 3;

    private final Device device;
    private IBinder display;
    private VirtualDisplay virtualDisplay;

    public ScreenCapture(Device device) {
        this.device = device;
    }

    public void start(Surface surface) {
        ScreenInfo screenInfo = device.getScreenInfo();

        Rect deviceRect = device.getScreenInfo().getDeviceSize().toRect();
        Rect videoRect = device.getScreenInfo().getVideoSize().toRect();


        if (display != null) {
            SurfaceControl.destroyDisplay(display);
            display = null;
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }

        Options options = device.getOptions();
        if (options != null && options.isVirtualDisplayMode()) {
            // 仮想ディスプレイモード: 物理画面と独立した VirtualDisplay を生成
            int dpi = options.getVirtualDpi() > 0 ? options.getVirtualDpi() : 320;
            int flags = VIRTUAL_DISPLAY_FLAG_PUBLIC
                    | VIRTUAL_DISPLAY_FLAG_PRESENTATION
                    | VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY;
            try {
                virtualDisplay = ServiceManager.getDisplayManager()
                        .createVirtualDisplayWithFlags("scrcpy-virtual",
                                videoRect.width(), videoRect.height(), dpi, surface, flags);
                if (virtualDisplay != null && virtualDisplay.getDisplay() != null) {
                    device.setVirtualDisplayId(virtualDisplay.getDisplay().getDisplayId());
                }
                Ln.d("Display: virtual display mode (id=" + device.getVirtualDisplayId() + ")");
                return;
            } catch (Exception e) {
                Log.e("ScreenCapture", "createVirtualDisplayWithFlags failed", e);
                throw new AssertionError("Could not create virtual display", e);
            }
        }

        try {
            virtualDisplay = ServiceManager.getDisplayManager()
                    .createVirtualDisplay("scrcpy", videoRect.width(), videoRect.height(), 0, surface);
            Ln.d("Display: using DisplayManager API");
        } catch (Exception displayManagerException) {
            try {
                display = createDisplay();
                setDisplaySurface(display, surface, deviceRect, videoRect);
            } catch (Exception surfaceControlException) {
                throw new AssertionError("Could not create display");
            }
        }
    }

    public void release() {
        device.setRotationListener(null);
        if (display != null) {
            SurfaceControl.destroyDisplay(display);
            display = null;
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
    }

    private static IBinder createDisplay() throws Exception {
        // Since Android 12 (preview), secure displays could not be created with shell permissions anymore.
        // On Android 12 preview, SDK_INT is still R (not S), but CODENAME is "S".
        boolean secure = Build.VERSION.SDK_INT < Build.VERSION_CODES.R || (Build.VERSION.SDK_INT == Build.VERSION_CODES.R && !"S".equals(
                Build.VERSION.CODENAME));
        return SurfaceControl.createDisplay("scrcpy", secure);
    }

    private static void setDisplaySurface(IBinder display, Surface surface, Rect deviceRect, Rect displayRect) {
        SurfaceControl.openTransaction();
        try {
            SurfaceControl.setDisplaySurface(display, surface);
            SurfaceControl.setDisplayProjection(display, 0, deviceRect, displayRect);
            SurfaceControl.setDisplayLayerStack(display, 0);
        } finally {
            SurfaceControl.closeTransaction();
        }
    }
}
