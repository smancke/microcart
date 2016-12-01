package net.mancke.microcart.model;

public class Position {
	
	public static final String TYPE_ARTICLE = "article";
	public static final String TYPE_VOUCHER = "voucher";
	public static final String TYPE_DOWNLOAD = "download";

	String type = TYPE_ARTICLE;
	String articleId;
	String title;
	String imageUrl;
	float quantity;
	float quantityUnits;
	float quantityMin;
	boolean quantityFixed;
	boolean freeShipping;
	float pricePerUnit;
	float discountPercent;
	String downloadLink;

	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	public float getQuantityUnits() {
		return quantityUnits;
	}

	public void setQuantityUnits(float quantityUnits) {
		this.quantityUnits = quantityUnits;
	}

	public float getQuantityMin() {
		return quantityMin;
	}

	public void setQuantityMin(float quantityMin) {
		this.quantityMin = quantityMin;
	}

	public boolean isQuantityFixed() {
		return quantityFixed;
	}

	public void setQuantityFixed(boolean quantityFixed) {
		this.quantityFixed = quantityFixed;
	}

	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public float getQuantity() {
		return quantity;
	}
	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}
	public float getPricePerUnit() {
		return pricePerUnit;
	}
	public void setPricePerUnit(float pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}
	public float getPositionPrice() {
		return pricePerUnit * quantity;
	}
	public float getDiscountPercent() {
		return discountPercent;
	}
	public void setDiscountPercent(float discountPercent) {
		this.discountPercent = discountPercent;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isFreeShipping() {
		return freeShipping;
	}

	public void setFreeShipping(boolean freeShipping) {
		this.freeShipping = freeShipping;
	}


}
