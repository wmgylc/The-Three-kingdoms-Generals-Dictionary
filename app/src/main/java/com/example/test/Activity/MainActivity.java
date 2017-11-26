package com.example.test.Activity;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.test.Adapter.SearchAdapter;
import com.example.test.Adapter.ViewPagerAdapter;
import com.example.test.DataBase.General;
import com.example.test.Fragment.ConcernedGeneralFragment;
import com.example.test.Fragment.GeneralFragment;
import com.example.test.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

// TODO: 2017/11/8 将所有字符部分转为常量
// TODO: 2017/11/15 用户引导
// TODO: 2017/11/15 FAB点击动画 
// TODO: 2017/11/15 SD卡根目录保存
public class MainActivity extends AppCompatActivity {

    //搜索功能
    private SearchView searchView;

    SearchView.SearchAutoComplete mSearchAutoComplete;

    private List<General> generalList = new ArrayList<>();

    private RecyclerView recyclerView;

    private SearchAdapter adapter;

    //TabLayout功能
    private TabLayout tabLayout;

    private ViewPager viewPager;

    private List<Fragment> fragmentList = new ArrayList<>();

    private List<String> titles = new ArrayList<>();

    //FAB
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.activity_main_recycler_view);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        floatingActionButton = findViewById(R.id.floating_action_button);
        setSupportActionBar(toolbar);

        //只有第一次才会显示用户引导
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tapTarget = preferences.getBoolean("TAP_TARGET", false);
        if (!tapTarget) {
            TapTargetView.showFor(this, TapTarget.forView(floatingActionButton, "点击创建新的将士!", "")
                    .tintTarget(false), new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                    intent.putExtra("SOURCE", "FAB");
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                }
            });
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putBoolean("TAP_TARGET", true);
        editor.apply();

        //Create Database
        LitePal.getDatabase();

        // TODO: 2017/11/20 搜索无法下拉
        //搜索的RecyclerView
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new SearchAdapter(R.layout.search_item, generalList);
        recyclerView.setAdapter(adapter);

        //ViewPager
        GeneralFragment general = new GeneralFragment();
        ConcernedGeneralFragment concernedGeneral = new ConcernedGeneralFragment();
        fragmentList.add(general);
        fragmentList.add(concernedGeneral);
        titles.add("将士");
        titles.add("关注");
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);

        //搜索出的结果的点击事件

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
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
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
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });

        //展开一个新建窗口
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra("SOURCE", "FAB");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    floatingActionButton.show();
                } else if (position == 1) {
                    floatingActionButton.hide();
//                    绝对不能添加广播，卡顿的原因所在
//                    Intent intent1 = new Intent("RefreshConcernedList");
//                    sendBroadcast(intent1);
                    // TODO: 2017/11/26 如果fab在左列表消失后右列表可以刷出 
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        Intent intent = new Intent(this, MyService.class);
        startService(intent);

    }

    //每次回到MainActivity就进行广播更新页面数据
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent("Refresh");
        sendBroadcast(intent);
        Intent intent1 = new Intent("RefreshConcernedList");
        sendBroadcast(intent1);

        //SearchView -> InfoActivity -> SearchView
        if (searchView != null && tabLayout.getVisibility() == View.GONE) {
            try {
                mSearchAutoComplete.setText("");
                Method method  = searchView.getClass().getDeclaredMethod("onCloseClicked");
                method.setAccessible(true);
                method.invoke(searchView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //searchView的具体点击事件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLayout();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                switchLayout();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<General> filteredList = filter(DataSupport.findAll(General.class), newText);
                adapter.setNewData(filteredList);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //点击about进入APP详情页
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.game:
                Intent intent1 = new Intent(this, GameActivity.class);
                startActivity(intent1);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    //对搜索结果进行过滤，支持name，info，country的模糊搜索
    private List<General> filter(List<General> generals, String query) {
        final List<General> filteredList = new ArrayList<>();

        if (query.equals("")) {
            return filteredList;
        }

        for (General general : generals) {
            final String name = general.getName();
            final String info = general.getInfo();
            final String country = general.getCountry();

            if (name.contains(query) || info.contains(query) || country.contains(query)) {
                filteredList.add(general);
            }
        }

        return filteredList;
    }

    public void switchLayout() {
        if (tabLayout.getVisibility() == View.GONE) {
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.VISIBLE);
        } else if (tabLayout.getVisibility() == View.VISIBLE) {
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.GONE);
        }
    }

    //在searchView展开的情况下逻辑需要修改
    @Override
    public void onBackPressed() {
        Log.d("TAG", searchView.isShown() + "");
        if (tabLayout.getVisibility() == View.GONE) {
            //通过反射获得方法
            try {
                mSearchAutoComplete.setText("");
                Method method  = searchView.getClass().getDeclaredMethod("onCloseClicked");
                method.setAccessible(true);
                method.invoke(searchView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }
}
