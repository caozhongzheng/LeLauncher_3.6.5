package com.lenovo.algorithm.reorder;

import java.util.Iterator;
import java.util.LinkedList;

import android.util.Log;

import com.lenovo.algorithm.reorder.Reorder.SwapItem;
import com.lenovo.algorithm.reorder.Reorder.Type;
import com.weibo.sdk.android.api.WeiboAPI.TYPE;


public class EX_Z_Reorder implements ReoderInterface {
	private class SpaceInfo{
		public Type t = Type.noinit;
		public int emptyCount = 0;
		public int chessmanCount =0;
		public boolean isMoveEmpty = false;
		public SpaceInfo(Type t,int emptyCount,int chessmanCount){
			this.t = t;
			this.emptyCount = emptyCount;
			this.chessmanCount = chessmanCount;
		}
	}
	
	private class StrtuctInfo{
		public SwapItem it;
		public int x;
		public int y;
		public StrtuctInfo(SwapItem item,int x,int y){
			it = item;
			this.x =x;
			this.y = y;
			
		}
	}
	

	@Override
	public boolean reorder(SwapItem[][] occupied) {
		
		if(occupied == null){
			return false;
		}
		int x_length = occupied.length;

		SpaceInfo[]priority = new SpaceInfo [x_length];
		if(!initSapceInfo(occupied, priority) ){
			return false;
		}
		
		LinkedList<StrtuctInfo> ls = new LinkedList<StrtuctInfo>();
		SwapItem emptyItem = null ;
		int empty_x=-1;
		int empty_y =-1;
		boolean firstEmpty = false;
		boolean hasStone = false;
		for(int x=0;x<x_length;x++){
			int y_length = occupied[x].length;
			for(int y=0;y<y_length;y++){
				if(occupied[x][y].t == Type.empty && !firstEmpty && priority[x].t ==Type.chessman ){
					empty_x = x;
					empty_y = y;
					emptyItem = occupied[x][y];
					firstEmpty = true;
					Log.d("sort", "firstEmpty  ["+empty_x+","+empty_y+"]" +"is empty \n\n" );

				}


				
				if(occupied[x][y].t == Type.chessman){
					if(firstEmpty /**&&  priority[empty_x].t !=Type.stone**/){
						Log.d("sort", "move  ["+empty_x+","+empty_y+"]" +"is empty \n\n" );
						Log.d("sort", "move  ["+x+","+y+"]" +"is empty \n\n" );

						occupied[empty_x][empty_y] = occupied[x][y];
						occupied[x][y]= emptyItem;
						updateSapceInfo(priority,empty_x,x);
						boolean hasEmpty = false;
						do{
							
							empty_y = (empty_y+1)%y_length;
							if(empty_y ==0){
								empty_x +=1;
							}
							Log.d("sort", priority[empty_x].t.toString() );
							Log.d("sort", occupied[empty_x][empty_y].t.toString() );
							if((priority[empty_x].t ==Type.chessman || priority[empty_x].isMoveEmpty) && occupied[empty_x][empty_y].t == Type.empty   ){
								hasEmpty = true;
								Log.d("sort", "["+empty_x+","+empty_y+"]" +"is first empty" );
								break;
							}
							Log.d("sort", "["+empty_x+","+empty_y+"]" +"is not empty" );
						}while(empty_x<x ||(empty_x==x && empty_y<y)  );
						firstEmpty = hasEmpty;
					}
				}
				
				
				if( priority[x].t ==Type.stone &&occupied[x][y].t == Type.chessman ){
					Log.d("sort", "["+x+","+y+"]" +"is  stone chessman" );
					hasStone = true;
					ls.add(new StrtuctInfo(occupied[x][y], x, y));
					
				}
				
				if(hasStone  &&y==y_length-1 &&priority[x+1].t !=Type.stone ){
					hasStone = false;
					int [] res = findEmptySapce(x+1,priority,occupied);
					Log.d("sort", "res[0] =" + res[0] + "res[1] =" + res[1] + "res[2] =" + res[2]);
					Iterator<StrtuctInfo>it = ls.iterator();
					Log.d("sort", "ls.size() =" + ls.size());
					
					int count =ls.size() - res[0]-res[2]-res[1];
					int count1 = count >0 ?res[1] : ls.size() - res[0]-res[2];
					int count2 = count1 >0? res[0] :ls.size() ;
					StrtuctInfo s ;
					while(it.hasNext()){
						s = it.next();
						if(count >0){
							
							count --;
						}else if(count1 >0){
							swapItem_ex(x+1,occupied,priority,s);
							count1 --;
						}else if(count2 >0){
							swapItem(x+1,occupied,priority,s);
							count2 --;
						}
						it.remove();
						
					}
					
					/**
					if(res[0]>=ls.size()){
						StrtuctInfo s ;
						while(it.hasNext()){
							s = it.next();
							Log.d("sort", "["+s.x+","+s.y+"]" +"is  StrtuctInfo chessman" );
							swapItem(x+1,occupied,priority,s);
						}
						ls.clear();
						
					}else if(res[0] + res[2]>=ls.size()){
						int i=0;
						while(it.hasNext()){
							swapItem(x+1,occupied,priority,it.next());
							i++;
							it.remove();
							if(i ==res[0]){
								break;
							}
						}
					}else if(res[0] + res[2]+res[1] >ls.size()){
						
						int count = ls.size() - res[0]-res[2];
						Log.d("sort", "count =" + count);
						int i=0;
						while(it.hasNext()){
							if(i <count){
								swapItem_ex(x+1,occupied,priority,it.next());
							}else{
								swapItem(x+1,occupied,priority,it.next());
							}
							i++;
							it.remove();
							if(i ==(res[0] + res[1])){
								break;
							}
						}

						
					}else{
						int count =  ls.size() - res[0]-res[2]-res[1];
						int i =0;
						StrtuctInfo s ;
						while(it.hasNext()){
							s = it.next();
							if(i< count){
								it.remove();
							}else{
								i=0;
								if(i <res[2]){
									swapItem_ex(x+1,occupied,priority,it.next());
								}else{
									swapItem(x+1,occupied,priority,it.next());
								}
								
								it.remove();
								if(i ==(res[0] + res[1])){
									break;
								}
							}
							i++;
						}
						
					}**/
				}
				
				
				
				
			}
			
		}
		
		
		return true;
	
	}
	
	

	
	private boolean initSapceInfo(SwapItem[][] occupied,SpaceInfo[]priority){
		if(priority == null || occupied == null){
			return false;
		}
		int chessmanCount =0;
		int emptyCount =0;
//		Type lastStatus  = Type.noinit;
		Type  status = Type.noinit;
		int x_length = occupied.length;
		for(int x=0;x<x_length;x++){
			int y_length = occupied[x].length;
			for(int y=0;y<y_length;y++){
				if(occupied[x][y].t == Type.empty ){
					emptyCount ++;
					status = status ==Type.noinit?Type.empty:status;
				}else if(occupied[x][y].t == Type.chessman){
					chessmanCount ++;
					status = status!=Type.stone ? Type.chessman:status;
				}else{
					status = Type.stone;
				}
			}
			priority[x] = new SpaceInfo(status,emptyCount,chessmanCount);
			Log.d("sort", "init"+priority[x].t.toString() );
			emptyCount =0;
			chessmanCount =0;
			status = Type.noinit;

		}
		return true;
	}
	
	private void  updateSapceInfo(SpaceInfo[]priority,int x1,int x2){
		if(priority[x1].emptyCount >0){
			priority[x1].emptyCount--;
			priority[x1].chessmanCount++;
		}
		
		if(priority[x2].chessmanCount >1 || priority[x2].t == Type.stone){
			priority[x2].emptyCount++;
			priority[x2].chessmanCount--;
		}else{
			priority[x2].emptyCount++;
			priority[x2].chessmanCount=0;
			priority[x2].t = Type.empty;
			priority[x2].isMoveEmpty = true;
			Log.d("sort", "updateSapceInfo  "+priority[x2].t.toString() );
		}
		
	}
	
	
	private int[] findEmptySapce(int x_startIndex,SpaceInfo[]priority,SwapItem[][] occupied){
		int x_length = occupied.length;
		boolean meetStone = false;
		int empty_count_1 =0;
		int chessman_count_1 =0;
		int stone_count=0;
		int stone_all_empty_count=0;
		for(int x=x_startIndex;x<x_length;x++){
			if(priority[x].t == Type.empty && !meetStone){
				empty_count_1 +=priority[x].emptyCount;
				Log.d("sort", "findEmptySapce  "+x+"行" +"is 空行" +"    empty_count_1  =" + empty_count_1);
			}else if(priority[x].t == Type.chessman && !meetStone){
				chessman_count_1 +=priority[x].emptyCount; 
				Log.d("sort", "findEmptySapce  "+x+"行" +"is no空行" +"    empty_count_1  =" + empty_count_1);
			}else if(priority[x].t == Type.stone){
				meetStone =true;
				Log.d("sort", "findEmptySapce meet stone");
				
				stone_count += priority[x].chessmanCount;
				
			}else {
				Log.d("sort", "findEmptySapce meet  other");

				stone_all_empty_count  += priority[x].emptyCount;
			}
		}
		stone_all_empty_count = stone_all_empty_count -stone_count;
		int res[] = new int []{chessman_count_1,empty_count_1,stone_all_empty_count};
		return res;
	}
	
	private void swapItem_ex(int x,SwapItem[][] occupied,SpaceInfo[]priority,StrtuctInfo info){ 
		int x_length = occupied.length;
		for(int x1 =x ;x1<x_length;x1++){
			int y_length = occupied[x].length;
			if(priority[x1].t == Type.stone){
				break;
			}else if(priority[x1].t != Type.stone){
				for(int y=0;y<y_length;y++){
					if(occupied[x1][y].t == Type.empty){
						Log.d("sort", "old location" + "x =" + info.x + "      y =" + info.y);
						Log.d("sort", "find location" + "x1 =" + x1 + "      y =" + y);
						occupied[info.x][info.y] = occupied[x1][y];
						priority[info.x].chessmanCount --;
						priority[info.x].emptyCount++;
						occupied[x1][y] = info.it;
						priority[x1].chessmanCount ++;
						priority[x1].emptyCount--;
						if(priority[x1].chessmanCount >0){
							priority[x1].t = Type.chessman;
						}

						return;
					}
				}
			}

		}
		
	}
	
	private void swapItem(int x,SwapItem[][] occupied,SpaceInfo[]priority,StrtuctInfo info){
		int x_length = occupied.length;
		for(int x1 =x ;x1<x_length;x1++){
			int y_length = occupied[x].length;
			if(priority[x1].t == Type.stone){
				break;
			}else if(priority[x1].t == Type.chessman){
				for(int y=0;y<y_length;y++){
					if(occupied[x1][y].t == Type.empty){
						Log.d("sort", "old location" + "x =" + info.x + "      y =" + info.y);
						Log.d("sort", "find location" + "x1 =" + x1 + "      y =" + y);
						occupied[info.x][info.y] = occupied[x1][y];
						priority[info.x].chessmanCount --;
						priority[info.x].emptyCount++;
						occupied[x1][y] = info.it;
						priority[x1].chessmanCount ++;
						priority[x1].emptyCount--;
						return;
					}
				}
			}

		}
		
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

/*
 * 		
		if(occupied == null){
			return;
		}
		
		int x_length = occupied.length;
		SpaceInfo[]priority = new SpaceInfo [x_length];
		if(!initSapceInfo(occupied, priority) ){
			return;
		}
		
		boolean firstEmpty = false;
		int empty_x=-1;
		int empty_y =-1;
		SwapItem emptyItem = null;
		
		for(int x=0;x<x_length;x++){
			if(priority[x].t ==Type.empty){
				continue;
			}else if(priority[x].t ==Type.stone){
				//stone
				
//				for()
			}else{
				//chessman
				int y_length = occupied[x].length;
				for(int y=0;y<y_length;y++){
					if(occupied[x][y].t == Type.empty && !firstEmpty ){
						empty_x = x;
						empty_y = y;
						emptyItem = occupied[x][y];
						firstEmpty = true;
					}else if(occupied[x][y].t == Type.chessman){
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
		}
		
		
 * */
