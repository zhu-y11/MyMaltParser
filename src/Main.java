import java.io.IOException;

class Main 
{

	public static void main( String[] args ) throws IOException
	{
		Parser parser = new Parser();
		CorpusReader reader = new CorpusReader();
		
		//Training
		String train_filename = Corpus.GetDanishTrain();
		Corpus train_corp = reader.Read( train_filename );
		Corpus sub_train_corp = train_corp.Shuffleto( 600 );	
		parser.Train( sub_train_corp );
		
		//Testing
		String test_filename = Corpus.GetDanishTest();
		Corpus test_corp = reader.Read( test_filename );
		parser.Parse( test_corp );
		String[] parameter = { parser.test_file_name, parser.train_model_file_name, "Result" };
		svm_predict.Run( parameter );

	}
}
