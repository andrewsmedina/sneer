package wheel.reactive;

import java.util.List;

import wheel.reactive.ListSignal.ListValueChange;
import wheel.reactive.ListSignal.ListValueChangeVisitor;

public final class ListReplaced<VO> implements ListValueChange<VO> {

	private final List<VO> _contents;

	public ListReplaced(List<VO> contents) {
		_contents = contents;
	}

	@Override
	public void accept(ListValueChangeVisitor<VO> visitor) {
		visitor.listReplaced(_contents);
	}
	
	@Override
	public String toString() {
		return "List replaced with: " + _contents.toString();
	}

}
