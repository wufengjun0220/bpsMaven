package com.mingtech.application.ecds.common.domain;



/**
 * BatchSequenceDto entity. @author MyEclipse Persistence Tools
 */

public class BatchSequenceDto  implements java.io.Serializable {


    // Fields    

     private String batchId;
     private String SDate;
     private String SSeq;


    // Constructors

    /** default constructor */
    public BatchSequenceDto() {
    }

    
    /** full constructor */
    public BatchSequenceDto(String SDate, String SSeq) {
        this.SDate = SDate;
        this.SSeq = SSeq;
    }

   
    // Property accessors

    public String getBatchId() {
        return this.batchId;
    }
    
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getSDate() {
        return this.SDate;
    }
    
    public void setSDate(String SDate) {
        this.SDate = SDate;
    }

    public String getSSeq() {
        return this.SSeq;
    }
    
    public void setSSeq(String SSeq) {
        this.SSeq = SSeq;
    }
   








}