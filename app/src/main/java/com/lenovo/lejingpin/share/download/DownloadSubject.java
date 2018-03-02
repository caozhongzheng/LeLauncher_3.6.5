package com.lenovo.lejingpin.share.download;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;

/**
 * @author philn
 */
abstract class AbstractSubject {
	private static final String TAG = "xujing3";
	private Map<DownloadInfo, ArrayList<IDownloadListener>> observers = new ConcurrentHashMap<DownloadInfo, ArrayList<IDownloadListener>>();

	public void setDownloadListener(DownloadInfo di, IDownloadListener dl) {
		synchronized (observers) {
			if (null == di) {
				return;
			}
			ArrayList<IDownloadListener> listener = observers.get(di);
			if (null != listener) {
				listener.add(dl);
			} else {
				listener = new ArrayList<IDownloadListener>();
				listener.add(dl);
			}
			observers.put(di, listener);
		}
	}

	public void setDownloadListener(Context context, DownloadInfo di,
			IDownloadListener dl) {
		synchronized (observers) {
			if (null == di) {
				return;
			}
			ArrayList<IDownloadListener> listener = observers.get(di);
			if (null != listener) {
				listener.add(dl);
			} else {
				listener = new ArrayList<IDownloadListener>();
				listener.add(dl);
			}
			observers.put(di, listener);
			DownloadInfo newItem = DownloadHelpers.getDownloadInfo(context, di);
			if (null != newItem) {
				DownloadSubject.getInstance().changes(newItem);
			}
		}
	}

	public void cancelListener(DownloadInfo di, IDownloadListener dl) {
		synchronized (observers) {
			if (null == di) {
				return;
			}
			if (null == dl) {
				observers.remove(di);
			} else {
				ArrayList<IDownloadListener> listener = observers.get(di);
				if (null != listener) {
					listener.remove(dl);
					if (listener.size() == 0) {
						observers.remove(di);
					}
				}
			}
		}
	}

	public synchronized void cancelListener() {
		observers.clear();
	}

	public void notifyObderver(DownloadInfo di) {
		synchronized (observers) {
			if (null == di)
				return;
			if (observers.containsKey(di)) {
				ArrayList<IDownloadListener> listeners = observers.get(di);
				if (null != listeners) {
					for (IDownloadListener listener : listeners) {
						try {
							if (null != listener) {
								listener.update(di);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}

		}
	}

}

/**
 * Download a specific theme
 */
public class DownloadSubject extends AbstractSubject {
	private static DownloadSubject sp = null;

	private DownloadSubject() {
	}

	public static DownloadSubject getInstance() {
		if (null == sp) {
			sp = new DownloadSubject();
		}
		return sp;
	}

	public void changes(DownloadInfo di) {
		this.notifyObderver(di);
	}
}
