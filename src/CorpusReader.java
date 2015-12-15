import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CorpusReader 
{
	public Corpus Read( String filename ) throws IOException
	{		
		BufferedReader reader = new BufferedReader(
							    new InputStreamReader(
								new FileInputStream(
								new File( filename ) ) ) );
		String line;
		Corpus corp = new Corpus();
		Sentence sent = new Sentence();
		Word word;
		
		while( ( line = reader.readLine() ) != null )
		{
			if( line.trim().isEmpty() )
			{
				sent.addArcs();
				corp.addSentence( sent );
				for( Arc arc: sent.arcs )
				{
					if( arc.head == 0 || arc.dependent == 0 )
						continue;
				}
				sent.Clear();
				continue;
			}
			
			word = new Word( line );
			sent.AddWord( word );
		}
		reader.close();
		return corp;
	}
	
}
