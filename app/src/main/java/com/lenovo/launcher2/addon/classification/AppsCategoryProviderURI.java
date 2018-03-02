package com.lenovo.launcher2.addon.classification;

import com.lenovo.launcher2.LauncherProvider;
import com.lenovo.launcher2.customizer.Constants;

import android.net.Uri;
import android.provider.BaseColumns;


public class AppsCategoryProviderURI {
    
    private AppsCategoryProviderURI(){
        
    }
    
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI_CATEGORY_AND_APPS = Uri.parse("content://" + AppsCategoryProvider.AUTHORITY + "/"
            + AppsCategoryProvider.CATEGORY_AND_APPS_URL);
    
    public static final Uri CONTENT_URI_CATEGORY_BY_PACKAGE_NAME = Uri.parse("content://" + AppsCategoryProvider.AUTHORITY + "/"
            + AppsCategoryProvider.CATEGORY_BY_PACKAGE_NAME);
    

    public static final String PACKAGE_NAME = "pkgname";

    public static final String CATEGORY_P = "cate_p";
    
    public static final String CATEGORY_ID = "id";
    
    public static final String CATEGORY_NAME = "cate_name";
    
    
    public static final class Categories implements BaseColumns {
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + Constants.AUTHORITY + "/"
                + LauncherProvider.TABLE_CATEGORY);

        static Uri getContentUri(long id) {
            return Uri.parse("content://" + Constants.AUTHORITY + "/" + LauncherProvider.TABLE_CATEGORY + "/"
                    + id);
        }

        /**
         * The name of the category
         * <P>Type: TEXT</P>
         */
        public static final String NAME = "name";

        /**
         * The name of the category
         * <P>Type: TEXT</P>
         */
        public static final String DRAWABLE = "drawable_name";

        /**
         * The extra column
         * <P>Type: TEXT</P>
         */
        public static final String EXT_COLUMN_1 = "ext_column_1";
    }
    
    public static final class CategoriesMaps implements BaseColumns {
        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + Constants.AUTHORITY + "/"
                + LauncherProvider.TABLE_CATEGORY_MAP);

        /**
         * The component name of the application
         * <P>Type: TEXT</P>
         */
        public static final String COMPONENT_NAME = "componentName";

        /**
         * The category _id value of application
         * <P>Type: Long</P>
         */
        public static final String CATEGORY_ID = "categoryId";

        /**
         * The position of the application
         * <P>Type: INTEGER</P>
         */
        public static final String POSITION = "position";

        /**
         * The extra column
         * <P>Type: TEXT</P>
         */
        public static final String EXT_COLUMN_1 = "ext_column_1";
    }
    
    
}
