import java.util.ArrayList;
import java.util.Arrays;

public class Word 
{
	int id;
	String form;
	String lemma;
	String ctag;
	String tag;
	ArrayList<String> feats;
	int head;
	String deprel;
	
	Word( String wordline )
	{
		String[] wordvec = wordline.split( "\\s+" );
		this.id = Integer.parseInt( wordvec[0] );
		this.form = wordvec[1];
		this.lemma = wordvec[2];
		this.ctag = wordvec[3];
		this.tag = wordvec[4];
		String[] featvec = wordvec[5].split( "|" );
		feats = new ArrayList<String>( Arrays.asList( featvec ) );
		head = Integer.parseInt( wordvec[6] );
		deprel = wordvec[7];
	}
	
	Word( Word word )
	{
		this.id = word.id;
		this.form = word.form;
		this.lemma = word.lemma;
		this.ctag = word.ctag;
		this.tag = word.tag;
		this.feats = new ArrayList<String>( word.feats );
		this.head = word.head;
		this.deprel = word.deprel;
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
		
		final Word word = ( Word ) obj;
		if( word.id != this.id || !word.form.equals( this.form ) || 
			!word.lemma.equals( this.lemma ) || !word.ctag.equals( this.ctag ) ||
			!( word.feats.containsAll( this.feats ) && this.feats.containsAll( word.feats ) ) ||
			!word.tag.equals( this.tag ) || word.head != this.head || !word.deprel.equals( this.deprel ) )
		{
			return false;
		}

		return true;
		
	}
/*
	@Override
	public String toString()
	{
		
	}
	*/

}
