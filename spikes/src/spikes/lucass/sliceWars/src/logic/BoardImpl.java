package spikes.lucass.sliceWars.src.logic;

import java.awt.Polygon;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


public class BoardImpl implements Board{

	private Map<BoardCell, Set<BoardCell>> linkedBoardCells = new LinkedHashMap<BoardCell, Set<BoardCell>>();
	
	public BoardCell createAndAddToBoardCellForPolygon(Polygon polygon) {
		BoardCell cell = new BoardCellImpl(polygon);
		linkedBoardCells.put(cell, new LinkedHashSet<BoardCell>());
		return cell;
	}
	
	public void addCell(BoardCellImpl boardCell) {
		linkedBoardCells.put(boardCell, new LinkedHashSet<BoardCell>());
	}

	@Override
	public Set<BoardCell> getBoardCells(){
		return linkedBoardCells.keySet();
	}
	
	public void link(BoardCell boardCell1,BoardCell boardCell2) {
		Set<BoardCell> polygon1LinkedCells = linkedBoardCells.get(boardCell1);
		polygon1LinkedCells.add(boardCell2);
		Set<BoardCell> polygon2LinkedCells = linkedBoardCells.get(boardCell2);
		polygon2LinkedCells.add(boardCell1);
	}
	
	public void link(Polygon polygon1, Polygon polygon2) {
		BoardCell boardCell1 = getForPolygon(polygon1);
		BoardCell boardCell2 = getForPolygon(polygon2);
		link(boardCell1, boardCell2);
	}
	
	private BoardCell getForPolygon(Polygon p){
		Set<BoardCell> keySet = linkedBoardCells.keySet();
		for (BoardCell boardCell : keySet) {
			if(boardCell.getPolygon().equals(p)){
				return boardCell;
			}
		}
		return null;
	}

	@Override
	public boolean areLinked(BoardCell c1, BoardCell c2) {
		Set<BoardCell> set = linkedBoardCells.get(c1);
		if(set == null) return false;
		return set.contains(c2);
	}

	public Polygon getPolygonAt(int x, int y) {
		Set<BoardCell> keySet = linkedBoardCells.keySet();
		for (BoardCell boardCell : keySet) {
			if(boardCell.getPolygon().contains(x,y))
				return boardCell.getPolygon();
		}
		return null;
	}

	@Override
	public BoardCell getCellAtOrNull(int x, int y) {
		Set<BoardCell> keySet = linkedBoardCells.keySet();
		for (BoardCell boardCell : keySet) {
			if(boardCell.getPolygon().contains(x,y))
				return boardCell;
		}
		return null;
	}

	@Override
	public boolean isFilled() {
		Set<BoardCell> boardCells = getBoardCells();
		for (BoardCell boardCell : boardCells) {
			if(boardCell.getOwner().equals(Player.EMPTY))
				return false;
		}
		return true;
	}

	@Override
	public int getCellCount() {
		return linkedBoardCells.size();
	}

	@Override
	public int getBiggestLinkedCellCountForPlayer(Player player) {
		Set<BoardCell> boardCells = getBoardCells();
		int count = 0;
		for (BoardCell boardCell : boardCells) {
			if(boardCell.getOwner().equals(player)){
				int newCount = getLinkedCellCount(boardCell);
				count = Math.max(count, newCount);
			}
		}
		return count;
	}

	private int getLinkedCellCount(BoardCell boardCell) {
		Set<BoardCell> alreadyCounted = new LinkedHashSet<BoardCell>();
		return getLinkedCellCount(boardCell,alreadyCounted);
	}
	
	private int getLinkedCellCount(BoardCell boardCell,Set<BoardCell> alreadyCounted) {
		if(alreadyCounted.contains(boardCell)) return 0;
		alreadyCounted.add(boardCell);
		Set<BoardCell> linked = linkedBoardCells.get(boardCell);
		
		int sum = 1;
		
		for (BoardCell boardCellLinked : linked) {
			if(boardCellLinked.getOwner().equals(boardCell.getOwner())){				
				sum += getLinkedCellCount(boardCellLinked,alreadyCounted);
			}
		}
		return sum;
	}

	@Override
	public boolean areaAllCellsFilled(Player player) {
		Set<BoardCell> boardCells = getBoardCells();
		for (BoardCell boardCell : boardCells) {
			if(boardCell.getOwner().equals(player)){				
				if(boardCell.getDiceCount()<Cell.MAX_DICE)
					return false;
			}
		}
		return true;
	}

	@Override
	public void remove(BoardCell cell) {
		Set<BoardCell> boardCells = getBoardCells();
		for (BoardCell boardCell : boardCells) {
			linkedBoardCells.get(boardCell).remove(cell);
		}
		linkedBoardCells.remove(cell);
	}


}