package com.aantik.dexeditorplus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.aantik.dexeditorplus.Permission.Permission;
import com.aantik.dexeditorplus.dexentry.DexActivity;
import android.os.Environment;
import android.widget.Toast;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView edi_t;
    private Button pi_t;
    private Button pi_tt;

    private TextView git_t;
    private Map<String, String> dexP = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Permission.check(this);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        edi_t = findViewById(R.id.dexp_t);
        pi_t = findViewById(R.id.pi_t);
        git_t = findViewById(R.id.git_t);
        pi_tt = findViewById(R.id.pi_tt);

        git_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/ispointer/DexEditorPlus"));
                startActivity(intent);
            }
        });

        pi_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DexActivity.class);
                intent.putExtra("dexP", (HashMap<String, String>) dexP);
                startActivity(intent);
            }
        });

        pi_tt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogProperties fs = new DialogProperties();
                fs.selection_mode = DialogConfigs.MULTI_MODE;
                fs.selection_type = DialogConfigs.FILE_SELECT;
                File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                fs.root = root;
                fs.error_dir = root;
                fs.offset = root;
                fs.extensions = new String[]{"dex", "DEX"};
                FilePickerDialog fpdialog = new FilePickerDialog(MainActivity.this, fs);
                fpdialog.setTitle("Select Dex Files");
                fpdialog.setPositiveBtnName("Select");
                fpdialog.setNegativeBtnName("Cancel");
                fpdialog.setDialogSelectionListener((String[] files) -> {
                    if (files != null && files.length > 0) {
                        dexP.clear();
                        StringBuilder sb = new StringBuilder();
                        for (String path : files) {
                            File file = new File(path);
                            dexP.put(file.getName(), path);
                            sb.append(path).append("\n");
                        }
                        edi_t.setText(sb.toString().trim());
                    } else {
                        log.log("Dex Select");
                    }
                });
                fpdialog.show();
            }
        });
    }
}
