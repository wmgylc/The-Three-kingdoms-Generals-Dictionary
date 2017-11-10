package com.example.test.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.Adapter.GeneralAdapter;
import com.example.test.DataBase.General;
import com.example.test.InfoActivity;
import com.example.test.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/11/2.
 */

public class GeneralFragment extends Fragment {

    private Context mContext;

    private GeneralAdapter adapter;

    private RecyclerView recyclerView;

    private List<General> GeneralList = new ArrayList<>();

    private RefreshReceiver refreshReceiver;

    private String[] options1 = new String[] {"加入至关注列表", "删除该项"};

    private String[] options2 = new String[] {"移除出关注列表", "删除该项"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Log.d("TAG", "onCreateView");
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        GeneralList = DataSupport.findAll(General.class);
        adapter = new GeneralAdapter(GeneralList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new GeneralAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int position, int id) {
                General general = DataSupport.find(General.class, id);
                String path = general.getImagePath();
                String name = general.getName();
                int sex = general.getSex();
                String age = general.getAge();
                String country = general.getCountry();
                String info = general.getInfo();
                int concerned = general.getConcerned();
                Intent intent = new Intent(getContext(), InfoActivity.class);
                intent.putExtra("IMAGE", path);
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

            @Override
            public void onLongClick(View view, final int position, final int id) {

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
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                        DataSupport.delete(General.class, id);
                                        getContext().sendBroadcast(intent);
                                        break;
                                    default:
                                }
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.show();
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

    public void notifyChange() {
        //默认按照id排序，不设条件搜索
        GeneralList = DataSupport.findAll(General.class);
        adapter.setAdapterData(GeneralList);
        adapter.notifyDataSetChanged();
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
