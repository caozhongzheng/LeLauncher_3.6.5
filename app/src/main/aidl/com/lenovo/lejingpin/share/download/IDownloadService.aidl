package com.lenovo.lejingpin.share.download;
import com.lenovo.lejingpin.share.download.DownloadInfo;
import java.util.List;

interface IDownloadService {	  
      String addTask(in DownloadInfo appInfo);
      boolean startTask(in DownloadInfo appInfo);
      boolean pauseTask(in DownloadInfo appInfo);
      boolean resumeTask(in DownloadInfo appInfo);
      boolean deleteTask(in DownloadInfo appInfo);
      boolean reDownloadTask(in DownloadInfo appInfo);
      DownloadInfo getDownloadInfo(in DownloadInfo info);
      int getAllDownloadCount();
}