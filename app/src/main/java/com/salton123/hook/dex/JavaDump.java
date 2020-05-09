package com.salton123.hook.dex;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class JavaDump {
    private static final String TAG = "JavaDump";

    public static void dump(final String packageName, ClassLoader classLoader) {
        Log.i(TAG, "start hook Instrumentation#newApplication");
        XposedHelpers.findAndHookMethod("android.app.Instrumentation",
                classLoader, "newApplication",
                ClassLoader.class, String.class, Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.i(TAG, "Application=" + param.getResult());
                        dump(packageName, param.getResult().getClass());
                        attachBaseContextHook(packageName, ((Application) param.getResult()));
                    }
                });
    }

    private static void dump(String packageName, Class<?> aClass) {
        Object dexCache = XposedHelpers.getObjectField(aClass, "dexCache");
        Log.i(TAG, "decCache=" + dexCache);
        Object o = XposedHelpers.callMethod(dexCache, "getDex");
        byte[] bytes = (byte[]) XposedHelpers.callMethod(o, "getBytes");
        String path = "/data/data/" + packageName + "/dump";
        File file = new File(path, "source-" + bytes.length + ".dex");
        if (file.exists()) {
            Log.i(TAG, file.getName() + " exists");
            return;
        }
        writeByteToFile(bytes, file.getAbsolutePath());
    }

    private static void attachBaseContextHook(final String packageName, final Application application) {
        ClassLoader classLoader = application.getClassLoader();
        XposedHelpers.findAndHookMethod(ClassLoader.class,
                "loadClass", String.class, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.i(TAG, "loadClass->" + param.args[0]);
                        Class result = (Class) param.getResult();
                        if (result != null) {
                            dump(packageName, result);
                        }
                    }
                });
        XposedHelpers.findAndHookMethod("java.lang.ClassLoader",
                classLoader, "loadClass", String.class, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.i(TAG, "loadClassWithclassLoader->" + param.args[0]);
                        Class result = (Class) param.getResult();
                        if (result != null) {
                            dump(packageName, result);
                        }
                    }
                });
    }

    private static void writeByteToFile(byte[] data, String path) {
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(path);
            localFileOutputStream.write(data);
            localFileOutputStream.close();
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }
}
