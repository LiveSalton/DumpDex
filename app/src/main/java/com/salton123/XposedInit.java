package com.salton123;

import com.salton123.hook.dex.DumpDexManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        DumpDexManager.INSTANCE.dumpAction(lpparam.packageName, lpparam.classLoader);
    }
}
