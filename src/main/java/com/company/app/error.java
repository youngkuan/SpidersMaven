package com.company.app;

public class error {
	private String error;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @param error
	 */
	public error(String error) {
		super();
		this.error = error;
	}

	@Override
	public String toString() {
		return "error [error=" + error + "]";
	}
	
	
	
	
}
