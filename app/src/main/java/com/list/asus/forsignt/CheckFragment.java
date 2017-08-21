package com.list.asus.forsignt;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.list.asus.forsignt.bean.CheckRecord;
import com.list.asus.forsignt.bean.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * Created by ASUS on 2017/5/20.
 *
 */

public class CheckFragment extends Fragment {


    public TextView range;          //range、isCheck是button下的textView
    public TextView isCheck;
    public TextView id;             //id、courseN是教师ID以及课程的名字，两个textView
    public TextView courseN;
    public ImageView image_range;
    public ImageView image_isCheck;
    public Button check;           //buttton check
    int year,month,day,hour,minute;
    String teachingClass;          //教学班
    String courseId;                //课程id
    String courseName;             //用String储存课程名称
    String teachId;                //教师编号
    String checkId;                //打卡id
    String classTime;              //课的节次
    String classRoom;
    Boolean isHasClass=false;
    Boolean isCheckin=false;
    int count=0;                   //记录button的次数

    Boolean isGetPlace=false;
//    Boolean isCheck;

    private java.lang.Double latitude,longitude;


    //百度地图
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate=true;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View checkView = inflater.inflate(R.layout.main_fra_check, container, false);



        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
//        SDKInitializer.initialize(getApplicationContext());


        initView(checkView);

        BmobUser bmobUser = BmobUser.getCurrentUser();
        id.setText(bmobUser.getUsername());
        teachId=id.getText().toString();
        Log.d("this", "teachId "+teachId);


        //得到课的节次classTime，提供给查询Schedule表
        makeClassTime();




        //拼凑出checkId来
        makeCheckId();


        //查询授课表里的该教师的教学班、课程名称，查询到才能发起打卡
        querySchedule(classTime);



        mLocationClient = new LocationClient(getApplicationContext());  //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );         //注册监听函数
        //申请权限用于定位
        getPermissions();











       try {
           //点击事件，即点击成功就是向checkRecord表中添加一行数据
           check.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View view) {

                   //防止多次点击，控制十分钟内点的第二次无效
//                if (!ButtonUtils.isFastDoubleClick(R.id.check)) {
//
//                    addToCheckResult();
//
//                }



                   if (isGetPlace==true&&isHasClass==true&&isCheckin==false){
                       //向考勤记录表里添加一行发起打卡数据
                       addRecordToCheckRecord();
                   }
                   //提醒用户请勿多次点击打卡
                   count++;
                   if (count>=2){
                       Toast.makeText(getContext(),"请勿多次点击打卡",Toast.LENGTH_LONG).show();
                   }

               }
           });
       }catch (Exception e){

       }

        return checkView;

    }

    private void getLocation() {
        initLocation();
        mLocationClient.start();


        if (isGetPlace=true){
            range.setText("已定位成功");
            image_range.setImageResource(R.drawable.yes);
        }else {
            range.setText("还未定位成功");
            image_range.setImageResource(R.drawable.no);
        }
    }

      //申请权限
    private void getPermissions() {
        List<String> permissionList=new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(android.Manifest.permission.SEND_SMS);
        }

        if (!permissionList.isEmpty()){
            String [] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(),permissions,1);
        }else {
            getLocation();
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //低功耗定位模式，只会使用网络定位
//        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
//        int span=1000;
//        option.setScanSpan(span);
//        mLocationClient.setLocOption(option);





        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span=5000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getContext(), "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                    getLocation();
                } else {
                    Toast.makeText(getContext(), "发生未知错误", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            default:
        }
    }

    //初始化控件
    private void initView(View checkView) {
        //上边两个TextView
        id=(TextView)checkView.findViewById(R.id.name) ;
        courseN=(TextView)checkView.findViewById(R.id.course);
        //button下边的两个TextView
        range=(TextView) checkView.findViewById(R.id.range);
        isCheck=(TextView)checkView.findViewById(R.id.isCheck);
        image_isCheck=(ImageView)checkView.findViewById(R.id.image_isCheck);
        image_range=(ImageView)checkView.findViewById(R.id.image_range);
        check=(Button) checkView.findViewById(R.id.check);

        mapView=(MapView)checkView.findViewById(R.id.bmapView);
        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);


    }




    //根据教师的Id和课程的节次 在授课表里 查询教学班
    private void querySchedule2() {

        final BmobQuery<Schedule>queryTeachingClass=new BmobQuery<>();
        queryTeachingClass.addWhereEqualTo("teaId",teachId);
        queryTeachingClass.addWhereEqualTo("classTime",classTime);
//        queryTeachingClass.setLimit(1);
        queryTeachingClass.findObjects(new FindListener<Schedule>() {
            @Override
            public void done(List<Schedule> list, BmobException e) {
                if (list!=null){
                    for (Schedule schedule:list){
                        //teachingClass为教学班号
                        teachingClass= schedule.getTeachingClass();
                        courseId=schedule.getCourseId();

                        courseName=schedule.getCourseName();
                        courseN.setText("当前课程："+courseName+" "+teachingClass);

                        isHasClass=true;


                    }
                }else{
                    //此处好像有点问题
//                    Toast.makeText(getContext(),"当前你没有课哦",Toast.LENGTH_SHORT).show();
                    courseN.setText("当前你没有课哦");
                    Toast.makeText(getContext(),"4",Toast.LENGTH_SHORT).show();
                    Log.d("","44444444444");

                }

            }



        });

    }




    private void querySchedule(final String classtime ) {

//       BmobQuery<Schedule>queryTeaId=new BmobQuery<>();
//        queryTeaId.addWhereEqualTo("teaId",teachId);
//        BmobQuery<Schedule>queryClassTime=new BmobQuery<>();
//        queryClassTime.addWhereEqualTo("classTime",classTime);
//        List<BmobQuery<Schedule>>querySchedule=new ArrayList<BmobQuery<Schedule>>();
//        querySchedule.add(queryTeaId);
//        querySchedule.add(queryClassTime);
//
//        BmobQuery<Schedule>query=new BmobQuery<>();
//        query.and(querySchedule);

        BmobQuery<Schedule>query=new BmobQuery<>();
        query.addWhereEqualTo("teaId",teachId);
        query.addWhereEqualTo("classTime",classTime);
        query.findObjects(new FindListener<Schedule>() {
            @Override
            public void done(List<Schedule> list, BmobException e) {
                if (e==null){
                    if (!list.isEmpty()){
                        for (Schedule schedule:list){
                            //teachingClass为教学班号
                            teachingClass= schedule.getTeachingClass();
                            courseId=schedule.getCourseId();
                            courseName=schedule.getCourseName();
                            classRoom=schedule.getClassroom();
                            courseN.setText(courseName+" "+classRoom);

                            isHasClass=true;
                            Log.d("this", "querySchedule1: "+teachingClass+" ");
                            Log.d("this", "querySchedule1: "+teachId);

                            //在checkRecord中查询是否有打卡记录，没有才能打卡成功
                            queryCheckRecord();

                        }

                    }else {
                        courseN.setText("当前你没有课哦");

                    }
                }else{
                    //此处好像有点问题

                    Toast.makeText(getContext(), "失败"+e.getErrorCode(),Toast.LENGTH_LONG).show();

                }


            }
        });
    }

    private void queryCheckRecord() {
//        BmobQuery<CheckRecord>queryTeaId=new BmobQuery<>();
//        queryTeaId.addWhereEqualTo("teaId",teachId);
//        BmobQuery<CheckRecord>queryCheckId=new BmobQuery<>();
//        queryCheckId.addWhereEqualTo("checkId",checkId);
//        List<BmobQuery<CheckRecord>>querySchedule=new ArrayList<>();
//        querySchedule.add(queryTeaId);
//        querySchedule.add(queryCheckId);
//
//        BmobQuery<CheckRecord>query=new BmobQuery<>();
//        query.and(querySchedule);


        Log.d("this", "queryCheckRecord: "+checkId);
        BmobQuery<CheckRecord>query=new BmobQuery<>();
        query.addWhereEqualTo("teaId",teachId);
        query.addWhereEqualTo("checkId",checkId);
        query.findObjects(new FindListener<CheckRecord>() {
            @Override
            public void done(List<CheckRecord> list, BmobException e) {
               if (e==null){
                   if (list.isEmpty()){
                       isCheckin=false;
                       Log.d("this", "queryCheckRecord:查找失败 ");

                   }else {
                       check.setText("已发起考勤");
                       isCheckin=true;

                      for (CheckRecord checkRecord:list){
                          image_isCheck.setImageResource(R.drawable.yes);
                          isCheck.setText("上次发起考勤时间是："+checkRecord.getUpdatedAt());
                          Log.d("this", "queryCheckRecord: "+checkId+" "+teachId+"  "+teachingClass);


                      }
                   }
               }else {
                   Toast.makeText(getContext(), "失败"+e.getErrorCode(),Toast.LENGTH_LONG).show();

               }
            }
        });

    }



    //向考勤记录表里添加一行发起打卡数据
    private void addRecordToCheckRecord() {

        BmobGeoPoint point = new BmobGeoPoint(longitude,latitude);
        //点击butto向后台check_record表中添加一条记录
        CheckRecord check_record=new CheckRecord();
        check_record.setCheckId(checkId);
        check_record.setTeachingClass(teachingClass);
        check_record.setTeaId(teachId);
        check_record.setLocation(point);
        check_record.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    //考勤成功就把button文字改变
                    check.setText("发起考勤成功");
                    image_isCheck.setImageResource(R.drawable.yes);

                    isCheckin=true;

                    getTime1();


                }else {
                    Toast.makeText(getContext(),"发起考勤失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //根据手机系统时间来判断现在是第几节课
    private void makeClassTime(){

//          此处为实际函数方法
//        if (hour==8||hour==9){
//            classTime="星期四一二节课";
//
//        }else if (hour==10||hour==11){
//            classTime="星期四三四节课";
//
//        }else if (hour==14||hour==15){
//            classTime="星期四五六节课";
//        }else if (hour==16||hour==17){
//            classTime="星期四七八节课";
//        }else if (hour==19||hour==20){
//           classTime="星期四九十节课";
//        }else if (hour==21||hour==22){
//            classTime="星期四十一十二节课";
//        }

        getTime2();
        Log.d("here", "1 "+classTime);

        switch (hour){
            case 8:
            case 9:
                classTime="星期五一二节课";
                Log.d("here", "2 "+classTime);
                break;
            case 10:
            case 11:
                classTime="星期五三四节课";
                Log.d("here", "3"+classTime);
                break;
            case 14:
            case 15:
                classTime="星期五五六节课";
                break;
            case 16:
            case 17:
                classTime="星期五七八节课";
                Log.d("here", "4"+classTime);
                break;
            case 19:
            case 20:
                classTime="星期五九十节课";
                Log.d("here", "5 "+classTime);
                break;
            case 21:
            case 22:
                classTime="星期五十一十二节课";
                Log.d("here", "6 "+classTime);
                break;
            default:
                classTime="null";
                break;
        }

//        classTime="星期五一二节课";
        Log.d("here", "7 "+classTime);


//        classTime="星期四七八节课";
        Log.d("here", "makeClassTime: "+classTime);
    }


    //拼凑出checkId来
    private void makeCheckId(){


        //得到系统时间为拼凑CkeckId
//        getTime2();

        //判断月份和号数是否小于10，小于的话在它前面加个0保持checkId的位数一样

//        if (month>10&&day>10){
//
//            checkId=year+""+month+""+day+""+teachId;
//        }else if (month>10&&day<10){
//
//            checkId=year+""+month+"0"+day+""+teachId;
//        }else if (month<10&&day>10){
//
//            checkId=year+"0"+month+""+day+""+teachId;
//        }else if (month<10&&day<10){
//
//            checkId=year+"0"+month+"0"+day+""+teachId;
//            Log.d("this", "makeCkeckId: "+teachingClass+" "+teachId);
//
//        }


//        String a=null;
//        String b=null;
//
//        if (month<10){
//             a="0"+month;
//        }else {
//            a=""+month;
//        }
//        if (day<10){
//             b="0"+day;
//        }else {
//            b=""+day;
//        }
//        checkId=year+a+b;
//        Log.d("this", "makeCkeckId: "+year+" "+month+"  "+day+" "+teachId);
//        Log.d("this", "makeCkeckId: "+month+" "+b);
//        if (checkId!=null){
//            queryCheckRecord();
//        }
        SimpleDateFormat sDateFormat= new SimpleDateFormat("yyyyMMddHH");
        Date curDate=new Date(System.currentTimeMillis());
        String date = sDateFormat.format(curDate);
        checkId= ""+date+teachId;

        if (checkId!=null){
            queryCheckRecord();
        }
    }

    //得到当前系统时间  直接获取总时间
    private void getTime1() {
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss     ");
        Date curDate=new Date(System.currentTimeMillis());
        String date=sDateFormat.format(curDate);
        isCheck.setText("发起打卡时间是："+date);
    }

    //得到当前系统时间  分年月日时分单独获取
    private void getTime2() {
        Calendar c=Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
//            //获取定位结果
////        StringBuffer sb = new StringBuffer(256);
//        StringBuilder sb=new StringBuilder();
//        sb.append("\nlatitude : ");
//        sb.append(bdLocation.getLatitude());    //获取纬度信息
//
//        sb.append("\nlontitude : ");
//        sb.append(bdLocation.getLongitude());    //获取经度信息
//
//            sb.append("\naddr : ");
//            sb.append(bdLocation.getAddrStr());
//
//
//
//         Log.d("11111", sb.toString());
//        Toast.makeText(getContext(),bdLocation.getLatitude()+bdLocation.getLongitude()+" ",Toast.LENGTH_LONG).show();
//            Log.d("2222", bdLocation.getStreet());
//
//
//            latitude=bdLocation.getLatitude();
//            longitude=bdLocation.getLongitude();
//            isGetPlace=true;
//            image_range.setImageResource(R.drawable.yes);




            //获取定位结果
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度


            if (location.getLocType()==BDLocation.TypeGpsLocation||location.getLocType()==BDLocation.TypeNetWorkLocation){
                navigateTo(location);
            }

            if (location.getLocType() == BDLocation.TypeGpsLocation){

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");


                latitude=location.getLatitude();
                longitude=location.getLongitude();
                isGetPlace=true;


            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {

                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

            }

            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息

            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }

            Log.i("BaiduLocationApiDem", sb.toString());




        }

        private void navigateTo(BDLocation location) {
            if (isFirstLocate){
                LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());
                MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(update);
                update=MapStatusUpdateFactory.zoomTo(18f);
                baiduMap.animateMapStatus(update);
                isFirstLocate=false;
            }
            MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
            locationBuilder.latitude(location.getLatitude());
            locationBuilder.longitude(location.getLongitude());
            MyLocationData locationData=locationBuilder.build();
            baiduMap.setMyLocationData(locationData);

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }


//    @Override
//    public void onReceiveLocation(BDLocation bdLocation) {
//        //获取定位结果
//        StringBuffer sb = new StringBuffer(256);
//
//        sb.append("\nlatitude : ");
//        sb.append(bdLocation.getLatitude());    //获取纬度信息
//
//        sb.append("\nlontitude : ");
//        sb.append(bdLocation.getLongitude());    //获取经度信息
//
//        Log.d("11111", "精度："+bdLocation.getLatitude()+"   "+"纬度："+bdLocation.getLongitude());
//        range.setText(bdLocation.getLatitude()+"");
//    }

//    @Override
//    public void onConnectHotSpotMessage(String s, int i) {
//
//    }
}
