package com.example.test.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.test.DataBase.General;
import com.example.test.R;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by admin on 2017/11/8.
 */

public class SearchAdapter extends BaseQuickAdapter<General, BaseViewHolder>{

    public SearchAdapter(int layoutResId, List<General> filteredList) {
        super(layoutResId, filteredList);
    }

    @Override
    protected void convert(BaseViewHolder helper, General item) {
        if (item.getImagePath() != null) {
            //Log.d("TAG", "position:" + position + "&&" + "notnull" + general.getName());
            String path=item.getImagePath();
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            helper.setImageBitmap(R.id.search_general_image, bitmap);
        } else if (item.getImageRes() != 0) {
            helper.setImageResource(R.id.search_general_image, item.getImageRes());
        }
        else {
            helper.setImageResource(R.id.search_general_image, R.drawable.circle);
        }
        if (item.getConcerned() > 0) {
            //Toast.makeText(mContext, "ok", Toast.LENGTH_SHORT).show();
            helper.setImageResource(R.id.search_concerned, R.drawable.star_on);
        }
        helper.setText(R.id.search_name, item.getName());
        //helper.setImageResource(R.id.search_concerned, 0);
        helper.itemView.setTag(item.getid());
    }
}
