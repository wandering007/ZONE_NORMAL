import java.awt.*;
import java.util.List;
import java.util.*;

import javax.swing.*;

public class BudVis extends JPanel
{
	private static final long serialVersionUID = 1L;
	public final static int StartX = 40;
	public final static int StartY = 100;
	public final int H = 5;
	public final int total_size = 220 * 1024 * 4;
	public static int used_size = 0,  inter_frag = 0;
	final static Color[] ColorArray = {Color.orange, Color.cyan, Color.gray,
			Color.lightGray, Color.magenta, Color.darkGray, Color.yellow,
			Color.pink, Color.blue, Color.red, Color.green}; //11 kinds
	public List< ProDesc > ProRec = new LinkedList< ProDesc >();

	public void  paintComponent(Graphics g)
	{
		super.paintComponent(g);
//		System.out.println("drawing");
		g.drawRect(StartX, StartY, 1025, 220 * H + 1);
		g.setColor(Color.white);
		g.fillRect(StartX + 1, StartY + 1, 1024, 220 * H);
		int Length = ProRec.size();
		long current_time = System.currentTimeMillis();
		used_size = 0;
		inter_frag = 0;
		for(int i = 0; i < Length; ++i)
		{
			ProDesc tmp = ProRec.get(i);
			int order = tmp.getOrder();
			if(tmp.getStayTime() <= current_time - tmp.getStartTime())
			{
				if(0 == order)
					BudSys.free_page(tmp.getStartAddr());
				else
					BudSys.free_pages_bulk(tmp);
				BudSys.pid[tmp.getPID()] = false;
				ProRec.remove(i);
				i = i - 1;
				Length = Length - 1;
				continue;
			}
			int x = tmp.getStartAddr() % 1024 + StartX + 1;
			int y = tmp.getStartAddr() / 1024 * H + StartY + 1;
			int number = Pow2(order);
			used_size += number;
			inter_frag += number - tmp.getReqSpace();
			if(number <= 1025 + StartX - x)
			{
				g.setColor(Color.black);
				if(number <= 2)
				{
					g.setColor(ColorArray[order]);
					g.fillRect(x, y, number, H);
				}
				else
				{
					g.drawRect(x, y, number - 1, H - 1);
					g.setColor(ColorArray[order]);
					g.fillRect(x + 1, y + 1, number - 2, H - 2);
				}
				
			}
			else
			{
				g.setColor(Color.black);
				if(1025 + StartX - x <= 2)
				{
					g.setColor(ColorArray[order]);
					g.fillRect(x + 1, y + 1, 1025 + StartX - x, H);
				}
				else
				{
					g.drawRect(x, y, 1024 + StartX - x, H - 1);
					g.setColor(ColorArray[order]);
					g.fillRect(x + 1, y + 1, 1023 + StartX - x, H - 2);
				}
				g.setColor(Color.black);
				if(number - (1025 + StartX - x) <= 2)
				{
					g.fillRect(StartX + 2, y + 2, number - (1025 + StartX - x), H);
				}
				else
				{
					g.drawRect(StartX + 1, y + 1, number - (1026 + StartX - x), H - 1);
					g.setColor(ColorArray[order]);
					g.fillRect(StartX + 2, y + 2, number - (1027 + StartX - x), H - 2);
				}
			}
		}
		g.setColor(Color.black);
		Font font = new Font("Georgia", Font.PLAIN, 25);
		g.setFont(font);
		g.drawString("ZONE_NORMAL(  16 MB - 896 MB )", 300, StartY / 2); //220
		font = font.deriveFont(14f);
		g.setFont(font);
		used_size *= 4;
		inter_frag *= 4;
		g.drawString("Total Size: " + Integer.toString(total_size) + " MB", 750, 15);
		g.drawString("Used Size: " + Integer.toString(used_size) + " MB", 900, 15);
		g.drawString("Internal Fragmentation: " + Integer.toString(inter_frag) + " MB", 750, 35);
		g.drawString("Memory Usage: " + Long.toString((long)used_size * 1000 / total_size) + "бы", 750, 55);
		if(0 == used_size)
			g.drawString("Internal / Used: " + "NaN", 750, 75);
		else
			g.drawString("Internal / Used: " + Long.toString((long)inter_frag * 1000 / used_size) + "бы", 750, 75);
		g.dispose();
	}
	private int Pow2(int n)
	{
		int res = 1, x = 2;
	    if (0 == n)
	        return res;
	    while (n != 0)
	    {
	        if (1 == (n & 1))
	            res *= x;
	        x = x * x;
	        n >>= 1;
	    }
	    return res;
	}
//	@Override
//	public void run()
//	{
//	    while(true)
//	    {
//	    	try
//	    	{
//	    		
//	            Thread.sleep(200);
//	        } 
//	    	catch (InterruptedException e)
//	    	{
//	            e.printStackTrace();
//	        }
//	    }
//	 }
}
