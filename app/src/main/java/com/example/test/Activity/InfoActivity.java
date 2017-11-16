package com.example.test.Activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.example.test.DataBase.General;
import com.example.test.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.litepal.crud.DataSupport;

// TODO: 2017/11/14 添加内容后滚动到对应位置
public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private String source;

    private ImageView imageView;

    private FloatingActionButton floatingActionButton;

    private int isConcerned = 0;

    private MaterialEditText nameEditText;

    private MaterialEditText ageEditText;

    private MaterialEditText countryEditText;

    private MaterialEditText moreInfoEditText;

    private int checkSex = 0;

    private int currentId = 0;

    private RadioGroup radioGroup;

    private RadioButton[] buttons = new RadioButton[3];

    private String name_former;

    private int checkedSex_former;

    private String age_former;

    private String country_former;

    private String info_former;

    private int isConcerned_former;

    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_info);

        initData();

        Intent intent = getIntent();
        source = intent.getStringExtra("SOURCE");

        if (source.equals("FAB")) {
            //concern不用判断，默认就是0
            nameEditText.setHint("暂无姓名");
            buttons[0].setChecked(true);
            ageEditText.setHint("暂无生卒年");
            countryEditText.setHint("暂无所属势力");
            moreInfoEditText.setHint("暂无更多信息");
        } else if (source.equals("REC")) {
            String path=intent.getStringExtra("IMAGE_URI");
            int image = intent.getIntExtra("IMAGE_RES", 0);
            if (path != null) {
                //如果path == null，说明没有设置图片
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageView.setImageBitmap(bitmap);
            } else if (image != 0) {
                //考虑初始设置的十个将士的图片调用的是图片资源
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageResource(image);
            }
            isConcerned = intent.getIntExtra("CONCERNED", 0);
            if (isConcerned == 0) {
                floatingActionButton.setImageResource(R.drawable.star_off);
            } else {
                floatingActionButton.setImageResource(R.drawable.star_on);
            }

            String name_temp = intent.getStringExtra("NAME");
            if (name_temp.equals("暂无姓名")) {
                nameEditText.setHint("暂无姓名");
            } else {
                nameEditText.setText(name_temp);
            }
            String age_temp = intent.getStringExtra("AGE");
            if (age_temp.equals("暂无生卒年")) {
                ageEditText.setHint("暂无生卒年");
            } else {
                ageEditText.setText(age_temp);
            }
            String country_temp = intent.getStringExtra("COUNTRY");
            if (country_temp.equals("暂无所属势力")) {
                countryEditText.setHint("暂无所属势力");
            } else {
                countryEditText.setText(country_temp);
            }
            String info_temp = intent.getStringExtra("INFO");
            if (info_temp.equals("暂无更多信息")) {
                moreInfoEditText.setHint("暂无更多信息");
            } else {
                moreInfoEditText.setText(info_temp);
            }

            buttons[intent.getIntExtra("SEX", 0)].setChecked(true);
            currentId = intent.getIntExtra("ID", 0);
        }

        //用来记录文本内容是否发生改变
        isConcerned_former = isConcerned;
        name_former = nameEditText.getText().toString();
        checkedSex_former = radioGroup.getCheckedRadioButtonId();
        age_former = ageEditText.getText().toString();
        country_former = countryEditText.getText().toString();
        info_former = moreInfoEditText.getText().toString();
    }

    public void initData() {
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        Toolbar toolbar = findViewById(R.id.general_info_toolbar);
        CardView cardView = findViewById(R.id.InfoActivity_card);
        imageView = findViewById(R.id.general_info_image);
        floatingActionButton = findViewById(R.id.general_info_fab);
        radioGroup = findViewById(R.id.radiogroup);
        buttons[0] = findViewById(R.id.radiogroup_others);
        buttons[1] = findViewById(R.id.radiogroup_man);
        buttons[2] = findViewById(R.id.radiogroup_woman);
        nameEditText = findViewById(R.id.general_info_name_content);
        ageEditText = findViewById(R.id.general_info_age_and_sex_content);
        countryEditText = findViewById(R.id.general_info_country_content);
        moreInfoEditText = findViewById(R.id.general_info_more_content);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle(" ");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageView.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);

        //信息卡片只会出现一次
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("INFO_ACTIVITY_CARD", false)) {
            cardView.setVisibility(View.GONE);
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("INFO_ACTIVITY_CARD", true);
        editor.apply();
    }

    public void save() {

        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radiogroup_others:
                checkSex = 0;
                break;
            case R.id.radiogroup_man:
                checkSex = 1;
                break;
            case R.id.radiogroup_woman:
                checkSex = 2;
                break;
            default:
        }

        //如果来源是REC的话，已经new出过数据，这时候根据id找到这个数据进行更新。如果来源是FAB的话，新建一个数据。
        if (source.equals("REC")) {
            General general_current = DataSupport.find(General.class, currentId);
            String name = TextUtils.isEmpty(nameEditText.getText())? "暂无姓名" : nameEditText.getText().toString();
            String age = TextUtils.isEmpty(ageEditText.getText())? "暂无生卒年" : ageEditText.getText().toString();
            String country = TextUtils.isEmpty(countryEditText.getText())? "暂无所属势力" : countryEditText.getText().toString();
            String info = TextUtils.isEmpty(moreInfoEditText.getText())? "暂无更多信息" : moreInfoEditText.getText().toString();
            general_current.setConcerned(isConcerned);
            general_current.setName(name);
            general_current.setImagePath(imagePath);
            general_current.setSex(checkSex);
            general_current.setAge(age);
            general_current.setCountry(country);
            general_current.setInfo(info);
            general_current.save();
        } else if (source.equals("FAB")) {
            General general = new General();
            String name = TextUtils.isEmpty(nameEditText.getText())? general.getName() : nameEditText.getText().toString();
            String age = TextUtils.isEmpty(ageEditText.getText())? general.getAge() : ageEditText.getText().toString();
            String country = TextUtils.isEmpty(countryEditText.getText())? general.getCountry() : countryEditText.getText().toString();
            String info = TextUtils.isEmpty(moreInfoEditText.getText())? general.getInfo() : moreInfoEditText.getText().toString();
            general.setConcerned(isConcerned);
            general.setName(name);
            general.setImagePath(imagePath);
            general.setSex(checkSex);
            general.setAge(age);
            general.setCountry(country);
            general.setInfo(info);
            general.save();
        }

    }
    // TODO: 2017/11/11 使用真实路径传输图片，如果原图片删除，就会导致无法显示

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.done:
                // 这里的逻辑和back的保存逻辑是一致的。
                save();
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.general_info_image:
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setCancelable(true)
                        .setTitle("上传头像")
                        .setItems(new String[] {"从相册选择"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        //Toast.makeText(InfoActivity.this, "您选择了[从相册选择]", Toast.LENGTH_SHORT).show();
                                        if (ContextCompat.checkSelfPermission(InfoActivity.this,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(InfoActivity.this, new String[]
                                                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                        } else {
                                            openAlbum();
                                            //Toast.makeText(InfoActivity.this, "由于代码问题，不建议设置超过200KB的图片", Toast.LENGTH_LONG).show();
                                        }
                                        break;
                                    default:
                                }
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.general_info_fab:
                if (isConcerned == 0) {
                    isConcerned = 1;
                    floatingActionButton.setImageResource(R.drawable.star_on);
                } else {
                    isConcerned = 0;
                    floatingActionButton.setImageResource(R.drawable.star_off);
                }
                break;
            default:
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "缺少权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    imagePath = handleImage(data);
                    displayImage();
                }
                break;
            default:
        }
    }

    private String handleImage(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getPath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getPath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String getPath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage() {
        if (imagePath != null) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "图片设置失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //注意区分hint和text的区别
        String name = nameEditText.getText().toString();
        String age = ageEditText.getText().toString();
        int checkedSex = radioGroup.getCheckedRadioButtonId();
        String country = countryEditText.getText().toString();
        String info = moreInfoEditText.getText().toString();

        //如果什么都没修改，直接退出
        if (isConcerned_former == isConcerned && name.equals(name_former) && age.equals(age_former) && checkedSex == checkedSex_former
                && country.equals(country_former) && info.equals(info_former)
                && imagePath == null) { //如果imagePath == null，说明此次没有调用相册，那么也不可能更新图片
            finish();
            return;
        }

        //否则弹出提示框
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("您的更改尚未保存")
                .setNegativeButton("舍弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //舍弃的话就不读取信息，直接退出
                        finish();
                    }
                })
                //只有点击保存才会建立新的数据
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        save();
                        finish();
                    }
                });
        builder.show();

    }
}
