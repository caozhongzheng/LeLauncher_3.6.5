/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lenovo.launcher2.commoninterface;

import java.util.ArrayList;

import com.lenovo.launcher2.commoninterface.LauncherSettings.Favorites;

import android.content.ContentValues;
import android.graphics.Bitmap;

/**
 * Represents a folder containing shortcuts or apps.
 */
public class FolderInfo extends ItemInfo {

    /**
     * Whether this folder has been opened
     */
    public boolean opened;

    /**
     * The folder name.
     */
    public CharSequence title;

    /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-03-26 . START */
    /**
     * user customize icon.
     */
    public Bitmap mReplaceIcon;
    /* RK_ID: RK_QUICKACTION. AUT: liuli1 . DATE: 2012-03-26 . END */

    /**
     * The apps and shortcuts
     */
    public ArrayList<ShortcutInfo> contents = new ArrayList<ShortcutInfo>();

    public ArrayList<FolderListener> listeners = new ArrayList<FolderListener>();

    public FolderInfo() {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_FOLDER;
    }

    /**
     * Add an app or shortcut
     *
     * @param item
     */
    public void add(ShortcutInfo item) {
        contents.add(item);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onAdd(item);
        }
        itemsChanged();
    }

    /**
     * Remove an app or shortcut. Does not change the DB.
     *
     * @param item
     */
    public void remove(ShortcutInfo item) {
        contents.remove(item);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onRemove(item);
        }
        itemsChanged();
    }
    
    public boolean contains(ItemInfo item) {
        if (item == null) {
            return false;
        }
        if (item instanceof ShortcutInfo) {
            for (int i = 0; i < contents.size(); i++) {
                ShortcutInfo info = contents.get(i);
                if (info != null && info.equalsIgnorePosition((ShortcutInfo) item)) {
                    return true;
                }
            }
        } else if (item instanceof ApplicationInfo) {
            for (int i = 0; i < contents.size(); i++) {
                ShortcutInfo info = contents.get(i);
                if (info != null && ((ApplicationInfo) item).componentName.equals(info.intent.getComponent())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTitleChanged(title);
        }
    }

    @Override
    public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put(LauncherSettings.Favorites.TITLE, title.toString());
        /* RK_ID: RK_QUICKACTION . AUT: liuli1 . DATE: 2012-04-13 . START */
        writeReplaceBitmap(values, mReplaceIcon);
        /* RK_ID: RK_QUICKACTION . AUT: liuli1 . DATE: 2012-04-13 . END */
    }

    public void addListener(FolderListener listener) {
        listeners.add(listener);
    }

    public void removeListener(FolderListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }
    
    public void clearListener() {
        listeners.clear();
    }

    void itemsChanged() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onItemsChanged();
        }
    }

    @Override
    public void unbind() {
        super.unbind();
        listeners.clear();
    }

    public interface FolderListener {
        public void onAdd(ShortcutInfo item);
        public void onRemove(ShortcutInfo item);
        public void onTitleChanged(CharSequence title);
        public void onItemsChanged();
    }

    public FolderInfo copy() {
        FolderInfo newInfo = new FolderInfo();

        newInfo.id = this.id;
        newInfo.cellX = this.cellX;
        newInfo.cellY = this.cellY;
        newInfo.spanX = this.spanX;
        newInfo.spanY = this.spanY;
        newInfo.screen = this.screen;
        newInfo.itemType = this.itemType;
        newInfo.container = this.container;
        newInfo.attachedIndexArray = this.attachedIndexArray;

        newInfo.opened = this.opened;
        newInfo.title = this.title;
        newInfo.mReplaceIcon = this.mReplaceIcon;
        newInfo.contents = this.contents;
        newInfo.listeners = this.listeners;

        return newInfo;
    }

    @SuppressWarnings("unchecked")
    public FolderInfo clone() {
        FolderInfo newInfo = new FolderInfo();

        newInfo.id = this.id;
        newInfo.cellX = this.cellX;
        newInfo.cellY = this.cellY;
        newInfo.spanX = this.spanX;
        newInfo.spanY = this.spanY;
        newInfo.screen = this.screen;
        newInfo.itemType = this.itemType;
        newInfo.container = this.container;
        newInfo.attachedIndexArray = this.attachedIndexArray;

        newInfo.opened = this.opened;
        newInfo.title = this.title;
        if (this.mReplaceIcon != null) {
            newInfo.mReplaceIcon = this.mReplaceIcon.copy(this.mReplaceIcon.getConfig(), this.mReplaceIcon.isMutable());
        }
        if (this.contents != null) {
            newInfo.contents = (ArrayList<ShortcutInfo>) this.contents.clone();
        }
        if (this.listeners != null) {
            newInfo.listeners = (ArrayList<FolderListener>) this.listeners.clone();
        }

        return newInfo;
    }
}
