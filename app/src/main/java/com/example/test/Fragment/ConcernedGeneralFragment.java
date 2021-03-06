package com.example.test.Fragment;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.test.Adapter.GeneralAdapter;
import com.example.test.DataBase.General;
import com.example.test.Activity.InfoActivity;
import com.example.test.R;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by admin on 2017/11/4.
 */

public class ConcernedGeneralFragment extends Fragment {

    private Context mContext;

    private GeneralAdapter adapter;

    private List<General> GeneralList = DataSupport.where("isConcerned > ?", "0").find(General.class);

    private RefreshConcernedListReceiver refreshConcernedListReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_view, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_rv);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new GeneralAdapter(R.layout.item, GeneralList);
        recyclerView.setAdapter(adapter);
        adapter.openLoadAnimation();
        adapter.isFirstOnly(false);
        adapter.setEmptyView(R.layout.empty_view, container);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int id = (int) view.getTag();
                General general = DataSupport.find(General.class, id);
                String path = general.getImagePath();
                int image = general.getImageRes();
                String name = general.getName();
                int sex = general.getSex();
                String age = general.getAge();
                String country = general.getCountry();
                String info = general.getInfo();
                int concerned = general.getConcerned();
                Intent intent = new Intent(getContext(), InfoActivity.class);
                intent.putExtra("IMAGE_URI", path);
                intent.putExtra("IMAGE_RES", image);
                intent.putExtra("NAME", name);
                intent.putExtra("SEX", sex);
                intent.putExtra("AGE", age);
                intent.putExtra("COUNTRY", country);
                intent.putExtra("INFO", info);
                intent.putExtra("CONCERNED", concerned);
                intent.putExtra("SOURCE", "REC");
                intent.putExtra("ID", id);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });

        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter1, final View view, int position) {
                final int id = (int) view.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setCancelable(true)
                        .setMessage("将该项移除出关注列表？")
                        .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                General general = DataSupport.find(General.class, id);
                                general.setConcerned(0);
                                general.save();
                                Snackbar.make(view, "已经移除出关注列表", Snackbar.LENGTH_SHORT)
                                        .setAction("知道了", null)
                                        .show();
                                GeneralList = DataSupport.where("isConcerned > ?", "0").find(General.class);
                                //Log.d("TAG", "onResume" + GeneralList.size());
                                adapter.setNewData(GeneralList);
                                adapter.notifyDataSetChanged();
                                Intent intent = new Intent("Refresh");
                                //通知将士列表刷新
                                getContext().sendBroadcast(intent);
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.show();
                return false;
            }
        });

        return recyclerView;
    }

    //注册广播
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("RefreshConcernedList");
        refreshConcernedListReceiver = new RefreshConcernedListReceiver();
        context.registerReceiver(refreshConcernedListReceiver, intentFilter);
    }

    //受到广播后刷新关注列表
    public class RefreshConcernedListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            GeneralList = DataSupport.where("isConcerned > ?", "0").find(General.class);
            adapter.setNewData(GeneralList);
            adapter.notifyDataSetChanged();
        }
    }

    //销毁广播
    @Override
    public void onDetach() {
        super.onDetach();
        mContext.unregisterReceiver(refreshConcernedListReceiver);
    }
}
