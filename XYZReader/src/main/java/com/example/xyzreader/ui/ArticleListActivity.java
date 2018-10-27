package com.example.xyzreader.ui;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.room.Article;
import com.example.xyzreader.ui.articlelist.ArticleListFragment;
import com.example.xyzreader.ui.articlelist.ArticleListViewModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import timber.log.Timber;


/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity {
    private final String MY_RECYCLER_VIEW_ID_TAG = "my_recycler";
    private final String MY_FIRST_FRAGMENT = "first_fragment";
    private List<Article> myListofArticles = new ArrayList<>();
    private LiveData<List<Article>> myListofArticlesLive;
    private static Context context;
    private static final String TAG = ArticleListActivity.class.toString();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Boolean isRestarted = false;
    private Boolean itGotDataOnce = false;
    ArticleListFragment fragment;
    myArticleAdapter adapter;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);


    @Override
    protected void onStart() {
        super.onStart();
        if (isRestarted) {
            myListofArticles = fragment.getSecondGoodie();
            mRecyclerView = findViewById(R.id.recycler_view);
            UpdateUI(myListofArticles, mRecyclerView);
            Log.e(TAG, "onStart: ");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list_activity);
        final ArticleListViewModel mViewModel = ViewModelProviders.of(this).get(ArticleListViewModel.class);
        context = getBaseContext();

        Timber.plant(new Timber.DebugTree());

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        final ImageView imageView = (ImageView) findViewById(R.id.top_bar_logo);

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        Log.e(TAG, "onCreate: " + mToolbar.getId());
        mToolbar.setTitleTextColor(getResources().getColor(R.color.theme_primary_dark));
        setSupportActionBar(mToolbar);
        setAppBarNonVisible();
        getSupportActionBar().setTitle("Hi!");

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                setSupportActionBar(mToolbar);
                if (i>=-(mToolbar.getMeasuredHeight()+5)) {
                    imageView.setVisibility(View.VISIBLE);
                    setAppBarNonVisible();
                }
               else{
                    appBarLayout.setBackgroundColor(getResources().getColor(R.color.lightColor));
                    setAppBarVisible();
                    imageView.setVisibility(View.GONE);
                    mToolbar.setTitleTextColor(getResources().getColor(R.color.theme_accent));
                }
            }
        });
        setSupportActionBar(mToolbar);
        final View toolbarContainerView = findViewById(R.id.toolbar_layout);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                refresh();
            }
        });

        if (savedInstanceState == null) {
            FragmentManager fm = getFragmentManager();
            fragment = new ArticleListFragment();
            //Make Fragment retainable
            fragment.setRetainInstance(true);
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.container2, fragment, MY_FIRST_FRAGMENT);
            fragmentTransaction.commit();
            mViewModel.getArticles().observe(this, new Observer<List<Article>>() {
                @Override
                public void onChanged(@Nullable List<Article> articles) {
                    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                    //Checking if the change in the data was triggered due to swipe
                    //and turn it off, and removed all padding that was added

                    if(mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.setRefreshing(false);
                        LinearLayout linearLayout=findViewById(R.id.LL_for_rv);
                        float i=getResources().getDimension(R.dimen.gap_on_top_of_recycler);
                        linearLayout.setPadding(0,(int)i,
                                0,0);
                    }
                    myListofArticles = articles;
                    adapter = new myArticleAdapter(articles);
                    if (!itGotDataOnce) {
                        UpdateUI(articles, mRecyclerView);
                        itGotDataOnce = true;
                    }
                }
            });

        } else {
            fragment = (ArticleListFragment) getFragmentManager().findFragmentByTag(MY_FIRST_FRAGMENT);
            mRecyclerView = fragment.mRecyclerView;
            adapter = fragment.GetTheGoodies();
            mRecyclerView.setAdapter(adapter);
            myListofArticles = fragment.getSecondGoodie();
            adapter.mArticles=myListofArticles;
            UpdateUI(myListofArticles,mRecyclerView);
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
        outState.putInt(MY_RECYCLER_VIEW_ID_TAG, R.id.recycler_view);
        super.onSaveInstanceState(outState);

    }

    private void UpdateUI(List<Article> articles, RecyclerView recyclerView) {
            recyclerView.setAdapter(adapter);
            this.fragment.SetTheGoodies(adapter, articles);
            int columnCount = getResources().getInteger(R.integer.list_column_count);
            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(sglm);
            setAnimation(recyclerView);
        runLayoutAnimation(recyclerView);
    }
    private void setAnimation(RecyclerView recyclerView) {
        int resId = R.anim.layout_animation_grow;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), resId);
        recyclerView.setLayoutAnimation(animation);
    }
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_grow);
        if (controller.getAnimation().getDuration()==(long)getResources().getInteger(R.integer.anim_duration_long)){
            long animationDuration=(long)getResources().getInteger(R.integer.anim_duration_medium);
            controller.getAnimation().setDuration(animationDuration);
            Log.e(TAG, "runLayoutAnimation: not the first time"+ controller.getAnimation().getDuration() );
        }
        if(!itGotDataOnce){
            long animationDuration=(long)getResources().getInteger(R.integer.anim_duration_long);
            controller.getAnimation().setDuration(animationDuration);
            Log.e(TAG, "runLayoutAnimation: FIRST"+ controller.getAnimation().getDuration() );
        }

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    private void refresh() {
        //TODO:Initiate refresh for View model with drag down
        LinearLayout linearLayout=findViewById(R.id.LL_for_rv);
        float i=getResources().getDimension(R.dimen.refresh_padding);
        linearLayout.setPadding(0,(int)i,
                0,0);
        itGotDataOnce=false;
        ViewModel mViewModel=ViewModelProviders.of(this).get(ArticleListViewModel.class);
       ((ArticleListViewModel) mViewModel).loadArticlesOnline();


    }
    public float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi /DisplayMetrics.DENSITY_DEFAULT);
    }
    public float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
    private void setAppBarVisible(){
        getSupportActionBar().show();
        CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.theme_accent));
    }
    private void setAppBarNonVisible(){
        Toolbar mToolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().hide();
        CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.nothing));
    }
    public class myArticleAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Article> mArticles;

        public myArticleAdapter(List<Article> articles) {
            mArticles = articles;
        }

        @Override
        public long getItemId(int position) {
            return mArticles.get(position).getId();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
                }
            });
            return vh;
        }

        private Date parsePublishedDate(int position) {
            try {
                String date = mArticles.get(position).getPublished_date();
                return dateFormat.parse(date);
            } catch (ParseException ex) {
                Log.e(TAG, ex.getMessage());
                Log.i(TAG, "passing today's date");
                return new Date();
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Article thisArticle = mArticles.get(position);
            holder.titleView.setText(thisArticle.getTitle());
            Date publishedDate = parsePublishedDate(position);
            if (!publishedDate.before(START_OF_EPOCH.getTime())) {

                holder.subtitleView.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + "<br/>" + " by "
                                + thisArticle.getAuthor()));
            } else {
                holder.subtitleView.setText(Html.fromHtml(
                        outputFormat.format(publishedDate)
                                + "<br/>" + " by "
                                + thisArticle.getAuthor()));
            }
            Picasso.get().load(thisArticle.getThumb()).into(holder.thumbnailView);
            //holder.thumbnailView.setAspectRatio(Float.parseFloat(thisArticle.getAspect_ratio()));
        }

        @Override
        public int getItemCount() {
            return mArticles.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }
    }
