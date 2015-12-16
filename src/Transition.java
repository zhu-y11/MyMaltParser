public class Transition 
{
	public final static String LEFT_ARC = "LEFT_ARC";
	public final static String RIGHT_ARC = "RIGHT_ARC";
	public final static String SHIFT = "SHIFT";
	public final static String REDUCE = "REDUCE";
	
	public static int Left_Arc( Configuration conf, String relation )
	{
		if( conf.buffer.isEmpty() || conf.stack.isEmpty() )
			return -1;
		
		int stack_wordid = conf.stack.peek(),
			buffer_wordid = conf.buffer.peek();
		
		for( Arc arc: conf.arcs )
		{
			if( arc.dependent == stack_wordid )
			{
				return -1;
			}
		}
		
		conf.arcs.add( new Arc( buffer_wordid, stack_wordid, relation, Arc.LEFT_ARC ) );
		conf.stack.pop();
		return 1;
	}
	
	public static int Right_Arc( Configuration conf, String relation )
	{
		if( conf.buffer.isEmpty() || conf.stack.isEmpty() )
			return -1;
		
		int stack_wordid = conf.stack.peek(),
			buffer_wordid = conf.buffer.peek();
		
		conf.arcs.add( new Arc( stack_wordid, buffer_wordid, relation, Arc.RIGHT_ARC ) );
		conf.stack.push( conf.buffer.poll() );
		return 1;
	}
	
	public static int Reduce( Configuration conf )
	{
		if( conf.stack.isEmpty() )
			return -1;
		
		int stack_wordid = conf.stack.peek();
		
		boolean if_dep = false;
		for( Arc arc: conf.arcs )
		{
			if( arc.dependent == stack_wordid )
			{
				if_dep = true;
				break;
			}
		}
		
		if( if_dep )
		{
			conf.stack.pop();
			return 1;
		}
		else
		{
			return -1;
		}
		
	}
	
	public static int Shift( Configuration conf )
	{
		if( conf.buffer.isEmpty() )
		{
			return -1;
		}
		else
		{
			conf.stack.push( conf.buffer.poll() );
			return 1;
		}
	}
}
