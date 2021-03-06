package com.elftree.mall.activity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.elftree.mall.R;
import com.elftree.mall.config.NetConfig;
import com.elftree.mall.model.Region;
import com.elftree.mall.model.RegionInfo;
import com.elftree.mall.model.User;
import com.elftree.mall.retrofit.RetrofitCreator;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.viewpagerindicator.as.library.indicator.RecyclerCirclePageIndicator;
import com.viewpagerindicator.as.library.pageview.RecyclerViewPager;
import com.zhz.retrofitclient.RetrofitClient;
import com.zhz.retrofitclient.net.BaseSubscriber;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zouhongzhi on 2017/9/12.
 */

public class SplashAcitvity extends BaseActivity implements View.OnClickListener{

    private static final String SPLASH_DIR = "splash";
    private RecyclerViewPager mRecyclerViewPager;
    private RecyclerCirclePageIndicator mIndicator;
    private RecyclerView.Adapter mPagerAdapter;
    private List<Bitmap> mBitmapList;
    private Button btn_start;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initDatas(Bundle savedInstanceState){
        mBitmapList = new ArrayList<>();
        AssetManager assetManager = getAssets();
        String[] pathList = null;
        try {
            pathList = assetManager.list(SPLASH_DIR);
            for(String path:pathList){
               // ImageView imageView
                //Logger.d("splash path:"+path);
                Bitmap bitmap = BitmapFactory.decodeStream(assetManager.open(SPLASH_DIR+ File.separator+path));
                mBitmapList.add(bitmap);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //获取地区信息并插入到数据库中
        getRegionInfo();
    }

    private void getRegionInfo(){
        User user = new User();
        RetrofitClient.getInstance().createBaseApi()
                .json(NetConfig.GET_REGION_INFO,user.genRequestBody())

                .subscribe(new BaseSubscriber(mContext) {
                    @Override
                    public void onSuccess(final String jsonStr) {
                        //Logger.d(jsonStr);
                        //Type type = new TypeToken<ArrayList<Region>>(){}.getType();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                RegionInfo regionInfo = mGson.fromJson(jsonStr,RegionInfo.class);
                                insertRegionInfo(regionInfo);
                            }
                        }).start();

                    }


                });
    }

    private void insertRegionInfo(RegionInfo info){

        MyApplication.getInstances().getDaoSession().getRegionDao().deleteAll();
        for(Region region :info.getProvince()){
            MyApplication.getInstances().getDaoSession().getRegionDao().insert(region);
            //Logger.d(region.toString());
        }
        for(Region region :info.getCity()){
            MyApplication.getInstances().getDaoSession().getRegionDao().insert(region);
        }
        for(Region region :info.getArea()){
            MyApplication.getInstances().getDaoSession().getRegionDao().insert(region);
        }
    }
    @Override
    public void initViews(){
        setContentView(R.layout.activity_splash);
        mRecyclerViewPager = (RecyclerViewPager)findViewById(R.id.viewpager);
        mIndicator = (RecyclerCirclePageIndicator)findViewById(R.id.indicator);
        mPagerAdapter = new PagerAdaper();
        mRecyclerViewPager.setAdapter(mPagerAdapter);

        // config layoutmanager
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation (LinearLayoutManager.HORIZONTAL);
        mRecyclerViewPager.setLayoutManager(manager);

        // config indicator
        mIndicator.setViewPager(mRecyclerViewPager);

        mRecyclerViewPager.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
                Logger.d("oldPosition:"+oldPosition+"\n"+"newPosition:"+newPosition);
            }
        });

        //btn_start =(Button) findViewById(R.id.button);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                finish();
                break;
            default:
                break;
        }
    }


    public class PagerAdaper extends RecyclerView.Adapter<SplashViewHolder>{

        @Override
        public SplashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.layout_splash_item,parent,false);
            SplashViewHolder viewHolder = new SplashViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SplashViewHolder holder, int position) {
            holder.imageview.setImageBitmap(mBitmapList.get(position));
            holder.button.setOnClickListener(SplashAcitvity.this);
            if(position == mBitmapList.size()-1){
                holder.button.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mBitmapList.size();
        }
    }

    public class SplashViewHolder extends RecyclerView.ViewHolder{
        public SimpleDraweeView imageview;
        public Button button;
        public SplashViewHolder(View itemView) {
            super(itemView);
            imageview = (SimpleDraweeView)itemView.findViewById(R.id.imageview);
            button = (Button)itemView.findViewById(R.id.btn_start);
        }
    }

}
