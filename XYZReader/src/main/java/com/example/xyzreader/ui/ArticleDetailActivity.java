package com.example.xyzreader.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.example.xyzreader.R;
import com.example.xyzreader.room.Article;
import com.example.xyzreader.ui.articlelist.ArticleListViewModel;

import java.util.List;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private int mStartId;

    private int mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    private int mTopInset;
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private View mUpButtonContainer;
    private View mUpButton;

    private ViewModel mViewModel;
    private List<Article> mArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.detail_activity_article);
        mPager = (ViewPager) findViewById(R.id.pager);
        final ViewPager mPagerReference=mPager;
        final int mSecondStartId=0;

        //Defining ViewModel
        mViewModel=ViewModelProviders.of(this).get(ArticleListViewModel.class);

       //Init sequence
        if (savedInstanceState == null) {
            //Observing the model
            ((ArticleListViewModel) mViewModel).getArticles().observe(this, new Observer<List<Article>>() {
                @Override
                public void onChanged(@Nullable List<Article> articles) {
                    //TODO Update UI
                    mArticles=articles;
                    //Set Up Adapter After data is provided
                    mPagerAdapter = new MyPagerAdapter(mArticles,getSupportFragmentManager());
                    mPager.setAdapter(mPagerAdapter);
                    mPager.setCurrentItem(mStartId);
                }
            });
            //Getting the item that was selected
            if (getIntent() != null && getIntent().hasExtra(ArticleDetailFragment.ARG_ITEM_ID)) {
                mSelectedItemId=getIntent().getExtras().getInt(ArticleDetailFragment.ARG_ITEM_ID);
                mStartId=mSelectedItemId;
            }
            SetUpPagerListener(mPager);
        }
        else {
            //Re-setting everything up after rotation
            mArticles=((ArticleListViewModel) mViewModel).getArticles().getValue();
            mSelectedItemId=savedInstanceState.getInt(ArticleDetailFragment.ARG_ITEM_ID);
            mPagerAdapter = new MyPagerAdapter(mArticles,getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);
            mPager.setCurrentItem(mSelectedItemId);
            SetUpPagerListener(mPager);
        }
    }

    public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
        if (itemId == mSelectedItemId) {
            mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
            updateUpButtonPosition();
        }
    }

    void SetUpPagerListener(ViewPager mPager){
        this.mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        this.mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));
        //Listeners for when the items get changed
        this.mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mUpButton.animate()
                        .alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
                        .setDuration(300);
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("onPageSelected", ""+position);
                mSelectedItemId = position;
                updateUpButtonPosition();
            }
        });

        mUpButtonContainer = findViewById(R.id.up_container);

        mUpButton = findViewById(R.id.action_up);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUpButtonContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    view.onApplyWindowInsets(windowInsets);
                    mTopInset = windowInsets.getSystemWindowInsetTop();
                    mUpButtonContainer.setTranslationY(mTopInset);
                    updateUpButtonPosition();
                    return windowInsets;

                }
            });
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ArticleDetailFragment.ARG_ITEM_ID,mPager.getCurrentItem());
        super.onSaveInstanceState(outState);

    }

    private void updateUpButtonPosition() {
        int upButtonNormalBottom = mTopInset + mUpButton.getHeight();
        mUpButton.setTranslationY(Math.min(mSelectedItemUpButtonFloor - upButtonNormalBottom, 0));
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        private List<Article> mArticles;

        public MyPagerAdapter(List<Article> marticles, FragmentManager fm) {
            super(fm);
            mArticles=marticles;
        }


        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
            if (fragment != null) {
                mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
                updateUpButtonPosition();
            }
        }

        @Override
        public Fragment getItem(int position) {
            return ArticleDetailFragment.newInstance(mArticles.get(position).getId());
        }

        @Override
        public int getCount() {
            return (mArticles != null)
                    ? mArticles.size() : 0;
        }
    }

}
