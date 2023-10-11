package com.hoddmimes.distributor;

/**
 * Distributor error exception used to signal (fatal) error exception
 * 
 * @author POBE
 * 
 */
public class DistributorException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for creating an DistributorException instance.
	 * 
	 * @param pMessage - exception message
	 */
	public DistributorException(String pMessage) {
		super(pMessage);
		this.fillInStackTrace();
	}

	public DistributorException(String pMessage, Exception e) {
		super(pMessage, e);
		this.fillInStackTrace();
	}

	public DistributorException( Exception e) {
		super(e);
		this.fillInStackTrace();
	}
}
