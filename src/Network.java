
public class Network {

	 double global_error;
	 int input_count;
	 int hidden_count;
	 int output_count;
	 int neuron_count;
	 int weightCount;
	 
	 double fire[];
	 double matrix[];
	 double best_matrix[];
	 double acc_matrix_delta[];
	 double matrix_delta[];
	 double error[];
	 double error_delta[];
	 double best_matrix_error;
	 double threshold[];
	 double acc_threshold_delta[];
	 double threshold_delta[];
	 double learn_rate_const;
	 double momentum_const;
	 
	 double matrix_distance = 0.2;

	 public Network(int inputCount, int hiddenCount, int outputCount, double learnRate, double momentum) {

		  this.learn_rate_const = learnRate;
		  this.momentum_const = momentum;
		
		  this.input_count = inputCount;
		  this.hidden_count = hiddenCount;
		  this.output_count = outputCount;
		  neuron_count = inputCount + hiddenCount + outputCount;
		  weightCount = (inputCount * hiddenCount) + (hiddenCount * outputCount);
		
		  fire    = new double[neuron_count];
		  matrix   = new double[weightCount];
		  best_matrix = new double[weightCount];
		  matrix_delta = new double[weightCount];
		  threshold = new double[neuron_count];
		  error_delta = new double[neuron_count];
		  error    = new double[neuron_count];
		  acc_threshold_delta = new double[neuron_count];
		  acc_matrix_delta = new double[weightCount];
		  threshold_delta = new double[neuron_count];
		  global_error = 0.0;
		  
		  best_matrix_error = Double.MAX_VALUE;
		
		  this.setNetwork();
	 }
	 
	 public Network(int inputCount, int hiddenCount, int outputCount, double[] weights, double[] threshold) {
		 
		 this.input_count = inputCount;
		 this.hidden_count = hiddenCount;
		 this.output_count = outputCount;
		 this.neuron_count = inputCount + hiddenCount + outputCount;
		 this.weightCount = (inputCount * hiddenCount) + (hiddenCount * outputCount);		 
		 
		 this.fire    		= new double[neuron_count];
		 this.matrix 		= weights;
		 this.matrix_delta 	= new double[weightCount];
		 this.threshold		= threshold;
		 this.error_delta 	= new double[neuron_count];
		 this.error    		= new double[neuron_count];
		 this.acc_threshold_delta 	= new double[neuron_count];
		 this.acc_matrix_delta 		= new double[weightCount];
		 this.threshold_delta 		= new double[neuron_count];
	 }
	
	public void bestMatrix( double current_error) {
		//copy the matrix of the error is good
		if ( current_error - best_matrix_error > matrix_distance ) {
			this.matrix = this.best_matrix.clone();
			System.out.println( "Copied Mateix" );
		} else {
			this.best_matrix = this.matrix.clone();
			this.best_matrix_error = current_error;
		}
	}
	
	public void setNetwork() {
		 int i;

		 	for (i = 0; i < neuron_count; i++) {
		 		//between -0.5 and +0.5
		 		
		 		threshold[i] = 0.05 - (Math.random() / 10);
		 		threshold_delta[i] = 0;
		 		acc_threshold_delta[i] = 0;
		 	}
	  
		 	for (i = 0; i < matrix.length; i++) {
		 		//between -0.5 and +0.5
		 		matrix[i] = 0.05 - (Math.random() / 10);
		 		matrix_delta[i] = 0;
		 		acc_matrix_delta[i] = 0;
		 	}
	 }
	 
	 public double []getOutputs(double input[]) {
		 int i, j;
		 final int hidden_index = input_count;
		 final int out_index = input_count + hidden_count;
		 
		 //input = fire( here we keep the input parameters )
		 for (i = 0; i < input_count; i++) {
			 fire[i] = input[i];
		 }

		 //computation for the hidden layer
		 int inx = 0;

		 for (i = hidden_index; i < out_index; i++) {
			 //get the current threshold for the selected node			 
			 double sum = threshold[i];
			 
			 //the network is fully connected - linear combination + threshold
			 for (j = 0; j < input_count; j++) {
				 sum += fire[j] * matrix[inx++];
			 }
			 
			 fire[i] = threshold(sum);
		 }

		 //output layer

		 double result[] = new double[output_count];

		 for (i = out_index; i < neuron_count; i++) {
			 //get the current threshold for the selected node
			 double sum = threshold[i];

			 for (j = hidden_index; j < out_index; j++) {
				 sum += fire[j] * matrix[inx++];
			 }
			 
			 fire[i] = threshold(sum);
			 result[i - out_index] = fire[i];
		 }
		 
		 return result;
	 }
	 
	 public double threshold(double sum) {
		 //sigmoid function is our 'fire' method
		 return 1.0 / (1 + Math.exp(-1.0 * sum));
	 }
	 
	 public double getError( int len ) {
		 double error = Math.sqrt(global_error / (len * output_count));
		 global_error = 0; // clear the accumulator
		 
		 return error;
	 }
	 
	 public void calculateError(double target[], boolean update_weights ) {
		 int i, j;
		 final int hidden_index = input_count;
		 final int output_index = input_count + hidden_count;

		 //first clear hidden layer errors from the previous stage
		 for (i = input_count; i < neuron_count; i++) {
			 error[i] = 0;
		 }
		 
		 if ( ! update_weights ) {
			 for ( i = output_index; i < neuron_count; i++ ) {
				 error[ i ] = target[ i - output_index ] - fire[ i ];
				 global_error += ( error[ i ] * error[ i ] ) / 2;
			 }			 
		 } else {
			 //backpropagation start
			 //layer errors and deltas for output layer
			 for ( i = output_index; i < neuron_count; i++ ) {
				 error[ i ] = target[ i - output_index ] - fire[ i ];
				 global_error += ( error[ i ] * error[ i ] ) / 2;
				 
				 //from the formula (derivative) - the error for the output neurons
				 error_delta[ i ] = error[ i ] * fire[ i ] * ( 1 - fire[ i ] );
			 }
			 
			 //output layer errors
			 int weight_inx = input_count * hidden_count;
	
			 for ( i = output_index; i < neuron_count; i++ ) {
				 for ( j = hidden_index; j < output_index; j++ ) {
					 // compute delta * x(input from the neuron)				 
					 acc_matrix_delta[ weight_inx ] += error_delta[ i ] * fire[ j ];
					 error[ j ] += matrix[ weight_inx ] * error_delta[ i ];
					 weight_inx++;
				 }
				 
				 acc_threshold_delta[ i ] += error_delta[ i ];
			 }
	
			 //hidden layer deltas
			 for ( i = hidden_index; i < output_index; i++ ) {
				 error_delta[ i ] = error[ i ] * fire[ i ] * ( 1 - fire[ i ] );
			 }
			 
			 //input layer errors
			 weight_inx = 0;
			 for ( i = hidden_index; i < output_index; i++ ) {
				 for ( j = 0; j < hidden_index; j++ ) {
					 // compute delta * x(input from the neuron)
					 acc_matrix_delta[ weight_inx ] += error_delta[ i ] * fire[ j ];
					 error[ j ] += matrix[ weight_inx ] * error_delta[ i ];
					 weight_inx++;
				 }
				 
				 acc_threshold_delta[ i ] += error_delta[ i ];
			 }
		 }
	 }
	 
	 public void updateWeights() {
		 int i;

		 //process the matrix (weights).
		 //Here we use momentum to speed up convergence - taken from http://axon.cs.byu.edu/papers/IstookIJNS.pdf 
		 
		 for (i = 0; i < matrix.length; i++) {
			 matrix_delta[i] = (learn_rate_const * acc_matrix_delta[i]) + (momentum_const * matrix_delta[i]);
			 matrix[i] += matrix_delta[i];
			 acc_matrix_delta[i] = 0;
		 }

		 //process the thresholds
		 for (i = input_count; i < neuron_count; i++) {
			 threshold_delta[i] = learn_rate_const * acc_threshold_delta[i] + (momentum_const * threshold_delta[i]);
			 threshold[i] += threshold_delta[i];
			 acc_threshold_delta[i] = 0;
		 }
	 }
}