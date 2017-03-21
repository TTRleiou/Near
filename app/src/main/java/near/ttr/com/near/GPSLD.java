package near.ttr.com.near;


import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import java.util.List;


public class GPSLD extends AppCompatActivity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private LocationClient mLocClient;
    private EditText truetext;
    private EditText falsetext;
    private Button of;
    private boolean yx = true;
    private Button search;
    private Button set;
    private BDNotifyListener mNotifyer;
    String out = new String();
    String in = new String();
    final java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.000000");
    private PoiSearch mPoiSearch ;
    double myLatitude;
    double myLongitude;
    private BitmapDescriptor bitmap = null;
    private Marker marker = null;
    private Marker marker2 = null;
    private OverlayOptions option1;
    boolean f = false;
    private LatLng cenpt;
    private TextView textView3;
    boolean ss = false;
    private String city;
    private BitmapDescriptor bitmap1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); //隐藏状态栏

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_gpsld);

        truetext = (EditText) findViewById(R.id.editText);
        falsetext = (EditText) findViewById(R.id.editText2);
        truetext.setKeyListener(null);
        falsetext.setKeyListener(null);
        textView3 = (TextView) findViewById(R.id.editText3);
        textView3.setVisibility(View.INVISIBLE);

        of = (Button) findViewById(R.id.button5);
        search = (Button) findViewById(R.id.button3);
        set = (Button) findViewById(R.id.button4);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);

        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        initLocation();
        mLocationClient.start();
        //  mLocationClient.requestLocation();


        Mybutton listener = new Mybutton();
        of.setOnClickListener(listener);
        search.setOnClickListener(listener);
        set.setOnClickListener(listener);

        mNotifyer = new NotifyLister();
        mLocationClient.registerNotify(mNotifyer);

        bitmap1 = BitmapDescriptorFactory
                .fromResource(R.mipmap.dj);
        bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.dq);
        OverlayOptions option1 = new MarkerOptions()
                .position(new LatLng(0, 0))  //设置marker的位置
                .icon(bitmap);
        marker = (Marker)(mBaiduMap.addOverlay(option1));

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                myLatitude = point.latitude;
                myLongitude = point.longitude;
                in = df.format(myLongitude) + "," + df.format(myLatitude);
                falsetext.setText(in);
                LatLng ths = new LatLng(myLatitude, myLongitude);
                OverlayOptions option = new MarkerOptions()
                        .position(ths)
                        .icon(bitmap1);
                if(marker2 != null) marker2.remove();
                marker2 = (Marker)(mBaiduMap.addOverlay(option));
            }

            public boolean onMapPoiClick(MapPoi arg0) {
                //在此处理底图标注点击事件
                in = arg0.getName(); //名称
                myLongitude = arg0.getPosition().longitude;
                myLatitude = arg0.getPosition().latitude;
                in = in + ": " + df.format(myLongitude) + "," + df.format(myLatitude); //坐标
                LatLng ths = new LatLng(myLatitude, myLongitude);
                OverlayOptions option = new MarkerOptions()
                        .position(ths)
                        .icon(bitmap1);
                if(marker2 != null) marker2.remove();
                marker2 = (Marker)(mBaiduMap.addOverlay(option));
                return false;
            }
        });

        final BitmapDescriptor bitmap2 = BitmapDescriptorFactory
                .fromResource(R.mipmap.now);

        mPoiSearch = PoiSearch.newInstance();
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
            public void onGetPoiResult(PoiResult result){
                    //获取POI检索结果
                List<PoiInfo> allAddr = result.getAllPoi();
                for (PoiInfo p: allAddr) {
                    Log.d("MainActivity", "p.name--->" + p.name +"p.phoneNum" + p.phoneNum +" -->p.address:" + p.address + "p.location" + p.location);
                    OverlayOptions option = new MarkerOptions()
                            .position(new LatLng(p.location.latitude, p.location.longitude))
                            .icon(bitmap2);
                    mBaiduMap.addOverlay(option);
                }
            }
            public void onGetPoiDetailResult(PoiDetailResult result){
                //获取Place详情页检索结果
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);



    }


    class Mybutton implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId())  {
                case R.id.button5:
                    if (yx) {
                        yx = false;
                        mLocationClient.stop();
                        of.setText("开启定位");
                    }
                    else {
                        yx = true;
                        mLocationClient.start();
                        of.setText("关闭定位");
                    }
                    break;
                case R.id.button4:
                    if (!yx)  Toast.makeText(getApplicationContext(), "请开启定位！", Toast.LENGTH_SHORT).show();
                    else mNotifyer.SetNotifyLocation(myLongitude,myLatitude,500,"gps");
                    break;
                case R.id.button3:
                    if (!ss) {
                        textView3.setVisibility(View.VISIBLE);
                        textView3.setText("");
                        Button button = (Button) findViewById(R.id.button3);
                        button.setText("确定");
                        ss = true;
                    }
                    else {
                        mBaiduMap.clear();
                        String s = textView3.getText().toString();
                        textView3.setVisibility(View.INVISIBLE);
                        Button button = (Button) findViewById(R.id.button3);
                        button.setText("搜索地点");
                        mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city(city)
                            .keyword(s));
                        ss = false;
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mPoiSearch.destroy();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    public  class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取定位结果
            StringBuffer sb = new StringBuffer(256);

            city = location.getCity();

            sb.append("city : ");
            sb.append(location.getCity());

            sb.append("\ntime : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度

            out = df.format(location.getLongitude()) +"," +df.format(location.getLatitude());
            option1 = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(),location.getLongitude()))
                    .icon(bitmap);
            if (!f) cenpt =  new LatLng(location.getLatitude(),location.getLongitude());


            new Thread() {
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            truetext.setText(out);
                            falsetext.setText(in);
                            if (!f) {
                                f = true;
                                MapStatus mMapStatus = new MapStatus.Builder()
                                        .target(cenpt)
                                        .zoom(12)
                                        .build();
                                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                                mBaiduMap.setMapStatus(mMapStatusUpdate);
                            }

                            marker.remove();
                            marker = (Marker)(mBaiduMap.addOverlay(option1));
                        }
                    });
                }
            }.start();

            if (location.getLocType() == BDLocation.TypeGpsLocation) {

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

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");

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

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }


    }


    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span=1000;
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

    public class NotifyLister extends BDNotifyListener{

        public void onNotify(BDLocation mlocation, float distance){
            Vibrator mVibrator01 = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            mVibrator01.vibrate(1000);
            //振动提醒已到设定位置附近
        }
    }

}