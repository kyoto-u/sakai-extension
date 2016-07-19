/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.impl.dao;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyJobDao;
import org.sakaiproject.kaltura.impl.dao.jdbc.data.SiteCopyJobData;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;

/**
 * Implementation of DAO Interface for Kaltura Site Copy Batch 
 * 
 * @author Esh Nagappan (ynagappan @ unicon.net)
 */
public class KalturaSiteCopyJobDaoImpl implements KalturaSiteCopyJobDao {

    private static final Logger log = LoggerFactory.getLogger(KalturaSiteCopyJobDaoImpl.class);

    private SiteCopyJobData siteCopyJobData;
    public void setSiteCopyJobData(SiteCopyJobData siteCopyJobData) {
        this.siteCopyJobData = siteCopyJobData;
    }

    /**
     * Check the work queue for any Kaltura Site Copy Batch job with new status 
     * 
     * @return {@link KalturaSiteCopyBatch) object
     */
    public Optional<KalturaSiteCopyJob> checkWorkQueue(String status){
        List<KalturaSiteCopyJob> jobs = siteCopyJobData.getJobs(status);

        log.debug("Checked Kaltura Site Copy Batch work queue. Found rows: {}", jobs.size());

        return jobs.stream().findFirst();
    }

    /**
    * Get the kaltura site copy job object related to job id
    * 
    * @param jobId the job ID
    * @return the {@link KalturaSiteCopyJob} object
    */
    public KalturaSiteCopyJob getSiteCopyJob(Long jobId){
        if (jobId == null) {
            throw new IllegalArgumentException("Job ID cannot be blank.");
        }

        KalturaSiteCopyJob kalturaSiteCopyJob = siteCopyJobData.getJob(jobId);

        if (kalturaSiteCopyJob == null) {
            log.debug("Checked Kaltura Site Copy Job table. Found no rows.");
        } else {
            log.debug("Checked Kaltura Site Copy Job table. Found row for jobId: " + jobId.toString());
            return kalturaSiteCopyJob;
        }

        return null;
    }

    /**
     *Get the list of Kaltura site copy jobs associated with the batch ID
     *@param batchId - batch Id associated with kaltura site copy job
     *@return List of KalturaSiteCopyJob {@link KalturaSiteCopyJob} objects
     */
    public List<KalturaSiteCopyJob> getAllJobs(Long batchId){
        if (batchId == null) {
            throw new IllegalArgumentException("Batch ID cannot be blank.");
        }

        List<KalturaSiteCopyJob> jobs = siteCopyJobData.getBatchJobs(batchId);

        if (jobs.isEmpty()) {
            log.debug("Checked Kaltura Site Copy Job table. Found no rows.");
        } else {
            log.debug("Checked Kaltura Site Copy Job table. Found row for jobId: " + batchId.toString());
        }

        return jobs;
    }
    
    /**
    * Add/update a new kaltura site copy job object
    * 
    * @param kalturaSiteCopyJob the {@link KalturaSiteCopyJob} object to add
    * @return true, if added/updated successfully
    */
    public Long save(KalturaSiteCopyJob job, boolean update ){
    
        if (!job.isValid()) {
            log.error("Job details are not valid. Could not create kaltura site copy job:" + job.toString());
        }

        Long jobId = siteCopyJobData.persistJob(job, update);

        if (!update && jobId == null) {
            log.error("Could not persist kaltura site copy job.");
        } else {
            log.info("Persisted kaltura site copy job row:" + jobId);
        }

        return jobId;
    }

}
