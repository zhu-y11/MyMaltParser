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
		for( Arc arc: this.arcs )
		{
			int small_idx, big_idx;
			if( arc.dependent >= arc.head )
			{
				small_idx = arc.head;
				big_idx = arc.dependent;
			}
			else
			{
				small_idx = arc.dependent;
				big_idx = arc.head;
			}
			
			for( int i = small_idx + 1; i < big_idx; i++ )
			{
				for( int j = 0; j < this.words.size(); j++ )
				{
					if( j >= small_idx && j <= big_idx )
					{
						continue;
					}
					
					if( this.ContainArc( i, j ) )
					{
						return false;
					}
				}
			}
			
		}
		return true;
	}
	
	boolean ContainArc( int start, int end )
	{
		if( start >= end )
		{
			return false;
		}
		
		for( Arc arc: this.arcs )
		{
			if( arc.dependent == start && arc.head == end ||
				arc.dependent == end && arc.head == start )
			{
				return true;
			}
		}
		return false;
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
				rel = Arc.RIGHT_ARC + "-" + arc.deprel;
				break;
			}
			
			if( arc.head == buffer_wordid && arc.dependent == stack_wordid )
			{
				rel = Arc.LEFT_ARC + "-" + arc.deprel;
				break;
			}
		}
		return rel;
	}
	
	@Override
	public String toString()
	{
		String sent = "";
		for( Word word: this.words )
		{
			sent += word.form + " ";
		}
		sent = sent.substring( 0, sent.length() - 1 );
		return sent;
	}
}

class Arc
{
	public static final String LEFT_ARC = "LEFT_ARC";
	public static final String RIGHT_ARC = "RIGHT_ARC";
	
	Arc()
	{
	}
	
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