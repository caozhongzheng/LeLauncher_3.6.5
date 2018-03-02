package com.lenovo.launcher.components.XAllAppFace;

import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.XDragController.XDragListener;
import com.lenovo.launcher.components.XAllAppFace.slimengine.BaseDrawableGroup;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;
import com.lenovo.launcher2.commonui.LauncherAppWidgetHost;
import com.lenovo.launcher2.commonui.LeDialog;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

public class XDeleteDropTarget extends BaseDrawableGroup implements XDropTarget, XDragListener {
    private Drawable mNormalBg;
    private Drawable mDragEnterBg;
    private Bitmap mNormalBitmap;
    private Bitmap mActiveBitmap;

    private XIconDrawable mDeleteIcon;
//    private XIconDrawable mDisabledIcon;

    private XIconDrawable mAnimIconR;
    private XIconDrawable mAnimIconL;

    private ValueAnimator mDisabledRAnim ;
    private ValueAnimator mDisabledLAnim ;

//    private boolean[] mDisabledRunning = new boolean[2];

    private ValueAnimator mMovementAnim ;
    private static final long ANIM_LONG_MOVE = 300;
    private static final long ANIM_NORMAL_MOVE = 250;
    private static final long ANIM_SHORT_MOVE = 100;

    private static final long ANIM_DURATION = 500;
//    private static final long ANIM_DELAY = 500;
    private static final String TAG = "XDeleteDropTarget";

    private Bitmap mDisabledBitmap;
//    private Drawable mDisableBitmapR;
//    private Drawable mDisableBitmapL;

    private ValueAnimator mShowOrHideAnim ;
    private boolean mShowRunning = false;
    private static final long ANIM_SHOW_DURATION = 250;
    private static final long ANIM_HIDE_DURATION = 350;

    private XDragLayer mDragLayer;
    private FolderInfo mDragSource;

    /** Whether this drop target is active for the current drag */
    protected boolean mActive;
    private boolean mIsSingle;
    // whether this is system application, or not null folder.
    private int mInfoType;
    private static final int SYSAPP_OR_FOLDER = 1;
    private static final int INSTALLAPP_OR_NULLFOLDER = 2;
    // shortcut, commend application or disabled class.
    private static final int OTHER_TYPE_APP = 3;

    private boolean mShowOptionalDlg = false;

    private boolean mShowAlertWhenEnd = false;
    private static final long DELAY_ALERT_TO_FULLSCREEN = 1000;
    private XTextView mAlertText;
    private int mPaddingTop;

    private boolean hasSequal = false;
    private ImageView mBackView;

    public XDeleteDropTarget(XContext context, RectF rect) {
        super(context);

        mDeleteIcon = new XIconDrawable(context, null);
        mDeleteIcon.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
        addItem(mDeleteIcon);

//        mDisabledIcon = new XIconDrawable(context, null);
//        mDisabledIcon.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
//        addItem(mDisabledIcon);

        mAnimIconR = new XIconDrawable(context, null);
        mAnimIconR.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
        addItem(mAnimIconR);

        mAnimIconL = new XIconDrawable(context, null);
        mAnimIconL.setFillMode(XIconDrawable.FILLMODE_WRAPSRC);
        addItem(mAnimIconL);

        String s = context.getResources().getString(R.string.uninstall_system_app);
        mAlertText = new XTextView(context, s, new RectF(0f, 0f, 0f, 0f));
        mAlertText.setTextColor(Color.WHITE);
        int textSize = Integer.valueOf(SettingsValue.getIconTextSizeValue(context.getContext()));
        mAlertText.setTextSize(1.1f * textSize * context.getResources().getDisplayMetrics().density);
        mAlertText.setTextAlign(Align.CENTER);
        mAlertText.setEllipsize(TextUtils.TruncateAt.END);
        addItem(mAlertText);

        resize(rect);
        mIsSingle = SettingsValue.getSingleLayerValue(context.getContext());
    }

    @Override
    public void resize(RectF rect) {
        super.resize(rect);
        int width = 0, height = 0;
        if (mDeleteIcon != null) {
            Bitmap normalBitmap = getBitmap(R.drawable.ic_delete_drop_normal_1);
            width = normalBitmap.getWidth();
            height = normalBitmap.getHeight();
            mDeleteIcon.resize(new RectF(0, 0, width, height));
        }

        if (mAnimIconR != null) {
            mAnimIconR.resize(new RectF(0, 0, width, height));
        }

        if (mAnimIconL != null) {
            mAnimIconL.resize(new RectF(0, 0, width, height));
        }

        if (mAlertText != null) {
            mAlertText.resize(new RectF(0, 0, rect.width(), height));
        }
        resetAllChildren();
    }

    private void resetAllChildren() {
        if (mDeleteIcon != null) {
            final float left = (this.getWidth() - mDeleteIcon.getWidth()) / 2;
            mDeleteIcon.setRelativeX(left);
            mPaddingTop = getXContext().getResources().getDimensionPixelSize(
                    R.dimen.delete_zone_icon_padding_top);
            mDeleteIcon.setRelativeY(mPaddingTop);
        }

        if (mAnimIconR != null) {
            final float left = (this.getWidth() - mAnimIconR.getWidth()) / 2;
            mAnimIconR.setRelativeX(left);
            mAnimIconR.setRelativeY(mPaddingTop);
        }

        if (mAnimIconL != null) {
            final float left = (this.getWidth() - mAnimIconL.getWidth()) / 2;
            mAnimIconL.setRelativeX(left);
            mAnimIconL.setRelativeY(mPaddingTop);
        }

        if (mAlertText != null) {
            mAlertText.setRelativeY(mPaddingTop - getHeight());
        }
    }

    @Override
    public boolean isDropEnabled() {
        return mActive;
    }

    @Override
    public void onDrop(XDragObject dragObject) {
        Log.i(TAG, "onDrop ~~~~~" + dragObject);

        if (dragObject.dragInfo instanceof ItemInfo) {
            final ItemInfo item = (ItemInfo) dragObject.dragInfo;
            final Context c = getXContext().getContext();

            if (mIsSingle && item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
                ((XLauncher) c).getWorkspace().cancelcloseFolderDelayed();
                ((XLauncher) c).getHotseat().cancelcloseFolderDelayed();

//                showMovementAnim(dragObject, true, ANIM_SHORT_MOVE);
                if (mInfoType == SYSAPP_OR_FOLDER || mInfoType == INSTALLAPP_OR_NULLFOLDER) {
                    uninstallAppOrNot(dragObject, (ShortcutInfo) item, dragObject.x, dragObject.y);
                    return;
                }
            } else if (mIsSingle && item.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER) {
//                showMovementAnim(dragObject, true, ANIM_SHORT_MOVE, this.getHeight());
                deliverFolderToChildren((FolderInfo) item);
                return;
            }

            if (dragObject.dragSource instanceof XScreenContentTabHost) {
//            	((XLauncher)c).showWidgetSnap(true, XScreenMngView.State.ADDED);
                return;
            }

            if (item instanceof ShortcutInfo) {
                ((XLauncher) c).getWorkspace().cancelcloseFolderDelayed();
                ((XLauncher) c).getHotseat().cancelcloseFolderDelayed();

                XLauncherModel.deleteItemFromDatabase(c, item);

                // if the item from an open folder, do not close it.
                mDragLayer.cleanupFolderMsg(item.container);
            } else if (item instanceof FolderInfo) {
                FolderInfo folderInfo = (FolderInfo) item;
                /* PK_ID:DELETE NUMS FORM SHAREDPREFENCES AUTH GECN1 DATE 2013-04-19 S */
                XFolderIcon.deleteNumbersTipFromPreference(folderInfo.id, c);
                /* PK_ID:DELETE NUMS FORM SHAREDPREFENCES AUTH GECN1 DATE 2013-04-19 E */
                XLauncherModel.deleteFolderContentsFromDatabase(c, folderInfo);

                ((XLauncher) c).removeFolder(folderInfo);

            } else if (item instanceof LauncherAppWidgetInfo) {
                XLauncherModel.deleteItemFromDatabase(c, item);

                final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) item;
                final LauncherAppWidgetHost appWidgetHost = ((XLauncher) c).getAppWidgetHost();
                if (appWidgetHost != null) {
                    // Deleting an application widget ID is a void call but writes to disk before
                    // returning to the caller...
                    new Thread("deleteAppWidgetId") {
                        public void run() {
                            appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
                        }
                    }.start();

                    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 S */
                    AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(c)
                            .getAppWidgetInfo(launcherAppWidgetInfo.appWidgetId);
                    if (appWidgetInfo != null && appWidgetInfo.provider != null) {
                        // Launcher.processReaper(mLauncher, Reaper.REAPER_EVENT_REMOVE_WIDGET,
                        // appWidgetInfo.provider.getPackageName(), Reaper.REAPER_NO_INT_VALUE);
                        Reaper.processReaper(c, Reaper.REAPER_EVENT_CATEGORY_WIDGET,
                                Reaper.REAPER_EVENT_ACTION_WIDGET_LONGCLICKDELETE,
                                appWidgetInfo.provider.getPackageName(), Reaper.REAPER_NO_INT_VALUE);
                    }
                    /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2013-3-26 E */

                } // end if (appWidgetHost != null)
            } else if (item instanceof LenovoWidgetViewInfo) {
                XLauncherModel.deleteItemFromDatabase(c, item);

                XLauncherModel.sLeosWidgets.remove(item);
                Intent intent = new Intent("com.lenovo.launcher.LEOS_E2E_WIDGET_DELETE_ALL");
                intent.putExtra("LEOS_E2E_WIDGET_VIEW", ((LenovoWidgetViewInfo) item).className);
                c.sendBroadcast(intent);

                /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 S */
                Reaper.processReaper(c, Reaper.REAPER_EVENT_CATEGORY_WIDGET,
                        Reaper.REAPER_EVENT_ACTION_WIDGET_LONGCLICKDELETEIDEA,
                        ((LenovoWidgetViewInfo) item).className, Reaper.REAPER_NO_INT_VALUE);
                /* RK_ID: RK_LELAUNCHER_REAPER_EVENT. AUT: zhangdxa DATE: 2012-3-26 E */
            }
          //add by zhanggx1 for reordering all pages on 2013-11-20. s
            ((XLauncher) c).autoReorder();
            //add by zhanggx1 for reordering all pages on 2013-11-20. e     
        }
    }

    private void deliverFolderToChildren(final FolderInfo info) {
//        getXContext().post(new Runnable() {

//            @Override
//            public void run() {
//                showDialog(info);

//                int size = info.contents.size();
                final Context c = getXContext().getContext();

                if (mInfoType == INSTALLAPP_OR_NULLFOLDER) {
                    XLauncherModel.deleteFolderContentsFromDatabase(c, info);
                    ((XLauncher) c).removeFolder(info);

                } else {
                    mShowAlertWhenEnd = true;
                    mAlertText.setText(c.getString(R.string.delete_folder_error_msg));
//                    showToast(c, R.string.delete_folder_error_msg);
                }
//            }
//        });
    }

//    private void showToast(Context c, int msgID) {
//        View view = LayoutInflater.from(c).inflate(R.layout.delete_toast, null);
//        TextView message = (TextView) view.findViewById(R.id.message);
//        message.setText(msgID);
//
//        Toast toast = new Toast(c);
//        toast.setGravity(Gravity.TOP, 0, 10);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setView(view);
//        toast.show();
//    }

    protected void showDialog(final FolderInfo info) {
        final Context c = getXContext().getContext();
        LayoutInflater inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.alert_dialog_with_message,
                null, false);

        TextView dialogTitle = (TextView) layout.findViewById(R.id.dialog_title);
        dialogTitle.setText(c.getString(R.string.deliver_folder_title) + " " + info.title);
        TextView dialogMsg = (TextView) layout.findViewById(R.id.message);
        dialogMsg.setText(R.string.deliver_folder_msg);

        final LeDialog d = new LeDialog(c, R.style.Theme_LeLauncher_Dialog_Shortcut);
        d.setContentView(layout);

        Button cancelBtn = (Button) layout.findViewById(R.id.canceladd);
        cancelBtn.setText(c.getString(android.R.string.cancel));
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                d.dismiss();
            }

        });
        Button finishBtn = (Button) layout.findViewById(R.id.addfinish);
        finishBtn.setText(c.getString(android.R.string.ok));
        finishBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((XLauncher) c).deliverFolder(info);
                d.dismiss();
            }
        });

        d.show();
    }

    private void uninstallAppOrNot(final XDragObject dragObject, final ShortcutInfo info,
            final int x, final int y) {
        final ComponentName component = info.intent != null ? info.intent.getComponent() : null;
        final String pkg = component != null ? component.getPackageName() : null;
        if (pkg == null) {
            return;
        }

        if (dragObject.dragSource instanceof XFolder) {
            mDragSource = ((XFolder) dragObject.dragSource).mInfo.copy();
        }

        if (mInfoType == SYSAPP_OR_FOLDER) {// isSystemApp(info)
            // show text.
            Context c = getXContext().getContext();
//            showToast(c, R.string.uninstall_system_app);
            mShowAlertWhenEnd = true;
            mAlertText.setText(c.getString(R.string.uninstall_system_app));

            reAddShortcut(info, c, false);
            animIntoPosition(dragObject.dragView, info);
        } else if (mInfoType == INSTALLAPP_OR_NULLFOLDER) {
            final XLauncherView rootView = (XLauncherView) getXContext();
            final XLauncher c = (XLauncher) rootView.getContext();

            if (c.checkCallingOrSelfPermission(android.Manifest.permission.DELETE_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                // uninstall it.
                Intent intent = new Intent(Intent.ACTION_DELETE);// Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + pkg));
                getXContext().getContext().startActivity(intent);

                reAddShortcut(info, c);
            } else {
                mShowOptionalDlg = true;
                final View view = LayoutInflater.from(c).inflate(R.layout.uninstall_apps, null);
                int shadowHeight = c.getResources().getDimensionPixelSize(
                        R.dimen.uninstall_shadow_height);
                //add by zhanggx1 for new layout.s
                final int statusBarHeight = SettingsValue.getStatusBarHeight(c);
                //add by zhanggx1 for new layout.e
                float delta = c.getResources().getDimension(R.dimen.uninstall_apps_dialog_height)
                        - statusBarHeight - shadowHeight;

                rootView.post(new Runnable() {

                    @Override
                    public void run() {
                        addUninstallDialog(dragObject, view, c, rootView, info, x, y);
                    }
                }); // end post.

                showMovementAnim(dragObject, false, ANIM_LONG_MOVE, delta, null);
            } // end else inside.
        } // end else outside.
    }

    private void animIntoPosition(final XDragView dragView, ItemInfo info) {
        if (dragView.hasDrawn()) {
            final XLauncher xlauncher = (XLauncher) getXContext().getContext();

            if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                xlauncher.getWorkspace().animDragviewIntoPosition(dragView, info);

            } else if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                xlauncher.getHotseat().animDragviewIntoPosition(dragView, info);

            } else {
                XFolder folder = xlauncher.getWorkspace().getOpenFolder();
                if (folder != null) {
                    // open folder
                    XPagedView pageView = folder.getPagedView();
                    final DrawableItem item = pageView.getChildAt(info.screen, info.cellX, info.cellY);
                    item.setVisibility(false);

                    int padding = 0;
                    int currentScreen = pageView.getCurrentPage();
                    if (info.screen != currentScreen) {
                        padding = (int) (pageView.getWidth() * (info.screen - currentScreen + 1));
                    }

                    final int p = padding;
                    getXContext().post(new Runnable() {

                        @Override
                        public void run() {
                            mDragLayer.initPageBeforeAnim();
                            mDragLayer.animateViewIntoPosition(dragView, item, 0, null, p, 1.0f);
                        }
                    });
                } else {
                    // close folder
                    long id = info.container;
                    FolderInfo folderInfo = XLauncherModel.sFolders.get(id);
                    if (folderInfo == null) {
                        folderInfo = mDragSource;
                    }

                    folder = xlauncher.getFolderInstance(folderInfo);
                    folder.animDragViewIntoPosition(dragView, info);
                }
                mDragSource = null;
            }
        }
    }

    private void reAddShortcut(ShortcutInfo info, Context c) {
        reAddShortcut(info, c, true);
    }

    private void reAddShortcut(ShortcutInfo info, Context c, boolean destroySource) {
        XLauncher launcher = (XLauncher) c;
        if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            launcher.getWorkspace().addDraggingViewBack(info);
        } else if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            launcher.getHotseat().addDraggingViewBack(info);
        } else {
            XFolder folder = launcher.getWorkspace().getOpenFolder();
            if (folder == null) {

                long id = info.container;
                FolderInfo folderInfo = XLauncherModel.sFolders.get(id);

                if (folderInfo == null) {
                    Log.e(TAG, "reAddShortcut ~~ this folder had been removed..");
                    folderInfo = mDragSource;
                    addFolder(launcher, folderInfo);
                }

                folder = launcher.getFolderInstance(folderInfo);
            }
            folder.addDraggingViewBack(info);

            if (destroySource) {
                mDragSource = null;
            }
        }
    }

    private void addFolder(XLauncher launcher, FolderInfo info) {
        if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            XWorkspace workspace = launcher.getWorkspace();
            XPagedView parent = workspace.getPagedView();
            XPagedViewItem item = parent.findPageItemAt(info.screen, info.cellX, info.cellY);
            if (item == null || item.getDrawingTarget() == null) {
                workspace.addInScreen(info);

                XLauncherModel.addItemToDatabase(launcher, info, info.container, info.screen,
                        info.cellX, info.cellY, false);
            }
        } else if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            XHotseat content = launcher.getHotseat();
            DrawableItem item = content.getLayout().getChildAt(info.cellX);
            if (item == null || item.getTag() instanceof ItemInfo) {
                ItemInfo tag = item != null ? (ItemInfo) item.getTag() : null;
                if (tag == null || tag.id != info.id) {
                    // current item should be this folder.
                    Log.i(TAG, "folder add itself ==== " + info.id);

                    content.addFolder(info, launcher.getIconCache());
                    XLauncherModel.addItemToDatabase(launcher, info, info.container, info.screen,
                            info.cellX, info.cellY, false);
                }
            }
        }
        info.opened = false;
    }

    private void addUninstallDialog(final XDragObject dragObject, final View view,
            final XLauncher c, final XLauncherView rootView, final ShortcutInfo info, int x, int y) {
        rootView.addView(view);
        view.setTag(info);
        c.getDragLayer().setTouchable(false);

        // layout params.
        final FrameLayout.LayoutParams param = (LayoutParams) view.getLayoutParams();
        Resources r = c.getResources();
        param.height = (int) r.getDimension(R.dimen.uninstall_apps_dialog_height);
        view.setLayoutParams(param);

        // background pad
        float density = c.getResources().getDisplayMetrics().density;
        if (SettingsValue.getCurrentMachineType(c) == 0 && density > 1f && density < 2f) {
            Log.i(TAG, "this background get drawable for density .. ");
            View contentView = view.findViewById(R.id.uninstall_apps_dlg);
            Drawable d = Utilities.findDrawableByResourceName("uninstall_shadow", c);
            Log.i(TAG, "d.width == " + d.getIntrinsicWidth());
            contentView.setBackgroundDrawable(d);
        }

        // name and icon
        ImageView image = (ImageView) view.findViewById(R.id.uninstall_image);
        Bitmap src = info.getIcon(c.getIconCache(), true);
        image.setImageBitmap(src);

        TextView title = (TextView) view.findViewById(R.id.uninstall_title);
        title.setText(c.getString(R.string.uninstall_title_key) + info.title);

        // animation.
        int left = r.getDimensionPixelSize(R.dimen.uninstall_apps_view_padding_left);
        int top = r.getDimensionPixelSize(R.dimen.uninstall_apps_image_padding_top);
        int halfWidth = src.getWidth() / 2;
        int halfHeight = src.getHeight() / 2;

        View imageLayout = view.findViewById(R.id.uninstall_image_layout);
        final int fromX = x - (halfWidth + left);
        final int fromY = y - (halfHeight + top);
        TranslateAnimation animation = new TranslateAnimation(fromX, 0, fromY, 0);
        animation.setInterpolator(new OvershootInterpolator(0.7f));
        animation.setDuration(250);
        imageLayout.startAnimation(animation);

        // buttons
        Button cancelBtn = (Button) view.findViewById(R.id.uninstall_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetToNormalStatus(c, rootView, view, param.height, dragObject, true);
            }
        });

        Button okBtn = (Button) view.findViewById(R.id.uninstall_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mShowOptionalDlg) {
                    return;
                }

                resetToNormalStatus(c, rootView, view, param.height, dragObject, false);

                // uninstall
                final ComponentName component = info.intent != null ? info.intent.getComponent()
                        : null;
                final String pkg = component != null ? component.getPackageName() : null;
                if (pkg != null) {
                    PackageDeleteObserver observer = new PackageDeleteObserver(info);
                    c.getPackageManager().deletePackage(pkg, observer, 0);
                }
            }
        });

    }

    private void resetToNormalStatus(final XLauncher c, final XLauncherView rootView, final View view,
            int height, XDragObject dragObject, final boolean reAdd) {
        if (!mShowOptionalDlg) {
            return;
        }
        mShowOptionalDlg = false;

        // reset drag layer touch
        c.getDragLayer().setTouchable(true);
        
        //add by zhanggx1 for new layout.s
        final int statusBarHeight = SettingsValue.getStatusBarHeight(c);
        //add by zhanggx1 for new layout.e

        final int shadowHeight = c.getResources().getDimensionPixelSize(R.dimen.uninstall_shadow_height);
        float delta = height - statusBarHeight - shadowHeight;

        final ShortcutInfo info = (ShortcutInfo) view.getTag();
        if (reAdd) {
            animateViewToHome(c, rootView, view, info, ANIM_LONG_MOVE);
        } else {
            resetFolderAlpha(c, info);
            mDragSource = null;
        }

        // reset other views
        showMovementAnim(dragObject, true, ANIM_LONG_MOVE-ANIM_SHORT_MOVE, delta, new Runnable() {
            @Override
            public void run() {
                continueToTop(rootView, view, ANIM_SHORT_MOVE, c, statusBarHeight
                        + shadowHeight);
            }
        });
    }

    private void resetFolderAlpha(XLauncher c, ShortcutInfo info) {
        if (info.container != LauncherSettings.Favorites.CONTAINER_DESKTOP
                && info.container != LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            XFolder folder = c.getWorkspace().getOpenFolder();
            if (folder == null) {
                long id = info.container;
                FolderInfo folderInfo = XLauncherModel.sFolders.get(id);
                if (folderInfo != null) {
                    folder = c.getFolderInstance(folderInfo);
                }
            }
            if (folder != null) {
                folder.updateFinalAlpha();
            }
        }
    }

    private void continueToTop(final XLauncherView rootView, final View view, long duration,
            final XLauncher c, int height) {
        ValueAnimator animation = ValueAnimator.ofInt(height, 0);
        animation.setDuration(duration);
        animation.setInterpolator(new DecelerateInterpolator(1.5f));
        animation.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int h = (Integer) (animation.getAnimatedValue());

                Message msg = mHandler.obtainMessage(OPTIONAL_DLG_MOVEMENT);
                msg.arg1 = h;
                msg.obj = 0f;
                mHandler.sendMessage(msg);
            }
        });
        animation.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // reset status bar.
                if (!hasSequal) {
                    Message msg = mHandler.obtainMessage(RESET_STATUS_BAR);
                    mHandler.sendMessage(msg);
                }

                view.setTag(null);
                rootView.removeView(view);
                mShowOptionalDlg = false;

        		// for widget alpha
        		try {
        			final XPagedView pv = ((XLauncher) (getXContext().getContext())).getWorkspace()
        					.getPagedView();
        			pv.unlockViewContainerDrawingModeForCurrentPage();
        			pv.setStageVisibility(true);
        		} catch (Exception e) {
        		}
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        animation.start();
    }

    private void animateViewToHome(final XLauncher launcher, final XLauncherView rootView,
            View view, ShortcutInfo info, long duration) {
        if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP
                || info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            if (launcher.getWorkspace().getOpenFolder() != null) {
                reAddShortcut(info, launcher);
                return;
            }
        }

        if (mBackView == null) {
            // because some devices, such as S880
            // maybe removeView execute, double click cancel key
            // launcher crashed, so add only once.
            // no remove.
            mBackView = new ImageView(launcher);
            rootView.addView(mBackView);
        }
        mBackView.setVisibility(View.VISIBLE);
        mBackView.setTranslationX(0f);
        mBackView.setTranslationY(0f);
        mBackView.setScaleX(1.0f);
        mBackView.setScaleY(1.0f);
        rootView.bringChildToFront(mBackView);

        Bitmap src = info.getIcon(launcher.getIconCache(), true);
        mBackView.setImageBitmap(src);


        // remove image on layout.
        View imageLayout = view.findViewById(R.id.uninstall_image_layout);
        if (imageLayout != null) {
            imageLayout.setVisibility(View.INVISIBLE);
        }

        final FrameLayout.LayoutParams param = (LayoutParams) mBackView.getLayoutParams();
        param.height = src.getHeight();
        param.width = src.getWidth();
        mBackView.setLayoutParams(param);

        Resources res = launcher.getResources();
        int startX = res.getDimensionPixelSize(R.dimen.uninstall_apps_view_padding_left);
        int startY = res.getDimensionPixelSize(R.dimen.uninstall_apps_hint_height)
                - res.getDimensionPixelSize(R.dimen.uninstall_apps_image_height);

        if (SettingsValue.getCurrentMachineType(launcher) != -1) {
            // pad.
            int totalWidth = rootView.getWidth();
            Log.i(TAG, "width of screen ===" + totalWidth);

            // info.width
            int infoWidth = view.findViewById(R.id.uninstall_infos).getWidth();
            Log.i(TAG, "width of info ===" + infoWidth);
            startX += (totalWidth - infoWidth) / 2;
        }

        getEndCoordAndStartAnim(launcher, info, src, rootView, duration, new int[] { startX,
                startY });
    }

    private int[] getEndCoordAndStartAnim(XLauncher launcher, ShortcutInfo info, Bitmap src,
            XLauncherView rootView, long duration, int[] start) {

        int[] end = new int[2];

        if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            getWorkspaceEndCoord(launcher, info, end, src.getWidth());
            justTranslateAnim(launcher, info, rootView, start[0], start[1], end, duration);

        } else if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            getHotseatEndCoord(launcher, info, end, src.getWidth());
            justTranslateAnim(launcher, info, rootView, start[0], start[1], end, duration);

        } else {
            XFolder folder = launcher.getWorkspace().getOpenFolder();
            if (folder != null) {
                getOpenFolderEndCoord(launcher, folder, info, end, src.getWidth());
                justTranslateAnim(launcher, info, rootView, start[0], start[1], end, duration);

            } else {
                int[] reTranslate = new int[2];
                float scale = getCloseFolderEndCoor(launcher, info, end, reTranslate, src.getWidth());
                translateAndStayAnim(launcher, info, rootView, start[0], start[1], end,
                        reTranslate, scale, 250);

            }
        }

        return end;
    }

    private void translateAndStayAnim(final XLauncher launcher, final ShortcutInfo info,
            final XLauncherView rootView, int startX, int startY,
            final int[] end, final int[] reTranslate, final float scale, long duration) {
        Log.i(TAG, "startX===" + startX + "    and endX ===" + end[0]);
        Log.i(TAG, "startY===" + startY + "    and endY ===" + end[1]);

        float factor = 0.6f;

        AnimatorSet animatorT = new AnimatorSet();

        ObjectAnimator xTranslate = ObjectAnimator.ofFloat(mBackView, "translationX", startX, end[0]);
        ObjectAnimator yTranslate = ObjectAnimator.ofFloat(mBackView, "translationY", startY, end[1]);

        animatorT.setInterpolator(new OvershootInterpolator(factor));
        animatorT.setDuration(duration);
        animatorT.play(xTranslate).with(yTranslate);

        animatorT.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator a) {
            }

            @Override
            public void onAnimationRepeat(Animator a) {
            }

            @Override
            public void onAnimationEnd(Animator a) {
                iconIntoFolder(launcher, info, end, reTranslate, scale);
            }

            @Override
            public void onAnimationCancel(Animator a) {
            }
        });

        hasSequal = true;
        animatorT.start();
    }

    private void reCalculateTranslate(XFolderIcon icon, int w, float scale, int[] translate,
            int[] end) {
        // the scaled icon size
        int scaleSize = (int) (w * scale);
        Log.i(TAG, "scaled icon size === " + scaleSize);
        int cellCount = icon.mFolder.getPagedView().getCellCountX();

        Resources res = getXContext().getResources();
        int paddingVer = res.getDimensionPixelSize(R.dimen.closed_folder_child_padding_v);
        int paddingHor = res.getDimensionPixelSize(R.dimen.closed_folder_child_padding_h);
        int paddingTop = (w - scaleSize * cellCount - paddingVer * (cellCount - 1)) / 2;
        int paddingLeft = (w - scaleSize * cellCount - paddingHor * (cellCount - 1)) / 2;

        // icon target cellX, cellY
        int target = icon.mFolder.getPagedView().getPageViewItemCount();
        if (target >= (cellCount * cellCount)) {
            // over than one page, give the center icon coordinate.
            target = 4;
        }
        int cellX = target % cellCount;
        int cellY = (target / cellCount) % cellCount;

        translate[0] = end[0] + (paddingLeft + scaleSize * cellX + paddingHor * cellX);
        translate[1] = end[1] + (paddingTop + scaleSize * cellY + paddingVer * cellY);
        Log.i(TAG, "endX===" + translate[0] + "    and endY ===" + translate[1]);

        // because scale and translate together, so re-calculate translateX and Y
        int delta = (int) ((1 - scale) * w / 2);
        translate[0] -= delta;
        translate[1] -= delta;
    }

    private float getCloseFolderEndCoor(XLauncher launcher, ShortcutInfo info, int[] end, int[] reTranslate, int w) {
        // find folder icon.
        long id = info.container;
        FolderInfo folderInfo = XLauncherModel.sFolders.get(id);

        if (folderInfo == null) {
            Log.e(TAG, "this folder had been removed..");
            folderInfo = mDragSource;
            addFolder(launcher, folderInfo);
        }

        if (folderInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            getWorkspaceEndCoord(launcher, folderInfo, end, w);
        } else {
            getHotseatEndCoord(launcher, folderInfo, end, w);
        }

        XFolder folder = launcher.getFolderInstance(folderInfo);
        float scale = folder.getXFolderIcon().getFolderScale();
        reCalculateTranslate(folder.getXFolderIcon(), w, scale, reTranslate, end);

        return scale;
    }

    private void getOpenFolderEndCoord(XLauncher launcher, XFolder folder, ShortcutInfo info,
            int[] end, int w) {
        XPagedView pageView = folder.getPagedView();
        int index = pageView.getPageViewItemCount();
        // pageView.getCellIndex(info.screen, info.cellX, info.cellY);
        if (index >= pageView.getChildCount()) {
            Log.i(TAG, "folder ~~ index outof array..");
        }

        XCell cell = (XCell) pageView.getChildAt(index);
        int pageWidth = (int) pageView.getWidth();
        int screen = pageView.getPageCount() - 1;
        int padding = (screen - pageView.getCurrentPage() + 1) * pageWidth;
        int cellLeft = (int) (cell != null ? cell.getRelativeX() + padding : padding);
        int cellTop = (int) (cell != null ? cell.getRelativeY() : 0);

        end[0] = (int) (pageView.getRelativeX() + cellLeft + (pageView.getCellWidth() - w) / 2);
        end[1] = (int) (launcher.getFolderOldTop() + pageView.getRelativeY() + cellTop
                + XShortcutIconView.getFolderIconPaddingTop());
    }

    private void getWorkspaceEndCoord(XLauncher launcher, ItemInfo info, int[] end, int w) {
        XWorkspace workspace = launcher.getWorkspace();
        XPagedView pageView = workspace.getPagedView();

        int index = pageView.getCellIndex(info.screen, info.cellX, info.cellY);
        if (index > pageView.getChildCount()) {
            Log.i(TAG, "index outof array.." + info);
        }

        XCell cell = (XCell) pageView.getChildAt(index);
        int padding = (int) ((info.screen - workspace.getCurrentPage() + 1) * pageView.getWidth());
        end[0] = (int) (workspace.getRelativeX() + pageView.getRelativeX() + cell.getRelativeX()
                + padding + (pageView.getCellWidth() - w) / 2);
        end[1] = (int) (launcher.getWorkspaceOldTop() + pageView.getRelativeY()
                + cell.getRelativeY() + XShortcutIconView.getIconPaddingTop());
    }

    private void getHotseatEndCoord(XLauncher launcher, ItemInfo info, int[] end, int w) {
        XHotseatCellLayout parent = launcher.getHotseat().getLayout();
        DrawableItem item = parent.getChildAt(info.cellX);
        if (item != null) {
            end[0] = (int) (item.getRelativeX() + (item.getWidth() - w) / 2);
            end[1] = (int) (item.getRelativeY() + XShortcutIconView.getHotseatIconPaddingTop() + launcher
                    .getHotseatOldTop());
        } else {
            // there is no child.
            Log.e(TAG, "there is no child..");
            end[0] = (int) ((parent.getWidth() - w) / 2);
            end[1] = (int) ((parent.getHeight() - w) / 2);
        }
    }

    private void justTranslateAnim(final XLauncher launcher, final ShortcutInfo info,
            final XLauncherView rootView, int startX, int startY, int[] end, long duration) {
        Log.i(TAG, "startX===" + startX + "    and endX ===" + end[0]);
        Log.i(TAG, "startY===" + startY + "    and endY ===" + end[1]);

        TranslateAnimation animation = new TranslateAnimation(startX, end[0], startY, end[1]);
//        animation.setInterpolator(new OvershootInterpolator(0.6f));
        animation.setDuration(duration);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation a) {
            }

            @Override
            public void onAnimationRepeat(Animation a) {
            }

            @Override
            public void onAnimationEnd(Animation a) {
                mBackView.setVisibility(View.GONE);
//                rootView.removeView(mBackView);
//                mBackView = null;
                reAddShortcut(info, launcher);
            }
        });

        mBackView.startAnimation(animation);
    }

    private boolean isSystemApp(ShortcutInfo itemInfo) {
        boolean isSystemApp = false;
        final PackageManager packageManager = getXContext().getContext().getPackageManager();
        int appFlags = 0;
        ComponentName component = itemInfo.intent.getComponent();
        String packageName = null;

        if (component != null) {
            // application info icon have a component name
            packageName = component.getPackageName();

        } else {
            // shortcut info icon have an intent without component name
            final Intent intent = itemInfo.intent;
            Parcelable extra = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            if (extra != null && extra instanceof ShortcutIconResource) {
                ShortcutIconResource iconResource = (ShortcutIconResource) extra;
                packageName = iconResource.packageName;

            } else if (itemInfo.iconResource != null) {
                packageName = itemInfo.iconResource.packageName;
            } else {
                List<ResolveInfo> apps = packageManager.queryIntentActivities(intent, 0);

                if (apps != null && apps.size() > 0) {
                    ResolveInfo info = apps.get(0);
                    appFlags = info.activityInfo.applicationInfo.flags;
                }
            }
        }

        if (packageName != null && packageName.equals(getXContext().getContext().getPackageName())) {
            Log.i(TAG, "this is com.lenovo.launcher, disable uninstall");
            isSystemApp = true;
            mInfoType = SYSAPP_OR_FOLDER;

        } else if (packageName != null) {
            try {
                appFlags = packageManager.getApplicationInfo(packageName, 0).flags;

            } catch (NameNotFoundException e) {
                // cannot find this app for intent, so disable uninstall.
                isSystemApp = true;
                Log.w(TAG, "cannot get this application .. " + packageName);
                mInfoType = OTHER_TYPE_APP;
                e.printStackTrace();
            }
        } else if (appFlags == 0) {
            // has no package, and had no application, so disable uninstall.
            isSystemApp = true;
            mInfoType = OTHER_TYPE_APP;
        }

        if ((appFlags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0) {
            isSystemApp = true;
            mInfoType = SYSAPP_OR_FOLDER;
//            if ((appFlags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
//                isSystemApp = false;
        } else if (appFlags != 0) {
            mInfoType = INSTALLAPP_OR_NULLFOLDER;
        }

        if (itemInfo.intent.getComponent() != null) {
            List<ResolveInfo> apps = packageManager.queryIntentActivities(itemInfo.intent, 0);
            if (apps == null || apps.size() == 0) {
                Log.w(TAG, "cannot query this activity .. " + itemInfo.intent.getComponent());
                mInfoType = OTHER_TYPE_APP;
            }
        }

        return isSystemApp;
    }

    @Override
    public void onDragEnter(XDragObject dragObject) {
        setActiveBgAndIcon();

//        showMovementAnim(dragObject, false, ANIM_NORMAL_MOVE, this.getHeight());

        /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. START***/
        ((XLauncher) getXContext().getContext()).getWorkspace().cancelcloseFolderDelayed();
        ((XLauncher) getXContext().getContext()).getHotseat().cancelcloseFolderDelayed();
        /*** fixbug 12707 . AUT: zhaoxy . DATE: 2013-05-21. END***/
    }

//    private void showMovementAnim(XDragObject dragObject, final boolean up, long duration,
//            final float height) {
//        showMovementAnim(dragObject, up, duration, height, null);
//    }

    private void showMovementAnim(XDragObject dragObject, final boolean up, long duration,
            final float height, Runnable r) {
        final ItemInfo item = (ItemInfo) dragObject.dragInfo;
        if (item instanceof LauncherAppWidgetInfo || item instanceof LenovoWidgetViewInfo) {
            return;
        }

        long container = ItemInfo.NO_ID;
        if (dragObject.dragInfo instanceof ItemInfo) {
            ItemInfo info = (ItemInfo) dragObject.dragInfo;
            container = info.container;
        }
        final XLauncher launcher = (XLauncher) getXContext().getContext();
        if (launcher.getWorkspace().getOpenFolder() == null) {
            container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
        }

        showMovementAnim(container, up, duration, height, r);
    }

    private void showMovementAnim(final long container, final boolean up, long duration,
            final float height, final Runnable r) {
        if (mMovementAnim != null) {
            getXContext().getRenderer().ejectAnimation(mMovementAnim);
        }

        final XLauncher launcher = (XLauncher) getXContext().getContext();
        final float workspaceTop = launcher.getWorkspaceOldTop();
        final float hotseatTop = launcher.getHotseatOldTop();

        final float folderTop = launcher.getFolderOldTop();
        //add by zhanggx1 for new layout.s
        final int statusBarHeight = SettingsValue.getStatusBarHeight(launcher);
        //add by zhanggx1 for new layout.e
        final int shadowHeight = launcher.getResources().getDimensionPixelSize(
                R.dimen.uninstall_shadow_height);

        if (up) {
            mMovementAnim = ValueAnimator.ofFloat(height, 0f);
            mMovementAnim.setInterpolator(new LinearInterpolator());
        } else {
            mMovementAnim = ValueAnimator.ofFloat(0f, height);
            mMovementAnim.setInterpolator(new DecelerateInterpolator(1.5f));
        }

        mMovementAnim.setDuration(duration);
        mMovementAnim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float h = (Float) (animation.getAnimatedValue());

                if (container == LauncherSettings.Favorites.CONTAINER_DESKTOP
                        || container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    float wT = workspaceTop + h;
                    float hT = hotseatTop + h;
                    launcher.showAnimDownOrUp(wT, hT);
                } else {
                    launcher.moveFolderDownOrUp(folderTop + h);
                }

                Message msg = mHandler.obtainMessage(OPTIONAL_DLG_MOVEMENT);
                msg.arg1 = (int) (statusBarHeight + h + shadowHeight);
                msg.obj = h / height;
                mHandler.sendMessage(msg);
            }
        });

        mMovementAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator anim) {
        		// for widget alpha
        		try {
        			final XPagedView pv = ((XLauncher) (getXContext().getContext())).getWorkspace()
        					.getPagedView();
        			pv.lockViewContainerDrawingModeToForcedForCurrentPage(XViewContainer.DRAWING_MODE_CACHE);
        			pv.setStageVisibility(false);
        		} catch (Exception e) {
        		}
            }

            @Override
            public void onAnimationRepeat(Animator anim) {
            }

            @Override
            public void onAnimationEnd(Animator anim) {
//                if (container == LauncherSettings.Favorites.CONTAINER_DESKTOP
//                        || container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    resetWorkspaceAndHotseat(launcher, workspaceTop, height, up);
//                } else {
                    resetFolder(launcher, folderTop, height, up);
//                }
                if (r != null) {
                    launcher.getMainView().post(r);
                }
            }

            @Override
            public void onAnimationCancel(Animator anim) {
            }
        });

        getXContext().getRenderer().injectAnimation(mMovementAnim, false);
    }

    private void resetFolder(XLauncher launcher, float folderTop, float height, boolean up) {
        float fT = folderTop;
        if (!up) {
            fT += height;
        }
        launcher.moveFolderDownOrUp(fT);
    }

    private void resetWorkspaceAndHotseat(XLauncher launcher, float workspaceTop, float height, boolean up) {
        float wT = workspaceTop;
        float hT = launcher.getHotseatOldTop();
        Log.i(TAG, "hT ====" + hT);
        if (!up) {
            wT += height;
            hT += height;
        }
        launcher.showAnimDownOrUp(wT, hT);
    }

    @Override
    public void onDragOver(XDragObject dragObject) {
    }

    @Override
    public void onDragExit(XDragObject dragObject) {
        setNormalBgAndIcon();

//        showMovementAnim(dragObject, true, ANIM_NORMAL_MOVE, this.getHeight());
        // fix bug 20496.
        ((XLauncher) getXContext().getContext()).getWorkspace().closeFolderDelayed();
    }

    @Override
    public XDropTarget getDropTargetDelegate(XDragObject dragObject) {
        return null;
    }

    @Override
    public boolean acceptDrop(XDragObject dragObject) {
        return true;
    }

    @Override
    public void getHitRect(Rect outRect) {
        outRect.set(0, 0, (int) getWidth(), (int) getHeight());
    }

    @Override
    public void getLocationInDragLayer(int[] loc) {
        if (mDragLayer != null)
            mDragLayer.getLocationInDragLayer(this, loc);
    }

    @Override
    public int getLeft() {
        return 0;
    }

    @Override
    public int getTop() {
        return 0;
    }

    public void setup(XDragLayer dragLayer, XDragController controller) {
        mDragLayer = dragLayer;
        controller.addDragListener(this);
    }

    private void setNormalBgAndIcon() {
        if (mNormalBitmap == null) {
            mNormalBitmap = getBitmap(R.drawable.ic_delete_drop_normal_1);
        }
        mDeleteIcon.setIconBitmap(mNormalBitmap);

        if (mNormalBg == null) {
            mNormalBg = getDrawable(R.drawable.ic_delete_drop_bg_normal);
        }
        setBackgroundDrawable(mNormalBg);
    }

    private void setActiveBgAndIcon() {
        if (mActiveBitmap == null) {
            mActiveBitmap = getBitmap(R.drawable.ic_delete_drop_active_1);
        }
        if (mInfoType != SYSAPP_OR_FOLDER) {
            mDeleteIcon.setIconBitmap(mActiveBitmap);
        }

        if (mDragEnterBg == null) {
            mDragEnterBg = getDrawable(R.drawable.ic_delete_drop_bg_active);
        }
        setBackgroundDrawable(mDragEnterBg);
    }

    private Bitmap getBitmap(int id) {
        Drawable d = getDrawable(id);
        return Utilities.drawable2BitmapNoScale(d);
    }

    private Drawable getDrawable(int id) {
        Context context = getXContext().getContext();
        LauncherApplication app = (LauncherApplication) context.getApplicationContext();
        return app.mLauncherContext.getDrawable(id);
    }

    @Override
    public void onDragStart(XDragSource source, Object info, int dragAction) {
        if (!(info instanceof ItemInfo) || info instanceof XScreenMngView.PreviewInfo) {
            return;
        }
        Log.v(TAG, "onDragStart ~~~~~");
        mActive = true;
        setNormalBgAndIcon();
        if (mAnimIconR != null) {
            mAnimIconR.setAlpha(0f);
        }

        resetAllChildren();
        mShowOrHideAnim = initShowOrHideAnim(mShowOrHideAnim, true);
        getXContext().getRenderer().injectAnimation(mShowOrHideAnim, false);

        ItemInfo item = (ItemInfo) info;
        if (mIsSingle && item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
            isSystemApp((ShortcutInfo) item);
        } else if (mIsSingle && item.itemType == LauncherSettings.Favorites.ITEM_TYPE_FOLDER) {
            mInfoType = ((FolderInfo) item).contents.size() > 0 ? SYSAPP_OR_FOLDER : INSTALLAPP_OR_NULLFOLDER;
        } else {
            mInfoType = OTHER_TYPE_APP;
        }

    }

    private ValueAnimator initShowOrHideAnim(ValueAnimator anim, final boolean show) {
        if (anim != null) {
            getXContext().getRenderer().ejectAnimation(anim);
        }

        final int height = (int) getHeight();

        if (show) {
            anim = ValueAnimator.ofFloat(0.0f, 1.0f);
            anim.setStartDelay(300);
            anim.setDuration(ANIM_SHOW_DURATION);
            setRelativeY(-height);
        } else {
            anim = ValueAnimator.ofFloat(1.0f, 0.0f);
            anim.setDuration(ANIM_HIDE_DURATION);
        }

        anim.setInterpolator(new DecelerateInterpolator(1.5f));
        anim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (show && !mShowRunning) {
                    return;
                }
                Float value = (Float) (animation.getAnimatedValue());
                float relativeY = height * (value - 1);
                Log.v(TAG, "show ===" + show + " ~~value==" + value + "   relativeY==" + relativeY);
                if (mShowAlertWhenEnd) {
                    setChildrenRelative(relativeY, value);
                } else {
                    setRelativeY(relativeY);
                    setAlpha(value);
                }
            }
        });

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator anim) {
                Log.v(TAG, "showTranslateAnim ~~ START ~~~~" + show);
                if (show) {
                    mShowRunning = true;
                } else if (mShowAlertWhenEnd) {
                    setActiveBgAndIcon();
                }
            }

            @Override
            public void onAnimationRepeat(Animator anim) {
            }

            @Override
            public void onAnimationEnd(Animator anim) {
                Log.v(TAG, "showTranslateAnim ~~ END ~~~~" + show);
                if (show) {
                    mShowRunning = false;

                    if (mInfoType == SYSAPP_OR_FOLDER) {
                        showDisabledAnim();
                    }
                }
                resetSelf(show);
            }

            @Override
            public void onAnimationCancel(Animator anim) {
                Log.v(TAG, "showTranslateAnim ~~ CANCEL ~~~~" + show);
            }
        });

        return anim;
    }

    private void setChildrenRelative(float relativeY, Float value) {
        setRelativeY(0f);
        setAlpha(1.0f);

        relativeY += mPaddingTop;
        mDeleteIcon.setRelativeY(relativeY);
        mAnimIconR.setRelativeY(relativeY);
        mAnimIconL.setRelativeY(relativeY);

        mDeleteIcon.setAlpha(value);
        mAnimIconR.setAlpha(value);
        mAnimIconL.setAlpha(value);

        float alertRelativeY = mPaddingTop - value * getHeight();
        mAlertText.setRelativeY(alertRelativeY);
        mAlertText.setAlpha(1 - value);
    }

    protected void resetSelf(boolean show) {
        Log.v(TAG, "resetSelf ~~~~~" + mActive);
        if (show) {
            setRelativeY(0f);
            setAlpha(1.0f);
        } else if (!mActive && mShowAlertWhenEnd) {
            // add message view. uninstall system apps
            // show alert message.
            endAlertAnim();
            mShowAlertWhenEnd = false;

        } else if (!mActive && mShowOptionalDlg) {
            // show optional dialog,
            // cannot reset window fullscreen for now
            clear();
//            mShowOptionalDlg = false;

        } else if (!mActive) {
            clear();
            XLauncher launcher = (XLauncher) getXContext().getContext();
            launcher.setLauncherWindowStatus(false);
        } else {
            // cancel this reset, because it show again.
            mDeleteIcon.setAlpha(1.0f);
            mAlertText.setAlpha(1.0f);
        }
    }

    private void clear() {
        final int height = (int) getHeight();
        setRelativeY(-height);
        setAlpha(0.0f);

        mDeleteIcon.setIconBitmap(null);
        setBackgroundDrawable(null);

//        mDisabledIcon.setIconBitmap(null);
        mAnimIconR.setIconBitmap(null);
        mAnimIconR.setBackgroundDrawable(null);
        mAnimIconL.setBackgroundDrawable(null);
    }

    private void endAlertAnim() {
        getXContext().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDeleteIcon.setAlpha(1.0f);
                mAlertText.setAlpha(1.0f);

                if (mActive) {
                    return;
                }
                Log.v(TAG, "ALERT~~~~~  REMOVE\n");
//                XLauncher launcher = (XLauncher) getXContext().getContext();
//                launcher.setLauncherWindowStatus(false);
//
//                clear();
                mShowOrHideAnim = initShowOrHideAnim(mShowOrHideAnim, false);
                getXContext().getRenderer().injectAnimation(mShowOrHideAnim, false);
            }
        }, DELAY_ALERT_TO_FULLSCREEN);
    }

    private void showDisabledAnim() {
//        if (mDisabledBitmap == null) {
//            mDisabledBitmap = Utilities.drawable2BitmapNoScale(getXContext().getResources()
//                    .getDrawable(R.drawable.ic_launcher_clear_active_holo));
//        }
//        mDisabledIcon.setIconBitmap(mDisabledBitmap);
//        mDisabledIcon.setAlpha(0.0f);

/*        if (mDisableBitmapR == null) {
            mDisableBitmapR = getDrawable(R.drawable.ic_launcher_delete_disabled_right);
        }
        if (mDisableBitmapL == null) {
            mDisableBitmapL = getDrawable(R.drawable.ic_launcher_delete_disabled_left);
        }
        mAnimIconR.setBackgroundDrawable(mDisableBitmapR);
        mAnimIconR.setAlpha(0.0f);
        mAnimIconL.setBackgroundDrawable(mDisableBitmapL);
        mAnimIconL.setAlpha(0.0f);

        mDisabledRAnim = initAnimation(mDisabledRAnim, mAnimIconR, 1);
        mDisabledLAnim = initAnimation(mDisabledLAnim, mAnimIconL, 2);

        getXContext().getRenderer().injectAnimation(mDisabledRAnim, false);
        getXContext().getRenderer().injectAnimation(mDisabledLAnim, false);*/
    	
//    	if( mDisableBitmapR == null ){
//    		mDisableBitmapR = getDrawable( R.drawable.ic_launcher_delete_disabled);
//    	}
//    	mAnimIconR.setBackgroundDrawable( mDisableBitmapR );
        if (mDisabledBitmap == null) {
//            mDisabledBitmap = Utilities.drawable2BitmapNoScale(getXContext().getResources()
//                    .getDrawable(R.drawable.ic_launcher_delete_disabled));
            mDisabledBitmap = getBitmap(R.drawable.ic_launcher_delete_disabled_1);
        }
        mAnimIconR.setIconBitmap(mDisabledBitmap);
    	mAnimIconR.setAlpha( .0f );
    	mDisabledRAnim = initBounceAnim(mDisabledRAnim, mAnimIconR);
    	getXContext().getRenderer().injectAnimation( mDisabledRAnim, false );
    	
    }
    
    private ValueAnimator initBounceAnim( ValueAnimator anim, final XIconDrawable icon ){
        if (anim != null) {
        	anim.cancel();
        }
        
        anim = ValueAnimator.ofFloat( 2.7f, 1.0f );
        anim.setDuration( ANIM_DURATION );
        anim.setStartDelay( 100L );
        anim.setInterpolator( new BounceInterpolator() );
        anim.addUpdateListener( new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
                if (!mActive) {
                    return;
                }
                Matrix m = icon.getMatrix();
                m.reset();
                Float value = (Float) (animation.getAnimatedValue());
                m.setScale(value, value, icon.localRect.centerX(), icon.localRect.centerY());
                icon.updateMatrix(m);

                float alpha = (2.7f - value) / 1.7f;
                icon.setAlpha(alpha);
			}
		});
        
        anim.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				resetIcon(icon);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
        
        return anim;
    }

//    private ValueAnimator initAnimation(ValueAnimator anim, final XIconDrawable icon, final int i) {
//        if (anim != null) {
//            getXContext().getRenderer().ejectAnimation(anim);
//        }
//
//        anim = ValueAnimator.ofFloat(8.0f, 1.0f);
//        anim.setDuration(ANIM_DURATION);
//        anim.setStartDelay(ANIM_DELAY * i);
//        anim.setInterpolator(new DecelerateInterpolator(3.5f));
//        anim.addUpdateListener(new AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                if (!mDisabledRunning[i - 1] || !mActive) {
//                    return;
//                }
//                Matrix m = icon.getMatrix();
//                m.reset();
//                Float value = (Float) (animation.getAnimatedValue());
//                Log.v(TAG, "disable anim ~~ ing ~~~~" + value + "   i===" + i);
//                m.setScale(value, value, icon.localRect.centerX(), icon.localRect.centerY());
//                icon.updateMatrix(m);
//
//                float alpha = (8.0f - value) / 7.0f;
//                icon.setAlpha(alpha);
//            }
//        });
//
//        anim.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator anim) {
//                Log.v(TAG, "disable anim ~~ START ~~~~" + i);
//                mDisabledRunning[i - 1] = true;
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator anim) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animator anim) {
//                Log.v(TAG, "disable anim ~~ END ~~~~" + i);
//                mDisabledRunning[i - 1] = false;
//                resetIcon(icon);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator anim) {
//                Log.v(TAG, "disable anim ~~ CANCEL ~~~~" + i);
//                mDisabledRunning[i - 1] = false;
//                resetIcon(icon);
//            }
//        });
//        return anim;
//    }

    private void resetIcon(XIconDrawable icon) {
        Matrix m = icon.getMatrix();
        m.reset();
        m.setScale(1.0f, 1.0f, icon.localRect.centerX(), icon.localRect.centerY());
        icon.updateMatrix(m);
        icon.setAlpha(1.0f);
    }

    @Override
    public void onDragEnd() {
        mActive = false;

        cancelAnim(mDisabledRAnim);
        cancelAnim(mDisabledLAnim);

        mShowOrHideAnim = initShowOrHideAnim(mShowOrHideAnim, false);
        getXContext().getRenderer().injectAnimation(mShowOrHideAnim, false);
    }

    private void cancelAnim(ValueAnimator anim) {
        if (anim != null && anim.isStarted()) {
            anim.end();
        }
    }

    void changeThemes() {
        mDeleteIcon.setIconBitmap(null);
        setBackgroundDrawable(null);

        mAnimIconR.setIconBitmap(null);
        mAnimIconR.setBackgroundDrawable(null);
        mAnimIconL.setBackgroundDrawable(null);

        mNormalBg = null;
        mDragEnterBg = null;

        if (mNormalBitmap != null && !mNormalBitmap.isRecycled()) {
            mNormalBitmap.recycle();
            mNormalBitmap = null;
        }
        if (mActiveBitmap != null && !mActiveBitmap.isRecycled()) {
            mActiveBitmap = null;
        }

        if (mDisabledBitmap != null && !mDisabledBitmap.isRecycled()) {
            mDisabledBitmap.recycle();
            mDisabledBitmap = null;
        }
//        mDisableBitmapR = null;
//        mDisableBitmapL = null;
    }

    // return true indicate workspace or folder cannot move.
    public boolean resetToNormal(XLauncher c, XLauncherView rootView, boolean fromKey) {
        return resetToNormal(c, rootView, fromKey, ANIM_LONG_MOVE, true);
    }

    private boolean resetToNormal(final XLauncher c, final XLauncherView rootView, boolean fromKey,
            long duration, boolean reAdd) {
        final View view = rootView.findViewById(R.id.uninstall_apps_dlg);
        if (!mShowOptionalDlg) {
            if (fromKey) {
                // set image to invisible.
                if (mBackView != null && mBackView.getVisibility() == View.VISIBLE) {
                    mBackView.setVisibility(View.INVISIBLE);
                }
            }
            Log.i(TAG, "return vaule == " + c.isCurrentWindowFullScreen());
            return c.isCurrentWindowFullScreen();
        }
        mShowOptionalDlg = false;

        int height = c.getResources().getDimensionPixelSize(R.dimen.uninstall_apps_dialog_height);
        final int shadowHeight = c.getResources().getDimensionPixelSize(R.dimen.uninstall_shadow_height);

        // reset workspace
        c.getDragLayer().setTouchable(true);

        final ShortcutInfo info = (ShortcutInfo) view.getTag();
        Log.i(TAG, "info.container ====" + info.container);
        if (reAdd) {
            animateViewToHome(c, rootView, view, info, duration);
        } else {
            resetFolderAlpha(c, info);
            mDragSource = null;
        }
        
        //add by zhanggx1 for new layout.s
        final int statusBarHeight = SettingsValue.getStatusBarHeight(c);
        //add by zhanggx1 for new layout.e

        // reset other views
        float delta = height - statusBarHeight - shadowHeight;
        showMovementAnim(info.container, true, duration - ANIM_SHORT_MOVE, delta, new Runnable() {
            @Override
            public void run() {
                continueToTop(rootView, view, ANIM_SHORT_MOVE, c, statusBarHeight
                        + shadowHeight);
            }
        });

        return true;
    }

    private final int UNINSTALL_COMPLETE = 1;
    private final int OPTIONAL_DLG_MOVEMENT = 2;
    private final int RESET_STATUS_BAR = 3;

    class PackageDeleteObserver extends android.content.pm.IPackageDeleteObserver.Stub {
        private ShortcutInfo mInfo;

        public PackageDeleteObserver(ShortcutInfo info) {
            mInfo = info;
        }

        @Override
        public void packageDeleted(String packageName, int returnCode) {
            Message msg = mHandler.obtainMessage(UNINSTALL_COMPLETE);
            msg.arg1 = returnCode;
            msg.obj = mInfo;
            mHandler.sendMessage(msg);
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            XLauncher launcher = (XLauncher) getXContext().getContext();

            switch (msg.what) {
            case UNINSTALL_COMPLETE:
                final ShortcutInfo info = (ShortcutInfo) msg.obj;
                final ComponentName component = info.intent != null ? info.intent.getComponent()
                        : null;
                final String packageName = component != null ? component.getPackageName() : null;

                // Update the status text
                final int statusText;
                switch (msg.arg1) {
                    case PackageManager.DELETE_SUCCEEDED:
                        statusText = R.string.uninstall_done;
                        // Show a Toast and finish the activity
                        Toast.makeText(launcher, statusText, Toast.LENGTH_LONG).show();
                        launcher.deleteFromLauncher(info);
                        return;
                    case PackageManager.DELETE_FAILED_DEVICE_POLICY_MANAGER:
                        Log.d(TAG, "Uninstall failed because " + packageName
                                + " is a device admin");
                        statusText = R.string.uninstall_failed_device_policy_manager;
                        break;
                    default:
                        Log.d(TAG, "Uninstall failed for " + packageName + " with code "
                                + msg.arg1);
                        statusText = R.string.uninstall_system_app_text;
                        break;
                }
                Toast.makeText(launcher, statusText, Toast.LENGTH_LONG).show();
                reAddShortcut(info, launcher);
                break;

            case OPTIONAL_DLG_MOVEMENT:
                final View view = launcher.getMainView().findViewById(R.id.uninstall_apps_dlg);

                if (view != null) {
                    final FrameLayout.LayoutParams param = (LayoutParams) view.getLayoutParams();
                    param.height = msg.arg1;
                    view.setLayoutParams(param);

                    float alpha = (Float) msg.obj;
                    view.findViewById(R.id.button_bar_layout).setAlpha(alpha);

                    final float dragAlpha = 1 - 0.3f * alpha;
                    launcher.getDragLayer().setAlpha(dragAlpha);
                }
                break;

            case RESET_STATUS_BAR:
                launcher.setLauncherWindowStatus(false);
                break;
            } // end switch
        } // end handleMessage
    };

    private void iconIntoFolder(final XLauncher launcher, final ShortcutInfo info,
            int[] end, int[] reTranslate, float scale) {
        AnimatorSet animatorS = new AnimatorSet();

        ObjectAnimator scaleAnimX = ObjectAnimator.ofFloat(mBackView, "scaleX", 1.0f, scale);
        ObjectAnimator scaleAnimY = ObjectAnimator.ofFloat(mBackView, "scaleY", 1.0f, scale);

        Log.i(TAG, "mStartXY[1]===" + end[1] + "    and mEndXY[1] ===" + reTranslate[1]);
        ObjectAnimator reTranslateX = ObjectAnimator.ofFloat(mBackView, "translationX", end[0],
                reTranslate[0]);
        ObjectAnimator reTranslateY = ObjectAnimator.ofFloat(mBackView, "translationY", end[1],
                reTranslate[1]);

        animatorS.setDuration(ANIM_NORMAL_MOVE);
        animatorS.setInterpolator(new AccelerateInterpolator());
        animatorS.playTogether(scaleAnimX, scaleAnimY, reTranslateX, reTranslateY);

        animatorS.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator a) {
                reAddShortcut(info, launcher);
            }

            @Override
            public void onAnimationRepeat(Animator a) {
            }

            @Override
            public void onAnimationEnd(Animator a) {
//                ViewGroup parent = (ViewGroup) mBackView.getParent();
//                parent.removeView(mBackView);
                hasSequal = false;
//                mBackView = null;
                mBackView.setVisibility(View.GONE);

                Message msg = mHandler.obtainMessage(RESET_STATUS_BAR);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onAnimationCancel(Animator a) {
            }
        });

        animatorS.start();
    }

    public void removeApps(XLauncherView rootView, List<ApplicationInfo> list) {
        final View view = rootView.findViewById(R.id.uninstall_apps_dlg);
        if (view == null || list == null) {
            return;
        }

        final ShortcutInfo info = (ShortcutInfo) view.getTag();
        if (info == null || info.intent == null) {
            return;
        }

        XLauncherModel.deleteItemFromDatabase(view.getContext(), info);
        int size = list.size();
        for (int index = 0; index < size; index++) {
            ApplicationInfo appInfo = list.get(index);
            if (appInfo != null && appInfo.componentName.equals(info.intent.getComponent())) {
                XLauncher c = (XLauncher) getXContext().getContext();
                resetToNormal(c, rootView, false, ANIM_SHORT_MOVE, false);
                break;
            }
        }
    }

    public void onConfigureChanged(XLauncher c, XLauncherView rootView) {
        if (!mShowOptionalDlg) {
            return;
        }
        mShowOptionalDlg = false;

        // reset workspace.
        c.getDragLayer().setTouchable(true);
        c.getDragLayer().setAlpha(1f);

        final View view = rootView.findViewById(R.id.uninstall_apps_dlg);
        final ShortcutInfo info = (ShortcutInfo) view.getTag();
        reAddShortcut(info, c);

        view.setTag(null);
        rootView.removeView(view);

        // reset status bar.
        Message msg = mHandler.obtainMessage(RESET_STATUS_BAR);
        mHandler.sendMessage(msg);

        // for widget alpha
        try {
            final XPagedView pv = c.getWorkspace().getPagedView();
            pv.unlockViewContainerDrawingModeForCurrentPage();
            pv.setStageVisibility(true);
        } catch (Exception e) {
        }
    }

}
