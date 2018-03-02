package com.lenovo.launcher.components.XAllAppFace.slimengine;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.lenovo.launcher.components.XAllAppFace.utilities.Debug.R2;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class DrawingItemsPump {

	public class State {
		public boolean sLockedByExternalDrawing = false;
		public boolean sLockedByDataUpdating = false;
		public boolean sDataDirty = false;
		public boolean sDataReady = true;
	}

	public static final int MSG_ADD_DRAWING_ITEMS = 1;
	public static final int MSG_REMOVE_DRAWING_ITEMS = 2;
	public static final int MSG_EVENT_THREAD_EXIT = 3;

	private State mState = new State();

	private HandlerThread mWorkerThread;
	private Handler mWorker;

	private ConcurrentLinkedQueue<DrawableItem> drawingList;
	private ConcurrentLinkedQueue<DrawableItem> itemsToAdd;
	private ConcurrentLinkedQueue<DrawableItem> itemsToUpdate;
	private ConcurrentLinkedQueue<DrawableItem> itemsToRemove;

	public DrawingItemsPump() {
		init();
	}

	private void init() {

		mWorkerThread = new HandlerThread("DrawingPump-Thread");

		mWorkerThread.start();

		mWorker = new Handler(mWorkerThread.getLooper()) {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				if (msg.what == MSG_ADD_DRAWING_ITEMS) {
					performAdd();
				} else if (msg.what == MSG_REMOVE_DRAWING_ITEMS) {
					performRemove();
				} else if (msg.what == MSG_EVENT_THREAD_EXIT) {
					Looper.myLooper().quit();
					return;
				}
			}

			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
			}
		};
	}

	private void makeSureListNotNull() {
		if (drawingList == null) {
			drawingList = new ConcurrentLinkedQueue<DrawableItem>();
		}

		if (itemsToAdd == null) {
			itemsToAdd = new ConcurrentLinkedQueue<DrawableItem>();
		}

		if (itemsToRemove == null) {
			itemsToRemove = new ConcurrentLinkedQueue<DrawableItem>();
		}

		if (itemsToUpdate == null) {
			itemsToUpdate = new ConcurrentLinkedQueue<DrawableItem>();
		}
	}

	public synchronized void addDrawingItems(
			ConcurrentLinkedQueue<DrawableItem> itemsToAdd) {

		makeSureListNotNull();

		if (itemsToAdd != null && !itemsToAdd.isEmpty()) {
			this.itemsToAdd.addAll(itemsToAdd);
		}

		mWorker.removeMessages(MSG_ADD_DRAWING_ITEMS);
		mWorker.sendEmptyMessage(MSG_ADD_DRAWING_ITEMS);
	}

	public synchronized void addDrawingItem(DrawableItem itemToAdd) {
		ConcurrentLinkedQueue<DrawableItem> tmpList = new ConcurrentLinkedQueue<DrawableItem>();
		if (itemToAdd != null) {
			tmpList.add(itemToAdd);
		}
		this.addDrawingItems(tmpList);
	}

	public synchronized void removeDrawingItems(
			ConcurrentLinkedQueue<DrawableItem> itemsToRemove) {

		makeSureListNotNull();

		if (itemsToRemove != null && !itemsToRemove.isEmpty()) {
			this.itemsToRemove.addAll(itemsToRemove);
		}

		mWorker.removeMessages(MSG_REMOVE_DRAWING_ITEMS);
		mWorker.sendEmptyMessage(MSG_REMOVE_DRAWING_ITEMS);
	}

	public synchronized void removeDrawingItem(DrawableItem itemToRemove) {
		ConcurrentLinkedQueue<DrawableItem> tmpList = new ConcurrentLinkedQueue<DrawableItem>();
		if (itemToRemove != null) {
			tmpList.add(itemToRemove);
		}
		this.removeDrawingItems(tmpList);
	}

	private void performRemove() {

		// if (State.sLockedByExternalDrawing) {
		// return;
		// }

		// State.sLockedByDataUpdating = true;

		if (drawingList != null) {
			drawingList.removeAll(itemsToRemove);
			itemsToRemove.clear();
		}
		// State.sLockedByDataUpdating = false;
		//
		// RenderThread.getInstance().invalidate();
	}

	private void performAdd() {

		// if (State.sLockedByExternalDrawing) {
		// return;
		// }

		// State.sLockedByDataUpdating = true;

		drawingList.addAll(itemsToAdd);
		itemsToAdd.clear();
		mState.sLockedByDataUpdating = false;
		//
		// RenderThread.getInstance().invalidate();
	}

	/**
	 * offer the list and lock
	 * */
	public ConcurrentLinkedQueue<DrawableItem> offerAndLockDrawing() {
		makeSureListNotNull();
		mState.sLockedByExternalDrawing = true;
		return drawingList;
	}

	public void unLockDrawing() {
		mState.sLockedByExternalDrawing = false;
	}

	public void destroy() {
		mWorkerThread.getLooper().quit();
		clear();
	}

	public void clear() {

		if (drawingList != null) {
			drawingList.clear();
		}

		drawingList = null;

		if (mControllerQueue != null) {
			mControllerQueue.clear();
		}

		if (mWorker != null) {
			mWorker.removeMessages(MSG_ADD_DRAWING_ITEMS);
			mWorker.removeMessages(MSG_REMOVE_DRAWING_ITEMS);
			mWorker.sendEmptyMessage(MSG_REMOVE_DRAWING_ITEMS);
			mWorker.getLooper().quit();
			mWorker = null;
		}
	}

	private ConcurrentLinkedQueue<IController> mControllerQueue = new ConcurrentLinkedQueue<IController>();

	public ConcurrentLinkedQueue<IController> getControllers() {
		return mControllerQueue;
	}

	public void registerIController(IController controller) {
		if (controller != null && !mControllerQueue.contains(controller)) {
//			R2.printStack("D2");
			mControllerQueue.add(controller);
		}
	}

	public void unregisterIController(IController controller) {
//		R2.printStack("D2");
		mControllerQueue.remove(controller);
	}

}
