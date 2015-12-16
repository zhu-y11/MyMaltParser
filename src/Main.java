import java.io.IOException;

public class Main 
{

	public static void main( String[] args ) throws IOException
	{
		Parser parser = new Parser();

		String train_filename = "data/english/train/en-universal-train.conll";
		CorpusReader reader = new CorpusReader();
		Corpus train_corp = reader.Read( train_filename );
		Corpus sub_train_corp = train_corp.Shuffleto( 20 );	
		parser.Train( sub_train_corp );
		
		String test_filename = "data/english/train/en-universal-train.conll";
		Corpus test_corp = reader.Read( test_filename );
		Corpus sub_test_corp = test_corp.Shuffleto( 20 );	
		parser.Parse( sub_test_corp );
	}
}
