import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class Parser 
{
	public static List<String> feat_dict = new ArrayList<String>();
	public static Map<String, Double> transition_dict = new HashMap<String, Double>();
	public String train_file_name = "Training_Data";
	public String train_model_file_name = "model";
	
	List<String> Train( Corpus train_data ) throws IOException
	{
		List<String> training_seq = new ArrayList<String>();
		BufferedWriter writer = new BufferedWriter( 
							   new OutputStreamWriter( 
							   new FileOutputStream( new File( train_file_name ) ) ) );
		
		System.out.println( train_data.sent_num + " sentences in the training set.");
		System.out.println( "Training SVM..." );
		int projective_num = 0;
		for( Sentence sent: train_data.corpus )
		{
			//check if the sentence is projective
			if( !sent.CheckProjective() )
			{
				continue;
			}
			projective_num ++;
	
			//Add training samples
			Configuration conf = new Configuration( sent );
			while( !conf.buffer.isEmpty() )
			{
				List<String> features = this.ExtractFeatures( conf, sent );
				String bin_features = this.toBinFeatures( features );
				
				int buffer_wordid = conf.buffer.peek();				
				if( !conf.stack.isEmpty() )
				{
					int stack_wordid = conf.stack.peek();
					String rel = sent.FindRelation( stack_wordid, buffer_wordid );
					
					if( rel == null )
					{
						boolean flag = false;
						for( int i = 0; i < stack_wordid; i++ )
						{
							if( sent.FindRelation( i, buffer_wordid ) != null )
							{
								flag = true;
								break;
							}
						}
						
						if( flag )
						{
							String key = Transition.REDUCE;
							if( !transition_dict.containsKey( key ) )
							{
								transition_dict.put( key, 1.0 * transition_dict.size() );
							}
							training_seq.add( key );
							Transition.Reduce( conf );
							//add features:transition to libsvm file
							writer.write( key + " " + bin_features + "\n" );
							continue;
						}
					}
					else if( rel.contains( Arc.LEFT_ARC ) )
					{
						String key = rel,
							   relation = rel.substring( rel.indexOf( "_") + 1 );
						if( !transition_dict.containsKey( key ) )
						{
							transition_dict.put( key, 1.0 * transition_dict.size() );
						}
						training_seq.add( key );
						Transition.Left_Arc( conf, relation );
						//add features:transition to libsvm file
						writer.write( key + " " + bin_features + "\n" );
						continue;
					}
					else if( rel.contains( Arc.RIGHT_ARC ) )
					{
						String key = rel,
							   relation = rel.substring( rel.indexOf( "_" ) + 1 );
						if( !transition_dict.containsKey( key ) )
						{
							transition_dict.put( key, 1.0 * transition_dict.size() );
						}
						training_seq.add( key );
						Transition.Right_Arc( conf, relation );
						//add features:transition to libsvm file
						writer.write( key + " " + bin_features + "\n" );
						continue;
					}	
				}
				
				//Shift
				String key = Transition.SHIFT;
				if( !transition_dict.containsKey( key ) )
				{
					transition_dict.put( key, 1.0 * transition_dict.size() );
				}
				Transition.Shift( conf );
				training_seq.add( key );
				writer.write( key + " " + bin_features + "\n" );
				//add features:transition to libsvm file	
			}
		}
		writer.close();
		
		//Training using libsvm
		SVM.train( this.train_file_name, this.train_model_file_name );

		System.out.println( "Training finished, " + projective_num + 
							" projective sentences are used for training" );
		return training_seq;
	}
	
	List<String> ExtractFeatures( Configuration conf, Sentence sentence)
	{
		List<String> features = new ArrayList<String>();
		Stack<Integer> stack = ( Stack<Integer> ) conf.stack.clone();
		Queue<Integer> buffer = new LinkedList<Integer>( conf.buffer );
		List<Arc> arcs = new ArrayList<Arc>( conf.arcs );
		
		if( !stack.isEmpty() )
		{
			int stack_wordid = stack.peek();
			Word stack_word = sentence.GetWord( stack_wordid );
			
			//ROOT
			if( stack_word == null )
			{
				features.add( "STK_0_TAG_" + "TOP" );
				features.add( "STK_0_CTAG_" + "TOP" );
			}
			else
			{
				// stack[0], word
				if( this.CheckInformative( stack_word.form, true ) )
				{
					features.add( "STK_0_FORM_" + stack_word.form );
				}

				// stack[0], feats
				if( this.CheckInformative( stack_word.feats.get( 0 ), false ) )
				{
					for( String feat: stack_word.feats )
					{
						features.add( "STK_0_FEATS_" + feat );
					}
				}

				// stack[0], lemma
				if( this.CheckInformative( stack_word.lemma, false ) )
				{
					features.add( "STK_0_LEMMA_" + stack_word.lemma );
				}

				// stack[0], ctag
				if( this.CheckInformative( stack_word.ctag, false ) )
				{
					features.add( "STK_0_CTAG_" + stack_word.ctag );
				}

				// stack[0], tag
				if( this.CheckInformative( stack_word.tag, false ) )
				{
					features.add( "STK_0_TAG_" + stack_word.tag );
				}

				// stack[0], leftmost dependencies
				int left_id  = this.FindLeftDeps( stack_wordid, arcs );
				if( left_id < 10000 )
				{
					Word left_dep = sentence.GetWord( left_id );
					if( left_dep != null && this.CheckInformative( left_dep.form, true ) )
					{
						features.add( "STK_0_LDEP_" + left_dep.form );
					}
				}

				// stack[0], right dependencies
				int right_id  = this.FindRightDeps( stack_wordid, arcs );
				if( right_id > 0 )
				{
					Word right_dep = sentence.GetWord( right_id );
					if( right_dep != null && this.CheckInformative( right_dep.form, true ) )
					{
						features.add( "STK_0_RDEP_" + right_dep.form );
					}
				}
			}
			
			//stack[1]
			stack.pop();
			if( !stack.isEmpty() )
			{
				int stack_nextwordid = stack.peek();
				Word stack_nextword = sentence.GetWord( stack_nextwordid );
				// stack[1], ctag
				if( stack_nextword == null)
				{
					features.add( "STK_1_CTAG_" + "TOP" );
				}
				else
				{
					if( this.CheckInformative( stack_nextword.tag, false ) )
					{
						features.add( "STK_1_CTAG_" + stack_nextword.tag );
					}
				}
			}
		}
		
		if( !buffer.isEmpty() )
		{
			int buffer_wordid = buffer.peek();
			Word buffer_word = sentence.GetWord( buffer_wordid );
			
			// buffer[0], word
			if( this.CheckInformative( buffer_word.form, true ) )
			{
				features.add( "BUF_0_FORM_" + buffer_word.form );
			}
			
			// buffer[0], feats
			if( this.CheckInformative( buffer_word.feats.get( 0 ), false ) )
			{
				for( String feat: buffer_word.feats )
				{
					features.add( "BUF_0_FEATS_" + feat );
				}
			}
			
			// buffer[0], ctag
			if( this.CheckInformative( buffer_word.ctag, false ) )
			{
				features.add( "BUF_0_CTAG_" + buffer_word.ctag );
			}
			
			// buffer[0], tag
			if( this.CheckInformative( buffer_word.tag, false ) )
			{
				features.add( "BUF_0_TAG_" + buffer_word.tag );
			}
			
			// buffer[0], leftmost dependencies
			int left_id  = this.FindLeftDeps( buffer_wordid, arcs );
			if( left_id < 10000 )
			{
				Word left_dep = sentence.GetWord( left_id );
				if( left_dep != null && this.CheckInformative( left_dep.form, true ) )
				{
					features.add( "BUF_0_LDEP_" + left_dep.form );
				}
			}
			
			// buffer[0], right dependencies
			int right_id  = this.FindRightDeps( buffer_wordid, arcs );
			if( right_id > 0 )
			{
				Word right_dep = sentence.GetWord( right_id );
				if( right_dep != null && this.CheckInformative( right_dep.form, true ) )
				{
					features.add( "BUF_0_RDEP_" + right_dep.form );
				}
			}
			
			//buffer[1]
			buffer.poll();
			if( !buffer.isEmpty() )
			{
				int buffer_nextwordid = buffer.peek();
				Word buffer_nextword = sentence.GetWord( buffer_nextwordid );
				// buffer[1], word
				if( this.CheckInformative( buffer_nextword.form, true ) )
				{
					features.add( "BUF_1_FORM" + buffer_nextword.form );
				}
				
				// buffer[1], tag
				if( this.CheckInformative( buffer_nextword.tag, false ) )
				{
					features.add( "BUF_1_TAG_" + buffer_nextword.tag );
				}
			}
		}
		
		return features;
	}
	
	String toBinFeatures( List<String> features )
	{
		List<Integer> feature_ids = new ArrayList<Integer>();
		String binfeatures = "";
		for( String feature: features )
		{
			if( feat_dict.contains( feature ) )
			{
				feature_ids.add( feat_dict.indexOf( feature ) );
			}
			else
			{
				feat_dict.add( feature );
				feature_ids.add( feat_dict.indexOf( feature ) );
			}
		}
		
		Collections.sort( feature_ids );
		
		for( int id: feature_ids )
		{
			binfeatures += id + ":1.0 ";
		}
		binfeatures = binfeatures.substring( 0, binfeatures.length() - 1 );
		
		return binfeatures;
	}
	
	int FindLeftDeps( int head_id, List<Arc> arcs )
	{
		int left_id = 10000;
		for( Arc arc: arcs )
		{
			if( arc.direction.equals( Arc.LEFT_ARC ) && arc.head == head_id )
			{
				if( arc.dependent < left_id )
				{
					left_id = arc.dependent;
				}
			}
		}
		return left_id;
	}
	
	int FindRightDeps( int head_id, List<Arc> arcs )
	{
		int right_id = 0;
		for( Arc arc: arcs )
		{
			if( arc.direction.equals( Arc.RIGHT_ARC ) && arc.head == head_id )
			{
				if( arc.dependent > right_id )
				{
					right_id = arc.dependent;
				}
			}
		}
		return right_id;
	}
	
	boolean CheckInformative( String str, boolean if_underscore_informative )
	{
		if( str == null || str.isEmpty() )
		{
			return false;
		}
		
		if( !if_underscore_informative && str.equals( "_" ) )
		{
			return false;
		}
		
		return true;
	}
	
	List<String> Parse( Corpus test_data ) throws IOException
	{
		List<String> training_seq = new ArrayList<String>();
		BufferedWriter writer = new BufferedWriter( 
							   new OutputStreamWriter( 
							   new FileOutputStream( new File( train_file_name ) ) ) );
		
		System.out.println( train_data.sent_num + " sentences in the training set.");
		System.out.println( "Training SVM..." );
		int projective_num = 0;
		for( Sentence sent: train_data.corpus )
		{
			//check if the sentence is projective
			if( !sent.CheckProjective() )
			{
				continue;
			}
			projective_num ++;
	
			//Add training samples
			Configuration conf = new Configuration( sent );
			while( !conf.buffer.isEmpty() )
			{
				List<String> features = this.ExtractFeatures( conf, sent );
				String bin_features = this.toBinFeatures( features );
				
				int buffer_wordid = conf.buffer.peek();				
				if( !conf.stack.isEmpty() )
				{
					int stack_wordid = conf.stack.peek();
					String rel = sent.FindRelation( stack_wordid, buffer_wordid );
					
					if( rel == null )
					{
						boolean flag = false;
						for( int i = 0; i < stack_wordid; i++ )
						{
							if( sent.FindRelation( i, buffer_wordid ) != null )
							{
								flag = true;
								break;
							}
						}
						
						if( flag )
						{
							String key = Transition.REDUCE;
							if( !transition_dict.containsKey( key ) )
							{
								transition_dict.put( key, 1.0 * transition_dict.size() );
							}
							training_seq.add( key );
							Transition.Reduce( conf );
							//add features:transition to libsvm file
							writer.write( key + " " + bin_features + "\n" );
							continue;
						}
					}
					else if( rel.contains( Arc.LEFT_ARC ) )
					{
						String key = rel,
							   relation = rel.substring( rel.indexOf( "_") + 1 );
						if( !transition_dict.containsKey( key ) )
						{
							transition_dict.put( key, 1.0 * transition_dict.size() );
						}
						training_seq.add( key );
						Transition.Left_Arc( conf, relation );
						//add features:transition to libsvm file
						writer.write( key + " " + bin_features + "\n" );
						continue;
					}
					else if( rel.contains( Arc.RIGHT_ARC ) )
					{
						String key = rel,
							   relation = rel.substring( rel.indexOf( "_" ) + 1 );
						if( !transition_dict.containsKey( key ) )
						{
							transition_dict.put( key, 1.0 * transition_dict.size() );
						}
						training_seq.add( key );
						Transition.Right_Arc( conf, relation );
						//add features:transition to libsvm file
						writer.write( key + " " + bin_features + "\n" );
						continue;
					}	
				}
				
				//Shift
				String key = Transition.SHIFT;
				if( !transition_dict.containsKey( key ) )
				{
					transition_dict.put( key, 1.0 * transition_dict.size() );
				}
				Transition.Shift( conf );
				training_seq.add( key );
				writer.write( key + " " + bin_features + "\n" );
				//add features:transition to libsvm file	
			}
		}
		writer.close();
		
		//Training using libsvm
		SVM.train( this.train_file_name, this.train_model_file_name );

		System.out.println( "Training finished, " + projective_num + 
							" projective sentences are used for training" );
		return training_seq;
	}
	
}

class Configuration
{
	Stack<Integer> stack;
	Queue<Integer> buffer;
	List<Arc> arcs;
	
	Configuration( Sentence sent )
	{
		stack = new Stack<Integer>();
		stack.push( 0 );
		buffer = new LinkedList<Integer>();
		for( int i = 1; i <= sent.words.size(); i++ )
		{
			buffer.offer(i);
		}
		arcs = new ArrayList<Arc>();
	}
}