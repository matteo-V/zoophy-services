package edu.asu.zoophy.rest.pipeline;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.zoophy.rest.database.ZooPhyDAO;
import edu.asu.zoophy.rest.index.LuceneSearcher;

/**
 * Responsible for running ZooPhy jobs
 * @author devdemetri
 */
public class ZooPhyRunner {

	private final ZooPhyJob job;
	private final ZooPhyMailer mailer;
	private final Logger log;

	public ZooPhyRunner(String replyEmail, String jobName, boolean useGLM) throws PipelineException {
		log = Logger.getLogger("ZooPhyRunner");
		log.info("Initializing ZooPhy Job");
		job = new ZooPhyJob(generateID(),jobName,replyEmail, useGLM);
		log.info("Initializing ZooPhyMailer... : "+job.getID());
		mailer = new ZooPhyMailer(job);
	}
	
	/**
	 * Runs the ZooPhy pipeline on the given Accessions
	 * @param accessions
	 * @param dao 
	 * @param indexSearcher 
	 * @throws PipelineException
	 */
	public void runZooPhy(List<String> accessions, ZooPhyDAO dao, LuceneSearcher indexSearcher) throws PipelineException {
		try {
			log.info("Sending Start Email... : "+job.getID());
			mailer.sendStartEmail();
			log.info("Initializing Sequence Aligner... : "+job.getID());
			SequenceAligner aligner = new SequenceAligner(job, dao, indexSearcher, job.isUsingGLM());
			log.info("Running Sequence Aligner... : "+job.getID());
			aligner.align(accessions);
			log.info("Initializing Beast Runner... : "+job.getID());
			BeastRunner beast = new BeastRunner(job, mailer);
			log.info("Starting Beast Runner... : "+job.getID());
			File treeFile = beast.run();
			log.info("Sending Results Email... : "+job.getID());
			mailer.sendSuccessEmail(treeFile); 
			PipelineManager.removeProcess(job.getID());
			log.info("ZooPhy Job Complete: "+job.getID());
		}
		catch (PipelineException pe) {
			log.log(Level.SEVERE, "PipelineException for job: "+job.getID()+" : "+pe.getMessage());
			log.info("Sending Failure Email... : "+job.getID());
			mailer.sendFailureEmail(pe.getUserMessage()); 
		}
		catch (Exception e) {
			log.log(Level.SEVERE, "Unhandled Exception for job: "+job.getID()+" : "+e.getMessage());
			log.info("Sending Failure Email... : "+job.getID());
			mailer.sendFailureEmail("Internal Server Error");
		}
	}

	/**
	 * Generates a UUID to be used as a jobID
	 * @return Unused UUID
	 * @throws PipelineException 
	 */
	private String generateID() throws PipelineException {
		try {
			log.info("Generating UID...");
			String id  = java.util.UUID.randomUUID().toString();
			log.info("Assigned ID: "+id);
			return id;
		}
		catch (Exception e) {
			log.log(Level.SEVERE, "Error generating job ID: "+e.getMessage());
			throw new PipelineException("Error generating job ID: "+e.getMessage(), "Failed to start ZooPhy Job!");
		}
	}

	/**
	 * @return generated ID for the ZooPhy job being run
	 */
	public String getJobID() {
		return job.getID();
	}

}
