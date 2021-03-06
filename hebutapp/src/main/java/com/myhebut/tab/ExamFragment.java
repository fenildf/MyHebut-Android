package com.myhebut.tab;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhebut.activity.MainActivity;
import com.myhebut.activity.R;
import com.myhebut.application.MyApplication;
import com.myhebut.classes.NetworkImageHolderView;
import com.myhebut.classes.NoScrollViewPage;
import com.myhebut.entity.Banner;
import com.myhebut.exam.ExamTagPage;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExamFragment extends BaseFragment {

    @ViewInject(R.id.convenientBanner)
    private ConvenientBanner convenientBanner;

    @ViewInject(R.id.tpi_exam_index)
    private TabPageIndicator mTpiExam;

    @ViewInject(R.id.vp_exam_index)
    private NoScrollViewPage mVpExam;

    @ViewInject(R.id.toggleBtn_exam_index)
    private ToggleButton mToggleBtn;

    private String[] subjects = {"马原", "毛概上", "毛概下", "史纲"};

    private List<ExamTagPage> pages = new ArrayList<ExamTagPage>();

    @Override
    public View initView() {
        View root = View.inflate(mainActivity, R.layout.fragment_exam, null);
        ViewUtils.inject(this, root);
        return root;
    }


    @Override
    public void initData() {
        // 获取banner
        MyApplication application = (MyApplication) mainActivity.getApplication();
        List<Banner> banners = application.getBanners();
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < banners.size(); i++) {
            urls.add(banners.get(i).getUrl());
        }

        if (pages.size() == 0) {
            for (int i = 0; i < subjects.length; i++) {
                pages.add(new ExamTagPage((MainActivity) getActivity(), i));
            }
        }
        MyAdapter adapter = new MyAdapter();

        mVpExam.setAdapter(adapter);
        mTpiExam.setViewPager(mVpExam);

        // 设置viewpager默认跳转页
        int defaultPosition = SpUtil.getInt(mainActivity, MyConstants.SUBJECT, 0);
        mTpiExam.setCurrentItem(defaultPosition);

        convenientBanner.setPages(
                new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, urls)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                        //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        //　轮播起始页
        convenientBanner.setcurrentitem(new Random().nextInt(3));
        // 设置ToggleButton的用户选择
        if (SpUtil.getBoolean(mainActivity, MyConstants.ISENTEREXAM, false)){
            mToggleBtn.setChecked(true);
        }
        super.initData();
    }

    @Override
    public void initEvent() {
        // 记住用户每次选择的科目
        mTpiExam.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                SpUtil.setInt(mainActivity, MyConstants.SUBJECT, position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // 设置启动app是否要直接进入考试模块
        mToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SpUtil.setBoolean(mainActivity, MyConstants.ISENTEREXAM, true);
                } else {
                    SpUtil.setBoolean(mainActivity, MyConstants.ISENTEREXAM, false);
                }

            }
        });// 添加监听事件

        super.initEvent();
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return subjects.length;
        }

        /**
         * 页签显示数据调用该方法
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return subjects[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ExamTagPage page = pages.get(position);
            View root = page.getRoot();
            container.addView(root);

            return root;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(4000);
    }


    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }

}
