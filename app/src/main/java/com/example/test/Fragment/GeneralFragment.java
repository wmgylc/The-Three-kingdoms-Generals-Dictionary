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
 * Created by admin on 2017/11/2.
 */

public class GeneralFragment extends Fragment {

    private Context mContext;

    private GeneralAdapter adapter;

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


        adapter = new GeneralAdapter(R.layout.item, GeneralList);
        recyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.empty_view, container);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
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

        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter1, final View view, final int position) {
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
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                        DataSupport.delete(General.class, id);
                                        Snackbar.make(view, "人物已被删除", Snackbar.LENGTH_SHORT)
                                                .setAction("知道了", null)
                                                .show();
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
        General general1 = new General(R.drawable.liubei, null, "刘备", 1, "161年－223年", "蜀国",
                "刘备是汉朝的宗室，汉中山靖王刘胜的后代。章武三年（223年），刘备病逝于白帝城，终年63岁，谥号昭烈皇帝，庙号烈祖（一说太宗） ，葬惠陵。后世有众多文艺作品以其为主角，在成都武侯祠有昭烈庙为纪念。"
                , 0);
        General general2 = new General(R.drawable.caocao, null, "曹操", 1, "155年－220年", "魏国",
                "东汉末年，天下大乱，曹操以汉天子的名义征讨四方，对内消灭二袁、吕布、刘表、韩遂等割据势力，对外降服南匈奴、乌桓、鲜卑等，统一了中国北方，并实行一系列政策恢复经济生产和社会秩序，奠定了曹魏立国的基础。曹操在世时，担任东汉丞相，后为魏王，去世后谥号为武王。",
                0);
        General general3 = new General(R.drawable.diaochan, null, "貂蝉", 2, "生卒年不详", "第三方势力",
                "舍身报国的可敬女子，她为了挽救天下黎民，为了推翻权臣董卓的荒淫统治，受王允所托，上演了可歌可泣的连环计（连环美人计），周旋于两个男人之间，成功的离间了董卓和吕布，最终吕布将董卓杀死，结束了董卓专权的黑暗时期。",
                0);
        General general4 = new General(R.drawable.guanyu, null, "关羽", 1, "？－219年", "蜀国",
                "因本处势豪倚势凌人，关羽杀之而逃难江湖。闻涿县招军破贼，特来应募。与刘备、张飞桃园结义，羽居其次。使八十二斤青龙偃月刀随刘备东征西讨。虎牢关温酒斩华雄，屯土山降汉不降曹。为报恩斩颜良、诛文丑，解曹操白马之围。后得知刘备音信，过五关斩六将，千里寻兄。刘备平定益州后，封关羽为五虎大将之首，督荆州事。羽起军攻曹，放水淹七军，威震华夏。围樊城右臂中箭，幸得华佗医治，刮骨疗伤。但未曾提防东吴袭荆州，关羽父子败走麦城，突围中被捕，不屈遭害。",
                0);
        General general5 = new General(R.drawable.sunquan, null, "孙权", 1, "182年－252年", "吴国",
                "孙权19岁就继承了其兄孙策之位，力据江东，击败了黄祖。后东吴联合刘备，在赤壁大战击溃了曹操军。东吴后来又和曹操军在合肥附近鏖战，并从刘备手中夺回荆州、杀死关羽、大破刘备的讨伐军。曹丕称帝后孙权先向北方称臣，后自己建吴称帝，迁都建业。\n",
                0);
        General general6 = new General(R.drawable.zhangfei, null, "张飞", 1, "？－221年", "蜀国",
                "与刘备和关羽桃园结义，张飞居第三。随刘备征讨黄巾，刘备终因功被朝廷受予平原相，后张飞鞭挞欲受赂的督邮。十八路诸侯讨董时，三英战吕布，其勇为世人所知。曹操以二虎竞食之计迫刘备讨袁术，刘备以张飞守徐州，诫禁酒，但还是因此而鞭打曹豹招致吕布东袭。刘备反曹后，反用劫寨计擒曹将刘岱，为刘备所赞。徐州终为曹操所破，张飞与刘备失散，占据古城。误以为降汉的关羽投敌，差点一矛将其杀掉。曹操降荊州后引骑追击，刘备败逃，张飞引二十余骑，立马于长阪桥，吓退曹军数十里。庞统死后刘备召其入蜀，张飞率军沿江而上，智擒巴郡太守严颜并生获之，张飞壮而释放。于葭萌关和马超战至夜间，双方点灯，终大战数百回合。瓦口关之战时扮作醉酒，智破张郃。后封为蜀汉五虎大将。及关羽卒，张飞悲痛万分，每日饮酒鞭打部下，导致为帐下将张达、范强所杀，他们持其首顺流而奔孙权。",
                0);
        General general7 = new General(R.drawable.zhaoyun, null, "赵云", 1, "？－299年", "蜀国",
                "　初为袁绍将，后见绍不仁，于磐河战退绍将文丑，救瓒并投之。后又刺杀麹义。先主依讬瓒，云与之为田楷拒袁绍。后与先主执手泣别。后瓒败，云流浪卧牛山，与先主见，投之。当阳长阪恶战，云怀抱幼主，七进七出，杀曹军五十余将。先主娶孙夫人，云相随。及征蜀，云随诸葛亮、张飞等人沿江而上。及蜀平，又往征汉中，退曹大军。关羽亡，先主怒欲伐吴，云劝止，不从。后先主崩，云随亮南征、北伐，单骑退追兵。七年卒，后主哭倒于龙床上，谥云顺平侯、追大将军。\n",
                0);
        General general8 = new General(R.drawable.zhouyu, null, "周瑜", 1, "175年－210年", "吴国",
                "　偏将军、南郡太守。自幼与孙策交好，策离袁术讨江东，瑜引兵从之。为中郎将，孙策相待甚厚，又同娶二乔。策临终，嘱弟权曰：“外事不决，可问周瑜”。瑜奔丧还吴，与张昭共佐权，并荐鲁肃等，掌军政大事。赤壁战前，瑜自鄱阳归。力主战曹，后于群英会戏蒋干、怒打黄盖行诈降计、后火烧曹军，大败之。后下南郡与曹仁相持，中箭负伤，与诸葛亮较智斗，定假涂灭虢等计，皆为亮破，后气死于巴陵，年三十六岁。临终，上书荐鲁肃代其位，权为其素服吊丧。",
                0);
        General general9 = new General(R.drawable.zhugeliang, null, "诸葛亮", 1, "181年－234年", "蜀国",
                "　人称卧龙先生，有经天纬地之才，鬼神不测之机。刘皇叔三顾茅庐，遂允出山相助。曾舌战群儒、借东风、智算华容、三气周瑜，辅佐刘备于赤壁之战大败曹操，更取得荆州为基本。后奉命率军入川，于定军山智激老黄忠，斩杀夏侯渊，败走曹操，夺取汉中。刘备伐吴失败，受遗诏托孤，安居平五路，七纵平蛮，六出祁山，鞠躬尽瘁，死而后已。其手摇羽扇，运筹帷幄的潇洒形象，千百年来已成为人们心中“智慧”的代名词。",
                0);
        General general10 = new General(R.drawable.lusu, null, "鲁肃", 1, "172年－217年", "吴国",
                "鲁肃为周瑜的好友，在孙权继位后为周瑜推荐，仕于孙权，为孙权谋划战略，深受器重。赤壁之战时，鲁肃力主抗曹，出使联合刘备，并协助诸葛亮、周瑜说服孙权。鲁肃为人忠厚老实，不忍周瑜陷害诸葛亮，多次协助诸葛亮脱险。赤壁战后，鲁肃在诸葛亮的设计下，成为保人，将荆州“借”予刘备，此后多次讨要不成，处于两难的境地。周瑜去世后，鲁肃代周瑜成为水军都督，数年后病逝。",
                0);
        general1.save();
        general2.save();
        general3.save();
        general4.save();
        general5.save();
        general6.save();
        general7.save();
        general8.save();
        general9.save();
        general10.save();
        GeneralList.add(general1);
        GeneralList.add(general2);
        GeneralList.add(general3);
        GeneralList.add(general4);
        GeneralList.add(general5);
        GeneralList.add(general6);
        GeneralList.add(general7);
        GeneralList.add(general8);
        GeneralList.add(general9);
        GeneralList.add(general10);
    }


    public void notifyChange() {
        //默认按照id排序，不设条件搜索
        GeneralList = DataSupport.findAll(General.class);
        adapter.setNewData(GeneralList);
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
