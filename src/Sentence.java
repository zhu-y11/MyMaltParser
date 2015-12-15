import java.util.ArrayList;

public class Sentence 
{
	ArrayList<Word> words;
	ArrayList<Arc> arcs;
	
	Sentence()
	{
		words = new ArrayList<Word>();
		arcs = new ArrayList<Arc>();
	}
	
	Sentence( Sentence sent )
	{
		words = new ArrayList<Word>( sent.words );
		arcs = new ArrayList<Arc>( sent.arcs );
	}
	
	boolean AddWord( Word word )
	{
		return ( words.add( word ) );
	}
	
	void addArcs()
	{
		for( Word word: words )
		{
			arcs.add( new Arc( word ) );
		}
	}
	
	boolean CheckProjective()
	{
		boolean proj = true;
		
		for( Arc arc_right: arcs )
		{
			if( arc_right.direction.equals( Arc.LEFT_ARC ) )
			{
				continue;
			}
			
			for( Arc arc_left: arcs )
			{
				if( arc_left.direction.equals( Arc.RIGHT_ARC ) )
				{
					continue;
				}
				
				//Intersection, non projective
				if( arc_left.dependent < arc_right.head && 
					arc_left.head >arc_right.head && 
					arc_left.head <= arc_right.dependent
					|| 
				    arc_right.head < arc_left.dependent && 
				    	arc_right.dependent > arc_left.dependent && 
				    	arc_right.dependent <= arc_left.head )
				{
					proj = false;
					break;
				}
			}
			
			if( !proj )
			{
				break;
			}
		}
		
		return proj;
		
	}
	
	void Clear()
	{
		this.words.clear();
		this.arcs.clear();
	}
	
	Word GetWord( int id )
	{
		Word tg_word = null;
		for( Word word : this.words )
		{
			if( word.id == id )
			{
				tg_word = new Word( word );
				return tg_word;
			}
		}
		return tg_word;
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if( obj == null )
		{
			return false;
		}
		if ( this.getClass() != obj.getClass() ) 
		{
	        return false;
	    }
		
		final Sentence sentence = ( Sentence ) obj;
		
		if( !( sentence.words.containsAll( this.words ) && this.words.containsAll( sentence.words ) ) ||
			!( sentence.arcs.containsAll( this.arcs) && this.arcs.containsAll( sentence.arcs ) ) )
		{
			return false;
		}
		return true;
	}

	String FindRelation( int stack_wordid, int buffer_wordid )
	{
		String rel = null;
		
		for( Arc arc: this.arcs )
		{
			if( arc.head == stack_wordid && arc.dependent == buffer_wordid )
			{
				rel = Arc.RIGHT_ARC + ":" + arc.deprel;
				break;
			}
			
			if( arc.head == buffer_wordid && arc.dependent == stack_wordid )
			{
				rel = Arc.LEFT_ARC + ":" + arc.deprel;
				break;
			}
		}
		return rel;
	}
}

class Arc
{
	public static final String LEFT_ARC = "left_arc";
	public static final String RIGHT_ARC = "right_arc";
	
	Arc( Word word )
	{
		this.dependent = word.id;
		this.head = word.head;
		this.deprel = word.deprel;	
		this.direction = this.dependent > this.head ? RIGHT_ARC: LEFT_ARC;
	}
	
	Arc( int head_id, int dependent_id, String deprel, String direction )
	{
		this.head = head_id;
		this.dependent = dependent_id;
		this.deprel = deprel;
		this.direction = direction;
	}
	
	int head;
	int dependent;
	String deprel;
	String direction;
		
	@Override
	public boolean equals( Object obj )
	{
		if( obj == null )
		{
			return false;
		}
		if ( this.getClass() != obj.getClass() ) 
		{
	        return false;
	    }
		
		final Arc arc = ( Arc ) obj;
		
		if( arc.head != this.head || arc.dependent != this.dependent ||
			!arc.deprel.equals( this.deprel ) || !arc.direction.equals( this.direction ) )
		{
			return false;
		}
		
		return true;
	}
}