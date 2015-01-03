package net.mancke.microcart;

public class ValidationError {
	
	String message;
	String fieldId;
	
	public ValidationError(String fieldId, String message) {
		this.fieldId = fieldId;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

}
