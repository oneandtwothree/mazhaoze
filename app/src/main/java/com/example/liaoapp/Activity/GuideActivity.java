package com.example.liaoapp.Activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.framework.base.BasePageAdapter;
import com.example.framework.base.BaseUiActivity;
import com.example.framework.manager.MediaPlayerManager;
import com.example.framework.utils.AnimUtils;
import com.example.liaoapp.R;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseUiActivity implements View.OnClickListener {
    private ViewPager mviewpager;
    private ImageView ivMusicSwitch;
    private TextView tvGuideSkip;
    private ImageView ivGuidePoint1;
    private ImageView ivGuidePoint2;
    private ImageView ivGuidePoint3;


    private View view1;
    private View view2;
    private View view3;

    private PagerAdapter mpageradap;


    private ImageView ivGuideStar;
    private ImageView ivGuideNight;
    private ImageView ivGuideSmile;

    private MediaPlayerManager mGuideMusic;

    private ObjectAnimator mAnim;

    private List<View> mpagelist = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        mviewpager = findViewById(R.id.mviewpager);
        ivMusicSwitch = findViewById(R.id.iv_music_switch);
        tvGuideSkip = findViewById(R.id.tv_guide_skip);
        ivGuidePoint1 = findViewById(R.id.iv_guide_point_1);
        ivGuidePoint2 = findViewById(R.id.iv_guide_point_2);
        ivGuidePoint3 = findViewById(R.id.iv_guide_point_3);

        ivMusicSwitch.setOnClickListener(this);
        tvGuideSkip.setOnClickListener(this);

        view1 = View.inflate(this,R.layout.layout_pager_guide_1,null);
        view2 = View.inflate(this,R.layout.layout_pager_guide_2,null);
        view3 = View.inflate(this,R.layout.layout_pager_guide_3,null);

        mpagelist.add(view1);
        mpagelist.add(view2);
        mpagelist.add(view3);

        ivGuideStar = view1.findViewById(R.id.iv_guide_star);
        ivGuideNight = view2.findViewById(R.id.iv_guide_night);
        ivGuideSmile = view3.findViewById(R.id.iv_guide_smile);

        mviewpager.setOffscreenPageLimit(mpagelist.size());

        mpageradap= new BasePageAdapter(mpagelist);
        mviewpager.setAdapter(mpageradap);

        AnimationDrawable animstar = (AnimationDrawable) ivGuideStar.getBackground();
        animstar.start();
        AnimationDrawable animnight = (AnimationDrawable) ivGuideNight.getBackground();
        animnight.start();
        AnimationDrawable animsmile = (AnimationDrawable) ivGuideSmile.getBackground();
        animsmile.start();

        mviewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                seletePoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        startMusic();

    }
    private void startMusic() {
        mGuideMusic = new MediaPlayerManager();
        mGuideMusic.setLooping(true);
        final AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.guide);
        mGuideMusic.startPlay(file);

        mGuideMusic.setOnComplteionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mGuideMusic.startPlay(file);
            }
        });

        //旋转动画
        mAnim = AnimUtils.rotation(ivMusicSwitch);
        mAnim.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_music_switch:
                if(mGuideMusic.MEDIA_STATUS == MediaPlayerManager.MEDIA_STATUS_PAUSE){
                    mAnim.start();
                    mGuideMusic.continuePlay();
                    ivMusicSwitch.setImageResource(R.drawable.img_guide_music);
                }else if(mGuideMusic.MEDIA_STATUS == MediaPlayerManager.MEDIA_STATUS_PLAY){
                    mAnim.pause();
                    mGuideMusic.pausePlay();
                    ivMusicSwitch.setImageResource(R.drawable.img_guide_music_off);
                }
                break;
            case R.id.tv_guide_skip:
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
        }
    }

    private void seletePoint(int position) {
        switch (position){
            case 0:
                ivGuidePoint1.setImageResource(R.drawable.img_guide_point_p);
                ivGuidePoint2.setImageResource(R.drawable.img_guide_point);
                ivGuidePoint3.setImageResource(R.drawable.img_guide_point);
                break;
            case 1:
                ivGuidePoint1.setImageResource(R.drawable.img_guide_point);
                ivGuidePoint2.setImageResource(R.drawable.img_guide_point_p);
                ivGuidePoint3.setImageResource(R.drawable.img_guide_point);
                break;
            case 2:
                ivGuidePoint1.setImageResource(R.drawable.img_guide_point);
                ivGuidePoint2.setImageResource(R.drawable.img_guide_point);
                ivGuidePoint3.setImageResource(R.drawable.img_guide_point_p);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGuideMusic.stopPlay();
    }
}
