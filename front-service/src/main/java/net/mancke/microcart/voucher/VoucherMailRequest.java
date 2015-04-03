package net.mancke.microcart.voucher;

import java.util.List;

public class VoucherMailRequest {
	
	VoucherRequest voucher;
	List<String> to;
	String subject;
	String template;
	
	public VoucherRequest getVoucher() {
		return voucher;
	}
	public void setVoucher(VoucherRequest voucher) {
		this.voucher = voucher;
	}
	public List<String> getTo() {
		return to;
	}
	public void setTo(List<String> to) {
		this.to = to;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	
}
