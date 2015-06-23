import javax.swing.*;
import java.util.*;
import java.awt.Dimension;

class Node
{
	int StartAddr;
	Node next;
}
public class BudSys extends JFrame
{
	private static Node[] area = new Node[11];
	public final static int MAXN = 500000;
	public static boolean[] pid = new boolean[MAXN];
	public final static int low = 16, high = 500, batch = 128;
	private static Queue< Integer > FrameCache = new LinkedList< Integer >();
	static BudVis panel = new BudVis();
	public BudSys()
	{
		panel.setPreferredSize(new Dimension(2000, 220 * panel.H + 100 + BudVis.StartY));
		panel.setBackground(null);
		panel.setAutoscrolls(true);
		initial(); //初始化area
		setTitle("Memory Management");
		setSize(1150, 700);
		JScrollPane JSP = new JScrollPane(panel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JSP.setPreferredSize(new Dimension(200,200));
		add(JSP);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
//	    Thread Refresh = new Thread(panel);
//	    Refresh.start();
	}

	public static void main(String[] args)
	{
		new BudSys();
		generateProcess();
	}

	private static void generateProcess()
	{
		int PID = 0;
		while(true)
		{
			if(true == pid[PID])
			{
	            panel.repaint();
	            PID = (int) (Math.random() * MAXN);
	            continue;
			}
			pid[PID] = true;
			double m = Math.random() * 1024;
			if(m - Math.floor(m) < 0.3)
				m = 0.0;
			ProDesc process = new ProDesc(PID, 
				(int)(m * m / 1024) + 1, (int)(Math.random() * 40000) + 20000);
			int startAddr = buffer_rmqueue(process.getOrder());
			if(-1 == startAddr)
			{
				System.out.println("No memory available");
			}
			else
			{
				process.setStartAddr(startAddr);
				process.setStartTime();
				panel.ProRec.add(process);

	//			print(process.getOrder());
	//			System.out.println("" + process.getReqSpace() + " " + process.getOrder() + " " + process.getStayTime());
			}
			sleep((int)(Math.random() * 10));
			if(0  == (PID % 3))
			{
	            panel.repaint();
			}
			PID = (PID + 1) % MAXN;
		}
	}

	private static int buffer_rmqueue(int order)
	{
		if(0 != order)
			return rmqueue(order);
		if(FrameCache.size() <= low)
		{
			for(int i = 0; i < batch; ++i)
			{
				int k = rmqueue(0);
				if(k != -1)
					FrameCache.add(k);
				else break;
			}
		}
		if(true == FrameCache.isEmpty())
			return -1;
		int res = FrameCache.peek();
		FrameCache.remove();
		return res;
	}

	private static void sleep(int n)
	{
		for(long i = 0; i <= n * 40000000; ++i);
	}

	private static void initial()
	{
		for(int i = 0; i < 10; ++i)
		{
			area[i] = null;
		}
		area[10] = new Node();
		area[10].StartAddr = 0;
		Node p = area[10];
		for(int i = 1; i < 220; ++i) //16MB - 896 MB
		{
			Node  q = new Node();
			q.StartAddr = p.StartAddr + 1024;
			q.next = null;
			p.next = q;
			p = q;
		}
		FrameCache.clear();
		return;
	}

	private static int rmqueue(int order)
	{//buddy system找空闲块
		for (int current_order = order; current_order < 11; ++current_order) 
		{
			if (null != area[current_order])
			{
				int size = 1 << current_order;
				//process的起始地址
				int start_address = area[current_order].StartAddr;
				area[current_order] = area[current_order].next;
				while (current_order > order) 
				{//split the larger sizes smaller ones
				    size >>= 1;
					//buddy的起始地址
				    int buddyStartAddr = start_address + size;
				    --current_order;
				    /* insert buddy as first element in the list */
				    if(null == area[current_order])
				    {
				    	area[current_order] = new Node();
				    	area[current_order].StartAddr = buddyStartAddr;
				    	area[current_order].next = null;
				    }
				    else
				    {
				    	Node tmp = new Node();
					    tmp.next = area[current_order].next;
					    tmp.StartAddr = area[current_order].StartAddr;
					    area[current_order].StartAddr = buddyStartAddr;
					    area[current_order].next = tmp;
				    }
				}
				return start_address;
			}
		}
		return -1;
	}
	
	public static void free_page(int Addr)
	{
		ProDesc p = new ProDesc(0, 1, 0);
		if(FrameCache.size() >= high)
		{
			int k = FrameCache.peek();
			p.setStartAddr(k);
			FrameCache.remove();
			for(int i = 0; i < batch; ++i)
				free_pages_bulk(p);
		}
		FrameCache.add(Addr);
	}
	public static void free_pages_bulk(ProDesc dropped_proc)
	{
		int order = dropped_proc.getOrder();
		int start_addr = dropped_proc.getStartAddr();
		while (order < 10)
		{
			Node buddy = new Node();
		    buddy.StartAddr = start_addr ^ (1 << order);
		    Node p = area[order], q = area[order];
		    while(null != p && p.StartAddr != buddy.StartAddr)
		    {
		    	q = p;
		    	p = p.next;
		    }
		    if(null == p)
		    	return;
		    q.next = p.next; // 删除list中的buddy
		    start_addr &= buddy.StartAddr;
		    ++order;
		}
		//注意area[order]有可能为0
		if(null == area[order])
		{
			area[order] = new Node();
			area[order].StartAddr = start_addr;
		    area[order].next = null;
		}
		else
		{
			Node tmp = new Node();
			//注意：tmp不能应用area[order]，即tmp = area[order]
		    tmp.StartAddr = area[order].StartAddr;
		    tmp.next = area[order].next;
		    //将释放的空间添加到相应order的list中
		    area[order].StartAddr = start_addr;
		    area[order].next = tmp;
		}
		return;
	}
}

