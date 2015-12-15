import java.util.ArrayList;
import java.util.Iterator;

public class Corpus implements Iterable<Sentence>
{
	ArrayList<Sentence> corpus;
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
	
	@Override
	public Iterator<Sentence> iterator() 
	{
        return this.corpus.iterator();
    }
	
	boolean addSentence( Sentence sent )
	{
		this.sent_num ++;
		return ( corpus.add( sent ) );
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
}
