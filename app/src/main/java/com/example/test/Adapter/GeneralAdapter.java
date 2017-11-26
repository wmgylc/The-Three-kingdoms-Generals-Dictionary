package com.example.test.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.test.DataBase.General;
import com.example.test.R;

import java.util.List;

/**
 * Created by admin on 2017/11/12.
 */

//使用了开源RecyclerView框架BRAVH

public class GeneralAdapter extends BaseQuickAdapter<General, BaseViewHolder> {

    public GeneralAdapter(int layoutResId, List<General> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, General item) {
        Log.d("TAG", "convert" + item.getName());
        helper.setText(R.id.name, item.getName())
                .setText(R.id.country, item.getCountry())
                .setText(R.id.info, item.getInfo());

        if (item.getConcerned() != 0) {
            helper.setImageResource(R.id.concerned, R.drawable.star_on);
        } else {
            helper.setImageDrawable(R.id.concerned, null);
        }

        //优先考虑主动设定的图片
        if (item.getImagePath() != null) {
            String path=item.getImagePath();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            helper.setImageBitmap(R.id.general_image, bitmap);
            //bitmap.recycle();
            if (bitmap == null) {
                //如果照片在本地被删除了
                helper.setImageResource(R.id.general_image, R.drawable.circle);
            }
        } else if (item.getImageRes() != 0){
            helper.setImageResource(R.id.general_image, item.getImageRes());
        } else {
            helper.setImageResource(R.id.general_image, R.drawable.circle);
        }
        //ItemView传递id值
        helper.itemView.setTag(item.getid());
        helper.itemView.setClickable(true);
    }


}
