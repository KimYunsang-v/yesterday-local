package com.example.yesterday.yesterday.UI;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yesterday.yesterday.ClientLoginInfo;
import com.example.yesterday.yesterday.PushAlarm.AlarmProgressReceiver;
import com.example.yesterday.yesterday.R;

import com.example.yesterday.yesterday.RecyclerView.FoodItem;
import com.example.yesterday.yesterday.RecyclerView.RecyclerItem;
import com.example.yesterday.yesterday.UI.HomeFrags.AddFragment;
import com.example.yesterday.yesterday.UI.HomeFrags.GoalFragment;
import com.example.yesterday.yesterday.UI.HomeFrags.HomeFragment;
import com.example.yesterday.yesterday.UI.HomeFrags.StatisticsFragment;
import com.example.yesterday.yesterday.sqlite.ClientGoalDB;
import com.example.yesterday.yesterday.sqlite.FoodDataDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    //Activity
    Activity mActivity;
    //toolbar
    private Toolbar toolbar;
    //FragmentManager
    private FragmentManager fragmentManager;

    //MaterialDrawer
    private PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("홈").withIcon(R.drawable.ic_home_solid_white).withIconTintingEnabled(true);
    private PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("홈_첫번째").withIcon(R.drawable.ic_wb_sunny_black_24dp).withIconTintingEnabled(true);
    private PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("홈_두번째").withIcon(R.drawable.ic_help_outline_black_24dp).withIconTintingEnabled(true);
    private PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("섹션_첫번째").withIcon(R.drawable.ic_settings_black_24dp).withIconTintingEnabled(true);
    private PrimaryDrawerItem logout = new PrimaryDrawerItem().withIdentifier(5).withName("로그아웃").withIcon(R.drawable.ic_playlist_add_black_24dp).withIconTintingEnabled(true);

    private SecondaryDrawerItem sectionHeader = new SecondaryDrawerItem().withName("section_header");

    Drawer drawerResult;
    AccountHeader headerResult;
    //private Handler handler;

    //BottomBar
    BottomBar bottomBar;
    //Fragment
    private HomeFragment homeFragment;
    private AddFragment addFragment;
    private GoalFragment goalFragment;
    private StatisticsFragment statisticsFragment;
    //Canlendar Icon
    private ImageView calendarView;

    // clientGoalDB 객체
    ClientGoalDB clientGoalDB;

    // clientGoalDB 객체
    FoodDataDB foodDataDB;

    /*//SharedPreferences
    SharedPreferences loginSetting;
    SharedPreferences.Editor editor;*/

    /*//client
    ClientLoginInfo client;*/
    //ClientGoal DB 연동 결과값
    String result;
    //각 Fragment에서 items가 필요할 때 공유하기 위한 변수
    private ArrayList<RecyclerItem> goalItems;

    // 모든 음식 데이터
    private ArrayList<FoodItem> foodItems;

    //databse
    private SQLiteDatabase goalDB;


    public HomeActivity() {

        //Activity
        mActivity = HomeActivity.this;

        //FragmentManager
        fragmentManager = getSupportFragmentManager();
        //handler = new Handler();



        //Fragment
        homeFragment = new HomeFragment();
        addFragment = new AddFragment();
        goalFragment = new GoalFragment();
        statisticsFragment = new StatisticsFragment();


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG", "HomeActivity onStart / 시작");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG", "HomeActivity onRestart / 다시 실행");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAG", "HomeActivity onResume / 다시 시작");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TAG", "HomeActivity onPause / 일시 정지");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TAG", "HomeActivity onStop / 정지");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "HomeActivity onDestroy / 종료");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("TAG", "onCreate / 앱 생성(초기화)");

        // DB객체 초기화
        clientGoalDB = new ClientGoalDB(HomeActivity.this,"ClientGoal",null,1);
        foodDataDB = new FoodDataDB(HomeActivity.this,"FoodData",null,1);

        goalDB = clientGoalDB.getWritableDatabase();

        // 목표 데이터
        goalItems = new ArrayList<RecyclerItem>();
        //모든 음식 데이터
        foodItems = new ArrayList<FoodItem>();

        //파싱된 데이터를 메소드를 통해 items에 대입
        // 목표 데이터 가져옴
        goalItems = clientGoalDB.selectGoal(goalDB);

        //fooddata를 조회해 해당되는 food의 개수를 가져옴
        //TODO: fooddata 값을 가져오긴 하는데 기간 조회가 어려움

        // 모든 음식 데이터 가져오기
        foodItems = foodDataDB.selectAll();



        getCurrentCount();

        //fail인 아이템 currentCount 초기화
        for (int i = 0; i < goalItems.size(); i++) {
            if (goalItems.get(i).getType().equals("fail")) {
                goalItems.get(i).setCurrentCount(goalItems.get(i).getCount());
            }
        }
        //오늘 날짜와 목표설정 마감일을 비교하여 type 업데이트!!
        compareDate();

       /* //SharedPreference
        loginSetting = getSharedPreferences("loginSetting", 0);
        editor = loginSetting.edit();*/

        //10시 푸시 알림
        boolean isPush = true;
        if(isPush) {
            new AlarmProgress(getApplicationContext()).Alarm();
            isPush = false;
        }
        //MaterialDrawer 쓰기위해 toolbar의 id를 가져와 객체 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("어제 점심 뭐 먹었지 ?");

        //Intent로 client가져오기  -> client 아직 안쓰임
       /* Intent intent  = getIntent();
        client = (ClientLoginInfo) intent.getSerializableExtra("client");
        Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();*/
        //Calendar
        //Calendar View로 넘어가면 밑에 바텀바 focus 어케 해결!?
        calendarView = (ImageView) findViewById(R.id.toolbar_calendar_button);
        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
            }
        });

        // MaterialDrawer
        // Create the AccountHeader -> 사용자 계정(이미지,이름,이메일)헤더 생성
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)        //배경이미지
                .withCompactStyle(true)                         //소형스타일
                .withProfileImagesClickable(false)              //프로필이미지선택X
                //.withSavedInstance(savedInstanceState)
                .addProfiles(
                        new ProfileDrawerItem().withName("admin").withEmail("wwlee94@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                )
                .build();
        //create the drawer and remember the `Drawer` result object
        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)                           //toolbar에 drawer추가
                .withAccountHeader(headerResult)                //drawer에 계정 정보 추가
                .addDrawerItems(                                //drawer에 들어갈 item추가
                        item1, item2, item3,
                        new DividerDrawerItem(),
                        sectionHeader,
                        item4, logout
                )
                //drawer를 클릭 했을 때 이벤트 처리
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        /*if (drawerItem == logout) {
                            if ((client.getType()).equals("회원")) {
                                editor.clear();
                                editor.commit();
                            } else if ((client.getType()).equals("카카오")) {
                                onClickLogout();
                            }
                            Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();
                            Intent intent  = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }*/
                        return true;
                    }
                })
                //.withFullscreen(true)
                //반투명화 시켜줌.. style에 statusBar랑 같이 쓰면
                .withTranslucentStatusBar(false)
                //색 안 넣으면 투명화 layout
                .withDrawerLayout(R.layout.material_drawer_fits_not)
                //.withSavedInstance(savedInstanceState)
                .build();

        //bottomBar를 tab했을 때 id를 구분해 해당 내부코드를 실행하여 Fragment의 전환이 이루어짐
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {

                //transaction 객체를 가져옴
                //if 가져온 tabId가 tab_home일때 homeFragment화면으로 전환
                if (tabId == R.id.tab_home) {
                    replaceFragment(homeFragment, "HOME");
                }
                //if 가져온 tabId가 tab_add일때 해당 화면으로 전환
                else if (tabId == R.id.tab_add) {
                    replaceFragment(addFragment, "ADD");
                }
                //if 가져온 tabId가 tab_goal일때 해당 화면으로 전환
                else if (tabId == R.id.tab_goal) {
                    replaceFragment(goalFragment, "GOAL");
                }
                //if 가져온 tabId가 tab_statistics일때 해당 화면으로 전환
                else if (tabId == R.id.tab_statistics) {
                    replaceFragment(statisticsFragment, "STATISTICS");
                }
            }
        });

        //sharedfreference에 items 저장
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<RecyclerItem>>() {
        }.getType();
        String json = gson.toJson(goalItems, listType);
        editor.putString("ITEM", json);
        editor.commit();
    }

    //Fragment 화면 전환
    public void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentContainer, fragment, tag);
        transaction.commit();
    }

    // DB 연동해서 Select
    // 현재 로그인한 id에 해당하는 목표들을 가져오는 메소드
   /* public ArrayList<RecyclerItem> getClientGoal() {

        result = null;
        //ClientGoal 데이터베이스에 접속해 JSONObject 결과값 받아오는 코드
        try {  //admin 에서 client name으로 바꾸면 에러남
            result = new SelectGoalServer("admin").execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //성공 , 실패 여부
            if (result.equals("fail")) {
                Log.d("ClientGoal", "데이터 조회 실패");
            }
            else {
                Log.d("ClientGoal", "데이터 조회 성공");
                Log.d("ClientGoal", result);
            }
        }
        //result -> json - String 형태
        try {
            //이클립스에서 데이터 담을 때 역순 작업
            JSONObject jsonObject = new JSONObject(result);

            JSONArray jsonArray = (JSONArray) jsonObject.get("CLIENTGOAL");
            //jsonArray.length() -> 각각의 {id,food,...} 전체의 갯수
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject list = (JSONObject) jsonArray.get(i);

                String userID = list.getString("USERID");
                String food = list.getString("FOOD");
                int count = list.getInt("COUNT");
                String startDate = list.getString("STARTDATE");
                String endDate = list.getString("ENDDATE");
                int favorite = list.getInt("FAVORITE");
                String type = list.getString("TYPE");
                //item에 파싱한 list 값을 넣어줌
                items.add(new RecyclerItem(food, count, startDate, endDate, favorite, type));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }*/

    //목표에 해당하는 음식을 먹은 횟수 조회
    public void getCurrentCount() {

        int count = 0;
        /*result = null;
        //fooddata 데이터베이스에 접속해 JSONObject 결과값 받아오는 코드
        try {
            result = new SelectGroupByGoalServer("admin").execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //성공 , 실패 여부
            if (result.equals("fail")) {
                Log.d("ClientGoal", "음식 개수 조회 실패");
            } else {
                Log.d("ClientGoal", "음식 개수 조회 성공");
                Log.d("ClientGoal", result);
            }
        }*/


//            JSONObject jsonObject = new JSONObject(result);
//            JSONArray jsonArray = (JSONArray) jsonObject.get("FOODCOUNT");

            for (int i = 0; i < goalItems.size(); i++) {
//                JSONObject list = (JSONObject) jsonArray.get(i);
//                String food = list.getString("FOOD");
//                int currentCount = list.getInt("CURRENTCOUNT");
                String food = goalItems.get(i).getFood();
                for (int k = 0; k < foodItems.size(); k++) {
                    //items의 food와 DB에서 가져온 food가 같다면 해당되는 items에 currentCount 데이터 입력
                    // fooditems 의 이름과 goalitems의 food 이름이 같다면 count 증가 나중에 날짜 비교 추가해야함
                    if (foodItems.get(k).equals(food)) {
                                count++;
                        }
                    }
                goalItems.get(i).setCurrentCount(count);
            }
    }

    //날짜 비교 후 결과 값 도출
    public void compareDate(){

        String result = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        //오늘 날짜
        Date currentDate = new Date();
        Log.d("currentDate",format.format(currentDate));

        //검사 날짜
        for(int i=0;i<goalItems.size();i++) {
            //index:-1 -> 삭제 안 일어남
            int index=-1;
            if(goalItems.get(i).getType().equals("default")) {
                try {
                    Date checkDate = format.parse(goalItems.get(i).getEndDate());
                    int check = currentDate.compareTo(checkDate);
                    Log.i("check", "" + check);
                    //마감일이 지난 item들 currentDate > checkDate
                    if (check > 0) {
                        Log.d("currentDate > checkDate", format.format(checkDate));

                        //Type 변경 전 해당 음식의 type이 중복되는 지 Check!!
                        for (int k = 0; k < goalItems.size(); k++) {
                            //음식 이름이 같고 그 음식의 이름을 가진 타입 중 success가 있는 지 판별
                            if (goalItems.get(k).getFood().equals(goalItems.get(i).getFood())) {
                                if (goalItems.get(k).getType().equals("success")) {
                                    clientGoalDB.deleteGoal(goalDB,goalItems.get(k).getFood(), goalItems.get(k).getType());
                                    index = k;
                                }//finally
                            }//success
                        }

                        //중복 체크 후 DB 연동 Type 변경 코드
                        //
                        try {
                            clientGoalDB.checkType(goalItems.get(i).getFood(), goalItems.get(i).getFavorite(), goalItems.get(i).getType());
                            //result = new CheckTypeServer("admin", items.get(i).getFood(), items.get(i).getFavorite(), items.get(i).getType()).execute().get();
                            //Log.d("DB Update 하는 items값", items.get(i).getFood() + items.get(i).getFavorite() + items.get(i).getType());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }//finally

                        //DB에서 변경했으니 items에서도 변경!!
                        goalItems.get(i).setType("success");
                        //즐겨찾기 설정되어있으면 해제
                        if (goalItems.get(i).getFavorite() == 1) {
                            goalItems.get(i).setFavorite(0);
                        }
                        //count: -1 -> 삭제된 items 없음
                        //TODO: FOR문 밖에서 처리해야 할 수도
                        if (index != -1) {
                            //앞서 중복되는 success 타입의 데이터 items에서 제거
                            Log.d("index", "" + index);
                            goalItems.remove(index);
                        }
                    }//check > 0
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }//default
        }

    }

    //* 다른 Fragment에서 getItems 해온 뒤에 그 items 변수를 다른 곳에서 변경하던 지우던 다 주소값으로 연결되어 있는 듯
    //  즉, Fragment에서 바꾼 items가 여기 HomeActivity의 items에서도 바뀐다.
    public void setItems(ArrayList<RecyclerItem> items) {
        this.goalItems = items;
    }

    public ArrayList<RecyclerItem> getItems() {
        return goalItems;
    }

    //매일 10시에 알람
    public class AlarmProgress {
        Context context;

        public AlarmProgress(Context context) {
            this.context = context;
        }

        public void Alarm() {
            Log.d("AlarmProgress", "진행 상황 알림 설정");
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmProgressReceiver.class);

            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();

            //알람시간 calendar에 set해주기
            //현재 시각이 오전 10시를 지나지 않았다면
            if (calendar.get(Calendar.HOUR_OF_DAY) < 11) {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 11, 0, 0);
                Log.d("시간", "" + calendar.get(Calendar.HOUR_OF_DAY) + " < 10 [ 10시 이전 ]");
            }
            //현재 시각이 오전10시 이후라면 다음날 10시로
            else if (calendar.get(Calendar.HOUR_OF_DAY) >= 11) {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE+1), 11, 0, 0);
                Log.d("시간", "" + calendar.get(Calendar.HOUR_OF_DAY) + " >= 10 [ 10시 이후 ]");
            }

            //알람 예약
            //시,분,초 곱한 뒤 밀리세컨즈로 만드려고 *1000
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender);
        }
    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Toast.makeText(getApplicationContext(), "카카오톡 로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
