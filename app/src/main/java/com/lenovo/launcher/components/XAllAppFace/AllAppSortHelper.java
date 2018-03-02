package com.lenovo.launcher.components.XAllAppFace;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.commoninterface.LauncherSettings;
import com.lenovo.launcher2.commoninterface.UsageStatsMonitor;
import com.lenovo.launcher2.commoninterface.LauncherSettings.Applications;
import com.lenovo.launcher2.customizer.RegularApplist;

public class AllAppSortHelper {

    public static boolean isOutOfControl = false;

    public static final int HISTORY_COMPARATOR = -1;
    public static final int REGULAR_COMPARATOR = HISTORY_COMPARATOR + 1;
    public static final int NAME_COMPARATOR = REGULAR_COMPARATOR + 1;
    public static final int FIRST_INSTALL_COMPARATOR_ASC = NAME_COMPARATOR + 1;
    public static final int FIRST_INSTALL_COMPARATOR_DES = FIRST_INSTALL_COMPARATOR_ASC + 1;
    public static final int LAUNCH_COUNT_COMPARATOR = FIRST_INSTALL_COMPARATOR_DES + 1;
    public static final int LAST_RESUME_TIME_COMPARATOR = LAUNCH_COUNT_COMPARATOR + 1;

    private HashMap<String, Integer> cannotDragList = null;
    private HashMap<String, Integer> regularList = null;
    private HashMap<String, Integer> dbList = new HashMap<String, Integer>();
    private HashMap<String, Boolean> cannotDiyList = null;
    private static final Collator sCollator = Collator.getInstance();

    private Comparator<ApplicationInfo> APPS_HISTORY_COMPARATOR = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(ApplicationInfo a, ApplicationInfo b) {
            String aName = a.componentName.flattenToShortString();
            String bName = b.componentName.flattenToShortString();
            Integer aIndex = cannotDragList.get(aName);
            if (aIndex != null) {
                // a cannot drag
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b also cannot drag
                    return aIndex - bIndex;
                } else {
                    // b behind a
                    return -1;
                }
            } else {
                // a can drag
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b cannot drag, so a behind b
                    return 1;
                } else {
                    // both a and b can drag or be not in list
                    aIndex = dbList.get(aName);
                    bIndex = dbList.get(bName);
                    if (aIndex != null) {
                        if (bIndex != null) {
                            // a and b in the normal list
                            return aIndex - bIndex;
                        } else {
                            // b must at last
                            return -1;
                        }
                    } else {
                        // a be not in normal list
                        if (bIndex != null) {
                            // but b in normal list
                            return 1;
                        } else {
                            // both a and b not in the normal list, compare by
                            // name
                            int result = sCollator.compare(a.title.toString(), b.title.toString());
                            if (result == 0) {
                                result = a.componentName.compareTo(b.componentName);
                            }
                            return result;
                        }
                    }
                }
            }
        }
    };
    /*** AUT: zhaoxy . DATE: 2012-05-09. START ***/
    private Comparator<ApplicationInfo> APPS_NAME_COMPARATOR = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(ApplicationInfo a, ApplicationInfo b) {
            String aName = a.componentName.flattenToShortString();
            String bName = b.componentName.flattenToShortString();
            Integer aIndex = cannotDragList.get(aName);
            if (aIndex != null) {
                // a in regularList
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b also cannot drag
                    return aIndex - bIndex;
                } else {
                    // b behind a
                    return -1;
                }
            } else {
                // a not in regularList
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b cannot drag, so a behind b
                    return 1;
                } else {
                    // both a and b can drag or be not in list
                    return XLauncherModel.APP_NAME_COMPARATOR.compare(a, b);
                }
            }
        }

    };
    /*** AUT: zhaoxy . DATE: 2012-05-09. END ***/

    private Comparator<ApplicationInfo> APPS_REGULAR_COMPARATOR = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(ApplicationInfo a, ApplicationInfo b) {
            String aName = a.componentName.flattenToShortString();
            String bName = b.componentName.flattenToShortString();
            Integer aIndex = regularList.get(aName);
            if (aIndex != null) {
                // a in regularList
                Integer bIndex = regularList.get(bName);
                if (bIndex != null) {
                    // b also cannot drag
                    return aIndex - bIndex;
                } else {
                    // b behind a
                    return -1;
                }
            } else {
                // a not in regularList
                Integer bIndex = regularList.get(bName);
                if (bIndex != null) {
                    // b cannot drag, so a behind b
                    return 1;
                } else {
                    // both a and b can drag or be not in list
                    return XLauncherModel.APP_FIRST_INSTALL_COMPARATOR_ASC.compare(a, b);
                }
            }
        }
    };

    private Comparator<ApplicationInfo> APPS_FIRST_INSTALL_COMPARATOR_ASC = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(ApplicationInfo a, ApplicationInfo b) {
            String aName = a.componentName.flattenToShortString();
            String bName = b.componentName.flattenToShortString();
            Integer aIndex = cannotDragList.get(aName);
            if (aIndex != null) {
                // a in regularList
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b also cannot drag
                    return aIndex - bIndex;
                } else {
                    // b behind a
                    return -1;
                }
            } else {
                // a not in regularList
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b cannot drag, so a behind b
                    return 1;
                } else {
                    // both a and b can drag or be not in list
                    int result = a.firstInstallTime > b.firstInstallTime ? 1 : a.firstInstallTime < b.firstInstallTime ? -1 : 0;
                    if (result == 0) {
                        result = sCollator.compare(a.title.toString(), b.title.toString());
                        if (result == 0) {
                            result = a.componentName.compareTo(b.componentName);
                        }
                    }
                    return result;
                }
            }
        }

    };

    private Comparator<ApplicationInfo> APPS_FIRST_INSTALL_COMPARATOR_DES = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(ApplicationInfo a, ApplicationInfo b) {
            String aName = a.componentName.flattenToShortString();
            String bName = b.componentName.flattenToShortString();
            Integer aIndex = cannotDragList.get(aName);
            if (aIndex != null) {
                // a in regularList
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b also cannot drag
                    return aIndex - bIndex;
                } else {
                    // b behind a
                    return -1;
                }
            } else {
                // a not in regularList
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b cannot drag, so a behind b
                    return 1;
                } else {
                    // both a and b can drag or be not in list
                    int result = a.firstInstallTime > b.firstInstallTime ? -1 : a.firstInstallTime < b.firstInstallTime ? 1 : 0;
                    if (result == 0) {
                        result = sCollator.compare(a.title.toString(), b.title.toString());
                        if (result == 0) {
                            result = a.componentName.compareTo(b.componentName);
                        }
                    }
                    return result;
                }
            }
        }

    };

    private Comparator<ApplicationInfo> APPS_LAUNCH_COUNT_COMPARATOR = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(ApplicationInfo a, ApplicationInfo b) {
            String aName = a.componentName.flattenToShortString();
            String bName = b.componentName.flattenToShortString();
            Integer aIndex = cannotDragList.get(aName);
            if (aIndex != null) {
                // a in regularList
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b also cannot drag
                    return aIndex - bIndex;
                } else {
                    // b behind a
                    return -1;
                }
            } else {
                // a not in regularList
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b cannot drag, so a behind b
                    return 1;
                } else {
                    // both a and b can drag or be not in list
                    return UsageStatsMonitor.APP_LAUNCH_COUNT_COMPARATOR.compare(a, b);
                }
            }
        }

    };

    private Comparator<ApplicationInfo> APPS_LAST_RESUME_TIME_COMPARATOR = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(ApplicationInfo a, ApplicationInfo b) {
            String aName = a.componentName.flattenToShortString();
            String bName = b.componentName.flattenToShortString();
            Integer aIndex = cannotDragList.get(aName);
            if (aIndex != null) {
                // a in regularList
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b also cannot drag
                    return aIndex - bIndex;
                } else {
                    // b behind a
                    return -1;
                }
            } else {
                // a not in regularList
                Integer bIndex = cannotDragList.get(bName);
                if (bIndex != null) {
                    // b cannot drag, so a behind b
                    return 1;
                } else {
                    // both a and b can drag or be not in list
                    return UsageStatsMonitor.APP_LAST_RESUME_TIME_COMPARATOR.compare(a, b);
                }
            }
        }

    };

    private LauncherApplication mContext;

    public AllAppSortHelper(Application mContext) {
        this.mContext = (LauncherApplication)mContext;
    }

    public static boolean isValid(int sortMode) {
        return sortMode >= HISTORY_COMPARATOR && sortMode <= LAST_RESUME_TIME_COMPARATOR;
    }

    public boolean checkCannotDrag(ApplicationInfo info) {
        if (cannotDragList == null) {
            cannotDragList = mContext.getModel().getRegularApplist().getRegularList(true);
        }
        Integer index = cannotDragList.get(info.componentName.flattenToShortString());
        if (index != null) {
            return true;
        } else {
            return false;
        }
    }
    
    public void checkAndSetCanDragInfo(ArrayList<ApplicationInfo> mApps) {
        ApplicationInfo info;
        for (int i = 0; i < mApps.size(); i++) {
            info = mApps.get(i);
            if (checkCannotDrag(info)) {
                info.canDrag = false;
            } else {
                info.canDrag = true;
            }
        }
    }

    /*** RK_ID: CANNOTDIY.  AUT: zhaoxy . DATE: 2012-09-14 . START***/
    public boolean checkCannotDiy(ComponentName cmp) {
        if (cmp != null) {
            return checkCannotDiy(cmp.flattenToShortString());
        }
        return false;
    }

    public boolean checkCannotDiy(String packagename) {
        if (cannotDiyList == null) {
            cannotDiyList = mContext.getModel().getRegularApplist().getCannotDiyRule();
        }
        Boolean cannotdiy = cannotDiyList.get(packagename);
        if (cannotdiy == null) {
            return false;
        } else {
            return cannotdiy;
        }
    }
    /*** RK_ID: CANNOTDIY.  AUT: zhaoxy . DATE: 2012-09-14 . END***/

    public void updateFromDb() {
        /*** AUT: zhaoxy . DATE: 2012-05-09. START ***/
        if (cannotDragList == null) {
            cannotDragList = mContext.getModel().getRegularApplist().getRegularList(true);
        }
        /*** AUT: zhaoxy . DATE: 2012-05-09. END ***/
        long start = System.currentTimeMillis();
        Cursor c = mContext.getContentResolver().query(LauncherSettings.Applications.CONTENT_URI, null, null, null,
                LauncherSettings.Applications.CELL_INDEX + " ASC");
        if (c == null) {
            return;
        }
        dbList.clear();
        try {
            if (c.moveToFirst()) {
                final int classIndex = c.getColumnIndexOrThrow(LauncherSettings.Applications.CLASS);
                final int cellIndex = c.getColumnIndexOrThrow(LauncherSettings.Applications.CELL_INDEX);
                final int dragIndex = c.getColumnIndex(LauncherSettings.Applications.APP_CAN_DRAG);
                do {
                    boolean drag = true;
                    if (dragIndex > -1) {
                        drag = c.getInt(dragIndex) == 1;
                    }
                    if (!drag) {
                        continue;
                    }
                    String className = c.getString(classIndex);
                    int index = c.getInt(cellIndex);
                    dbList.put(className, index);
                } while (c.moveToNext());
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
        long end = System.currentTimeMillis();
        Log.d("SequencCache", "updateFromDb takes " + (end - start) + " ms");
    }

    public Comparator<ApplicationInfo> getComparator(int index) {
        switch (index) {
        case REGULAR_COMPARATOR:
            if (regularList == null) {
                regularList = mContext.getModel().getRegularApplist().getRegularList(false);
            }
            return APPS_REGULAR_COMPARATOR;
        case NAME_COMPARATOR:
            if (cannotDragList == null) {
                cannotDragList = mContext.getModel().getRegularApplist().getRegularList(true);
            }
            return APPS_NAME_COMPARATOR;
        
        case FIRST_INSTALL_COMPARATOR_ASC:
            if (cannotDragList == null) {
                cannotDragList = mContext.getModel().getRegularApplist().getRegularList(true);
            }
            return APPS_FIRST_INSTALL_COMPARATOR_ASC;
        case FIRST_INSTALL_COMPARATOR_DES:
            if (cannotDragList == null) {
                cannotDragList = mContext.getModel().getRegularApplist().getRegularList(true);
            }
            return APPS_FIRST_INSTALL_COMPARATOR_DES;
        case LAUNCH_COUNT_COMPARATOR:
            if (cannotDragList == null) {
                cannotDragList = mContext.getModel().getRegularApplist().getRegularList(true);
            }
            return APPS_LAUNCH_COUNT_COMPARATOR;
        case LAST_RESUME_TIME_COMPARATOR:
            if (cannotDragList == null) {
                cannotDragList = mContext.getModel().getRegularApplist().getRegularList(true);
            }
            return APPS_LAST_RESUME_TIME_COMPARATOR;
        case HISTORY_COMPARATOR:
        default:
            if (cannotDragList == null) {
                cannotDragList = mContext.getModel().getRegularApplist().getRegularList(true);
            }
            updateFromDb();
            return APPS_HISTORY_COMPARATOR;
        }
    }
}
