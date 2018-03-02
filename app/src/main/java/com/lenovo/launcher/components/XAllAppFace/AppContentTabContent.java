package com.lenovo.launcher.components.XAllAppFace;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;

public class AppContentTabContent extends BaseDrawableGroup {
    public AppContentTabContent(XContext context) {
		super(context);
	}

	//private TaskKillView mTaskKillView;
    private AppContentView mAppContentView;

    private int mTaskMarginH;
    private int mTaskHeight;
    private boolean mHasTaskBar = false;

//    public void setup(Context context, AppContentView appsView, TaskKillView taskView) {
//        Resources res = context.getResources();
//        mTaskMarginH = res.getDimensionPixelSize(R.dimen.task_kill_children_margin);
//        mTaskHeight = res.getDimensionPixelSize(R.dimen.task_kill_bar_height);
//
//        addItem(appsView);
//        addItem(taskView);
//        mAppContentView = appsView;
//        mTaskKillView = taskView;
//        initKillBar();
//    }

    @Override
    public void resize(RectF rect) {
        super.resize(rect);

        float app_bottom = mHasTaskBar ? getHeight() - mTaskHeight - mTaskMarginH : getHeight();

        mAppContentView.resize(new RectF(0, 0, getWidth(), app_bottom));
//        if (mHasTaskBar) {
//            mTaskKillView.resize(new RectF(mTaskMarginH, app_bottom, getWidth() - mTaskMarginH,
//                    getHeight() - mTaskMarginH));
//        }
    }

    private void initKillBar() {
//        mTaskKillView.setOnClickListener(TaskKillView.FLAG_BTN_CLEAR, new OnClickListener() {
//            @Override
//            public void onClick(DrawableItem item) {
//                mTaskKillView.recordCurrentMem();
//                mAppContentView.killRunningTask();
//                mTaskKillView.updateKillBarDelayed(true);
//            }
//        });
//        mTaskKillView.setOnClickListener(TaskKillView.FLAG_BTN_UPDATE, new OnClickListener() {
//            @Override
//            public void onClick(DrawableItem item) {
//                mAppContentView.updateRunningTask();
//                mTaskKillView.updateKillBar(false);
//            }
//        });

    }

    public void setTaskVisibility(String tab, boolean visible) {
        mAppContentView.setCurrentTab(tab);

//        mTaskKillView.setVisibility(visible);
        mHasTaskBar = visible;
        resize(this.localRect);
    }

}
