import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVM 
{
	public static void train( String train_file_name, String train_model_file_name ) throws IOException
	{
		svm_parameter param = SVM.SetParam();
		svm_problem problem = ReadProblem( train_file_name, param );
		String error_msg = svm.svm_check_parameter( problem, param );
		
		if(error_msg != null)
		{
			System.err.print("ERROR: "+error_msg+"\n");
			System.exit(1);
		}

		svm_model model = svm.svm_train(problem,param);
		svm.svm_save_model( train_model_file_name, model );
	}
	
	private static svm_parameter SetParam()
	{
		svm_parameter param = new svm_parameter();
		
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.POLY;
		param.degree = 2;
		param.gamma = 0.2;
		param.coef0 = 0;
		param.C = 0.5;
		param.probability = 1;
		
		param.cache_size = 100;	
		param.nu = 0.5;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		return param;
	}
	
	private static svm_problem ReadProblem( String input_file, svm_parameter param ) throws IOException
	{
		BufferedReader fp = new BufferedReader(new FileReader( input_file ) );
		Vector<Double> vy = new Vector<Double>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		int max_index = 0;

		while( true )
		{
			String line = fp.readLine();
			if(line == null) 
			{ 
				break;
			}

			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
			vy.addElement( Parser.transition_dict.get( st.nextToken() ) );
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = Integer.parseInt(st.nextToken());
				x[j].value = Double.parseDouble(st.nextToken());
			}
			if(m>0) max_index = Math.max(max_index, x[m-1].index);
			vx.addElement(x);
		}

		svm_problem prob = new svm_problem();
		prob.l = vy.size();
		prob.x = new svm_node[prob.l][];
		for(int i=0;i<prob.l;i++)
			prob.x[i] = vx.elementAt(i);
		prob.y = new double[prob.l];
		for(int i=0;i<prob.l;i++)
			prob.y[i] = vy.elementAt(i);

		if(param.gamma == 0 && max_index > 0)
			param.gamma = 1.0/max_index;

		if(param.kernel_type == svm_parameter.PRECOMPUTED)
			for(int i=0;i<prob.l;i++)
			{
				if (prob.x[i][0].index != 0)
				{
					System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
					System.exit(1);
				}
				if ((int)prob.x[i][0].value <= 0 || (int)prob.x[i][0].value > max_index)
				{
					System.err.print("Wrong input format: sample_serial_number out of range\n");
					System.exit(1);
				}
			}

		fp.close();
		return prob;
	}
}
