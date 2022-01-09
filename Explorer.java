public class Explorer extends Location{

	public int direction;
	public String directionStr;

	public Explorer(int x, int y, int direction){
		super(x,y);
		this.direction = direction;
	}
	public void move(int addX,int addY){
		setX(getX()+addX);
		setY(getY()+addY);
	}

	public int getDirection(){
		return direction;
	}

	public void setDirection(int direction){
		this.direction = direction;
	}
	public String getDirectionStr() {
		switch (direction) {
		case 1:
			return "EAST";
		case 2:
			return "SOUTH";
		case 3:
			return "WEST";
		case 4:
			return "NORTH";
		default:
			return "NA";
		}
	}






}