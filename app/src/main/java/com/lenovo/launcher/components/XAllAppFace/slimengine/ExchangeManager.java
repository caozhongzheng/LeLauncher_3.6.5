package com.lenovo.launcher.components.XAllAppFace.slimengine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.graphics.Matrix;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;

import com.lenovo.launcher.components.XAllAppFace.slimengine.LGestureDetector.OnDoubleTapListener;
import com.lenovo.launcher.components.XAllAppFace.slimengine.LGestureDetector.OnGestureListener;
import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.D2;

public class ExchangeManager implements OnGestureListener, OnDoubleTapListener {

	private DrawingItemsPump pumper = new DrawingItemsPump();
	private LGestureDetector gestureDetector;
	private float[] mTouchPoints = new float[2];
	private DrawableItem lastTouchedItem = null;

	private XContext mContext;

	public ExchangeManager(XContext context) {
		init(context);
	}

	public void init(XContext context) {
		mContext = context;
		gestureDetector = new LGestureDetector(mContext.getContext(), this);
	}

    /*** fixbug . AUT: zhaoxy . DATE: 2013-03-20 . START ***/
    public void ignoreOnceLongpress() {
        if (gestureDetector != null) {
            gestureDetector.ignoreOnceLongpress();
        }
    }
    /*** fixbug . AUT: zhaoxy . DATE: 2013-03-20 . END ***/

	MotionEvent[] eventsQueque = new MotionEvent[15];
	int currPointer = 0;
	
	public MotionEvent[] getEventQueque() {
		return eventsQueque;
	}

	public int getEventPointer() {
		return currPointer;
	}

	public void injectMontionEvent(MotionEvent e1) {
		currPointer++;
		if (currPointer >= eventsQueque.length - 1) {
			currPointer = 0;
		}
		eventsQueque[currPointer] = MotionEvent.obtain(e1);
		
		mContext.getRenderer().eventWorker.mNeedRepeat = true;
		mContext.getRenderer().eventWorker.wakeupEventHandler();

//		mContext.getRenderer().invalidate();
	}
	
	public void updateItem(long timeDelta) {
//	    for (DrawableItem item : pumper.offerAndLockDrawing()) {
//            item.updateItem(timeDelta);
//        }
//        pumper.unLockDrawing();
	    final ConcurrentLinkedQueue<IController> mControllers = pumper.getControllers();
	    for (IController controller : mControllers) {
            if (controller != null) {
                controller.update(timeDelta);
            }
        }
	}

	public void draw(IDisplayProcess disProc) {
		for (DrawableItem item : pumper.offerAndLockDrawing()) {
			item.draw(disProc);
		}
		pumper.unLockDrawing();
	}

//	private boolean minterruptLongPressed = false;
	private int lastAction = -1;
	
	public void onTouchEvent(MotionEvent event) {
	    if (event == null)
	        return;
	    if (event.getAction() == MotionEvent.ACTION_UP) {
	        if (lastAction == MotionEvent.ACTION_UP) {
	        	lastAction = -1;
	            return;
            }
        }
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        if (lastAction == MotionEvent.ACTION_DOWN) {
	        	lastAction = -1;
	            return;
            }
        }
	    lastAction = event.getAction();
//	    R2.echo("ee onTouchEvent event = " + lastAction);
		gestureDetector.onTouchEvent(MotionEvent.obtain(event));
//		if (minterruptLongPressed) {
////			gestureDetector = new GestureDetector(mContext.getContext(), this);
//		    gestureDetector.setIsLongpressEnabled(false);
//			MotionEvent down = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, event.getX(), event.getY(), 0);
//			gestureDetector.onTouchEvent(down);
//			minterruptLongPressed = false;
//		}
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			onFingerUp(event);
			gestureDetector.setIsLongpressEnabled(true);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            onFingerCancel(event);
            gestureDetector.setIsLongpressEnabled(true);
        }
				
//		mContext.getRenderer().invalidate();
	}

	@Override
	public boolean onDown(MotionEvent e) {
//		android.util.Log.i("touch", "onDown onScroll .");
		final ArrayList<DrawableItem> itemS = checkHitedItem(e);
        for (int i = itemS.size() - 1; i > -1; i--) {
            final DrawableItem item = itemS.get(i);
		if (item != null && item.isVisible() && item.isTouchable()) {
				boolean res = item.onDown(e);
				if (res) {
                    lastTouchedItem = item;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
//		android.util.Log.i("touch", "onShowPress onScroll .");
		final ArrayList<DrawableItem> itemS = checkHitedItem(e);
        for (int i = itemS.size() - 1; i > -1; i--) {
            final DrawableItem item = itemS.get(i);
		if (item != null && item.isVisible() && item.isTouchable()) {
                lastTouchedItem = item;
			item.onShowPress(e);
		}
	}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		android.util.Log.i("exchangee" , "inited with new exchangee.");
		final ArrayList<DrawableItem> itemS = checkHitedItem(e);
		for (int i = itemS.size() - 1; i > -1; i--) {
            final DrawableItem item = itemS.get(i);
		if (item != null && item.isVisible() && item.isTouchable()) {
				boolean res = item.onSingleTapUp(e);
				if (res) {
					//RK_TOUCH_SOUND dining 2013-0826
                    if(item.getXContext() != null){
						
						item.getXContext().playSoundEffect(SoundEffectConstants.CLICK);
					}
                  //RK_TOUCH_SOUND dining 2013-0826 END
				    lastTouchedItem = item;
					return true;
				}
			}
		}
		return false;
	}
	
	 public void onTouchCancel( MotionEvent e ) {
         if (lastTouchedItem != null) {
             lastTouchedItem.onTouchCancel( e );
         }
     }

	@Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float previousX, float previousY) {
//		android.util.Log.i("touch", "onScroll .");
		final ArrayList<DrawableItem> itemS = checkHitedItem(e2);
//		android.util.Log.i("touch", "itemS-----------______________>   " + itemS);
        for (int i = itemS.size() - 1; i > -1; i--) {
            final DrawableItem item = itemS.get(i);
		if (item != null && item.isVisible() && item.isTouchable()) {
				boolean res = item.onScroll(e1, e2, distanceX, distanceY, previousX, previousY);
				if (res) {
                    lastTouchedItem = item;
                    return true;
                }
            }
        }
//		android.util.Log.i("touch", "res false , ");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
//		android.util.Log.i("touch", "onLong pressed ." + LGestureDetector.getLongPressTimeout());
		final ArrayList<DrawableItem> itemS = checkHitedItem(e);
        for (int i = itemS.size() - 1; i > -1; i--) {
            final DrawableItem item = itemS.get(i);
		if (item != null && item.isVisible() && item.isTouchable()) {
			item.onLongPress(e);
			}
		}
	}
	
	public void interruptLongPress() {
//		this.minterruptLongPressed = true;
		gestureDetector.cancelLongPress();
	}
	
	private ArrayList<DrawableItem> filterDesireTouchEvent(ArrayList<DrawableItem> itemListToFilter){
		if( itemListToFilter == null || itemListToFilter.isEmpty() ){
			return itemListToFilter;
		}
		
		final ArrayList<DrawableItem> newList = new ArrayList<DrawableItem>();
		for(DrawableItem item : itemListToFilter){
			if(item.isDesiredTouchEventItem()){
				newList.add(item);
				return newList;
			}
		}
		
		return itemListToFilter;
	}

	@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//		android.util.Log.i("touch", "onFling pressed .");
		ArrayList<DrawableItem> itemS = checkHitedItem(e2);
		itemS = filterDesireTouchEvent(itemS);
        for (int i = itemS.size() - 1; i > -1; i--) {
            final DrawableItem item = itemS.get(i);
		if (item != null && item.isVisible() && item.isTouchable()) {
				boolean res = item.onFling(e1, e2, velocityX, velocityY);
				if (res) {
                    lastTouchedItem = item;
					return true;
				}
			}
		}
		return false;
	}
	
	private ArrayList<DrawableItem> checkHitedItem(MotionEvent e) {
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
		// for (DrawableItem item : items) {
		// itemRes = item;
		// if (ExchangeManager.checkHited(itemRes, e.getX(), e.getY())) {
		// lastTouchedItem = itemRes;
		// return itemRes;
		// }
		// }
		final ArrayList<DrawableItem> itemRes = new ArrayList<DrawableItem>();
		if( e == null ){
//			android.util.Log.i("sort", "event is : NULLL____________________________");
			return itemRes; 
		}
		final ConcurrentLinkedQueue<DrawableItem> drawingList = pumper
				.offerAndLockDrawing();
		final Iterator<DrawableItem> it = drawingList.iterator();
		while (it.hasNext()) {
			final DrawableItem item = it.next();
			
			if (checkHited(item, e.getX(), e.getY())) {
				// lastTouchedItem = itemRes;
//				android.util.Log.i("sort", "added item to check . : " + item);
				itemRes.add(item);
            }
		}

		return itemRes;
	}
	
	public boolean checkHited(DrawableItem item, float x, float y) {
	    if (item != null && item.isVisible() && item.isTouchable() ) {
	        mTouchPoints[0] = x;
            mTouchPoints[1] = y;
            Matrix resMatrix = item.getInvertMatrix();
            if(resMatrix== null) {
            	return false;
            }
            resMatrix.mapPoints(mTouchPoints);
            
            // for extra touch delagate rect
            if(item.hasExtraTouchBounds()){
//            	D2.echo("WOW" + item.getExtraTouchBounds() + "  , " + item.localRect );
            	boolean resLocal = item.localRect.contains(mTouchPoints[0], mTouchPoints[1]);
            	boolean resExtra = item.getExtraTouchBounds().contains(mTouchPoints[0], mTouchPoints[1]);
            	return resLocal || resExtra;
            }
            return item.localRect.contains(mTouchPoints[0], mTouchPoints[1]);
	    }
	    return false;
	}

	public boolean checkHited(DrawableItem item, float x, float y,
			boolean checkTouch) {
		if (item != null && item.isVisible()
				&& (!checkTouch || item.isTouchable())) {
			mTouchPoints[0] = x;
			mTouchPoints[1] = y;
			Matrix resMatrix = item.getInvertMatrix();
            if(resMatrix== null) {
            	return false;
            }
            resMatrix.mapPoints(mTouchPoints);

			// for extra touch delagate rect
			if (item.hasExtraTouchBounds()) {
				// D2.echo("WOW" + item.getExtraTouchBounds() + "  , " +
				// item.localRect );
				boolean resLocal = item.localRect.contains(mTouchPoints[0],
						mTouchPoints[1]);
				boolean resExtra = item.getExtraTouchBounds().contains(
						mTouchPoints[0], mTouchPoints[1]);
				return resLocal || resExtra;
			}
			return item.localRect.contains(mTouchPoints[0], mTouchPoints[1]);
		}
		return false;
	}

	public boolean onFingerUp(MotionEvent e) {
		//android.util.Log.i("touch", "onFingerUp pressed 1.");
		final ArrayList<DrawableItem> itemS = checkHitedItem(e);
		if (itemS.size() < 1) {
            if (lastTouchedItem != null) {
                lastTouchedItem.onTouchCancel( e );
                lastTouchedItem = null;
            }
        } else {
            for (int i = itemS.size() - 1; i > -1; i--) {
                final DrawableItem item = itemS.get(i);
                if (item != null && item.isVisible() && item.isTouchable()) {
                    boolean res = item.onFingerUp(e);
                    if (res) {
                        if (lastTouchedItem != item) {
                            if (lastTouchedItem != null) {
                                lastTouchedItem.onTouchCancel( e );
                            }
                            lastTouchedItem = item;
                        }
                        return true;
                    }
                }
            }
            if (lastTouchedItem != null) {
                lastTouchedItem.onTouchCancel( e );
                lastTouchedItem = null;
            }
        }
//		android.util.Log.i("touch", "onFingerUp pressed 2.");
		return false;
	}

	public void clear(){
		pumper.clear();
	}

    /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-21 . START */
    private void onFingerCancel(MotionEvent e) {
        if (lastTouchedItem != null) {
            lastTouchedItem.onFingerCancel(e);
        }

        final ArrayList<DrawableItem> itemS = checkHitedItem(e);
        for (int i = itemS.size() - 1; i > -1; i--) {
            final DrawableItem item = itemS.get(i);
            if (item != null && item.isVisible() && item.isTouchable() && item != lastTouchedItem) {
                item.onFingerCancel(e);
            }
        }
        
        lastTouchedItem = null;
    }
    /* RK_ID: RK_TOUCHEVENT. AUT: liuli1 . DATE: 2012-12-21 . END */

	public DrawingItemsPump getDrawingPass() {
		return pumper;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		final ArrayList<DrawableItem> itemS = checkHitedItem(e);
		for (int i = itemS.size() - 1; i > -1; i--) {
            final DrawableItem item = itemS.get(i);
		if (item != null && item.isVisible() && item.isTouchable()) {
				boolean res = item.onDoubleTapped( e, item );
				if (res) {
				    lastTouchedItem = item;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public LGestureDetector getLGestureDetector(){
		return gestureDetector;
	}

}
