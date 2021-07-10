package me.nereo.multiimageselector;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelector;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE = 2;
    private TextView mResultText;
    private RadioGroup mChoiceMode, mShowCamera;
    private EditText mRequestNum;
    private ArrayList<String> mSelectPath;
    private View button;
    private String[] permissions = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissions[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
        permissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissions[2] = Manifest.permission.CAMERA;

        mResultText = (TextView) findViewById(R.id.result);
        mChoiceMode = (RadioGroup) findViewById(R.id.choice_mode);
        mShowCamera = (RadioGroup) findViewById(R.id.show_camera);
        mRequestNum = (EditText) findViewById(R.id.request_num);

        mChoiceMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.multi) {
                    mRequestNum.setEnabled(true);
                } else {
                    mRequestNum.setEnabled(false);
                    mRequestNum.setText("");
                }
            }
        });

        button = findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    /**
     * 调起选择图片功能
     */
    private void pickImage() {
        boolean showCamera = mShowCamera.getCheckedRadioButtonId() == R.id.show;
        int maxNum = 9;

        if (!TextUtils.isEmpty(mRequestNum.getText())) {
            try {
                maxNum = Integer.valueOf(mRequestNum.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        MultiImageSelector selector = MultiImageSelector.create();
        selector.showCamera(showCamera);
        selector.count(maxNum);
        if (mChoiceMode.getCheckedRadioButtonId() == R.id.single) {
            selector.single();
        } else {
            selector.multi();
        }
        selector.origin(mSelectPath);
        selector.start(MainActivity.this, REQUEST_IMAGE);
    }


    @Override   //只要开发者调用了requestPermissions方法,就算用户勾选了"不再提示"框，也会回调本方法
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (checkPermissions(permissions)) {
            pickImage();
        } else {
            showReasonAndRequestAgainIfCouldbe();
        }
    }

    /**
     * 检查所需权限是否都授予
     *
     * @param permissions 权限数组
     * @return 判断结果
     */
    private boolean checkPermissions(String[] permissions) {
        for (String perm : permissions) {
            if (ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 弹出对话框向用户解释为何需要该权限，并尝试再次请求权限
     * <p>
     * 注:shouldShowRequestPermissionRationale(Activity activity,String permission)方法
     * 如果用户已经授予该权限，此方法将返回false。
     * 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
     * 如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
     */
    private void showReasonAndRequestAgainIfCouldbe() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("需要权限")
                    .setMessage("该功能需要您授予拍照权限")
                    .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                        }
                    })
                    .setNegativeButton("拒绝", null)
                    .create().show();
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {//用户拒绝并勾选了“不再提示”框，跳转应用设置界面
            new AlertDialog.Builder(this)
                    .setTitle("需要权限")
                    .setMessage("该功能需要您授予拍照权限,请手动开启权限")
                    .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri packageURI = Uri.parse("package:" + getPackageName());
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("拒绝", null)
                    .create().show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("需要权限")
                    .setMessage("该功能需要读取您的内存卡上的图片")
                    .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                        }
                    })
                    .setNegativeButton("拒绝", null)
                    .create().show();
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {//用户拒绝并勾选了“不再提示”框，跳转应用设置界面
            new AlertDialog.Builder(this)
                    .setTitle("需要权限")
                    .setMessage("该功能需要读取您的内存卡上的图片,请手动开启权限")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri packageURI = Uri.parse("package:" + getPackageName());
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .create().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                for (String p : mSelectPath) {
                    sb.append(p);
                    sb.append("\n");
                }
                mResultText.setText(sb.toString());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!checkPermissions(permissions)) {
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 0);
        } else {
            pickImage();
        }
    }
}