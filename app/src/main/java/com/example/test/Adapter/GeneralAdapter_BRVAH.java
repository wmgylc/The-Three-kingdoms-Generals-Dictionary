package com.example.test.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.test.DataBase.General;
import com.example.test.R;

import java.util.List;

/**
 * Created by admin on 2017/11/12.
 */

public class GeneralAdapter_BRVAH extends BaseQuickAdapter<General, BaseViewHolder> {

    public GeneralAdapter_BRVAH(int layoutResId, List<General> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, General item) {
        helper.setText(R.id.name, item.getName())
                .setText(R.id.country, item.getCountry())
                .setText(R.id.info, item.getInfo());

        if (item.getConcerned() != 0) {
            helper.setImageResource(R.id.concerned, R.drawable.star_on);
        } else {
            helper.setImageDrawable(R.id.concerned, null);
        }

        if (item.getImagePath() != null) {
            String path=item.getImagePath();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            helper.setImageBitmap(R.id.general_image, bitmap);
            //bitmap.recycle();
        } else {
            helper.setImageResource(R.id.general_image, R.drawable.circle);
        }

        //ItemView传递id值
        helper.itemView.setTag(item.getid());
    }
}