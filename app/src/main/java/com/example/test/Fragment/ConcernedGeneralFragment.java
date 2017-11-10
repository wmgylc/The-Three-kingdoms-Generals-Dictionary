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
import android.util.Log;
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
 * Created by admin on 2017/11/4.
 */

public class ConcernedGeneralFragment extends Fragment {

    private Context mContext;

    private GeneralAdapter adapter;

    private RecyclerView recyclerView;

    private List<General> GeneralList = new ArrayList<>();

    private RefreshConcernedListReceiver refreshConcernedListReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.d("TAG", "onCreateView");
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new GeneralAdapter(GeneralList);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //Log.d("TAG", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

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
            public void onLongClick(View view, int position, final int id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setCancelable(true)
                        .setMessage("将该项移除出关注列表？")
                        .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                General general = DataSupport.find(General.class, id);
                                general.setConcerned(0);
                                general.save();
                                GeneralList = DataSupport.where("isConcerned > ?", "0").find(General.class);
                                //Log.d("TAG", "onResume" + GeneralList.size());
                                adapter.setAdapterData(GeneralList);
                                adapter.notifyDataSetChanged();
                                Intent intent = new Intent("Refresh");
                                //通知将士列表刷新
                                getContext().sendBroadcast(intent);
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        //Log.d("TAG", "onAttach");
        super.onAttach(context);
        mContext = context;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("RefreshConcernedList");
        refreshConcernedListReceiver = new RefreshConcernedListReceiver();
        context.registerReceiver(refreshConcernedListReceiver, intentFilter);
    }



    public class RefreshConcernedListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            GeneralList = DataSupport.where("isConcerned > ?", "0").find(General.class);
            adapter.setAdapterData(GeneralList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext.unregisterReceiver(refreshConcernedListReceiver);
    }
}
