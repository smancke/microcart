package net.mancke.microcart.model;

import java.util.Date;

public class Voucher {

	public static final String TYPE_CREDIT = "credit";
	
	private String id;
	
	private Date expiryDate;
	
	private boolean used;
	
	private String usedBy;
	
	private String voucherType;
	
	private float voucherAmount;

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
}
