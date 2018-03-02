package com.lenovo.launcher.components.XAllAppFace;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.RectF;

import com.lenovo.launcher.components.XAllAppFace.slimengine.DrawableItem;
import com.lenovo.launcher.components.XAllAppFace.slimengine.IDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.NormalDisplayProcess;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.commoninterface.IconCache;

/**
 * 此为屏幕编辑的应用多选后长按时的缩略图
 * @author zhanggx1
 *
 */
public class XScreenIconPkgView extends DrawableItem {

	private List<XScreenShortcutInfo> mItemList;
	private static final int DRAW_NUM = 3;
	private Bitmap mBitmap;
	
	public XScreenIconPkgView(XContext context, 
			List<XScreenShortcutInfo> itemList, RectF rect) {		
		super(context);
		if (itemList == null || rect == null || rect.width() <= 0 || rect.height() <= 0) {
			return;
		}
		mItemList = itemList;
		
		mBitmap = Bitmap.createBitmap((int)rect.width(), (int)rect.height(), Config.ARGB_8888);
		
		NormalDisplayProcess tmpProc = new NormalDisplayProcess();
		tmpProc.beginDisplay(mBitmap);
		tmpProc.save();
		
		IconCache iconCache = ((XLauncher)context.getContext()).getIconCache();
		Bitmap first = itemList.get(0).getIcon(iconCache, false);
		float offsetWidth = rect.width() - first.getWidth();
		float offsetHeight = rect.height() - first.getHeight();
		float minOffset = offsetWidth > offsetHeight ? offsetHeight : offsetWidth;
		float offset = minOffset / (DRAW_NUM - 1);
		int startIndex = mItemList.size() >= DRAW_NUM ? DRAW_NUM - 1 : mItemList.size() - 1;
		
		/**
		 * 依次画三个应用叠加图
		 */
		for (int i = startIndex; i >= 0 ; i--) {
			Bitmap icon = itemList.get(i).getIcon(iconCache, false);
			tmpProc.drawBitmap(icon, i * offset, (DRAW_NUM - 1 - i) * offset, getPaint());
		}
		tmpProc.restore();
		
		resize(rect);
	}
	
	public List<XScreenShortcutInfo> getItemList() {
		return mItemList;
	}
	
	@Override
	public void onDraw(IDisplayProcess c) {
//		Paint paint = new Paint();
//		paint.setColor(Color.BLUE);		
//		c.drawRect(localRect, paint);
		
	    if (mBitmap != null) {
            c.drawBitmap(mBitmap, 0, 0, getPaint());
        }
	}
	
	@Override
	public void clean() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
		mBitmap = null;
		super.clean();
	}

}
