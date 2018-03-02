package com.lenovo.launcher.components.XAllAppFace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem.OnClickListener;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher.components.XAllAppFace.utilities.CubicInterpolator;
import com.lenovo.launcher.components.XAllAppFace.utilities.QuartInterpolator;
import com.lenovo.launcher2.commoninterface.Alarm;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.FolderInfo.FolderListener;
import com.lenovo.launcher2.commoninterface.IconCache;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.OnAlarmListener;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.customizer.Debug.R5;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

public class XFolder extends BaseDrawableGroup implements OnClickListener, FolderListener, XScrollDropTarget, XDragSource {
    
    public interface OnXFolderStateLinstener {
        void onFolderOpen();
        void onFolderClose();
    }

    private static final String TAG = "XFolder";
    private XFolderIcon mFolderIcon;
    protected FolderInfo mInfo;
    private final IconCache mIconCache;
    
    static final int STATE_NONE = -1;
    static final int STATE_SMALL = 0;
    static final int STATE_ANIMATING = 1;
    static final int STATE_OPEN = 2;
    private boolean mSuppressOnAdd = false;
    private int mState = STATE_NONE;
    private RectF mIconRect = new RectF();

    private int mFolderCellWidth = -1;
    private int mFolderCellHeight = -1;
    private int mMaxCountX;
    private int mMaxCountY;
    private int mMaxCount;
    private int mMaxNumItems;
    private int mExpandDuration;
    private static String sDefaultFolderName;
    private static String sHintText;
    private ArrayList<DrawableItem> mItemsInReadingOrder = new ArrayList<DrawableItem>();

    private XTextArea mFolderName;
    private XPagedView mContent;
    private DrawableItem mKuang;
    private XPagedViewIndicator xPageIndicator;
    
    private XDragController mDragController;
    private ShortcutInfo mCurrentDragInfo;
//    private ShortcutInfo mSavedDragInfo;
    private int[] mSavedCell = new int[3];
//    private boolean mDropOnSelf;
    private DrawableItem mCurrentDragView;
    private int[] mEmptyCell = new int[3];
    private int[] mTargetCell = new int[3];
//    private int[] mPreviousTargetCell = new int[3];
    private boolean mReorderDataSet = false;
    private boolean mEnterKuang = false;
    
    private OnXFolderStateLinstener mStateLinstener;

//    boolean mItemsInvalidated = false;
    
    /*** RK_ID: XFOLDER_EDIT.  AUT: zhaoxy . DATE: 2013-01-30 . START***/
    private Dialog mDialog;

    
    
//    int mEmptyNum = 0;
    private int mFolderNameMaxLen;
    String selectedPackageName;
    private int mContentPadding = 0;
    /*** RK_ID: XFOLDER_EDIT.  AUT: zhaoxy . DATE: 2013-01-30 . END***/
    private boolean mnoclose = false;
    
    private XLauncher mLauncher;
    private String mRenameBuffer; 

    private boolean mDragInProgress = false;
    private boolean mDeleteFolderOnDropCompleted = false;
    private boolean mSuppressFolderDeletion = false;
    private Resources resources;
    private int FOLDER_CELL_Y_OFFSET = 0;
    private int FOLDER_CELL_X_OFFSET = 0;
    private int folder_name_height=0;
    private int folder_indicate_offset=0;
    private float folder_scale = 1;
    private float folder_icons_scale = 1;
    private int folder_kuang_padding_h = 0;
    private int folder_kuang_padding_v = 0;

    public XFolder(XContext context,XLauncher launcher) {
        super(context);
        mLauncher = launcher;
        mXContext = context;
        mIconCache = ((LauncherApplication) context.getContext().getApplicationContext()).getIconCache();
        resources = context.getResources();
        mFolderCellWidth = resources.getDimensionPixelSize(R.dimen.folder_cell_width);
        mFolderCellHeight = resources.getDimensionPixelSize(R.dimen.folder_cell_height);
        mContentPadding = resources.getDimensionPixelSize(R.dimen.folder_content_padding);
        FOLDER_CELL_Y_OFFSET = resources.getDimensionPixelSize(R.dimen.folder_cell_y_offset);
        FOLDER_CELL_X_OFFSET = resources.getDimensionPixelSize(R.dimen.folder_cell_x_offset);
        folder_scale = com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.
                formatInt2Float(resources.getDimensionPixelSize(R.dimen.folder_scale));
        folder_icons_scale = com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.
                formatInt2Float(resources.getDimensionPixelSize(R.dimen.folder_icons_scale));
        Log.d(TAG, "folder_icons_scale = " + folder_icons_scale);
        folder_kuang_padding_h = resources.getDimensionPixelSize(R.dimen.folder_kuang_padding_h);
        folder_kuang_padding_v = resources.getDimensionPixelSize(R.dimen.folder_kuang_padding_v);
        mMaxCountX = resources.getInteger(R.integer.folder_max_count_x);
        mMaxCountY = resources.getInteger(R.integer.folder_max_count_y);
        mMaxCount = resources.getInteger(R.integer.folder_max_count);
        folder_name_height = resources.getDimensionPixelSize(R.dimen.folder_name_height);
        folder_indicate_offset = resources.getDimensionPixelSize(R.dimen.folder_indicate_offset);
        mMaxNumItems = Integer.MAX_VALUE;//resources.getInteger(R.integer.folder_max_num_items);
//        mMaxCountX = 3;//XLauncherModel.getCellCountX();
//        mMaxCountY = 4;//XLauncherModel.getCellCountY();
        /*** RK_ID: XFOLDER_EDIT.  AUT: zhaoxy . DATE: 2013-01-30 . START***/
        mFolderNameMaxLen = resources.getInteger(R.integer.folder_name_max_length);
        /*** RK_ID: XFOLDER_EDIT.  AUT: zhaoxy . DATE: 2013-01-30 . END***/
        mExpandDuration = resources.getInteger(R.integer.config_folderAnimDuration);

        if (sDefaultFolderName == null) {
            sDefaultFolderName = resources.getString(R.string.folder_editor_hint);
        }
        if (sHintText == null) {
            sHintText = resources.getString(R.string.folder_hint_text);
        }

        final Drawable kuang = resources.getDrawable(R.drawable.folder_kuang);
        final Drawable title_bg = resources.getDrawable(R.drawable.folder_title_bg);

        mPaddingLeft = mPaddingRight = mContentPadding;
        int mFolderNamePadding = resources.getDimensionPixelSize(R.dimen.folder_name_padding);
        int mFolderContentMargin = resources.getDimensionPixelSize(R.dimen.folder_content_margin);
        mFolderNamePadding = Math.max(5, mFolderNamePadding);

        final float cntWidth = mMaxCountX * mFolderCellWidth;
        final float cntHeight = mMaxCountY * mFolderCellHeight;
        
        mFolderName = new XTextArea(context, sDefaultFolderName, new RectF(mPaddingLeft, mFolderNamePadding, mPaddingLeft + cntWidth, mFolderNamePadding+resources.getDimension(R.dimen.folder_title_height)));
        mFolderName.setTextAlign(Align.CENTER);
        mFolderName.setTextSize(resources.getDimensionPixelSize(R.dimen.user_folder_Editname_textsize));
        mFolderName.setTextColor(0xffffffff);
        RectF mRect = mFolderName.localRect;
//        mRect.right = mRect.left + mFolderName.getTextWidth() + 6;
        mRect.right = mRect.left + cntWidth * .86f;
        mFolderName.resize(mRect);
        mFolderName.setBackgroundDrawable(title_bg);
        mFolderName.setEllipsize(TextUtils.TruncateAt.END);
        mFolderName.enableCache();
        mFolderName.setOnClickListener(this);
        
        final float cntLeft = mPaddingLeft;
        final float cntTop = mFolderName.localRect.bottom + mFolderContentMargin;
        mContent = new XPagedView(context, new RectF(cntLeft, cntTop, cntLeft + cntWidth, cntTop + cntHeight));
        mContent.setLoop(false);
        mContent.setup(0, mMaxCountX, mMaxCountY);

        mContent.setEnableEffect(false);
        mContent.setScrollBackEnable(true);
        
        xPageIndicator = new XPagedViewIndicator(getXContext(), new RectF(0, 0, 0, 0));
        xPageIndicator.setPagedView(mContent);
        mContent.addPageSwitchListener(xPageIndicator);
        
        mKuang = new DrawableItem(getXContext());
        mKuang.resize(new RectF(mContent.getRelativeX() - folder_kuang_padding_h, mContent.getRelativeY(), mContent.getRelativeX() + cntWidth + folder_kuang_padding_h, mContent.localRect.bottom + folder_kuang_padding_v));
        mKuang.setBackgroundDrawable(kuang);
        mKuang.setVisibility(false);

        this.addItem(mKuang);
        this.addItem(mFolderName);
        this.addItem(mContent);
        this.addItem(xPageIndicator);
        
        //modify for quick drag mode by sunzq3, begin;
        xPageIndicator.resize(new RectF(0, 0, cntWidth, xPageIndicator.getHomePointHeight()));
        //modify for quick drag mode by sunzq3, end;
        //        xPageIndicator.setRelativeY(mContent.getRelativeY() + cntHeight);
        resize(new RectF(0, 0, mPaddingLeft + cntWidth + mPaddingRight, mContent.localRect.bottom + xPageIndicator.getHeight()));
    }

    /**
     * @return the FolderInfo object associated with this folder
     */
    public FolderInfo getInfo() {
        return mInfo;
    }
    
    public void setDragController(XDragController mDragController) {
        this.mDragController = mDragController;
    }
    
    @Override
    public void resize(RectF rect) {
        super.resize(rect);

//        Rect padding = new Rect();
//        getBackgroundDrawable().getPadding(padding);

//        mTitleBar.resize(new RectF(0, 0, getWidth(), mTitleBarHeight));
//        mFolderEditor.setRelativeX(rect.width() - padding.right - mFolderEditor.getWidth());
//        float mNamePaddingRight = getWidth() - mFolderEditor.getRelativeX();
//        mFolderName.resize(new RectF(mNamePaddingRight, mFolderName.getRelativeY(), getWidth() - mNamePaddingRight, mFolderName.localRect.bottom));

        //X
        int mFolderNamePadding = resources.getDimensionPixelSize(R.dimen.folder_name_padding);
        int mFolderContentMargin = resources.getDimensionPixelSize(R.dimen.folder_content_margin);
//        final float cntHeight = mMaxCountY * mFolderCellHeight+folder_indicate_offset;//bug 17930
//        int xpageindicator_offset  = resources.getDimensionPixelSize(R.dimen.folder_cell_height);
        mFolderName.setRelativeY(mFolderNamePadding);
        mFolderName.setRelativeX(rect.centerX() - mFolderName.getWidth() * .5f);
        mContent.setRelativeX(rect.centerX() - mContent.getWidth() * .5f);
        mContent.setRelativeY(mFolderName.localRect.bottom + mFolderContentMargin);
        xPageIndicator.setRelativeX(rect.centerX() - xPageIndicator.getWidth() * .5f);
        xPageIndicator.setRelativeY(mContent.localRect.bottom + folder_indicate_offset);
        resizeKuangPoistion();
        final boolean flag = mLauncher.isCurrentWindowFullScreen();
        if (!SettingsValue.hasExtraTopMargin()) {
            if (flag) {
                mIconRect.offset(0, mLauncher.getStatusBarHeight());
            } else {
                mIconRect.offset(0, -mLauncher.getStatusBarHeight());
            }
        }
        //Y
//        float space = rect.height() + mFolderName.getRelativeY() - mKuang.localRect.bottom;
//        float deltaY = space * .4f - mFolderName.getRelativeY();
//        mFolderName.offsetRelative(0, deltaY);
//        mContent.offsetRelative(0, deltaY);
//        xPageIndicator.offsetRelative(0, deltaY);
//        mKuang.offsetRelative(0, deltaY);

//        mFolderName.setRelativeX(rect.centerX() - mFolderName.getWidth() * .5f);
//        mContent.setRelativeX(rect.centerX() - mContent.getWidth() * .5f);
//        mKuang.resize(new RectF(mContent.getRelativeX() - mContentPadding / 2f, mContent.getRelativeY() - mContentPadding, mContent.localRect.right + mContentPadding / 2f, xPageIndicator.localRect.bottom + mContentPadding / 2));
//        xPageIndicator.setRelativeX(rect.centerX() - xPageIndicator.getWidth() * .5f);
        resizeRenameDialogPoistion();
    }
    
    void setFolderIcon(XFolderIcon icon) {
        mFolderIcon = icon;
    }
    public XFolderIcon getXFolderIcon()
    {
    	return mFolderIcon;
    }
    void bind(FolderInfo info) {
        mInfo = info;
        ArrayList<ShortcutInfo> children = info.contents;
        ArrayList<ShortcutInfo> overflow = new ArrayList<ShortcutInfo>();
        /*** fixbug 18783  . AUT: zhaoxy . DATE: 2013-07-23. START***/
        if (!children.isEmpty()) {
            Collections.sort(children, new Comparator<ShortcutInfo>() {
                @Override
                public int compare(ShortcutInfo lhs, ShortcutInfo rhs) {
                    int l = -1;
                    int r = -1;
                    if (lhs != null) {
                        l = mContent.getCellIndex(lhs.screen, lhs.cellX, lhs.cellY);
                    }
                    if (rhs != null) {
                        r = mContent.getCellIndex(rhs.screen, rhs.cellX, rhs.cellY);
                    }
                    return l - r;
                }
            });
        }
        /*** fixbug 18783  . AUT: zhaoxy . DATE: 2013-07-23. END***/
        int count = 0;
        for (int i = 0; i < children.size(); i++) {
            ShortcutInfo child = (ShortcutInfo) children.get(i);
            if (!createAndAddShortcut(child)) {
                overflow.add(child);
            } else {
                count++;
            }
        }
        
        // We rearrange the items in case there are any empty gaps
        //setupContentForNumItems(count);

        // If our folder has too many items we prune them from the list. This is an issue 
        // when upgrading from the old Folders implementation which could contain an unlimited
        // number of items.
        for (ShortcutInfo item: overflow) {
            mInfo.remove(item);
            XLauncherModel.deleteItemFromDatabase(getXContext().getContext(), item);
        }
        
        if (updateLayout()) {
            updateItemLocationsInDatabase();
        }
        updateIndicatorVisible();
        updateKuangBottom(true, false);
        
//        mItemsInvalidated = true;
        mContent.getSourceItemsByOrder(mItemsInReadingOrder, XFolderIcon.NUM_ITEMS_IN_PREVIEW);
        
        mInfo.addListener(this);

        updateFolderEffect(mInfo.opened);

        if (mInfo.title == null || "".contentEquals(mInfo.title)) {
            mFolderName.setText(sDefaultFolderName);
        } else {
            mFolderName.setText(mInfo.title.toString());
        }

    }
    public boolean getisclose()
    {
    	return mnoclose;
    }
    public void setisclose(boolean close)
    {
    	mnoclose = close;
    }
    
    private void clearNewBg(Context context, ShortcutInfo info){
        if (info.mNewAdd != 1)
        {
            return;
        }
        
        info.mNewAdd = 0;
        ((XLauncher)mXContext.getContext()).clearAndShowNewBg(info.intent.getComponent().flattenToString());     
    }
    
    protected boolean createAndAddShortcut(final ShortcutInfo itemInfo) {

        /*** fixbug 9779  . AUT: zhaoxy . DATE: 2013-03-27. START***/
        //tentative schedule
        final XDragLayer dragLayer = ((XLauncherView) getXContext()).getDragLayer();
//        itemInfo.container = mInfo.id;
        final XShortcutIconView iconView = new XShortcutIconView(itemInfo, new RectF(0, 0, mFolderCellWidth - 4, mFolderCellHeight), getXContext());
        /*** fixbug 9779  . AUT: zhaoxy . DATE: 2013-03-27. END***/
        // We need to check here to verify that the given item's location isn't already occupied
        // by another item. If it is, we need to find the next available slot and assign
        // it that position. This is an issue when upgrading from the old Folders implementation
        // which could contain an unlimited number of items.
        int index = mContent.getCellIndex(itemInfo.screen, itemInfo.cellX, itemInfo.cellY);
        XCell old = (XCell) mContent.getChildAt(index);
        if ((old != null && old.getDrawingTarget() != null) || itemInfo.screen < 0 || itemInfo.screen >= mContent.getPageCount() || itemInfo.cellX < 0 || itemInfo.cellY < 0
                || itemInfo.cellX >= mContent.getCellCountX() || itemInfo.cellY >= mContent.getCellCountY()) {
            if (!findAndSetEmptyCells(itemInfo)) {
                return false;
            }
        }
        OnClickListener clicker = new OnClickListener() {

            @Override
            public void onClick(DrawableItem view) {
                /* RK_ID: RK_SHORTCUT . AUT: liuli1 . DATE: 2012-02-28 . START */
                clearNewBg(mXContext.getContext(), iconView.getLocalInfo());

                Context c = view.getXContext().getContext();
                if (c instanceof XLauncher) {
                    XLauncher launcher = (XLauncher) c;
                    if (launcher.getWorkspace() != null
                            && launcher.getWorkspace().filterLeLauncherShortcut(launcher,
                                    itemInfo.intent)) {
                        launcher.closeFolder();
                        return;
                    }
                }
                mnoclose = itemInfo.intent.getBooleanExtra("LENOVO_EX_SHORTCUT", false);
                /* RK_ID: RK_SHORTCUT . AUT: liuli1 . DATE: 2012-02-28 . END */
                int[] pos = new int[2];
                dragLayer.getLocationInDragLayer(view, pos);
                final Intent intent = itemInfo.intent;
                //test by liuli 2013-08-06
                int windowTop = ((XLauncherView) getXContext()).getWindowTop();
                pos[1] += windowTop;
                intent.setSourceBounds(new Rect(pos[0], pos[1], pos[0] + (int) view.getWidth(),
                        pos[1] + (int) view.getHeight()));
                intent.putExtra("STATE","FOLDER");
            	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. S***/
                //((XLauncher)getXContext().getContext()).startActivitySafely(itemInfo.intent, "");
            	if( (itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_COMMEND_SHORTCUT) &&
            		(itemInfo.uri != null )){
            		Uri uri = Uri.parse(itemInfo.uri);
            		Intent intentView = new Intent(Intent.ACTION_VIEW, uri);
            		((XLauncher) getXContext().getContext()).startActivitySafely(intentView, null);
//            		((XLauncher) getXContext().getContext()).getModel().getUsageStatsMonitor().add(intentView);
            	}else{
                    ((XLauncher) getXContext().getContext()).startActivitySafely(itemInfo.intent, null);
//                    ((XLauncher) getXContext().getContext()).getModel().getUsageStatsMonitor().add(itemInfo.intent);
            	}
            	/*RK_ID:RK_SD_APPS zhangdxa 2013-4-15. E***/
            }
            
        };
        
        iconView.getTextView().setOnClickListener(clicker);
        iconView.getIconDrawable().setOnClickListener(clicker);
        
        iconView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(DrawableItem item) {
                mKuang.setVisibility(true);
                mFolderName.setBackgroundVisible(false);
                                
                clearNewBg(mXContext.getContext(), iconView.getLocalInfo());
                                
                mCurrentDragInfo = iconView.getTag();
//                mSavedDragInfo = new ShortcutInfo(mCurrentDragInfo);
                mCurrentDragView = iconView;
                mEmptyCell[0] = mCurrentDragInfo.cellX;
                mEmptyCell[1] = mCurrentDragInfo.cellY;
                mEmptyCell[2] = mCurrentDragInfo.screen;
                
                mSavedCell[0] = mCurrentDragInfo.cellX;
                mSavedCell[1] = mCurrentDragInfo.cellY;
                mSavedCell[2] = mCurrentDragInfo.screen;
                mReorderDataSet = true;
                mCurrentDragView.setVisibility(false);
                ((XLauncher) getXContext().getContext()).getWorkspace().beginDragShared(iconView, XFolder.this, false);
                mContent.removePageItem(mCurrentDragInfo, false);
                if (DEBUG_REORDER)R5.echo("onLongClick itemInfo = " + itemInfo.toString());
                mInfo.remove(mCurrentDragInfo);

                iconView.getIconDrawable().setAlpha(1);
                
                ((XLauncher) getXContext().getContext()).setLauncherWindowStatus(true);
                
                iconView.getIconDrawable().setAlpha(.6f);
                updateKuangBottom(false, true);
                mDragInProgress = true;
                return true;
            }
        });
        XPagedViewItem itemToAdd = new XPagedViewItem(getXContext(), iconView, itemInfo);
        mContent.addPagedViewItem(itemToAdd);
        iconView.setInFolder(true);
        updateIndicatorVisible();
        updateKuangBottom(false, false);
        
        final int iconsize = SettingsValue.getIconSizeValueNew(getXContext()
                .getContext());
        XIconDrawable iconDrawable = iconView.getIconDrawable();
        com.lenovo.launcher.components.XAllAppFace.utilities.Utilities.iconSizeChange(iconDrawable,iconsize);
        if (mState != STATE_ANIMATING) {
            if (mState == STATE_OPEN) {
                updateContentExtraEffect(1, 0);
            } else {
                updateContentExtraEffect(0, 1);
            }
        }
        
        return true;
    }
    
    public Bitmap checkCommendShortcut(ShortcutInfo item) {
        if (!XLauncherModel.sItemsIdMap.containsKey(item.id)) {
            return null;
        }

        PackageManager pm = getXContext().getContext().getPackageManager();
        ComponentName componentName = item.intent.getComponent();
        Bitmap res = null;

        try {
            ActivityInfo ai = pm.getActivityInfo(componentName, 0);
            item.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;

            res = XLauncherModel.findViewIconFromCache(item);
            if (res == null && ai != null) {
                res = Utilities.createIconBitmap(mIconCache.getFullResIcon(ai, pm), getXContext().getContext(),
                        componentName.getPackageName());
                XLauncherModel.addViewIconCache(item.id, res);
            }

            XLauncherModel.updateItemInDatabase(getXContext().getContext(), item);
        } catch (Exception e) {
            android.util.Log.e(TAG, e.toString());
        }

        return  res;
    }
    
    public ArrayList<DrawableItem> getItemsInReadingOrder() {
//        if (mItemsInvalidated) {
//            mContent.getSourceItems(mItemsInReadingOrder);
//            mItemsInvalidated = false;
//        }
        return mItemsInReadingOrder;
    }
    
    @Override
    public String toString() {
        StringBuffer out = new StringBuffer("XFolder = {\n");
        out.append("    title = ").append(mInfo.title);
        out.append("\n    rect = ").append(localRect.toShortString());
        out.append("\n}");
        return out.toString();
    }
    
    private ValueAnimator inAnim = null;
    
    public void animateOpen() {
        this.setTouchable(false);
        if (inAnim != null) {
            getXContext().getRenderer().ejectAnimation(inAnim);
        }
        
        updatePositionAndSizeAsIcon();

        inAnim = ValueAnimator.ofFloat(0f, 1f);
        inAnim.setDuration(mExpandDuration);
        inAnim.setInterpolator(new CubicInterpolator(CubicInterpolator.OUT));
        inAnim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) (animation.getAnimatedValue());
//                updatePositionAndSizeAsIcon();
                updateFolderEffect(value, .6f);
            }
        });
        inAnim.addListener(new AnimatorListener() {
            
            @Override
            public void onAnimationStart(Animator animation) {
                if (mStateLinstener != null) {
                    mStateLinstener.onFolderOpen();
                }
                mFolderIcon.disableCache();
                mFolderIcon.getTextDrawable().enableCache();
                mFolderIcon.invalidate();
                mState = STATE_ANIMATING;
                mEnterKuang = false;
            }
            
            @Override
            public void onAnimationRepeat(Animator animation) {
                
            }
            
            @Override
            public void onAnimationEnd(Animator animation) {
                mFolderIcon.enableCache();
                resetContentExtraEffect();
                setTouchable(true);
                mState = STATE_OPEN;
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {
                
            }
        });
        getXContext().getRenderer().injectAnimation(inAnim, false);
        
    }

    public void animateClosed(final float startX, final float startY) {
        setTouchable(false);
        // fix bug 18205
        mContent.setCurrentPage(0);
        /*** fixbug LELAUNCHER-349. AUT: zhaoxy. DATE: 2013-10-17 . START***/
        mContent.resetOffset();
        /*** fixbug LELAUNCHER-349. AUT: zhaoxy. DATE: 2013-10-17 . END***/
        mDragController.removeDropTarget(this);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        mRenameBuffer = null;
        if (inAnim != null) {
            getXContext().getRenderer().ejectAnimation(inAnim);
        }
        
        updatePositionAndSizeAsIcon();
        
        inAnim = ValueAnimator.ofFloat(1f, 0f);
        inAnim.setDuration(mExpandDuration);
        inAnim.setStartDelay(150);
        inAnim.setInterpolator(new QuartInterpolator(QuartInterpolator.OUT));
        inAnim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) (animation.getAnimatedValue());
//                updatePositionAndSizeAsIcon();
                updateFolderEffect(value, .6f);
            }
        });
        inAnim.addListener(new AnimatorListener() {
            
            @Override
            public void onAnimationStart(Animator animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animator animation) {
                
            }
            
            @Override
            public void onAnimationEnd(Animator animation) {
                mEnterKuang = false;
                mKuang.getMatrix().reset();
                mKuang.setVisibility(false);
                mFolderName.setBackgroundVisible(true);
                setTouchable(true);
                mFolderIcon.enableCache();
                mState = STATE_SMALL;
                onCloseComplete();
                onTouchCancel( null );
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {
                
            }
        });
        getXContext().getRenderer().injectAnimation(inAnim, false);
        /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . START***/
        mKuang.getMatrix().reset();
        mState = STATE_ANIMATING;
        resetContentExtraEffect();
        mFolderIcon.disableCache();
        mFolderIcon.getTextDrawable().enableCache();
        /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . END***/
    }

    public void closeNow() {
        // fix bug 18205
        mContent.setCurrentPage(0);
        /*** fixbug LELAUNCHER-349. AUT: zhaoxy. DATE: 2013-10-17 . START ***/
        mContent.resetOffset();
        /*** fixbug LELAUNCHER-349. AUT: zhaoxy. DATE: 2013-10-17 . END ***/
        mDragController.removeDropTarget(this);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        mRenameBuffer = null;
        if (inAnim != null) {
            getXContext().getRenderer().ejectAnimation(inAnim);
        }

        updatePositionAndSizeAsIcon();

        updateFolderEffect(0, .6f);

        /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . START ***/
        mEnterKuang = false;
        mKuang.getMatrix().reset();
        mKuang.setVisibility(false);
        mState = STATE_SMALL;
        resetContentExtraEffect();
        mFolderIcon.getTextDrawable().enableCache();
        mFolderName.setBackgroundVisible(true);
        mFolderIcon.enableCache();
        onTouchCancel(null);
        setTouchable(true);
        onCloseComplete();
        /*** RK_ID: HIGH_QUALITY_EFFECTS. AUT: zhaoxy. DATE: 2013-10-16 . END ***/
    }

    public void stopAnim() {
        if (inAnim != null && inAnim.isRunning()) {
            inAnim.cancel();
        }
    }

    protected void updateFolderEffect(boolean opened) {
        if (opened) {
            updateFolderEffect(1f, .2f);
        } else {
            updateFolderEffect(0f, .1f);
        }
    }

    protected void updateFolderEffect(float input, float alphaInOut) {
        float centerXfrom = getRelativeX() + mContent.getRelativeX() + mContent.getCellWidth() * 1.5f - FOLDER_CELL_X_OFFSET;
        float centerYfrom = getRelativeY() + mContent.getRelativeY() + mContent.getCellHeight() * 1.5f - FOLDER_CELL_Y_OFFSET;
        final float centerXto = mIconRect.centerX();
        final float centerYto = mIconRect.centerY();
        Matrix m = getMatrix();
        m.reset();
        float scale = folder_scale + (1 - folder_scale) * input;
        float deltaX = (centerXto - centerXfrom) * (1f - input);
        float deltaY = (centerYto - centerYfrom) * (1f - input);
        m.postScale(scale, scale, centerXfrom, centerYfrom);
        m.postTranslate(deltaX, deltaY);

        float alpha = 0;
        if (input > alphaInOut) {
            alpha = (input - alphaInOut) / (1 - alphaInOut);
        }

        if (input > .2f) {
            mFolderIcon.getTextDrawable().setAlpha(0);
        } else {
            mFolderIcon.getTextDrawable().setAlpha(1 - input / .2f);
        }
        mFolderName.setAlpha(alpha);
        mKuang.setAlpha(alpha);
        xPageIndicator.setAlpha(alpha);
        updateMatrix(m);
        updateContentExtraEffect(alpha, 1 - input);
    }

    private void updateContentExtraEffect(float alpha, float input) {
        int count = mContent.getCellCountX() * mContent.getCellCountY();
        float x = 0;
        float y = 0;
        XCell centerCell = (XCell) mContent.getChildAt(mContent.getCurrentPage(), 1, 1);
        if (centerCell != null) {
            x = centerCell.getRelativeX();
            y = centerCell.getRelativeY();
        }
        for (int i = 0; i < count; i++) {
            final XCell cell = (XCell) mContent.getChildAt(mContent.getCurrentPage(), i % mContent.getCellCountX(), i / mContent.getCellCountX());
            /* RK_ID: RK_PAD . AUT: yumina . DATE: 2013-07-11 . start */
//            int phoneindex = SettingsValue.getCurrentMachineType(getXContext().getContext());
//            boolean flag = false;
//            if(phoneindex == -1){
//                flag = i<mMaxCount;
//            }else{
//                flag = i < mMaxCount+3 && (i+1)%mContent.getCellCountX() != 0 ;
//            }
            /* RK_ID: RK_PAD . AUT: yumina . DATE: 2013-07-11 . end */

            if (cell != null) {
                if (i < mMaxCount) {
                    DrawableItem item = (DrawableItem) cell.getDrawingTarget();
                    if (item != null) {
                        ((XShortcutIconView) item).getTextView().setAlpha(alpha);
                    }
                    if (input >= 0) {
                        Matrix m = cell.getExtraEffectMatrix();
                        m.reset();
                        float deltaX = (x - cell.getRelativeX()) * folder_icons_scale * input;
                        float deltaY = (y - cell.getRelativeY()) * folder_icons_scale * input;
                        m.postTranslate(deltaX, deltaY);
                        cell.setExtraEffectMatrix(m);
                    }
                } else {
                    DrawableItem item = cell.getDrawingTarget();
                    if (item != null) {
                        item.setExtraAlphaEnable(true);
                        item.setExtraAlpha(alpha);
                    }
                }
            }
        }
    }

    private void resetContentExtraEffect() {
        int count = mContent.getCellCountX() * mContent.getCellCountY();
        for (int i = 0; i < count; i++) {
            final XCell cell = (XCell) mContent.getChildAt(mContent.getCurrentPage(), i % mContent.getCellCountX(), i / mContent.getCellCountX());
            if (cell != null) {
                cell.getExtraEffectMatrix().reset();
            }
        }
    }
    
    protected boolean isAnimating() {
        return mState == STATE_ANIMATING;
    }

    protected void updatePositionAndSizeAsIcon() {
        DrawableItem descendant = mFolderIcon.getIconDrawable();
        Matrix mGlobalMatrix = new Matrix();
        XWorkspace workspace = mLauncher.getWorkspace();
        mIconRect.set(descendant.localRect);
        mIconRect.offsetTo(0, 0);
        mGlobalMatrix.set(descendant.getInvertMatrix());
        mGlobalMatrix.postTranslate(-descendant.getRelativeX(), -descendant.getRelativeY());
        mGlobalMatrix.preConcat(workspace.getMatrix());
        mGlobalMatrix.invert(mGlobalMatrix);
        float[] values = new float[9];
        mGlobalMatrix.getValues(values);
        for (int i = 0; i < values.length; i++) {
            if (values[i] == -0.0f) {
                values[i] = 0.0f;
            }
        }
        mGlobalMatrix.setValues(values);
        mGlobalMatrix.mapRect(mIconRect);
    }
    
    private boolean onCloseComplete() {
        XDragLayer parent = (XDragLayer) getParent();
        // fix bug 169793 by zhanggx1
        if (parent == null) {
            return false;
        }
        parent.removeItem(this, false);
        reuse();
        invalidate();
        if (mFolderIcon.neetInvalidateDelay) {
            mFolderIcon.invalidate();
            mFolderIcon.neetInvalidateDelay = false;
        }
        //mDragController.removeDropTarget(this);

//        if (mRearrangeOnClose) {
//            setupContentForNumItems(getItemCount());
//            mRearrangeOnClose = false;
//        }
        if (mContent.getPageViewItemCount() <= 1) {
            Log.i(TAG, "mDragInProgress ===" + mDragInProgress + "    mSuppressFolderDeletion ===" + mSuppressFolderDeletion);
            if (!mDragInProgress && !mSuppressFolderDeletion) {
                replaceFolderWithFinalItem();
            } else if (mDragInProgress) {
                mDeleteFolderOnDropCompleted = true;
            }
        }
        mSuppressFolderDeletion = false;
        boolean single = SettingsValue.getSingleLayerValue(getXContext().getContext());
        if (single) {
            deleteNonContentFolder();
        }
        if (mStateLinstener != null) {
            getXContext().post(new Runnable() {
                @Override
                public void run() {
                    mStateLinstener.onFolderClose();
                }
            });
        }
        return true;
    }
    
    public void setStateLinstener(OnXFolderStateLinstener mStateLinstener) {
        this.mStateLinstener = mStateLinstener;
    }
    
    public boolean findAndSetEmptyCells(ShortcutInfo item) {
        int[] emptyCell = new int[2];
        if (mContent.getPageCount() == 0)
        {
            mContent.addNewScreen();
        }

        if (item.screen < 0)
        {
            item.screen = mContent.getPageCount() - 1;
        }

        for (int s = item.screen; s < mContent.getPageCount(); s++) {
            item.screen = s;
            if (mContent.findCellForSpan(emptyCell, item.spanX, item.spanY, item.screen, null)) {
                item.cellX = emptyCell[0];
                item.cellY = emptyCell[1];
                return true;
            }
        }

        mContent.addNewScreen();
        item.screen = mContent.getPageCount() - 1;
        if (mContent.findCellForSpan(emptyCell, item.spanX, item.spanY, item.screen, null)) {
            item.cellX = emptyCell[0];
            item.cellY = emptyCell[1];
            return true;
        } else return false;
    }
    
    public XPagedViewItem findPageItemAt(int screen, int cellX, int cellY) {
    	return mContent.findPageItemAt(screen, cellX, cellY);
    }

    public void refreshIconCache(IconCache iconCache, boolean bitmap) {
        mContent.refreshIconCache(iconCache, bitmap);
    }
    
    static public void reloadHint(Context context) {
        sHintText = context.getString(R.string.folder_hint_text);
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        if (y < mFolderName.getRelativeY() - 20 || y > mKuang.localRect.bottom) {
            ((XLauncher) getXContext().getContext()).closeFolder();
            return true;
        }else if (clickOutSideofThisPage(e.getX(), y)) {
            // fix bug 18201
            ((XLauncher) getXContext().getContext()).closeFolder();
            return true;
        }else if(x<mKuang.localRect.left||x>mKuang.localRect.right){
        	//18547
        	 ((XLauncher) getXContext().getContext()).closeFolder();
             return true;
        }
        return super.onSingleTapUp(e);
    }

    private boolean clickOutSideofThisPage(float x, float y) {
        boolean resVal = false;

        int count = mContent.getPageViewItemCount();
        int currentPage = mContent.getCurrentPage();

        int pageCount = mContent.getCellCountX() * mContent.getCellCountY();
        int left = count - currentPage * pageCount;

        if (left <= 0 || left >= pageCount) {
            resVal = y < mFolderName.getRelativeY() - 20 || y > mKuang.localRect.bottom;
        } else {
            DrawableItem item = mContent.getChildAt(left - 1);
            if (item instanceof XCell) {
                XCell cell = (XCell) item;
                float bottom = cell.localRect.bottom + mContent.getRelativeY();
                float right = cell.localRect.right + mContent.getWidth() + mContent.getRelativeX();
                float top = cell.localRect.top + mContent.getRelativeY();

                Log.i(TAG, "bottom====" + bottom + "   right ===" + right);
                Log.i(TAG, "y====" + y + "   x ===" + x);
                resVal = (y > bottom || (x > right && y > top));
            }
        }

        return resVal;
    }

    
    /*** RK_ID: XFOLDER_EDIT.  AUT: zhaoxy . DATE: 2013-01-30 . START***/
    @Override
    public void onClick(DrawableItem item) {
                if (mDialog == null || !mDialog.isShowing()) {
                    mFolderName.setVisibility(false);
                    mFolderName.invalidate();
                    LayoutInflater inflater = (LayoutInflater) getXContext().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout folderEditor;
                    folderEditor = (LinearLayout) inflater.inflate(R.layout.folder_editor_grid, null, false);
                    
                    final EditText mFolderNameEditor = (EditText) folderEditor.findViewById(R.id.folder_name_editor);
                    final Button modifyButton = (Button)folderEditor.findViewById(R.id.folder_name_modify_ok);
                    modifyButton.setOnClickListener(new android.view.View.OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                            String rename = mFolderNameEditor.getText().toString();
                            if(mFolderName !=null && !rename.equals("") ){
                                mInfo.setTitle(rename);
                                XLauncherModel.updateItemInDatabase(XFolder.this.getXContext().getContext(), mInfo);
                                mFolderName.setText(rename);
                              }
                            if(mDialog !=null && mDialog.isShowing()){
                                mDialog.dismiss();
                            }
                        }
                    });
                    mFolderNameEditor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mFolderNameMaxLen)});
                    
                    /* RK_ID: RK_FOLDER. AUT: liuli1 . DATE: 2012-09-12 . START */
                    mFolderNameEditor.setOnKeyListener(new OnKeyListener() {
                        
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                InputMethodManager imm = (InputMethodManager) v.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                                
                                if (imm.isActive()) {
                                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                                }
                                
                                return true;
                            }
                            
                            return false;
                        }
                    });

                    if (mInfo.title != null && mInfo.title.length() > 0) {
                    	if(mRenameBuffer == null){
                    		mRenameBuffer = mInfo.title.toString();
                    	}
                        mFolderNameEditor.setText(mRenameBuffer);
                        mFolderNameEditor.selectAll();
                    }

                    mDialog = new Dialog(getXContext().getContext());
                    //mDialog = new Dialog(getXContext().getContext(),android.R.style.Theme_DeviceDefault_Light_Dialog);//R.style.Theme_LeLauncher_Dialog_Shortcut
                    mDialog.setCanceledOnTouchOutside(true);
                    mDialog.setOnDismissListener(new android.content.DialogInterface.OnDismissListener() {
                        
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        	if(mRenameBuffer != null){
                        		mRenameBuffer = mFolderNameEditor.getText().toString();
                        	}
                            mFolderName.setVisibility(true);
                            mFolderName.invalidate();
                        }
                    });
                    Window mWindow = mDialog.getWindow();  
                    mWindow.setGravity(Gravity.LEFT | Gravity.TOP);
                    mWindow.setBackgroundDrawableResource(R.drawable.folder_rename_bg);
                    mWindow.requestFeature(Window.FEATURE_NO_TITLE);
                    mWindow.setDimAmount(0);
                    mWindow.setWindowAnimations(R.style.FolderdialogWindowAnim);
                    mDialog.setContentView(folderEditor);
                    
                    resizeRenameDialogPoistion();
                    
                    
//                    LayoutParams lp1 = (LayoutParams) folderEditor.getLayoutParams();
//                    lp1.gravity = Gravity.TOP|Gravity.LEFT;
//                    lp1.setMargins((int)mFolderName.localRect.left, (int)mFolderName.localRect.top, 0, 0);
//                    lp1.width = (int) mFolderName.getWidth();
//                    lp1.height = (int) mFolderName.getHeight()+20;
//                    folderEditor.setLayoutParams(lp1);
                    
                    mFolderNameEditor.setFocusable(true);
                    mFolderNameEditor.setOnFocusChangeListener(new View.OnFocusChangeListener(){

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus && mDialog != null) {
                                mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                           }
                            
                        }
                        
                    });
                    mDialog.show();
                }
    }
    


    
    private void resizeRenameDialogPoistion(){
        if(mDialog !=null){
            Window mWindow = mDialog.getWindow(); 
            WindowManager.LayoutParams lp = mWindow.getAttributes();   
            lp.x = (int) mFolderName.localRect.left;//10;   //新位置X坐标  
            lp.y = (int) mFolderName.localRect.top - SettingsValue.getExtraTopMargin(); //新位置Y坐标  
            lp.width = (int) mFolderName.getWidth();
            lp.height = (int) mFolderName.getHeight();
            mDialog.onWindowAttributesChanged(lp);
        }
    }
    

    @Override
    public void onAdd(ShortcutInfo item) {
//        mItemsInvalidated = true;        
               
        // If the item was dropped onto this open folder, we have done the work associated
        // with adding the item to the folder, as indicated by mSuppressOnAdd being set
        if (!mSuppressOnAdd)
        {
            if (!findAndSetEmptyCells(item)) {
                // The current layout is full, can we expand it?
                //setupContentForNumItems(getItemCount() + 1);
                findAndSetEmptyCells(item);
            }
            createAndAddShortcut(item);
            XLauncherModel.addOrMoveItemInDatabase(
                    getXContext().getContext(), item, mInfo.id, item.screen, item.cellX, item.cellY);

            // fix bug 18698
            Log.i(TAG, "onAdd ~~~" + mAnimFolderIcon);
            mFolderAdded = true;
            if (mAnimFolderIcon != null) {
                getXContext().getRenderer().ejectAnimation(mAnimFolderIcon);
            }
        }
        mContent.getSourceItemsByOrder(mItemsInReadingOrder, XFolderIcon.NUM_ITEMS_IN_PREVIEW);
        
        mXContext.post(new Runnable() {
            public void run() {
                mFolderIcon.invalidate();
            }
        });
              
    }

    @Override
    public void onRemove(ShortcutInfo item) {
//        mItemsInvalidated = true;
        // If this item is being dragged from this open folder, we have already handled
        // the work associated with removing the item, so we don't have to do anything here.
        if (item != mCurrentDragInfo)
        {
            XPagedViewItem itemToRemove = mContent.findPageItemAt(item.screen, item.cellX, item.cellY);
            if (itemToRemove != null) {
                final DrawableItem paraDrawingItem = itemToRemove.getDrawingTarget();
                if (paraDrawingItem instanceof XShortcutIconView) {
                    ((XShortcutIconView) paraDrawingItem).setInFolder(false);
                }
            }
            mContent.removePageItem(item, true, mInfo.opened);
            int oldPageCount = mContent.getPageCount();
            int newPageCount = (int) Math.ceil(mContent.getPageViewItemCount() * 1f / (mContent.getCellCountX() * mContent.getCellCountY()));
            int delta = oldPageCount - newPageCount;
            if (delta > 0) {
                if (updateLayout()) {
                    updateItemLocationsInDatabase();
                }
            }
        }
        updateIndicatorVisible();
        mContent.getSourceItemsByOrder(mItemsInReadingOrder, XFolderIcon.NUM_ITEMS_IN_PREVIEW);
        mFolderIcon.invalidate();
        /*if (mState == STATE_ANIMATING) {
            mRearrangeOnClose = true;
        } else {
            setupContentForNumItems(getItemCount());
        }
        if (mContent.getCell <= 1) {
            replaceFolderWithFinalItem();
        }*/
    }

    private void updateIndicatorVisible() {
        final boolean visible = mContent.getPageCount() > 1;
        if (xPageIndicator.isVisible() != visible) {
            xPageIndicator.setVisibility(visible);
        }
    }
    
    private void updateKuangBottom(boolean now, boolean willAdd) {
     
        float left = mContent.getRelativeX() - folder_kuang_padding_h;
        float top = mContent.getRelativeY();
        float right = mContent.localRect.right + folder_kuang_padding_h;
        float bottom = mContent.localRect.bottom + folder_kuang_padding_v;
//        if (mContent.getPageCount() < 2) {
//            int count = mContent.getPageViewItemCount();
//            int col = (int) Math.ceil((float) count / mContent.getCellCountX());
//            if (count == 0 || (count % mContent.getCellCountX() == 0 && willAdd)) {
//                col++;
//            }
//            if (count >= 0 && col < mContent.getCellCountY()) {
//                bottom = col * mContent.getCellHeight() + mContent.getRelativeY() + kuangPadding.bottom;
//            }
//        }
        mKuang.resize(new RectF(left, top, right, bottom));
       
    }
    
    
    private  void resizeKuangPoistion(){
        float left = mContent.getRelativeX() - folder_kuang_padding_h;
        float top = mContent.getRelativeY();
        float right = mContent.localRect.right + folder_kuang_padding_h;
        float kuangHeight = mKuang.getHeight();
        mKuang.resize(new RectF(left, top, right, top+kuangHeight));
    }

    public boolean updateLayout() {
        boolean reset = false;
        int count = mContent.getPageCount() * mContent.getCellCountX() * mContent.getCellCountY();
        int empty = -1;
        for (int i = 0; i < count; i++) {
            final XPagedViewItem container = mContent.findPageItemAt(i);
            if (container == null) {
                empty = i;
                break;
            }
        }
        final int[] pos = new int[3];
        if (empty >= 0 && empty < mContent.getPageViewItemCount()) {
            reset = true;
            for (int i = empty + 1; i < count; i++) {
                final XPagedViewItem container = mContent.findPageItemAt(i);
                if (container != null && i > empty) {
                    mContent.getInfoFromIndex(empty, pos);
                    mContent.moveItemToPosition(container, pos[1], pos[2], pos[0], 0, 0, null);
                    empty++;
                }
            }
        }
        int oldPageCount = mContent.getPageCount();
        int newPageCount = (int) Math.ceil(mContent.getPageViewItemCount() * 1f / (mContent.getCellCountX() * mContent.getCellCountY()));
        int delta = oldPageCount - newPageCount;
        if (delta > 0) {
            for (int i = 0; i < delta; i++) {
                mContent.removeScreenAt(mContent.getPageCount() - 1);
            }
        }
        return reset;
    }

    @Override
    public void onTitleChanged(CharSequence title) {
        mFolderName.setText(title.equals("") ? sDefaultFolderName : title.toString());
    }

    @Override
    public void onItemsChanged() {
    }
    /*** RK_ID: XFOLDER_EDIT.  AUT: zhaoxy . DATE: 2013-01-30 . END***/

    public boolean isFull() {
        return mInfo.contents.size() >= mMaxNumItems;
    }

    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-03-04 . START ***/
    private static final boolean DEBUG_DRAG = true;
    private static final String TAG_DEBUG_DRAG = "DEBUG_DRAG";
    private static final boolean DEBUG_REORDER = false;
    
    @Override
    public void onDropCompleted(DrawableItem target, XDragObject d, boolean success) {
        if (DEBUG_REORDER)R5.echo("XFolder onDropCompleted");
        if (success) {
            if (mDeleteFolderOnDropCompleted/*&& !mItemAddedBackToSelfViaIcon*/) {
                replaceFolderWithFinalItem();
            }

//            if (!mDropOnSelf)
//            {
//                XPagedViewItem pageViewItem = mContent.findPageItemAt(mSavedCell[2], mSavedCell[0], mSavedCell[1]);
//                if (pageViewItem == null)
//                {
//                    final int cellBeginIndex = mContent.getCellIndex(mSavedCell[2], mSavedCell[0], mSavedCell[1]) + 1;
//                    //R5.echo("cellBeginIndex = " + cellBeginIndex + "mSavedCell = " + mSavedCell.toString());
//                    mContent.refreshPageItems(cellBeginIndex);
//                    mFolderIcon.invalidate();
//                }
//                //else
//                //{
//                    //R5.echo("pageViewItem !=  null no need refreshPageItems" + "mSavedCell = " + mSavedCell.toString());
//                //}
//
//            }

            mLauncher.getDragLayer().showPendulumAnim(d, target);
        } else {
            ((XLauncher) getXContext().getContext()).getWorkspace().cancelcloseFolderDelayed();

            addFolderBySelf();

            // The drag failed, we need to return the item to the folder
            mFolderIcon.onDrop((ItemInfo) d.dragInfo);

            // We're going to trigger a "closeFolder" which may occur before this item has
            // been added back to the folder -- this could cause the folder to be deleted
            if (STATE_ANIMATING == mState) {
                mSuppressFolderDeletion = true;
            }

            if (target != null) {
                int targetIndex = mInfo.contents.size() - 1;
                ShortcutInfo tempInfo = mInfo.contents.get(targetIndex);
                animDragViewIntoPosition(d.dragView, tempInfo);
            }
        }
        mCurrentDragInfo = null;
//        mSavedDragInfo = null;
        mCurrentDragView = null;
        mSuppressOnAdd = false;
        mDeleteFolderOnDropCompleted = false;
        mDragInProgress = false;
//        mDropOnSelf = false;
        updateLayout();
        updateIndicatorVisible();
        updateContentVisible();
        //resize(localRect);
        // Reordering may have occured, and we need to save the new item locations. We do this once
        // at the end to prevent unnecessary database operations.
        updateItemLocationsInDatabase();
        if (mFolderIcon != null)
            mFolderIcon.invalidate();
      //add for quick drag mode by sunzq3, begin;
        ((XLauncherView)mContext).getWorkspace().getPageIndicator().startNormalAnimation();
      //add for quick drag mode by sunzq3, end;
    }

    void animDragViewIntoPosition(final XDragView dragView, ItemInfo dragInfo) {
        if (dragView != null && dragInfo != null) {
            animDropIntoPosition(dragView, dragInfo, mInfo.id);
        }
    }

    void animDropIntoPosition(final XDragView dragView, ItemInfo dragInfo, final long container) {
        final XLauncher xlauncher = (XLauncher) getXContext().getContext();
        final int screen = dragInfo.screen;

        XFolder folder = XFolder.this;
        XCell item = (XCell) mContent.getChildAt(screen, dragInfo.cellX, dragInfo.cellY);
        if (item == null && mInfo != null) {
            // this folder had been added again.
            folder = xlauncher.getFolderInstance(mInfo);
            item = (XCell) folder.mContent.getChildAt(screen, dragInfo.cellX, dragInfo.cellY);
        }

        if (screen > 0) {
            // over than one page, give the center icon coordinate.
            item = (XCell) mContent.getChildAt(0, 1, 1);
        } else {
            item.setVisibility(false);
        }

        final XCell cell = item;
        final XFolder instance = folder;
        xlauncher.getDragLayer().animDropIntoPosition(dragView, cell, screen, instance, container);

    }

    private void updateContentVisible() {
        // fix bug 17956
        int size = mInfo.contents.size();
        for (int index = 0; index < size; index++) {
            XPagedViewItem item = mContent.findPageItemAt(index);
            if (item != null && item.getDrawingTarget() != null) {
                item.getDrawingTarget().setVisibility(true);
            }
        }
    }

    private boolean mFolderAdded = false;
    private ValueAnimator mAnimFolderIcon = null;

    private void addFolderBySelf() {
        mFolderAdded = true;
        if (mAnimFolderIcon != null) {
            getXContext().getRenderer().ejectAnimation(mAnimFolderIcon);
        }

        if (mInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            XWorkspace workspace = mLauncher.getWorkspace();
            XPagedView parent = workspace.getPagedView();
            XPagedViewItem item = parent.findPageItemAt(mInfo.screen, mInfo.cellX, mInfo.cellY);
            if (item == null || item.getDrawingTarget() == null) {
                Log.i(TAG, "add folder to workspace ~~~ " + mFolderAdded);
                workspace.addInScreen(mInfo);

                XLauncherModel.addItemToDatabase(mLauncher, mInfo, mInfo.container, mInfo.screen,
                        mInfo.cellX, mInfo.cellY, false);
            }
        } else if (mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            XHotseat content = mLauncher.getHotseat();
            DrawableItem item = content.getLayout().getChildAt(mInfo.cellX);
            if (item == null || item.getTag() instanceof ItemInfo) {
                ItemInfo info = item != null ? (ItemInfo) item.getTag() : null;
                if (info == null || info.id != mInfo.id) {
                    // current item should be this folder.
                    Log.i(TAG, "folder add itself ==== " + mInfo.id);

                    content.addFolder(mInfo, mLauncher.getIconCache());
                    XLauncherModel.addItemToDatabase(mLauncher, mInfo, mInfo.container, mInfo.screen,
                            mInfo.cellX, mInfo.cellY, false);
                }
            }
        }

    }

    void deleteNonContentFolder() {
        int size = mInfo.contents.size();
        Log.i(TAG, "folder content size ==== " + size);
        if (size == 0 && mInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            final XPagedView parent = mLauncher.getWorkspace().getPagedView();
            XPagedViewItem item = parent.findPageItemAt(mInfo.screen, mInfo.cellX, mInfo.cellY);
            if (item != null && item.getDrawingTarget() instanceof XFolderIcon) {
                animateRemoveFolderIcon(new Runnable() {
                    @Override
                    public void run() {
                        parent.removePageItem(mInfo);
                        mDragController.removeDropTarget(XFolder.this);
                        XLauncherModel.deleteItemFromDatabase(mLauncher, mInfo);
                        mLauncher.removeFolder(mInfo);
                    }
                });
            }
        } else if (size == 0 && mInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            animateRemoveFolderIcon(new Runnable() {
                @Override
                public void run() {
                    XHotseat hotseat = mLauncher.getHotseat();
                    hotseat.removeFolder(mInfo.cellX);
                    XLauncherModel.deleteItemFromDatabase(mLauncher, mInfo);
                    mLauncher.removeFolder(mInfo);
                    hotseat.resize(hotseat.localRect);
                }
            });
        }
    }

    /**
     * 删除动画
     * @param runnable
     */
    private void animateRemoveFolderIcon(final Runnable runnable) {
        if (mFolderIcon == null) {
            return;
        }

        if (mAnimFolderIcon != null) {
            getXContext().getRenderer().ejectAnimation(mAnimFolderIcon);
        }

        final float pointX = mFolderIcon.localRect.centerX();
        final float pointY = mFolderIcon.localRect.centerY();

        mAnimFolderIcon = ValueAnimator.ofFloat(1f, 0f);
        mAnimFolderIcon.setDuration(300L);
        mAnimFolderIcon.setStartDelay(0);
        mAnimFolderIcon.setInterpolator(new DecelerateInterpolator(2));

        mAnimFolderIcon.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mFolderAdded = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "REMOVE ANIMATION END ~~~ " + mFolderAdded);
                Matrix matrix = mFolderIcon.getMatrix();

                matrix.reset();
                if (mFolderAdded) {
                    mFolderIcon.setAlpha(1f);
                } else {
                    matrix.setScale(0, 0, pointX, pointY);
                    mFolderIcon.setAlpha(0f);
                }

                if (!mFolderAdded && runnable != null) {
                    runnable.run();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.i(TAG, "REMOVE ANIMATION CANCEL ~~~ " + mFolderAdded);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mAnimFolderIcon.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i(TAG, "REMOVE ANIMATION UPDATE ~~~ " + mFolderAdded);
                if (mFolderAdded) {
                    return;
                }
                final float value = (Float) animation.getAnimatedValue();
                Matrix matrix = mFolderIcon.getMatrix();

                matrix.reset();
                matrix.setScale(value, value, pointX, pointY);

                mFolderIcon.setAlpha(value);
            }
        });
        getXContext().getRenderer().injectAnimation(mAnimFolderIcon, false);
    }

    private void updateItemLocationsInDatabase() {
        ArrayList<DrawableItem> list = getItemsInReadingOrder();
        /* RK_ID: . AUT: liuli1 . DATE: 2012-03-02 . START */
        // fix bug Bug 156273
        if (list == null || list.size() == 1) {
            return;
        }
        /* RK_ID: . AUT: liuli1 . DATE: 2012-03-02 . END */
        for (int i = 0; i < list.size(); i++) {
            DrawableItem v = list.get(i);
            ItemInfo info = (ItemInfo) v.getTag();
            XLauncherModel.moveItemInDatabase(getXContext().getContext(), info, mInfo.id, info.screen,
                        info.cellX, info.cellY);
        }
    }

    private void replaceFolderWithFinalItem() {
/*        // Add the last remaining child to the workspace in place of the folder
        Runnable onCompleteRunnable = new Runnable() {
            @Override
            public void run() {
                XWorkspace workspace = mLauncher.getWorkspace();
                XPagedView parent = workspace.getPagedView();
                Log.i(TAG, "replaceFolderWithFinalItem ====" + mContent.getPageViewItemCount());

                if (mContent.getPageViewItemCount() <= 1) {
                    // Remove the folder
                    XLauncherModel.deleteItemFromDatabase(mLauncher, mInfo);
                    parent.removePageItem(mInfo);
                    if (mFolderIcon instanceof XDropTarget) {
                        mDragController.removeDropTarget((XDropTarget) mFolderIcon);
                    }
                    mLauncher.removeFolder(mInfo);
                }
                // Move the item from the folder to the workspace, in the position of the folder
                if (mContent.getPageViewItemCount() == 1) {
                    ShortcutInfo finalItem = mInfo.contents.get(0);

                    finalItem.container = mInfo.container;
                    finalItem.screen = mInfo.screen;
                    finalItem.cellX = mInfo.cellX;
                    finalItem.cellY = mInfo.cellY;

                    // We add the child after removing the folder to prevent both from existing at
                    // the same time in the CellLayout.
                    workspace.addInScreen(finalItem, mIconCache, false);
                    XLauncherModel.addOrMoveItemInDatabase(mLauncher, finalItem, mInfo.container,
                            mInfo.screen, mInfo.cellX, mInfo.cellY);
                }
            }
        };
//        View finalChild = getItemAt(0);
//        if (finalChild != null) {
//            mFolderIcon.performDestroyAnimation(finalChild, onCompleteRunnable);
//        }
        if (onCompleteRunnable != null) {
            onCompleteRunnable.run();
        }*/
    }

    @Override
    public boolean isDropEnabled() {
        return true;
    }

    @Override
    public void onDrop(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "XFolder onDrop");
//        mHandler.removeMessages(MSG_REFRESH);
//        mDropOnSelf = true;
        ShortcutInfo item;
        if (dragObject.dragInfo instanceof ApplicationInfo) {
            // Came from all apps -- make a copy
            item = ((ApplicationInfo) dragObject.dragInfo).makeShortcut();
            item.spanX = 1;
            item.spanY = 1;
        } else if (dragObject.dragInfo instanceof ShortcutInfo) {
            item = new ShortcutInfo((ShortcutInfo) dragObject.dragInfo);
        } else {
            return;
        }
        // Dragged from self onto self, currently this is the only path possible, however
        // we keep this as a distinct code path.
        if (item == mCurrentDragInfo) {
            ShortcutInfo si = (ShortcutInfo) mCurrentDragView.getTag();
            si.cellX = mEmptyCell[0];
            si.cellY  = mEmptyCell[1];
            si.screen = mEmptyCell[2];
            mSuppressOnAdd = true;
            mCurrentDragView.setVisibility(true);
        }
        item.container = mInfo.id;
        item.cellX = mEmptyCell[0];
        item.cellY = mEmptyCell[1];
        item.screen = mEmptyCell[2];
        if (DEBUG_REORDER)R5.echo("onDrop item.cellX = " + item.cellX + "item.cellY = " + item.cellY + "item.screen = " +item.screen);
        mInfo.add(item);
    }

    @Override
    public void onDragEnter(XDragObject dragObject) {
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "XFolder Enter");
        /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. START***/
        ((XLauncher) getXContext().getContext()).getWorkspace().cancelcloseFolderDelayed();
        ((XLauncher) getXContext().getContext()).getHotseat().cancelcloseFolderDelayed();
        /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. END***/
        mKuang.setVisibility(true);
        mFolderName.setBackgroundVisible(false);
        if (mReorderDataSet)
        {
            return;
        }
        mReorderDataSet = true;
//        mPreviousTargetCell[0] = -1;
//        mPreviousTargetCell[1] = -1;
//        mPreviousTargetCell[2] = -1;
        //mEmptyCell 赋值为最后一个元素的位置。
        
        ShortcutInfo item;
        if (dragObject.dragInfo instanceof ApplicationInfo) {
            // Came from all apps -- make a copy
            item = ((ApplicationInfo) dragObject.dragInfo).makeShortcut();
            item.spanX = 1;
            item.spanY = 1;
        } else if (dragObject.dragInfo instanceof ShortcutInfo) {
            item = new ShortcutInfo((ShortcutInfo) dragObject.dragInfo);
        } else {
            return;
        }
        
        item.screen = -1;
        findAndSetEmptyCells(item);
        updateKuangBottom(true, true);
        mEmptyCell[0] = item.cellX;
        mEmptyCell[1] = item.cellY;
        mEmptyCell[2] = item.screen;
    }

    float lastX = 0, lastY = 0;
    
    @Override
    public void onDragOver(XDragObject dragObject) {
//        final int screen = ((ItemInfo) dragObject.dragInfo).screen;
        if (isAnimating()) {
            return;
        }
        if (mContent.getPageCount() == 0)
        {
            return;
        }
        if (!mEnterKuang) {
            if (mKuang.localRect.contains(dragObject.x, dragObject.y)) {
                mEnterKuang = true;
            }
        }
        mTargetCell = mContent.findNearestArea(mContent.getCurrentPage(), dragObject.x, dragObject.y, 1, 1, 1, 1, null, false, mTargetCell, null);
        mTargetCell[2] = mContent.getCurrentPage();
//        if (DEBUG_REORDER)R5.echo("mTargetCell[2] = " + mTargetCell[2]);
        float xx = dragObject.x - lastX;
        float yy = dragObject.y - lastY;
        if ((mTargetCell[0] != mEmptyCell[0] || mTargetCell[1] != mEmptyCell[1]
            || mTargetCell[2] != mEmptyCell[2]) && (xx * xx + yy * yy > 4)) {
//            if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "onDragOver mTargetCell[" + mTargetCell[0] + ", " + mTargetCell[1] + "]");
            mReorderAlarm.cancelAlarm();
//            mXContext.getRenderer().getEventHandler().removeCallbacks(mRunnable);
            mReorderAlarm.setOnAlarmListener(mReorderAlarmListener);
            mReorderAlarm.setAlarm(150);
//            mPreviousTargetCell[0] = mTargetCell[0];
//            mPreviousTargetCell[1] = mTargetCell[1];
//            mPreviousTargetCell[2] = mTargetCell[2];
        }
        lastX = dragObject.x;
        lastY = dragObject.y;
    }

    @Override
    public void onDragExit(XDragObject dragObject) {
//        if (isAnimating()) {
//            return;
//        }
        if (DEBUG_DRAG) Log.d(TAG_DEBUG_DRAG, "XFolder Exit x = " + dragObject.x + " y = " + dragObject.y);
        if (!dragObject.dragComplete) {
            ((XLauncher) getXContext().getContext()).getWorkspace().closeFolderDelayed();
            refreshLocation();
        }
        mReorderAlarm.cancelAlarm();
//        mXContext.getRenderer().getEventHandler().removeCallbacks(mRunnable);  
//        mHandler.removeMessages(MSG_REFRESH);
//        mHandler.sendEmptyMessage(MSG_REFRESH);
        
        mReorderDataSet = false;
        mKuang.setVisibility(false);
        mFolderName.setBackgroundVisible(true);
    }

    @Override
    public XDropTarget getDropTargetDelegate(XDragObject dragObject) {
        return null;
    }

    @Override
    public boolean acceptDrop(XDragObject dragObject) {
        boolean isShortcut = (dragObject.dragInfo instanceof ShortcutInfo) || (dragObject.dragInfo instanceof ApplicationInfo);
        return isShortcut && mInfo.contents.size() < mMaxNumItems && !mInfo.contains((ItemInfo) dragObject.dragInfo);
    }

    @Override
    public void getHitRect(Rect outRect) {
        if (mEnterKuang) {
            outRect.set((int) mKuang.localRect.left - 40, (int) mKuang.localRect.top, (int) mKuang.localRect.right + 40, (int) mKuang.localRect.bottom);
        } else {
            outRect.set(0, 0, (int) getWidth(), (int) getHeight());
        }
    }

    @Override
    public void getLocationInDragLayer(int[] loc) {
        ((XLauncher) getXContext().getContext()).getDragLayer().getLocationInDragLayer(this, loc);
    }

    @Override
    public int getLeft() {
        return (int) getRelativeX();
    }

    @Override
    public int getTop() {
        return (int) getRelativeY();
    }
    /*** RK_ID: DRAG. AUT: zhaoxy . DATE: 2013-03-04 . END ***/
    
    public int getMaxCountX()
    {
        return mMaxCountX;
    }
    
    private static final int REORDER_ANIMATION_DURATION = 230;
    private Alarm mReorderAlarm = new Alarm();
    XContext  mXContext;
    private void realTimeReorder(int[] empty, int[] target) {
        if (DEBUG_REORDER)R5.echo("realTimeReorder");
        boolean wrap;
        int startX;
        int endX;
        int startY;
        int endY;
        int delay = 0;
        int startScreen;
        float delayAmount = 30;
        boolean screenWrap;
        if (readingOrderGreaterThan(target, empty)) {
            screenWrap = (empty[0] >= mContent.getCellCountX() - 1) && (empty[1] >= mContent.getCellCountY() - 1);
            startScreen = screenWrap ? empty[2] + 1 : empty[2];
            if (DEBUG_REORDER)R5.echo("startScreen = " + startScreen);
            for (int i = startScreen; i <= target[2]; i++)
            {
                wrap = empty[0] >= mContent.getCellCountX() - 1;
                startY = i == empty[2] ? (wrap ? empty[1] + 1 : empty[1]) : 0;
                endY = i < target[2] ? (mContent.getCellCountY() - 1) : target[1];
                if (DEBUG_REORDER)R5.echo("startY = " + startY + "endY = " + endY);
                for (int y = startY; y <= endY; y++) {
                    startX = (i == empty[2] && y == empty[1]) ? empty[0] + 1 : 0;
                    endX = (i == target[2] && y == target[1]) ? target[0] : mContent.getCellCountX() - 1;
                    if (DEBUG_REORDER)R5.echo("startX = " + startX + "endX = " + endX);
                    for (int x = startX; x <= endX; x++) {                        
                        XPagedViewItem v = (XPagedViewItem)mContent.findPageItemAt(i,x,y);
                        if(v == null)
                        {
                            continue;
                        }
                        
                        if (mContent.moveItemToPosition(v, empty[0], empty[1], empty[2], 150, 0, null, mInfo.opened)) {
//                        if (mContent.moveChildToPosition(v, empty[0], empty[1], empty[2])) {    
                            empty[0] = x;
                            empty[1] = y;
                            empty[2] = i;
                            delay += delayAmount;
                            delayAmount *= 0.9;
                        }
                    }
                }
            }
        } else {
            screenWrap = (empty[0] == 0) && (empty[1] == 0);
            startScreen = screenWrap ? empty[2] - 1 : empty[2];
            if (DEBUG_REORDER)R5.echo("startScreen = " + startScreen);
            for (int i = startScreen; i >= target[2]; i--)
            {
                wrap = empty[0] == 0;
                startY = i == empty[2] ? (wrap ? empty[1] - 1 : empty[1]) : mContent.getCellCountY() - 1;
                endY = i > target[2] ? 0 : target[1];
                if (DEBUG_REORDER)R5.echo("startY = " + startY + "endY = " + endY);
                for (int y = startY; y >= endY; y--) {
                    startX = (i == empty[2] && y == empty[1] )? empty[0] - 1 : mContent.getCellCountX() - 1;
                    endX = (i == target[2] && y == target[1] )?  target[0] : 0 ;
                    if (DEBUG_REORDER)R5.echo("startX = " + startX + "endX = " + endX);
                    for (int x = startX; x >= endX; x--) {
                        XPagedViewItem v = mContent.findPageItemAt(i, x,y);
                        if(v == null)
                        {
                            continue;
                        }
                        
                        if (mContent.moveItemToPosition(v, empty[0], empty[1], empty[2], 150, 0, null)) {
//                        if (mContent.moveChildToPosition(v, empty[0], empty[1], empty[2])) {
                            empty[0] = x;
                            empty[1] = y;
                            empty[2] = i;
                            delay += delayAmount;
                            delayAmount *= 0.9;
                        }
                    }
                }
            }
        }
//        mSavedDragInfo.cellX = target[0];
//        mSavedDragInfo.cellY = target[1];
//        mSavedDragInfo.screen = target[2];
        
        mSavedCell[0] = target[0];
        mSavedCell[1] = target[1];
        mSavedCell[2] = target[2];
        
    }
    
//    private Runnable mRunnable = new Runnable(){
//
//        @Override
//        public void run() {
//            realTimeReorder(mEmptyCell, mTargetCell);
//        }
//    };
    
    OnAlarmListener mReorderAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
//            mXContext.getRenderer().getEventHandler().postAtFrontOfQueue(mRunnable
//            );
//            
//            mXContext.getRenderer().invalidate();   
            realTimeReorder(mEmptyCell, mTargetCell);
        }
    };

    boolean readingOrderGreaterThan(int[] v1, int[] v2) {
        if (v1[2] > v2[2] || (v1[2] == v2[2] && (v1[1] > v2[1] || (v1[1] == v2[1] && v1[0] > v2[0])))) {
            return true;
        } else {
            return false;
        }
    }
    
//    public void refreseshAllItems(){
//        for (int i = 0; i < mContent.getPageCount(); i++)
//        {
//            for (int x = 0; x < mContent.getCellCountX(); x++)
//            {
//                for (int y = 0; x < mContent.getCellCountY(); y++)
//                {
//                    XPagedViewItem pageViewItem = mContent.findPageItemAt(i, x, y);
//                    if (pageViewItem == null)
//                    {
//                        final int cellBeginIndex = mContent.getCellIndex(i, x, y) + 1;
//                        R5.echo("cellBeginIndex = " + cellBeginIndex + "mCurrentDragInfo = " + mCurrentDragInfo.toString());
//                        mContent.refreshPageItems(cellBeginIndex);
//                        mFolderIcon.invalidate();
//                        return;
//                    }
//                }
//            }
//        }
//        
//    }
    
    @Override
    public void scrollLeft() {
        if (DEBUG_REORDER)R5.echo("XFolder scrollLeft");
        if (mContent.getCurrentPage() != 0)
        {
            mContent.scrollToLeft(300);
        }         
    }

    @Override
    public void scrollRight() {
        if (mContent.getCurrentPage() < mContent.getPageCount() - 1)
        {
            mContent.scrollToRight(300);
        }     
    }

    @Override
    public int getScrollWidth() {
        return (int) getWidth();
    }

    @Override
    public boolean isScrollEnabled() {
        return mState == STATE_OPEN;
    }

    @Override
    public boolean onEnterScrollArea(int x, int y, int direction) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onExitScrollArea() {
        // TODO Auto-generated method stub
        return true;
    }
    
    public int getScrollLeftPadding(){
        return (int) mKuang.getRelativeX();
    };
    
//    private static final int MSG_REFRESH = 100;
//    private Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what) {
//                case MSG_REFRESH:
//                    mXContext.getRenderer().getEventHandler().postAtFrontOfQueue(new Runnable(){
//
//                        @Override
//                        public void run() {
//                            XPagedViewItem pageViewItem = mContent.findPageItemAt(mSavedCell[2], mSavedCell[0], mSavedCell[1]);
//                            if (pageViewItem == null)
//                            {
//                                final int cellBeginIndex = mContent.getCellIndex(mSavedCell[2], mSavedCell[0], mSavedCell[1]) + 1;
//                                if (DEBUG_REORDER)
//                                {
//                                    R5.echo("cellBeginIndex = " + cellBeginIndex + "mSavedCell[0] = " + mSavedCell[0] 
//                                       + "mSavedCell[1] = " + mSavedCell[1] + "mSavedCell[2] = " + mSavedCell[2]);
//                                }
//                                mContent.refreshPageItems(cellBeginIndex);
////                                reInvalidate();
//                                int oldCount = mContent.getPageCount();
//                                int count = (int) Math.ceil((float)mInfo.contents.size() / (mMaxCountX * mMaxCountY));
//                                if (DEBUG_REORDER)R5.echo("oldCount = " + oldCount + "count = " + count);
//                                if (oldCount > count)
//                                {  
//                                    mContent.removeScreenAt(oldCount - 1);
//                                    mContent.updatePageCount(mInfo.contents.size());
//                                    updateIndicatorVisible();
//                                }
//
//                                mFolderIcon.invalidate();
//                            }
//                        }
//                        
//                    }
//                    );
//                    
//                    mXContext.getRenderer().invalidate();
//                    break;
//                default:
//                	break;
//            }
//        };
//    };
    
   
    
    public void changeFolderThemes() {
    	if (xPageIndicator != null){
        	//modify for quick drag mode by sunzq3, begin;
    		xPageIndicator.updateTheme();
        	//modify for quick drag mode by sunzq3, end;
	    }
    }

	public HashMap<Long, XPagedViewItem> getItemIDMap() {
		// TODO Auto-generated method stub
		return mContent.mItemIDMap;
	}
	
	
	public void clearNewBgAndSetNum(String componentName, int num){
        XPagedViewItem item;
        ItemInfo info;
        Set<Map.Entry<Long, XPagedViewItem>> set = mContent.mItemIDMap.entrySet();
        for (Map.Entry<Long, XPagedViewItem> map : set) {
            item = map.getValue();
            info = item.getInfo();
            if (info instanceof ShortcutInfo)
            {
                ShortcutInfo shortcutInfo = (ShortcutInfo)info;
                ComponentName component = shortcutInfo.intent.getComponent();
                if (component != null && componentName.equals(component.flattenToString()))
                {
                    shortcutInfo.updateInfo(num);
                    ((XShortcutIconView)item.getDrawingTarget()).showTipForNewAdded(shortcutInfo.mNewString);
                                    
                    invalidate();                        
                    
                }
                
            }
        }
    }
	
    public void refreshLocation() {
    	if (DEBUG_REORDER)R5.echo("refreshLocation mSavedCell[2] = " + mSavedCell[2] 
    			+ "mSavedCell[0] = " + mSavedCell[0] + "mSavedCell[1] = " + mSavedCell[1]);
    	boolean needInvalidate = false;
        XPagedViewItem pageViewItem = mContent.findPageItemAt(mSavedCell[2], mSavedCell[0], mSavedCell[1]);
        if (pageViewItem == null)
        {
            final int cellBeginIndex = mContent.getCellIndex(mSavedCell[2], mSavedCell[0], mSavedCell[1]) + 1;
            if (DEBUG_REORDER)
            {
                R5.echo("cellBeginIndex = " + cellBeginIndex + "mSavedCell[0] = " + mSavedCell[0] 
                   + "mSavedCell[1] = " + mSavedCell[1] + "mSavedCell[2] = " + mSavedCell[2]);
            }
            mContent.refreshPageItems(cellBeginIndex, true);
//            reInvalidate();
            needInvalidate = true;
        }
        
        int oldCount = mContent.getPageCount();
        int count = (int) Math.ceil((float)mInfo.contents.size() / (mMaxCountX * mMaxCountY));
        if (DEBUG_REORDER)R5.echo("oldCount = " + oldCount + "count = " + count);
        if (oldCount > count)
        {  
            mContent.removeScreenAt(oldCount - 1);
            mContent.updatePageCount(mInfo.contents.size());
            updateIndicatorVisible();
            needInvalidate = true;
        }

        if (needInvalidate)
        {
        	mFolderIcon.invalidate();
        }
    }

    protected XPagedView getPagedView() {
        return this.mContent;
    }

    protected DrawableItem getKuang() {
        return this.mKuang;
    }

    void addDraggingViewBack(ShortcutInfo info) {
        boolean single = SettingsValue.getSingleLayerValue(getXContext().getContext());
        if (single) {
            // this is un-install option, do not remove here.
            info.cellX = mEmptyCell[0];
            info.cellY = mEmptyCell[1];
            info.screen = mEmptyCell[2];
            mInfo.add(info);
            this.updateFinalAlpha();
        }
    }
}
