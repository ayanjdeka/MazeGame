import java.awt.Color;
import java.awt.Polygon;

public class Wall extends Location{

	private String type;

	private Color startColor = Color.BLACK;
	private Color endColor = Color.BLACK;
	private Polygon polygon;

	private int distance;



	public Wall(int x, int y, String type){
		super(x,y);
		this.type = type;
	}

	public String getType(){
		return type;
	}



	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public Color getStartColor() {
		return startColor;
	}

	public void setStartColor(Color startColor) {
		this.startColor = startColor;
	}

	public Color getEndColor() {
		return endColor;
	}

	public void setEndColor(Color endColor) {
		this.endColor = endColor;
	}

	@Override
	public String toString() {
		return "Wall [type=" + type + ", startColor=" + startColor + ", endColor=" + endColor + ", polygon=" + polygon
				+ ", distance=" + distance + ", getX()=" + getX() + ", getY()=" + getY() + "]";
	}


}

