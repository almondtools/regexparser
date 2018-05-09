package net.amygdalum.regexparser;

import static net.amygdalum.util.text.CharUtils.after;
import static net.amygdalum.util.text.CharUtils.before;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class CharNode implements RegexNode {

	public abstract List<DefinedCharNode> toCharNodes();

	@Override
	public CharNode clone() {
		try {
			return (CharNode) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public static List<DefinedCharNode> computeComplement(List<? extends DefinedCharNode> nodes, List<DefinedCharNode> allnodes) {
		List<DefinedCharNode> remainderNodes = new LinkedList<DefinedCharNode>();
		if (allnodes.isEmpty()) {
			return remainderNodes;
		}
		Collections.sort(nodes);
		Collections.sort(allnodes);
		Iterator<DefinedCharNode> allnodeIterator = allnodes.iterator();
		DefinedCharNode currentRange = allnodeIterator.next();
		for (DefinedCharNode node : nodes) {
			while (!node.cuts(currentRange) && allnodeIterator.hasNext()) {
				remainderNodes.add(currentRange);
				currentRange = allnodeIterator.next();
			}
			if (node.cuts(currentRange)) {
				if (currentRange.getFrom() < node.getFrom()) {
					remainderNodes.add(new RangeCharNode(currentRange.getFrom(), before(node.getFrom())));
				}
				if (node.getTo() < currentRange.getTo()) {
					currentRange = new RangeCharNode(after(node.getTo()), currentRange.getTo());
				} else if (allnodeIterator.hasNext()) {
					currentRange = allnodeIterator.next();
				} else {
					currentRange = null;
					break;
				}
			}
		}
		while (allnodeIterator.hasNext()) {
			remainderNodes.add(currentRange);
			currentRange = allnodeIterator.next();
		}
		if (currentRange != null) {
			remainderNodes.add(currentRange);
		}
		return remainderNodes;
	}

}
