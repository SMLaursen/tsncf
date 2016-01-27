package dk.smlaursen.TSNCF.architecture;

/**Vertices in a network should implement this class*/
public abstract class Node {
	protected String aId;

	@Override
	public boolean equals(Object other){
		boolean result;
		if(other == null || getClass() != other.getClass()){
			result = false;
		} else {
			Node otherNode = (Node) other;
			result = aId.equals(otherNode.aId);
		}
		return result;
	}

	@Override
	public int hashCode(){
		return this.getClass().getName().hashCode()+aId.hashCode();
	}

	@Override
	public String toString(){
		return aId;
	}
}
