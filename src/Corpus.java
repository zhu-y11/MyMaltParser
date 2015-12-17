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
		if( num <= 0 || num > this.sent_num )
		{
			return null;
		}
		
		Corpus sub_corp = new Corpus( this );	
		Collections.shuffle( sub_corp.corpus );
		sub_corp.sent_num = num;
		sub_corp.corpus = sub_corp.corpus.subList( 0, num );
		
		return sub_corp;
	}
	
	public static String GetEnglishTrain()
	{
		return "data/english/train/en-universal-train.conll";
	}
	public static String GetEnglishTest()
	{
		return "data/english/test/en-universal-test.conll";
	}
	public static String GetDanishTrain()
	{
		return "data/danish/ddt/train/danish_ddt_train.conll";
	}
	public static String GetDanishTest()
	{
		return "data/danish/ddt/test/danish_ddt_test.conll";
	}
	public static String GetSwedishTrain()
	{
		return "data/swedish/talbanken05/train/swedish_talbanken05_train.conll";
	}
	public static String GetSwedishTest()
	{
		return "data/swedish/talbanken05/test/swedish_talbanken05_train.conll";
	}

}
