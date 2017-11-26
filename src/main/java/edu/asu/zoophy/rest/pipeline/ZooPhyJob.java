package edu.asu.zoophy.rest.pipeline;

import java.util.List;
import java.util.Map;

import edu.asu.zoophy.rest.pipeline.glm.Predictor;

/**
 * Encapsulates related info for a ZooPhy Job
 * @author devdemetri
 */

/**
 * Adding support for markov jumps
 * @author matteo-V
 */
public final class ZooPhyJob {
	
	private final String ID;
	private final String JOB_NAME;
	private final String REPLY_EMAIL;
	private final boolean USE_GLM;
	//add new variable for ZooPhy Job
	private final boolean USE_JUMPS;
	private final boolean USE_CUSTOM_PREDICTORS;
	private final Map<String, List<Predictor>> predictors;
	private final XMLParameters XML_OPTIONS;
	//modify constructor to take extra bool param for jump analyis
	public ZooPhyJob(String id, String name, String email, boolean useGLM, boolean useJumps, Map<String, List<Predictor>> predictors, XMLParameters xmlOptions) {
		ID = id;
		JOB_NAME = name;
		REPLY_EMAIL = email;
		USE_GLM = useGLM;
		//set value of new var in constructor @ matteo-V
		USE_JUMPS = useJumps;
		if (predictors == null || predictors.isEmpty()) {
			USE_CUSTOM_PREDICTORS = false;
			this.predictors = null;
		}
		else {
			this.predictors = predictors;
			USE_CUSTOM_PREDICTORS = true;
		}
		XML_OPTIONS = xmlOptions;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getJobName() {
		return JOB_NAME;
	}
	
	public String getReplyEmail() {
		return REPLY_EMAIL;
	}
	
	public boolean isUsingGLM() {
		return USE_GLM;
	}

	// add new accessor method

	public boolean isUsingJumps() { return USE_JUMPS; }

	public boolean isUsingCustomPredictors() {
		return USE_CUSTOM_PREDICTORS;
	}

	public Map<String, List<Predictor>> getPredictors() {
		return predictors;
	}
	
	public XMLParameters getXMLOptions() {
		return XML_OPTIONS;
	}
	
}
