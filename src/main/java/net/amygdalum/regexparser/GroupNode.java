package net.amygdalum.regexparser;

public class GroupNode implements RegexNode {

	private RegexNode subNode;
	private int groupNumber;

	public GroupNode(RegexNode subNode) {
		this.subNode = subNode;
		this.groupNumber = 0;
	}
	
	public GroupNode(RegexNode subNode, int groupNumber) {
		this.subNode = subNode;
		this.groupNumber = groupNumber;
	}

	public RegexNode getSubNode() {
		return subNode;
	}

	public boolean isCapturingGroup(){
		return groupNumber > 0;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	@Override
	public <T> T accept(RegexNodeVisitor<T> visitor) {
		return visitor.visitGroup(this);
	}

	@Override
	public GroupNode clone() {
		try {
			GroupNode clone = (GroupNode) super.clone();
			clone.subNode = subNode.clone();
			clone.groupNumber = groupNumber;
			return clone;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return '(' + subNode.toString() + ')';
	}

}
