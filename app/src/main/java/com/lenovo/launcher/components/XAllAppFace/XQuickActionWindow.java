/*
 * Copyright (C) 2012
 *
 * AUT: liuli liuli0120@use.com.cn DATE: 2012-01-10
 * when user long click launcher's icon, including application icon, shortcut icon and widget
 * show quick action window for next operation
 */

package com.lenovo.launcher.components.XAllAppFace;

import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher2.commoninterface.FolderInfo;
import com.lenovo.launcher2.commoninterface.ItemInfo;
import com.lenovo.launcher2.commoninterface.LauncherAppWidgetInfo;
import com.lenovo.launcher2.commoninterface.LenovoWidgetViewInfo;
import com.lenovo.launcher2.commoninterface.ShortcutInfo;

public class XQuickActionWindow extends PopupWindow {
    public static final String TAG = "QuickActionWindow";
    private XQuickActionWindowInfo mXWinInfo;
    private XLauncher mXLauncher;

    public XQuickActionWindow(boolean focusable, XQuickActionWindowInfo info) {
        super(info.getContentView(), info.getWidth(), LayoutParams.WRAP_CONTENT, focusable);

        mXLauncher = info.getContext();
//        Drawable background = mXLauncher.getResources().getDrawable(R.drawable.menu_selected);
//        background.setAlpha(0);
//        setBackgroundDrawable(background);
        setBackgroundDrawable(new BitmapDrawable(mXLauncher.getResources()));

        mXWinInfo = info;
    }

    public void show() {
        // get informations
        if (mXWinInfo == null || mXLauncher == null)
            return;
//        CellInfo info = mXWinInfo.getCellInfo();
        int width = mXWinInfo.getWidth();
        int height = mXLauncher.getResources().getDimensionPixelSize(R.dimen.quick_action_height);
         //getBackground().getIntrinsicHeight();
        int count = mXWinInfo.getActionCount();
        ImageView indicator = mXWinInfo.getIndicator();

        if (indicator == null)// || info == null)
            return;
        DrawableItem child = mXWinInfo.getLongClickView();
        XWorkspace workspace = mXLauncher.getWorkspace();
        if (child == null || workspace == null)
            return;

        // cannot ignore hot seat case
        final Object itemInfo = child.getTag();
        DrawableItem v = child.getParent();// mXWinInfo.getLongClickView();
        boolean isHotseat = mXLauncher.isHotseatLayout(v);

        int x = 0;
        int y = 0;
        if (itemInfo == null)// || !(v instanceof CellLayout))
            return;
//        CellLayout cellLayout = (CellLayout) v;
        if (isHotseat) {
            y = (int) v.getParent().localRect.top - height;// workspace.getHeight() -
                                                           // cellLayout.getHeight() - height;
        } else {
//            int verPadding = workspace.getPaddingTop();
//            y = info.cellY > 0 ? verPadding + cellLayout.getPaddingTop() + child.getTop() - height
//                    : verPadding + cellLayout.getPaddingTop() + child.getBottom();
            final ItemInfo info = (ItemInfo) itemInfo;
            float top = workspace.findRectTopByCellY(child, info.cellY);
            int heightAddForPad= mXLauncher.getResources().getDimensionPixelSize(R.dimen.quick_action_height_add_for_pad);

            y = info.cellY > 0 ? (int) (top + child.localRect.top) - height
                    : (int) (child.localRect.bottom + top - heightAddForPad);
            x = Math.max(workspace.findRectLeftByCellX(child, info.cellX), 0);
        }
//        y = y-mXLauncher.getStatusBarHeight();

        int padding = 0;
        // calculate the startX and indicator position
        if (itemInfo instanceof ShortcutInfo || itemInfo instanceof FolderInfo) {
            final ItemInfo info = (ItemInfo) itemInfo;
            int cellX = (isHotseat && info.cellX > 2) ? info.cellX - 1 : info.cellX;
            int cellCountX = workspace.getPagedView().mCellCountX;
            if(cellX == (cellCountX - 1)){
            	padding = mXWinInfo.mBkPading + mXWinInfo.mCellWidth / 2 + mXWinInfo.mCellWidth
                        * (count - 1) - mXWinInfo.mHalfIndicator;
            }else if(cellX == 0){
                padding = mXWinInfo.mBkPading + mXWinInfo.mCellWidth / 2 - mXWinInfo.mHalfIndicator;
            }else {
            	padding = width / 2 - mXWinInfo.mHalfIndicator;
                if(count >4)
                    padding = width / 2 - mXWinInfo.mHalfIndicator +(2*cellX-3)*mXWinInfo.mCellWidth/2;
            }
            
            indicator.setPadding(padding, 0, 0, 0);
//            x = Math.max((int) (child.localRect.left + child.localRect.right) / 2 - padding
//                    - mXWinInfo.mHalfIndicator, 0);
            if (isHotseat) {
                x = Math.max((int) (child.localRect.left + child.localRect.right) / 2 - padding
                        - mXWinInfo.mHalfIndicator, 0);
            } else {
                x += (int) (child.localRect.left + child.localRect.right) / 2 - padding
                        - mXWinInfo.mHalfIndicator;
                x = Math.max(x, 0);
            }

            showAtLocation(mXLauncher.getMainView(), Gravity.TOP | Gravity.START, x, y);

        } else if (itemInfo instanceof LauncherAppWidgetInfo
                || (itemInfo instanceof ItemInfo && !(itemInfo instanceof LenovoWidgetViewInfo)
                        && (((ItemInfo) itemInfo).spanX > 1 || ((ItemInfo) itemInfo).spanY > 1))) {
            indicator.setPadding(width / 2 - mXWinInfo.mHalfIndicator, 0, 0, 0);
            x += ((int) (child.localRect.left + child.localRect.right)) / 2 - width / 2;
            x = Math.max(x, 0);
            showAtLocation(mXLauncher.getMainView(), Gravity.TOP | Gravity.START, x, y);
        } else if (itemInfo instanceof LenovoWidgetViewInfo) {
            indicator.setPadding(width / 2 - mXWinInfo.mHalfIndicator, 0, 0, 0);
            x += ((int) (child.localRect.left + child.localRect.right)) / 2 - width / 2;
            x = Math.max(x, 0);
            showAtLocation(mXLauncher.getMainView(), Gravity.TOP | Gravity.START, x, y);
        }

        // showAtLocation(cellLayout, Gravity.TOP | Gravity.START, x, y);
        // mPopupWindow.setAnimationStyle(R.style.quickaction_below);
    }

    protected XQuickActionWindowInfo getInfo() {
        return mXWinInfo;
    }
}
