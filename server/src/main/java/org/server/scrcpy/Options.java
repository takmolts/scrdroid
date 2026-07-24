package org.server.scrcpy;

public class Options {
    private int maxSize;
    private int bitRate;
    private boolean tunnelForward;

    // 仮想ディスプレイモード（拡張ディスプレイモード）
    // true の場合、対象端末の物理画面とは独立した VirtualDisplay を生成しキャプチャする
    private boolean virtualDisplayMode;
    // 仮想ディスプレイサイズ・DPI。0 の場合は物理画面の値を使う
    private int virtualWidth;
    private int virtualHeight;
    private int virtualDpi;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public boolean isTunnelForward() {
        return tunnelForward;
    }

    public void setTunnelForward(boolean tunnelForward) {
        this.tunnelForward = tunnelForward;
    }

    public boolean isVirtualDisplayMode() {
        return virtualDisplayMode;
    }

    public void setVirtualDisplayMode(boolean virtualDisplayMode) {
        this.virtualDisplayMode = virtualDisplayMode;
    }

    public int getVirtualWidth() {
        return virtualWidth;
    }

    public void setVirtualWidth(int virtualWidth) {
        this.virtualWidth = virtualWidth;
    }

    public int getVirtualHeight() {
        return virtualHeight;
    }

    public void setVirtualHeight(int virtualHeight) {
        this.virtualHeight = virtualHeight;
    }

    public int getVirtualDpi() {
        return virtualDpi;
    }

    public void setVirtualDpi(int virtualDpi) {
        this.virtualDpi = virtualDpi;
    }
}
