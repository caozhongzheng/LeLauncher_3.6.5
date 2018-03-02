package com.lenovo.algorithm.reorder;

import android.util.Log;

import com.lenovo.algorithm.reorder.Reorder.SwapItem;
import com.lenovo.algorithm.reorder.Reorder.Type;

public class Z_Reorder implements ReoderInterface {

	@Override
	public boolean reorder(SwapItem[][] occupied) {
		if(occupied == null){
			return false;
		}
		SwapItem emptyItem = null ;
		int empty_x=-1;
		int empty_y =-1;
		boolean firstEmpty = false;
		int x_length = occupied.length;
		for(int x=0;x<x_length;x++){
			int y_length = occupied[x].length;
			for(int y=0;y<y_length;y++){
				if(occupied[x][y].t == Type.empty && !firstEmpty ){
					empty_x = x;
					empty_y = y;
					emptyItem = occupied[x][y];
					firstEmpty = true;
				}
				if(occupied[x][y].t == Type.chessman){
					if(firstEmpty){
						Log.d("sort", "["+empty_x+","+empty_y+"]" +"is chessman \n\n" );
						occupied[empty_x][empty_y] = occupied[x][y];
						occupied[x][y]= emptyItem;
						//we should jump over stone
						do{
							
							empty_y = (empty_y+1)%y_length;
							if(empty_y ==0){
								empty_x +=1;
							}
							Log.d("sort", "["+empty_x+","+empty_y+"]" +"is stone" );
						}while(occupied[empty_x][empty_y].t == Type.stone);
					}else{
						continue;
					}
					
				}
				
			}
			
		}
		
		return true;
	}

	@Override
	public boolean reorderAll(SwapItem[][][] occupied, boolean acrossPage) {
		return false;
	}

	@Override
	public boolean reorderReverse(SwapItem[][] occupied) {
		return false;
	}

}
