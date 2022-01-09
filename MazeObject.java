import java.util.*;
public class MazeObject{

	private List<List<Wall>> walls;

	public MazeObject(List<List<Wall>> walls){
		this.walls = walls;
	}

	public List<List<Wall>> getWalls(){
		return walls;
	}

	public void printToScreen(){
		for(int i=0;i<walls.size();i++){
			List<Wall> colWall = walls.get(i);
			for(int j=0;j<colWall.size();j++){
				Wall pos = colWall.get(j);
				System.out.print(pos.getType());
			}
			System.out.println();
		}
	}


}