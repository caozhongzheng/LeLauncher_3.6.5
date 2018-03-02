package com.lenovo.lejingpin.share.download;

/**
 * 
 * @author philn
 * 
 */
public interface IDownloadListener {
	/**
	 * The system informs the caller that the method has the latest
	 */
	void update(DownloadInfo di);
}
