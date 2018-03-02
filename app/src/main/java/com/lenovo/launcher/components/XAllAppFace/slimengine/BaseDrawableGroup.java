package com.lenovo.launcher.components.XAllAppFace.slimengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.D2;
import com.lenovo.launcher2.customizer.Debug.R5;

public class BaseDrawableGroup extends DrawableItem {

	public BaseDrawableGroup(XContext context) {
		super(context);
	}

    private static final boolean DEBUG = false;
    private static final String TAG = "L2";
	protected ArrayList<DrawableItem> items = new ArrayList<DrawableItem>();

    protected DrawableItem lastTouchedItem = null;
    
    private DrawableItem desiredEventItem = null;
    
    public void setTouchDesiredItem(DrawableItem itemDesiring){
    	desiredEventItem = itemDesiring;
    }

	public boolean addItem(DrawableItem item) {
		return addItem(item, getChildCount() == -1 ? 0 : getChildCount());
	}
	
	public boolean addItem(DrawableItem item, int index){
		if (item != null) {
			if (items == null) {
				items = new ArrayList<DrawableItem>();
			}
			if (items.contains(item)) {
				return false;
			}
            if (index < 0 || index > items.size()) {
                return false;
            }
			item.setPrent(this);
			if (DEBUG) echo("setPaint");
			item.setPaint(getPaint());
			items.add(index, item);
			
			return true;
		}
		return false;
	}

    public void removeItem(int index) {
        removeItem(index, true);
    }

	public void removeItem(int index, boolean destory) {
		if (items != null) {
			if (index > -1 && index < items.size()) {
                /* RK_ID: RK_MEM. AUT: liuli1 . DATE: 2012-11-22 . START */
                DrawableItem item = items.remove(index);
                if (destory) {
                    item.destory();
                } else {
                    item.clean();
                }
                if (lastTouchedItem == item) {
                    lastTouchedItem = null;
                }
                /* RK_ID: RK_MEM. AUT: liuli1 . DATE: 2012-11-22 . END */
			}
		}
	}
	
	public int removeItem(DrawableItem item) {
	    return removeItem(item, true);
	}

    public int removeItem(DrawableItem item, boolean destory) {
        if (item != null && items != null) {
            int index = items.indexOf(item);
            if (index > -1) {
                // added by liuli1, for bug 173407
                synchronized (items) {
                    items.remove(index);
                }
                if (destory) {
                    item.destory();
                } else {
                    item.clean();
                }
                if (lastTouchedItem == item) {
                    lastTouchedItem = null;
                }
                return index;
            }
        }
        return -1;
    }

	public void removeItems(Collection<DrawableItem> itemsToRemove){
		if (items != null) {
			if( itemsToRemove.contains(lastTouchedItem) ){
				lastTouchedItem = null;
			}
			
			items.removeAll( itemsToRemove );
		}
	}
	
	public int getChildIndex(DrawableItem item) {
		if (items != null) {
			int i = 0;
			int count = items.size();
			for (; i < count; i++) {
				if (item == items.get(i)) {
					return i;
				}
			}
		};

		return -1;
	}

	public DrawableItem getChildAt(int index) {
		synchronized (items) {
			try {
				
					if (index > -1 && index < items.size()) {
						return items.get(index);
					}
				
			} catch (Exception e) {
			}			
		}
		return null;
	}
	
	public boolean moveChildToIndex(DrawableItem child, int toIndex) {
		int fromIndex = getChildIndex(child);
		if (fromIndex != -1 && fromIndex != toIndex ) {
//			android.util.Log.i("move", "before move : " + getChildIndex(child) + " , target : " + toIndex);
			final DrawableItem[] itemObjects = items
					.toArray(new DrawableItem[0]);
			final DrawableItem tmp = itemObjects[fromIndex];
			itemObjects[fromIndex] = itemObjects[toIndex];
			itemObjects[toIndex] = tmp;
//
			items = new ArrayList<DrawableItem>(Arrays.asList(itemObjects));

//			android.util.Log.i("move", "after move : " + getChildIndex(child));
			
			return true;
		}
		
		return false;
	}
	
	public void bringChildToFront(DrawableItem child) {
	    int fromIndex = getChildIndex(child);
	    if (fromIndex != -1 && fromIndex < getChildCount() - 1) {
	        items.remove(fromIndex);
	        items.add(child);
        }
	}
	
	public void bringChildToBack(DrawableItem child) {
	    int fromIndex = getChildIndex(child);
        if (fromIndex > 0 && fromIndex < getChildCount()) {
            items.remove(fromIndex);
            items.add(0, child);
        }
	}
	
	public boolean exchangeChildren(DrawableItem child1, DrawableItem child2){
		int fromIndex = getChildIndex(child1);
		int toIndex = getChildIndex(child2);
		DrawableItem itemTmp = child1;
		items.set(fromIndex, child2);
		items.set(toIndex, itemTmp);
		
		Object tag = child1.getTag();
		child1.setTag(child2.getTag());
		child2.setTag(tag);
		
		return true;
	}
	
	public boolean exchangeChildren(int from, int to) {
	    if (from == to || from < 0 || from > getChildCount() - 1 || to < 0 || to > getChildCount() - 1) {
            R5.echo("exchangeChildren false");
	        return false;
        }
	    DrawableItem fromTmp = getChildAt(from);
	    DrawableItem toTmp = getChildAt(to);
	    items.set(to, fromTmp);
	    items.set(from, toTmp);
	    return true;
	}
	
   /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . START ***/
//	@Override
//	public void clean() {
//	    this.isRecycled = true;
//	    clearAllItems();
//	    items = null;
//	    super.clean();
//	}
   /*** RK_ID: XFOLDER. AUT: zhaoxy . DATE: 2013-01-22 . END ***/

	public void clearAllItems() {
        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . START */
		if (items != null) {
			synchronized (items) {
				try {
					final int size = items.size();
					for (int i = size - 1; i >= 0; i--) {
						final DrawableItem item = items.remove(i);
						item.destory();
					}
				} catch (Exception e) {
				}
				lastTouchedItem = null;
			}
		}
        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . END */
	}

//	public void setClipRect(RectF r) {
//
//		super.setClipRect(r);
//
//		if (items != null && !items.isEmpty()) {
//			for (DrawableItem item : items) {
//				item.setClipRect(r);
//			}
//		}
//	}
	
	@Override
	public void setPaint(Paint paint) {
		super.setPaint(paint);
		if (items != null && !items.isEmpty()) {
//			for (DrawableItem item : items) {
//				item.setPaint(paint);
//			}
			for (int i = items.size() - 1; i >= 0; i--) {
                final DrawableItem item = items.get(i);
                if (item != null) {
                    item.setPaint(paint);
                }
            }
		}
	}

	@Override
	public void enableCache() {
	    super.enableCache();
	    if (items != null) {
//            for (DrawableItem item : items) {
//                item.disableCache();
//            }
	        for (int i = items.size() - 1; i >= 0; i--) {
                final DrawableItem item = items.get(i);
                if (item != null) {
                    item.disableCache();
                }
            }
        }
	}
	
	@Override
	public void setInvertMatrixDirty() {
	    super.setInvertMatrixDirty();
	    if (items != null) {
//            for (DrawableItem item : items) {
//                item.setInvertMatrixDirty();
//            }
            for (int i = items.size() - 1; i >= 0; i--) {
                final DrawableItem item = items.get(i);
                if (item != null) {
                    item.setInvertMatrixDirty();
                }
            }
        }
	}
	
	/*@Override
	public void disableCache() {
	    super.disableCache();
	    if (items != null) {
            for (DrawableItem item : items) {
                item.enableCache();
            }
        }
	}*/
	
	@Override
	public void onDraw(IDisplayProcess c) {
	    if (items != null && !isRecycled) {
//            for (DrawableItem item : items) {
//                item.draw(c);
//            }
            for (int i = 0; i < items.size(); i++) {
                final DrawableItem item = items.get(i);
                if (item != null && !item.isRecycled) {
                    if (isCacheDirty()) {
                        item.setBgAlphaDirty();
                    }
                    item.draw(c);
                }
            }
        }
	}
	
//	@Override
//	public void draw(IDisplayProcess c) {
//	    super.draw(c);
//	}

	/*public void draw(Canvas canvas) {
        if (isCached()) {
            super.draw(canvas);
        } else {
	    canvas.save();
	    if (getParent() != null) {
                canvas.translate(getParent().getRelativeX(), getParent().getRelativeY());

//			RectF clipR = new RectF();
//			boolean intersect = clipR.setIntersect(getClipRect(),
//					getParent().localRect);
//			if (intersect) {
//				canvas.clipRect(clipR);
//			} else {
//				canvas.restore();
//				return;
//			}
        }
	    if (getMatrix() != null  && !getMatrix().isIdentity()) {
	        canvas.concat(getMatrix());
        }
	    canvas.clipRect(localRect);

            onDraw(canvas);
            
		canvas.restore();
	}
	}*/

//	public List<DrawableItem> getChildren() {
//		return items == null ? new ArrayList<DrawableItem>() : items;
//	}
	
	public int getChildCount() {
	    if (items != null) {
	        return items.size();
        }
	    return -1;
	}

//	@Override
//	public void updateMatrix(Matrix matrix) {
//		super.updateMatrix(matrix);
//		//calculateGlobalTouchRect();
//	}

	@Override
	public void calculateGlobalTouchRect() {
		super.calculateGlobalTouchRect();
		if (items != null) {
//			for (DrawableItem item : items) {
//				item.calculateGlobalTouchRect();
//			}
		    for (int i = 0; i < items.size(); i++) {
                final DrawableItem item = items.get(i);
                if (item != null) {
                    item.calculateGlobalTouchRect();
                }
            }
		}
	}
	
//	@Override
//	public void updateItem(long timeDelta) {
//        super.updateItem(timeDelta);
//        if (items != null && !items.isEmpty()) {
//            for (DrawableItem item : items) {
//                item.updateItem(timeDelta);
//            }
//        }
//    }
	
	@Override
	public boolean onDown(MotionEvent e) {

		boolean superRes = super.onDown(e);

        if (superRes) return true;
        
	    if (items != null && !items.isEmpty()) {
			ArrayList<DrawableItem> itemS = checkHitedItem(e);
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
            if (item != null && item.isVisible() && item.isTouchable()) {
                    if (item.onDown(e)) {
                        if (lastTouchedItem != null && lastTouchedItem != item) {
                            lastTouchedItem.onTouchCancel( e );
                        }
                        lastTouchedItem = item;
						return true;
            }
        }
			}
		}
		return superRes;
	}
	
	@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		boolean superRes = super.onFling(e1, e2, velocityX, velocityY);

        if (superRes) return true;
        
	    if (items != null && !items.isEmpty()) {
			ArrayList<DrawableItem> itemS = checkHitedItem(e2);
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
            if (item != null && item.isVisible() && item.isTouchable()) {
                    if (item.onFling(e1, e2, velocityX, velocityY)) {
                        if (lastTouchedItem != null && lastTouchedItem != item) {
                            lastTouchedItem.onTouchCancel( e2 );
                        }
                        lastTouchedItem = item;
						return true;
            }
        }
			}
		}
        return false;
	}
	
	@Override
    public boolean onLongPress(MotionEvent e) {

	    if (items != null && !items.isEmpty()) {
			ArrayList<DrawableItem> itemS = checkHitedItem(e);
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
            if (item != null && item.isVisible() && item.isTouchable()) {
                    if (item.onLongPress(e)) {
                        if (lastTouchedItem != null && lastTouchedItem != item) {
                            lastTouchedItem.onTouchCancel( e );
                        }
                        lastTouchedItem = item;
                        return true;
                    }
            }
        }
		}

        return super.onLongPress(e);
	}
	
	@Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float previousX, float previousY) {
		boolean superRes = super.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);

        if (superRes) return true;
        
	    if (items != null && !items.isEmpty()) {
            final ArrayList<DrawableItem> itemS = checkHitedItem(e2);
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
                if (item != null && item.isVisible() && item.isTouchable()) {
                    if (item.onScroll(e1, e2, distanceX, distanceY, previousX, previousY)) {
                        if (lastTouchedItem != null && lastTouchedItem != item) {
                            lastTouchedItem.onTouchCancel( e2 );
                        }
                        lastTouchedItem = item;
						return true;
                    }
            	}
			}
		}
	    
    	if (lastTouchedItem != null) {
            lastTouchedItem.onTouchCancel( e2 );
            lastTouchedItem = null;
        }

        return false;
	}
	
	@Override
    public boolean onShowPress(MotionEvent e) {
	    boolean superRes = super.onShowPress(e);
	    
	    if (superRes) return true;

	    if (items != null && !items.isEmpty()) {
			ArrayList<DrawableItem> itemS = checkHitedItem(e);
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
            if (item != null && item.isVisible() && item.isTouchable()) {
                    if (item.onShowPress(e)) {
                        if (lastTouchedItem != null && lastTouchedItem != item) {
                            lastTouchedItem.onTouchCancel( e );
                        }
                        lastTouchedItem = item;
                        return true;
            }
        }
		}
        }
        
        return false;
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {		

		boolean superRes = super.onSingleTapUp(e);

        if (superRes) return true;
        
	    if (items != null && !items.isEmpty()) {
			ArrayList<DrawableItem> itemS = checkHitedItem(e);
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
	        if (item != null && item.isVisible() && item.isTouchable()) {
                    if (item.onSingleTapUp(e)) {
                        if (lastTouchedItem != null && lastTouchedItem != item) {
                            lastTouchedItem.onTouchCancel( e );
                        }
                        lastTouchedItem = item;
						return true;
            }
        }
			}
		}
		return superRes;
	}
	
	@Override
	public boolean onFingerUp(MotionEvent e) {
		boolean superRes = super.onFingerUp(e);

        if (superRes) return true;
        
        if (items != null && !items.isEmpty()) {
            ArrayList<DrawableItem> itemS = checkHitedItem(e);
            /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . START***/
            if (itemS.isEmpty()) {
                if (lastTouchedItem != null) {
                    lastTouchedItem.onTouchCancel( e );
                }
                lastTouchedItem = null;
                return true;
            }
            /*** RK_ID: fixbug.  AUT: zhaoxy . DATE: 2013-02-18 . END***/
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
                if (item != null && item.isVisible() && item.isTouchable()) {
                    if (item.onFingerUp(e)) {
                        if (lastTouchedItem != null && lastTouchedItem != item) {
                            lastTouchedItem.onTouchCancel( e );
                        }
                        lastTouchedItem = item;
                        return true;
                    }
                }
            }
        }
		return superRes;
	}
	
   /*** MODIFYBY: zhaoxy . DATE: 2012-11-19 . START***/
	@Override
	public void onTouchCancel( MotionEvent e ) {
	    super.onTouchCancel( e );
	    if (items != null) {
            synchronized (items) {
                for (int i = items.size() - 1; i > -1; i--) {
                    final DrawableItem item = items.get(i);
                    if (item != null) {
                        item.onTouchCancel( e );
                    }
                }
	        }
        }
	    lastTouchedItem = null;
	}

   /*** MODIFYBY: zhaoxy . DATE: 2012-11-19 . END***/

    /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-21 . START */
    @Override
    public boolean onFingerCancel(MotionEvent e) {
        boolean res = super.onFingerCancel(e);
        if (res)
            return true;

        if (lastTouchedItem != null) {
            res |= lastTouchedItem.onFingerCancel(e);
        }
        if (items != null && !items.isEmpty()) {
            ArrayList<DrawableItem> itemS = checkHitedItem(e);
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
                if (item != null && item.isVisible() && item.isTouchable() && item != lastTouchedItem) {
                    if (item.onFingerCancel(e)) {
                        res = true;
                        break;
                    }
                }
            }
        }
        
        lastTouchedItem = null;
        return res;
    }

    /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-21 . END */
	
	protected ArrayList<DrawableItem> checkHitedItem(MotionEvent e) {
		ArrayList<DrawableItem> itemRes = new ArrayList<DrawableItem>();
        /*** MODIFYBY: zhaoxy . DATE: 2012-11-19 . START***/
		// if (lastTouchedItem != null) {
		// if (ExchangeManager.checkHited(lastTouchedItem, e.getX(), e.getY()))
		// {
		// return lastTouchedItem;
		// } else {
		// lastTouchedItem.onTouchCancel();
		// }
		// }
        /*** MODIFYBY: zhaoxy . DATE: 2012-11-19 . END***/
//        for (DrawableItem item : items) {
//            itemRes = item;
//            if (ExchangeManager.checkHited(itemRes, e.getX(), e.getY())) {
//                lastTouchedItem = itemRes;
//                return itemRes;
//            }
//        }
		if (e != null) {

			for (int i = 0; i < items.size(); i++) {
	            final DrawableItem item = items.get(i);
	            if (item != null) {
            	
	                ExchangeManager exchange = getXContext().getExchangee();
					if (item.isTouchable() && exchange != null && exchange.checkHited(item, e.getX(),
								e.getY())) {

						if (item.isDesiredTouchEventItem()) {
//							if(D2.DEBUG)
//								D2.echo("desire : "
//									+ item.getClass().getSimpleName());
							itemRes.clear();
							itemRes.add(item);
							return itemRes;
						}
						// lastTouchedItem = itemRes;
						// android.util.Log.i("sort",
						// "add item to check in group. item : " + item);
						itemRes.add(item);
					}
				}
			}
		}

        return itemRes;
    }
	
//    public void setAlpha(int alpha) {
//        super.setAlpha(alpha);
//        if (!isCached() && getChildCount() > 0) {
//            for (int i = 0; i < getChildCount(); i++) {
//                DrawableItem item = getChildAt(i);
//                if (item != null) {
//                    item.setAlpha(alpha);
//                }
//            }
//        }
//    }
    
    public static void echo(String msg) {
        Log.d(TAG, msg);
    }
    
    @Override
    public void destory() {
        super.destory();
        clearAllItems();
    }

    // because this item after removing,
    // it will be use again, so it shouldn't clean this time.
    public int removeItemWithoutClean(DrawableItem item) {
        if (item != null && items != null) {
            int index = items.indexOf(item);
            if (index > -1) {
                synchronized (items) {
                    items.remove(index);
                }
                if (lastTouchedItem == item) {
                    lastTouchedItem = null;
                }
                return index;
            }
        }
        return -1;
    }

    public void removeItemWithoutClean(int index) {
        if (items != null) {
            if (index > -1 && index < items.size()) {
                DrawableItem item = items.remove(index);
                if (lastTouchedItem == item) {
                    lastTouchedItem = null;
                }
            }
        }
    }
    
    @Override
    public void setVisibility(boolean visibility) {
    	super.setVisibility(visibility);
    	
    	for(int i = getChildCount() - 1; i >= 0 ; i --){
    		if(getChildAt(i).wantKnowVisibleState()){
    			final OnVisibilityChangeListener listener = getChildAt(i).getOnVisibilityChangeListener();
    			if(listener != null){
    				listener.onVisibilityChange( this, visibility);
    			}
    		}
    	}
    }
    
    @Override
    protected void wantKnowVisibleState(boolean wanted) {
    	super.wantKnowVisibleState(wanted);
    	
    	if(getParent() != null){
    		getParent().wantKnowVisibleState(wanted);
    	}
    }
        
    @Override
    public void resetPressedState(boolean clearPrePressed) {
    	super.resetPressedState(clearPrePressed);
    	for(int i = 0; i< getChildCount(); i ++){
			if( getChildAt(i) != null ) {
                getChildAt(i).resetPressedState(clearPrePressed);
			}
    	}
    }

    @Override
    public String dumpLayoutInfo() {
        StringBuffer toprint = new StringBuffer(super.dumpLayoutInfo()).append("\n{");
        for (int i = 0; i < items.size(); i++) {
            DrawableItem child = items.get(i);
            if (child != null) {
                toprint.append("\n  ").append(i).append(". <").append(child.getClass().getSimpleName()).append(">")
                .append(": ").append(child.dumpLayoutInfo());
                ;
            }
        }
        toprint.append("\n}");
        return toprint.toString();
    }
    
    @Override
	public boolean onDoubleTapped(MotionEvent e, DrawableItem tappedItem) {
		boolean superRes = super.onDoubleTapped(e, tappedItem);

		if (superRes)
			return true;

		if (items != null && !items.isEmpty()) {
			ArrayList<DrawableItem> itemS = checkHitedItem(e);
			for (int i = itemS.size() - 1; i > -1; i--) {
				final DrawableItem item = itemS.get(i);
				if (item != null && item.isVisible() && item.isTouchable()) {
					if (item.onDoubleTapped(e, item)) {
						if (lastTouchedItem != null && lastTouchedItem != item) {
							lastTouchedItem.onTouchCancel( e );
						}
						lastTouchedItem = item;
						return true;
					}
				}
			}
		}
		return superRes;
	}
    public boolean addItemWithoutFilter(DrawableItem item, int index){
		if (item != null) {
			if (items == null) {
				items = new ArrayList<DrawableItem>();
			}
            if (index < 0 || index > items.size()) {
                return false;
            }
			item.setPrent(this);
			if (DEBUG) echo("setPaint");
			item.setPaint(getPaint());
			if(index<items.size()){
				items.set(index, item);
			}else if(index==items.size()){
				items.add(item);
			}
			return true;
		}
		return false;
	}
    
    //dooba
    public void clearAllItemsNotDestroy() {
        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . START */
		if (items != null) {
			synchronized (items) {
				items.clear();
				lastTouchedItem = null;
			}
		}
        /* RK_ID: RK_ALLAPPS. AUT: liuli1 . DATE: 2012-11-13 . END */
	}
}
