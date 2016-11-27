package net.mancke.microcart.model;

import java.util.Date;

public class Voucher {

	public static final String TYPE_CREDIT = "credit";

	private String id;

	private String comment;

	private Date expiryDate;

	private Date creationDate;

	private boolean used;
	
	private String usedBy;
	
	private String voucherType;

	private float voucherAmount;

	private float initialAmount;

	public Voucher() {
		this.creationDate = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public String getUsedBy() {
		return usedBy;
	}

	public void setUsedBy(String usedBy) {
		this.usedBy = usedBy;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public float getVoucherAmount() {
		return voucherAmount;
	}

	public void setVoucherAmount(float voucherAmount) {
		this.voucherAmount = voucherAmount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public float getInitialAmount() {
		return initialAmount;
	}

	public void setInitialAmount(float initialAmount) {
		this.initialAmount = initialAmount;
	}

}
