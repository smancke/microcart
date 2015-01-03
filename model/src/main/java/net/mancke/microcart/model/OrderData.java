package net.mancke.microcart.model;

import javax.ws.rs.FormParam;

/**
 * Representation Object for the data 
 * filled for an order.
 */
public class OrderData {

	@FormParam("familyName")
	private String familyName;
	
	@FormParam("givenName")
	private String givenName;

	@FormParam("honorificPrefix")
	private String honorificPrefix;

	@FormParam("email")
	private String email;

	@FormParam("phoneNumber")
	private String phoneNumber;
	
	@FormParam("streetAddress")
    private String streetAddress;

	@FormParam("houseNumber")
	private String houseNumber;

	@FormParam("locality")
    private String locality;

	@FormParam("postalCode")
    private String postalCode;
    
	@FormParam("paymentType")
    private String paymentType;

	@FormParam("note")
    private String note;

	@FormParam("agb")
	private boolean agb;
    
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getHonorificPrefix() {
		return honorificPrefix;
	}
	public void setHonorificPrefix(String honorificPrefix) {
		this.honorificPrefix = honorificPrefix;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public boolean isAgb() {
		return agb;
	}
	public void setAgb(boolean agb) {
		this.agb = agb;
	}    
}
