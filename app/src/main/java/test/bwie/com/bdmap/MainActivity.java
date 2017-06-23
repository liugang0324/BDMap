package test.bwie.com.bdmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    // 百度地图控件
    private MapView mMapView = null;
    // 百度地图对象
    private BaiduMap bdMap;
    private Button bt;
    boolean isFirstLoc = true; // 是否首次定位
    // 定位相关
    LocationClient mLocClient;
    private String path;
    public MyLocationListenner myListener = new MyLocationListenner();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        init();
    }
    /**
     * 初始化方法
     */
    private void init() {
        mMapView = (MapView) findViewById(R.id.bmapview);
        bt=(Button) findViewById(R.id.button);



        bdMap = mMapView.getMap();
//普通地图
        bdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        bdMap.setBaiduHeatMapEnabled(true);
        bdMap.setTrafficEnabled(true);

        bdMap.setMyLocationEnabled(true);
// 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);//设置获得地址位置
        option.setIsNeedLocationPoiList(true);//设置获得poi
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setResult(12,new Intent(path));

            }
        });

    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            String str = "定位的信息："+location.getAddress().address+",纬度："
                    +location.getLatitude();
            Toast.makeText(MainActivity.this,",纬度："+location.getLatitude()+"经度"+location.getLongitude(),Toast.LENGTH_LONG).show();
            bt.setText(location.getAddress().address);
            List l = location.getPoiList();
            str += "，poi数量:"+l.size()+",第一个："+((Poi)(l.get(0))).getName();
            path=location.getAddress().address;
            Log.i("TAG", str);

            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(5).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            bdMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                bdMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
