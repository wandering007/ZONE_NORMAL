
public class ProDesc
{
	private int PID;
	private long start_time;
	private int stay_time;
	private int req_space;
	private int start_address;
	private int order;
	public ProDesc(int p, int r, int t)
	{
		PID = p;
		req_space = r;
		stay_time = t;
		order = (int)Math.ceil((Math.log(r) / Math.log(2)));
	}
	public int getPID()
	{
		return PID;
	}
	public long getStartTime()
	{
		return start_time;
	}
	public int getStayTime()
	{
		return stay_time;
	}
	public int getReqSpace()
	{
		return req_space;
	}
	public void setStartAddr(int a)
	{
		start_address = a;
	}
	public int getStartAddr()
	{
		return start_address;
	}
	public int getOrder()
	{
		return order;
	}
	public void setStartTime()
	{
		start_time = System.currentTimeMillis();
	}
}
