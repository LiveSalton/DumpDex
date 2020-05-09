package com.salton123.hook.dex;

public class NativeDump {
    static {
        System.loadLibrary("DexDump");
    }

    public static native void dump(String packageName);
}
