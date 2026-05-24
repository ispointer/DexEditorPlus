package com.aantik.dexeditorplus.dexentry;

import static com.aantik.dexeditorplus.log.TAG;
import android.util.Log;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClassTreeGet {

    public static List<String> getAllClasses(Map<String, String> dexPaths) {
        if (dexPaths == null || dexPaths.isEmpty()) return new ArrayList<String>();

        final List<String> classNames = Collections.synchronizedList(new ArrayList<String>());
        List<Thread> threads = new ArrayList<Thread>();

        for (final String path : dexPaths.values()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        File dexFile = new File(path);
                        if (dexFile.exists()) {
                            DexFile dex = DexFileFactory.loadDexFile(dexFile, Opcodes.getDefault());
                            List<String> tmp = new ArrayList<String>();
                            for (ClassDef def : dex.getClasses()) {
                                tmp.add(def.getType());
                            }
                            classNames.addAll(tmp);
                        }
                    } catch (IOException e) {
                        Log.d(TAG, "getAllClasses Error: " + e.getMessage());
                    }
                }
            });
            t.start();
            threads.add(t);
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Log.d(TAG, "getAllClasses Interrupted: " + e.getMessage());
            }
        }

        List<String> res = new ArrayList<String>(classNames);
        Collections.sort(res);
        return res;
    }
}
