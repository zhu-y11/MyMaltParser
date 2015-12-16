import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Corpus
{
	List<Sentence> corpus;
	int sent_num;
	
	Corpus()
	{
		this.corpus = new ArrayList<Sentence>();
		this.sent_num = 0;
	}
	
	Corpus( Corpus corpus )
	{
		this.corpus = new ArrayList<Sentence>( corpus.corpus );
		this.sent_num = corpus.sent_num;
	}
	
	boolean addSentence( Sentence sent )
	{
		this.sent_num ++;
		return ( this.corpus.add( sent ) );
	}
	
	boolean Delete( Sentence sent )
	{
		for( Sentence sentence: this.corpus )
		{
			if( sentence.equals( sent ) )
			{
				corpus.remove( sentence );
				return true;
			}
		}
		return false;	
	}
	
	Corpus Shuffleto( int num )
	{
		if( num <= 0 || num >= this.sent_num )
		{
			return null;
		}
		
		Corpus sub_corp = new Corpus( this );	
		Collections.shuffle( sub_corp.corpus );
		sub_corp.sent_num = num;
		sub_corp.corpus = sub_corp.corpus.subList( 0, num );
		
		return sub_corp;
	}
}
