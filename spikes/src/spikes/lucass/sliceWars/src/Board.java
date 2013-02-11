package spikes.lucass.sliceWars.src;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Board {

	private Map<BoardCell, List<BoardCell>> linkedBoardCells = new LinkedHashMap<BoardCell, List<BoardCell>>();
	
	public void createAndAddToBoardCellForPolygon(Polygon polygon) {
		BoardCell cell = new BoardCell(polygon);
		linkedBoardCells.put(cell, new ArrayList<BoardCell>());
	}
	
	public void addCell(BoardCell boardCell) {
		linkedBoardCells.put(boardCell, new ArrayList<BoardCell>());
	}

	public Set<BoardCell> getBoardCells(){
		return linkedBoardCells.keySet();
	}
	
	public void link(Polygon polygon1, Polygon polygon2) {
		BoardCell boardCell1 = getForPolygon(polygon1);
		BoardCell boardCell2 = getForPolygon(polygon2);
		List<BoardCell> polygon1LinkedCells = linkedBoardCells.get(boardCell1);
		polygon1LinkedCells.add(boardCell2);
		List<BoardCell> polygon2LinkedCells = linkedBoardCells.get(boardCell2);
		polygon2LinkedCells.add(boardCell1);
	}
	
	private BoardCell getForPolygon(Polygon p){
		Set<BoardCell> keySet = linkedBoardCells.keySet();
		for (BoardCell boardCell : keySet) {
			if(boardCell.polygon.equals(p)){
				return boardCell;
			}
		}
		return null;
	}

	public boolean areLinked(Polygon polygon1, Polygon polygon2) {
		Set<BoardCell> keySet = linkedBoardCells.keySet();
		for (BoardCell boardCell : keySet) {
			if(boardCell.polygon.equals(polygon1)){
				List<BoardCell> list = linkedBoardCells.get(boardCell);
				for (BoardCell boardCell2 : list) {
					if(boardCell2.polygon.equals(polygon2))
						return true;
				}
				return false;
			}
		}
		return false;
	}

	public Polygon getPolygonAt(int x, int y) {
		Set<BoardCell> keySet = linkedBoardCells.keySet();
		for (BoardCell boardCell : keySet) {
			if(boardCell.polygon.contains(x,y))
				return boardCell.polygon;
		}
		return null;
	}


}
