package net.mancke.microcart.model;

public class PostProcessing {

	private boolean userConfirmationMailSent = false;

	public boolean isUserConfirmationMailSent() {
		return userConfirmationMailSent;
	}

	public void setUserConfirmationMailSent(boolean userConfirmationMailSent) {
		this.userConfirmationMailSent = userConfirmationMailSent;
	}
	
}
