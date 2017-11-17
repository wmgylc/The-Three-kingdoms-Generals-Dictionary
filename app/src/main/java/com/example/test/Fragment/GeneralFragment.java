package com.example.test.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.test.Activity.MainActivity;
import com.example.test.Adapter.GeneralAdapter_BRVAH;
import com.example.test.DataBase.General;
import com.example.test.Activity.InfoActivity;
import com.example.test.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/11/2.
 */

public class GeneralFragment extends Fragment {

    private Context mContext;

    private GeneralAdapter_BRVAH adapter_BRVAH;

    private RecyclerView recyclerView;

    private List<General> GeneralList = DataSupport.findAll(General.class);

    private RefreshReceiver refreshReceiver;

    private String[] options1 = new String[] {"加入至关注列表", "删除该项"};

    private String[] options2 = new String[] {"移除出关注列表", "删除该项"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!preferences.getBoolean("CREATE", false)) {
            initData();
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putBoolean("CREATE", true);
        editor.apply();


        adapter_BRVAH = new GeneralAdapter_BRVAH(R.layout.item, GeneralList);
        recyclerView.setAdapter(adapter_BRVAH);
        adapter_BRVAH.openLoadAnimation();
        adapter_BRVAH.isFirstOnly(false);
        adapter_BRVAH.setEmptyView(R.layout.empty_view, container);

        adapter_BRVAH.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //获得id
                int id = (int) view.getTag();
                General general = DataSupport.find(General.class, id);
                int image = general.getImageRes();
                String path = general.getImagePath();
                String name = general.getName();
                int sex = general.getSex();
                String age = general.getAge();
                String country = general.getCountry();
                String info = general.getInfo();
                int concerned = general.getConcerned();
                Intent intent = new Intent(getContext(), InfoActivity.class);
                intent.putExtra("IMAGE_RES", image);
                intent.putExtra("IMAGE_URI", path);
                intent.putExtra("NAME", name);
                intent.putExtra("SEX", sex);
                intent.putExtra("AGE", age);
                intent.putExtra("COUNTRY", country);
                intent.putExtra("INFO", info);
                intent.putExtra("CONCERNED", concerned);
                intent.putExtra("SOURCE", "REC");
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });

        adapter_BRVAH.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                final int id = (int) view.getTag();
                final int concerned = DataSupport.find(General.class, id).getConcerned();
                final Intent intent = new Intent("RefreshConcernedList");

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setCancelable(true)
                        .setItems(concerned > 0? options2 : options1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        General general = DataSupport.find(General.class, id);
                                        general.setConcerned(1 - concerned);
                                        general.save();
                                        getContext().sendBroadcast(intent);
                                        notifyChange();
                                        recyclerView.scrollToPosition(position);
                                        break;
                                    // TODO: 2017/11/8 局部刷新
                                    case 1:
                                        GeneralList.remove(position);
                                        adapter_BRVAH.notifyItemRemoved(position);
                                        adapter_BRVAH.notifyItemRangeChanged(position, adapter_BRVAH.getItemCount());
                                        DataSupport.delete(General.class, id);
                                        getContext().sendBroadcast(intent);
                                        break;
                                    default:
                                }
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.show();
                return false;
            }
        });

        return recyclerView;
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Refresh");
        refreshReceiver = new RefreshReceiver();
        context.registerReceiver(refreshReceiver, intentFilter);
    }

    private void initData() {
        General general = new General(R.drawable.liubei, null, "刘备", 1, "161年－223年", "蜀国",
                "刘备是汉朝的宗室，汉中山靖王刘胜的后代。章武三年（223年），刘备病逝于白帝城，终年63岁，谥号昭烈皇帝，庙号烈祖（一说太宗） ，葬惠陵。后世有众多文艺作品以其为主角，在成都武侯祠有昭烈庙为纪念。"
        , 0);
        general.save();
        GeneralList.add(general);
        //notifyChange();
    }


    public void notifyChange() {
        //默认按照id排序，不设条件搜索
        GeneralList = DataSupport.findAll(General.class);
        adapter_BRVAH.setNewData(GeneralList);
        adapter_BRVAH.notifyDataSetChanged();
        //将焦点移动到最下方 && 默认新添加的都在最下方，如果后期加入过滤方式就需要判断
        //recyclerView.scrollToPosition(GeneralList.size() - 1);
    }

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifyChange();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext.unregisterReceiver(refreshReceiver);
    }
}
