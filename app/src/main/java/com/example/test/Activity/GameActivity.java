package com.example.test.Activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by admin on 2017/11/26.
 */

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.test.DataBase.General;
import com.example.test.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import link.fls.swipestack.SwipeStack;



public class GameActivity extends AppCompatActivity implements SwipeStack.SwipeStackListener, View.OnClickListener {
    private List<General> GeneralList = DataSupport.findAll(General.class);
    private ArrayList<Questions> mData;
    private SwipeStack mSwipeStack;
    private SwipeStackAdapter mAdapter;
    private int cort_number;
    private RadioButton[] buttons1 = new RadioButton[5];
    private RadioButton[] buttons2 = new RadioButton[5];
    private RadioButton[] buttons3 = new RadioButton[5];
    private ArrayList<Integer> record = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSwipeStack = (SwipeStack) findViewById(R.id.swipeStack);
        mData=new ArrayList<>();
        fillWithTestData();
        mAdapter = new SwipeStackAdapter(mData);
        mSwipeStack.setAdapter(mAdapter);
        mSwipeStack.setListener(this);
        cort_number=0;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    //fill with question,initilize
    private void fillWithTestData() {
        for (int x = 0; x < 5; x++) {
            Questions q=new Questions();
            Random random=new Random();
            int pos=random.nextInt(GeneralList.size());
            boolean flag2=true;
            for (int tmp=0;tmp<record.size();tmp++) {
                if (record.get(tmp) == pos){
                    flag2=false;
                    break;
                }
            }
            if (flag2 == false){
                x--;
                continue;
            }
            record.add(pos);
            General general = GeneralList.get(pos);
            pos = pos%3;
            q.setName(general.getName(),pos);
            q.setTrue_ans(pos);
            q.setImagepath(general.getImagePath());
            q.setImageres(general.getImageRes());
            for (int i=0;i<3;i++) {
                if (q.getName(i).equals("")){
                    random=new Random();
                    int pos2=random.nextInt(GeneralList.size());
                    general = GeneralList.get(pos2);
                    boolean flag=true;
                    for(int cnt = 0;cnt < 3;cnt++) {
                        if (general.getName().equals(q.getName(cnt))) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag ==false){
                        i--;
                    }
                    else {
                        q.setName(general.getName(), i);
                    }
                }
            }
            mData.add(q);
        }
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onViewSwipedToLeft(int position) {
        int choose_ans=-1;
        if (mAdapter.getRadioButton1().isChecked()) {
            choose_ans=0;
        }
        if (mAdapter.getRadioButton2().isChecked()) {
            choose_ans=1;
        }
        if (mAdapter.getRadioButton3().isChecked()) {
            choose_ans=2;
        }
        Questions showed_q=mAdapter.getItem(position);
        showed_q.setChoose_ans(choose_ans);
        int res;
        if (choose_ans == showed_q.getTrue_ans()){
            cort_number++;
            res = 1;
        }
        else{
            res = 0;
        }
        Snackbar.make(mAdapter.getCurrentView(), res == 1? "答对了" : "答错了", Snackbar.LENGTH_SHORT)
                .setAction("知道了", null)
                .show();
    }

    @Override
    public void onViewSwipedToRight(int position) {
        int choose_ans=-1;
        if (buttons1[position].isChecked()) {
            choose_ans=0;
        }
        if (buttons2[position].isChecked()) {
            choose_ans=1;
        }
        if (buttons3[position].isChecked()) {
            choose_ans=2;
        }
        Questions showed_q=mAdapter.getItem(position);
        showed_q.setChoose_ans(choose_ans);
        int res;
        if (choose_ans == showed_q.getTrue_ans()){
            cort_number++;
            res = 1;
        }
        else{
            res = 0;
        }
        Snackbar.make(mAdapter.getCurrentView(), res == 1? "答对了" : "答错了", Snackbar.LENGTH_SHORT)
                .setAction("知道了", null)
                .show();
    }

    @Override
    public void onStackEmpty() {
        String result = null;
        switch (cort_number){
            case 0:
                //Toast.makeText(this,"",Toast.LENGTH_LONG).show();
                result = "0分";
                break;
            case 1:
                //Toast.makeText(this, "20分，太差啦", Toast.LENGTH_LONG).show();
                result = "20分";
                break;
            case 2:
                //Toast.makeText(this, "40分，加油啊，小弱鸡", Toast.LENGTH_LONG).show();
                result = "40分";
                break;
            case 3:
                //Toast.makeText(this, "60分，勉强及格", Toast.LENGTH_LONG).show();
                result = "60分";
                break;
            case 4:
                //Toast.makeText(this, "80分，哎哟，不错喔", Toast.LENGTH_LONG).show();
                result = "80分";
                break;
            case 5:
                //Toast.makeText(this, "满分!，666666", Toast.LENGTH_LONG).show();
                result = "满分!";
                break;
        }
        //否则弹出提示框
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("您的分数是：" + result + "\n是否再来一次？")
                //只有点击保存才会建立新的数据
                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cort_number=0;
                        mSwipeStack.resetStack();
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("不了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        builder.show();
    }
    public class SwipeStackAdapter extends BaseAdapter {

        private View mView;

        private List<Questions> mData;

        public SwipeStackAdapter(List<Questions> data) {
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Questions getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.d("TAG_GETVIEW","GETVIEW");
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.card, parent, false);
                this.mView = convertView;
            }
            ImageView head = (ImageView)convertView.findViewById(R.id.ques_image);
            Questions showed_question=mData.get(position);
            if (showed_question.getImagepath() != null) {
                //如果path == null，说明没有设置图片
                head.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Bitmap bitmap = BitmapFactory.decodeFile(showed_question.getImagepath());
                head.setImageBitmap(bitmap);
            } else if (showed_question.getImageres()!= 0) {
                //考虑初始设置的十个将士的图片调用的是图片资源
                head.setScaleType(ImageView.ScaleType.CENTER_CROP);
                head.setImageResource(showed_question.getImageres());
            }
            RadioButton choose1=(RadioButton)convertView.findViewById(R.id.choose1);
            RadioButton choose2=(RadioButton)convertView.findViewById(R.id.choose2);
            RadioButton choose3=(RadioButton)convertView.findViewById(R.id.choose3);
            choose1.setText(showed_question.getName(0));
            choose2.setText(showed_question.getName(1));
            choose3.setText(showed_question.getName(2));
            buttons1[position] = choose1;
            buttons2[position] = choose2;
            buttons3[position] = choose3;
            return convertView;
        }
        public RadioButton getRadioButton1() {
            return mView.findViewById(R.id.choose1);
        }
        public RadioButton getRadioButton2() {
            return mView.findViewById(R.id.choose2);
        }
        public RadioButton getRadioButton3() {
            return mView.findViewById(R.id.choose3);
        }
        View getCurrentView() {
            return mView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
