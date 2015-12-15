import java.io.IOException;

public class Main 
{

	public static void main( String[] args ) throws IOException
	{
		String infilename = "data/english/train/en-universal-train.conll";
		CorpusReader reader = new CorpusReader();
		Corpus corp = reader.Read( infilename );
		
		Parser parser = new Parser();
		parser.Train( corp );
	}
}
