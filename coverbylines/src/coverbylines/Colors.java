package coverbylines;

import java.awt.Color;

public class Colors 
{
	static int next = 0;
	static Color[] colors = new Color[] { Color.BLUE, Color.RED, Color.GREEN, Color.BLACK, Color.DARK_GRAY, Color.MAGENTA, Color.ORANGE, Color.GRAY, Color.PINK, Color.CYAN, };
	public static Color get(int i) { return colors[i%colors.length]; }
	public static Color get() { return colors[next++%colors.length]; }

}
