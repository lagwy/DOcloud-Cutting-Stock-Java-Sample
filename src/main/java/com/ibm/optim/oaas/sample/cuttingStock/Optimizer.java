package com.ibm.optim.oaas.sample.cuttingStock;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.optim.oaas.client.OperationException;
import com.ibm.optim.oaas.client.job.JobClient;
import com.ibm.optim.oaas.client.job.JobClientFactory;
import com.ibm.optim.oaas.client.job.JobException;
import com.ibm.optim.oaas.client.job.JobExecutor;
import com.ibm.optim.oaas.client.job.JobExecutorFactory;
import com.ibm.optim.oaas.client.job.JobOutput;
import com.ibm.optim.oaas.client.job.JobRequest;
import com.ibm.optim.oaas.client.job.JobResponse;

/**
 * Handles the actual optimization task. Creates and executes a job 
 * request for an optimization problem instance. Encapsulates the 
 * DOcloud API. The problem instance is specified by the OPL model 
 * and input class received from the invoking (e.g. <code>ColumnGeneration</code>) 
 * instance. Uses a generic parameter <code>R</code> to specify the output class. 
 * This class is completely independent of the specific optimization 
 * problem to be solved.
 * @param R the class to contain the optimization results. Note that R must be a super type of resultType
 */
public class Optimizer<R>  {

	public static Logger LOG = Logger.getLogger(ColumnGeneration.class.getName());

	// Job management objects	
	protected JobExecutor executor;
	protected JobClient jobclient;

	// Mapper for Java --> JSON serialization
	protected ObjectMapper mapper = new ObjectMapper();
	protected URL mod;
	protected Class<R> resultType;

	/**
	 * Constructs an Optimizer instance.
	 * 
	 * @param apiUrl DOcloud API URL
	 * @param token DOcloud solve service authentication token
	 * @param mod the OPL model
	 * @param resultType the class to contain the optimization results. Note that R must be a super type of resultType
	 */
	public Optimizer (String url, String token, URL mod, Class<R> resultType) {
		super();
		this.jobclient = JobClientFactory.createDefault(url, token);
		this.executor = JobExecutorFactory.createDefault();
		this.mod = mod;
		this.resultType = resultType;
	}

	public static <R> Optimizer<R> newInstance(String url, String token, URL mod, Class<R> resultType) {
		return new Optimizer<R>(url, token, mod, resultType);
	}

	/**
	 * Solves an optimization problem instance by calling the DOcloud 
	 * solve service (Oaas). Creates a single job request for a problem 
	 * instance to be processed by the solve service. Once the problem 
	 * is solved, the results are mapped to an instance of a resultType 
	 * class represented by the generic parameter R.
	 * 
	 * @param name The name of this problem instance
	 * @param inData The input data
	 * @return A solution class of result type R
	 */
	public R solve(String name, Object inData) {
		JobResponse response = null;
		try {
			JobRequest request = jobclient.newRequest()
					.input(name+".mod", this.mod)
					.input(name+".json", this.mapper, inData)
					.log(new File("results.log"))
					.output(mapper, this.resultType)
					.deleteOnCompletion(true)
					.livelog(System.out)
					.timeout(5, TimeUnit.MINUTES).build();

			response = request.execute(executor).get();
			switch (response.getJob().getExecutionStatus()) {
			case PROCESSED:
				List<? extends JobOutput> output = response.getOutput();
				@SuppressWarnings("unchecked")
				R solution = (R)output.get(0).getContent();
				return solution;
			case FAILED:
				// Get the failure message if it has been defined
				String message = "";
				if (response.getJob().getFailureInfo() != null) {
					message= response.getJob().getFailureInfo().getMessage();
				}
				LOG.info("Failed " + message);
				break;
			default:
				break;
			}
		} catch (OperationException | InterruptedException | ExecutionException
				| IOException | JobException e) {
			LOG.log(Level.WARNING, "An error was encountered during job execution",e);
		}

		return null;
	}

	public void shutdown(){
		executor.shutdown();
	}
}
