package com.ibm.optim.oaas.sample.cuttingStock;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ibm.optim.oaas.sample.cuttingStock.model.MasterData;
import com.ibm.optim.oaas.sample.cuttingStock.model.MasterResult;
import com.ibm.optim.oaas.sample.cuttingStock.model.SubproblemData;
import com.ibm.optim.oaas.sample.cuttingStock.model.SubproblemResult;

/**
 * This class solves a mathematical program using the column generation 
 * algorithm. The optimization model must be factored into a Master problem 
 * and a single Subproblem. The actual problem to be solved is almost 
 * completely transparent to this class and is specified by the Master 
 * and Subproblem models and their respective input and output classes. The 
 * actual solves are delegated to the class <code>Optimizer</code>.
 * 
 * This class is specifically adapted to solve the cutting stock problem. 
 * Comments in the optimize method indicate the modifications needed in order 
 * to adapt this class to solve a different column generation problem.
 */
public class ColumnGeneration {

	public static Logger LOG = Logger.getLogger(ColumnGeneration.class.getName()); 

	// Optimization models
	private URL masterModFile;
	private URL subModFile;
	private String label;

	// API URL and authentication token
	private String apiUrl;
	private String token;

	// Optimization Results	
	MasterResult masterOut;
	SubproblemResult subOut;
	List<MasterResult> masterIterations;
	List<SubproblemResult> subIterations;

	/**
	 * Constructs a ColumnGeneration instance.
	 * 
	 * @param baseURL The base URL for accessing DOcloud
	 * @param apiKeyClientId The client ID token
	 * @param label Identifies the problem
	 * @param masterDotMod URL for the OPL model file for the Master problem
	 * @param subDotMod URL for the OPL model file for the Subproblem
	 */
	public ColumnGeneration(String baseURL, String apiKeyClientId, String label,
			String masterDotMod, String subDotMod) {
		super();

		this.apiUrl = baseURL;
		this.token = apiKeyClientId;

		// Get the master OPL model file
		masterModFile = ColumnGeneration.class.getResource(masterDotMod);
		if (masterModFile == null) {
			throw new RuntimeException("Master .mod file " + masterDotMod + " not found");
		}

		// Get the sub problem OPL model file
		subModFile = ColumnGeneration.class.getResource(subDotMod);
		if (subModFile == null) {
			throw new RuntimeException("Subproblem .mod file " + subDotMod + " not found");
		}

		this.label = label;
		this.masterIterations = new ArrayList<MasterResult>();
		this.subIterations = new ArrayList<SubproblemResult>();
	}

	public String getLabel() {
		return label;
	}

	public MasterResult getMasterResult() {
		return masterOut;
	}

	public SubproblemResult getSubproblemResult() {
		return subOut;
	}

	public List<MasterResult> getMasterIterations() {
		return masterIterations;
	}

	public List<SubproblemResult> getSubIterations() {
		return subIterations;
	}

	/**
	 * Implements the column generation algorithm. The algorithm is almost 
	 * completely independent of the details of the optimization problem
	 * to be solved. However, certain methods of the classes MasterData, 
	 * SubproblemData, MasterResult, and SubproblemResult specifically 
	 * refer to objects of the cutting stock problem and  should be rewritten 
	 * and renamed for other kinds of problems. These are:
	 * <p>
	 * Data transfer to master problem 
	 * <ul>
	 * 	<li>MasterData.getPatterns()</li>
	 * 	<li>SubproblemResult.getNewPattern()</li>
	 * 	<li>MasterData.getSlices()</li>
	 * 	<li>SubproblemResult.getSlices()</li>
	 * </ul>
	 * <p>
	 * Data transfer to subproblem
	 * <ul>
	 * 	<li>SubproblemData.setDuals()</li>
	 * 	<li>MasterResult.Duals()</li>
	 * 	<li>SubproblemData.getParameters().incrementNbPatterns()</li>
	 * 	<li>SubproblemResult.getObjective().getValue()</li>
	 * </ul>
	 * <p>
	 * @param masterIn Input data to the Master problem
	 * @param subIn Input data to the Subproblem
	 */
	public void optimize(MasterData masterIn, SubproblemData subIn) {	

		masterOut = new MasterResult(getLabel() + " master problem solution");
		subOut = new SubproblemResult(getLabel() + " subproblem solution");

		Optimizer<MasterResult> master = 
				new Optimizer<MasterResult>(apiUrl, token, masterModFile, MasterResult.class);
		Optimizer<SubproblemResult> subproblem = 
				new Optimizer<SubproblemResult>(apiUrl, token, subModFile, SubproblemResult.class);

		Double reducedCost = Double.NEGATIVE_INFINITY;
		Double epsilon = 1.0e-6;

		int iterations = 0;

		while(iterations < 80 && reducedCost <- epsilon) {

			// Set up and solve Master Problem
			if(iterations > 0) {
				masterIn.getPatterns().add(subOut.getNewPattern());
				masterIn.getSlices().addAll(subOut.getSlices());
			}
			System.out.println("Iteration: " + iterations);
			iterations++;
			masterOut = master.solve(getLabel() +"_Master", masterIn);
			masterIterations.add(masterOut.copy());

			// Set up and solve Subproblem
			subIn.setDuals(masterOut.getDuals());		
			subOut = subproblem.solve(getLabel() + "_Subproblem", subIn);
			subIterations.add(subOut.copy());
			subIn.getParameters().incrementNbPatterns();
			reducedCost = subOut.getObjective().getValue();			
		}

		masterOut.displaySolution(masterIn);

		master.shutdown();
		subproblem.shutdown();
		return;
	}

	/**
	 * Print out iteration information.
	 * @param masterIn
	 * @param subIn
	 */
	public void DisplayIterations(MasterData masterIn, SubproblemData subIn) {
		for (int i = 0; i < this.getMasterIterations().size(); i++) {
			System.out.println("Iteration: " +i);
			System.out.println("Master Problem");
			masterIterations.get(i).displaySolution(masterIn);
			System.out.println();
			subIterations.get(i).displaySolution(subIn);
			System.out.println();		
		}
		return;
	}

	/**
	 * Main expecting the base URL and the client ID as arguments.
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length<2){
			System.out.println("The base URL and API key are missing");
			System.exit(1);
		}

		// Get base URL and API key from the given arguments
		String baseURL = args[0];
		String apiKeyClientId = args[1];

		// Create the controller
		ColumnGeneration ctrl = new ColumnGeneration(baseURL,
				apiKeyClientId,
				"CuttingStock",
				"opl/cuttingStock.mod",
				"opl/cuttingStock-sub.mod");

		// Optimize the model
		ctrl.optimize(MasterData.default1(), SubproblemData.default1());
	}
}
