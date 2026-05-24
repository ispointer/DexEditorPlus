package com.aantik.dexeditorplus.Permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.aantik.dexeditorplus.Preferences.SavePref;

public class Permission {

    private static final int RQ = 100;

    private static final String[] op_TIP = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void check(final Activity act) {

        int sdk = Build.VERSION.SDK_INT;

        // Android 11+
        if (sdk >= 30) {

            if (Environment.isExternalStorageManager()) {
                return;
            }

            boolean noAsk =
                    SavePref.getBool(act,
                            "permissionNotRemind",
                            false);

            if (noAsk) {
                return;
            }

            AlertDialog.Builder antik = new AlertDialog.Builder(act);

            antik.setTitle(
                    "Storage Permission"
            );

            antik.setMessage(
                    "Need All Files Access permission for app working."
            );

            antik.setPositiveButton(
                    "ALLOW",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {

                                Intent i = new Intent(
                                        "android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION"
                                );

                                i.setData(
                                        Uri.parse(
                                                "package:" + act.getPackageName()
                                        )
                                );

                                act.startActivity(i);

                            } catch (Exception e) {

                                Intent i = new Intent("android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION");

                                act.startActivity(i);
                            }
                        }
                    });

            antik.setNegativeButton("CANCEL", null);
            antik.setNeutralButton(
                    "DON'T ASK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SavePref.setBool(act, "permissionNotRemind", true);
                        }
                    });

            antik.show();

            return;
        }

        // Android 6 >>>>>>> 10
        if (sdk >= 23) {

            boolean granted = true;

            int i = 0;

            for (; i < op_TIP.length; i++) {

                String p = op_TIP[i];

                if (act.checkSelfPermission(p)
                        != PackageManager.PERMISSION_GRANTED) {

                    granted = false;
                    break;
                }
            }

            if (!granted) {
                act.requestPermissions(
                        op_TIP,
                        RQ
                );
            }
        }
    }
}