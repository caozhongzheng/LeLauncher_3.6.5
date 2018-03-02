package com.lenovo.launcher.components.XAllAppFace;

import com.lenovo.launcher.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.ViewDebug;
import android.view.ViewGroup;

public class XCellLayout {
	
	public static int[] rectToCell(Resources resources, int width, int height, int[] result, int maxSpanX, int maxSpanY) {
        // Always assume we're working with the smallest span to make sure we
        // reserve enough space in both orientations.
        int actualWidth = resources.getDimensionPixelSize(R.dimen.workspace_cell_width);
        int actualHeight = resources.getDimensionPixelSize(R.dimen.workspace_cell_height);
        int smallerSize = Math.min(actualWidth, actualHeight);

        // Always round up to next largest cell
        int spanX = (int) Math.ceil(width / (float) smallerSize);
        int spanY = (int) Math.ceil(height / (float) smallerSize);
        
        if (spanX > maxSpanX) {
        	spanX = maxSpanX;
        }
        if (spanY > maxSpanY) {
        	spanY = maxSpanY;
        }

        if (result == null) {
            return new int[] { spanX, spanY };
        }
        result[0] = spanX;
        result[1] = spanY;
        return result;
    }
	
	public static boolean findVacantCell(int[] vacant, int spanX, int spanY,
            int xCount, int yCount, boolean[][] occupied) {

        for (int y = 0; y < yCount; y++) {
            for (int x = 0; x < xCount; x++) {
                boolean available = !occupied[x][y];
out:            for (int i = x; i < x + spanX - 1 && x < xCount; i++) {
                    for (int j = y; j < y + spanY - 1 && y < yCount; j++) {
                        available = available && !occupied[i][j];
                        if (!available) break out;
                    }
                }

                if (available) {
                    vacant[0] = x;
                    vacant[1] = y;
                    return true;
                }
            }
        }

        return false;
    }
	
	public static class XLayoutParams extends ViewGroup.MarginLayoutParams {
	    /**
	     * Horizontal location of the item in the grid.
	     */
	    @ViewDebug.ExportedProperty
	    public int cellX;

	    /**
	     * Vertical location of the item in the grid.
	     */
	    @ViewDebug.ExportedProperty
	    public int cellY;	    
	    /**
	     * Number of cells spanned horizontally by the item.
	     */
	    @ViewDebug.ExportedProperty
	    public int cellHSpan;

	    /**
	     * Number of cells spanned vertically by the item.
	     */
	    @ViewDebug.ExportedProperty
	    public int cellVSpan;

	    /**
	     * Indicates whether the item will set its x, y, width and height parameters freely,
	     * or whether these will be computed based on cellX, cellY, cellHSpan and cellVSpan.
	     */
	    public boolean isLockedToGrid = true;
	   
	    // X coordinate of the view in the layout.
	    @ViewDebug.ExportedProperty
	    int x;
	    // Y coordinate of the view in the layout.
	    @ViewDebug.ExportedProperty
	    int y;

	    boolean dropped;

	    public XLayoutParams(Context c, AttributeSet attrs) {
	        super(c, attrs);
	        cellHSpan = 1;
	        cellVSpan = 1;
	    }

	    public XLayoutParams(ViewGroup.LayoutParams source) {
	        super(source);
	        cellHSpan = 1;
	        cellVSpan = 1;
	    }

	    public XLayoutParams(XLayoutParams source) {
	        super(source);
	        this.cellX = source.cellX;
	        this.cellY = source.cellY;
	        this.cellHSpan = source.cellHSpan;
	        this.cellVSpan = source.cellVSpan;
	    }

	    public XLayoutParams(int cellX, int cellY, int cellHSpan, int cellVSpan) {
	        super(XLayoutParams.MATCH_PARENT, XLayoutParams.MATCH_PARENT);
	        this.cellX = cellX;
	        this.cellY = cellY;
	        this.cellHSpan = cellHSpan;
	        this.cellVSpan = cellVSpan;
	    }

	    public void setup(int cellWidth, int cellHeight, int widthGap, int heightGap) {
	        if (isLockedToGrid) {
	            final int myCellHSpan = cellHSpan;
	            final int myCellVSpan = cellVSpan;
				
	            width = myCellHSpan * cellWidth + ((myCellHSpan - 1) * widthGap) -
	                    leftMargin - rightMargin;
	            height = myCellVSpan * cellHeight + ((myCellVSpan - 1) * heightGap) -
	                    topMargin - bottomMargin;
	            
	        }
	    }

	    public String toString() {
	        return "(" + this.cellX + ", " + this.cellY + ")";
	    }

	    public void setWidth(int width) {
	        this.width = width;
	    }

	    public int getWidth() {
	        return width;
	    }

	    public void setHeight(int height) {
	        this.height = height;
	    }

	    public int getHeight() {
	        return height;
	    }

	    public void setX(int x) {
	        this.x = x;
	    }

	    public int getX() {
	        return x;
	    }

	    public void setY(int y) {
	        this.y = y;
	    }

	    public int getY() {
	        return y;
	    }
	}

	
}

