package com.example.letsvpnlauncher;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fvbox.lib.FCore;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String PKG = "world.letsgo.booster.android.pro";
    private static final int USER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setPadding(40, 80, 40, 40);
        tv.setTextSize(16);

        Button btnInstall = new Button(this);
        btnInstall.setText("安装 letsvpn (/sdcard/letsvpn.apk)");

        Button btnLaunch = new Button(this);
        btnLaunch.setText("启动 letsvpn");

        Button btnClear = new Button(this);
        btnClear.setText("清除 letsvpn 数据（重置试用）");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 80, 40, 40);
        layout.addView(tv);
        layout.addView(btnInstall);
        layout.addView(btnLaunch);
        layout.addView(btnClear);
        setContentView(layout);

        updateStatus(tv);

        btnInstall.setOnClickListener(v -> {
            File src = new File("/sdcard/letsvpn.apk");
            if (!src.exists()) {
                Toast.makeText(this, "找不到 /sdcard/letsvpn.apk", Toast.LENGTH_SHORT).show();
                return;
            }
            // 复制到 app 私有目录，确保沙盒可读
            File apk = new File(getFilesDir(), "letsvpn.apk");
            try {
                java.io.FileInputStream in = new java.io.FileInputStream(src);
                java.io.FileOutputStream out = new java.io.FileOutputStream(apk);
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
                in.close();
                out.close();
            } catch (Exception e) {
                Toast.makeText(this, "复制失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            com.fvbox.lib.common.pm.InstallResult result = FCore.get().installPackageAsUser(apk, USER_ID);
            if (result != null && result.getSuccess()) {
                Toast.makeText(this, "安装成功: " + result.getPackageName(), Toast.LENGTH_SHORT).show();
            } else {
                String msg = result != null ? result.getMsg() : "null result";
                Toast.makeText(this, "安装失败: " + msg, Toast.LENGTH_LONG).show();
            }
            updateStatus(tv);
        });

        btnLaunch.setOnClickListener(v -> {
            if (!FCore.get().isInstalled(PKG, USER_ID)) {
                Toast.makeText(this, "未安装，请先点安装", Toast.LENGTH_SHORT).show();
                return;
            }
            FCore.get().launchApk(PKG, USER_ID);
        });

        btnClear.setOnClickListener(v -> {
            FCore.get().clearPackage(PKG, USER_ID);
            Toast.makeText(this, "数据已清除", Toast.LENGTH_SHORT).show();
            updateStatus(tv);
        });
    }

    private void updateStatus(TextView tv) {
        boolean installed = FCore.get().isInstalled(PKG, USER_ID);
        tv.setText("letsvpn 状态: " + (installed ? "已安装" : "未安装") + "\n\n"
                + "步骤：\n1. 把 letsvpn.apk 放到 /sdcard/letsvpn.apk\n"
                + "2. 点安装\n3. 点启动\n\n"
                + "已开启：hideRoot + hideVPN");
    }
}
