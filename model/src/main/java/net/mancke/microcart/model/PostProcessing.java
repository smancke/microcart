package net.mancke.microcart.model;

public class PostProcessing {

	private boolean userConfirmationMailSent = false;
	private int downloadCounter = 0;

	public int getDownloadCounter() {
		return downloadCounter;
	}

	public void setDownloadCounter(int downloadCounter) {
		this.downloadCounter = downloadCounter;
	}

	public boolean isUserConfirmationMailSent() {
		return userConfirmationMailSent;
	}

	public void setUserConfirmationMailSent(boolean userConfirmationMailSent) {
		this.userConfirmationMailSent = userConfirmationMailSent;
	}
	
}
