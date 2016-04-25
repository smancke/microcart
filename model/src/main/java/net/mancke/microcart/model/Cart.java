package net.mancke.microcart.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.joda.time.DateTime;

public class Cart {
	
	public static final Object PAYPAL = "paypal";
	public static final Object PRECASH = "preCash";
	
	private float shippingCosts;
	private float shippingCostLimit;
	private String id;
	private String userId;
	private List<Position> positions = Collections.EMPTY_LIST;
	private OrderData orderData = new OrderData();
    private DateTime timestamp = new DateTime();
    private DateTime timestampLastUpdated = new DateTime();
    private PostProcessing postProcessing = new PostProcessing();
    private Map<String,Boolean> categories;

    public Cart(float shippingCosts, float shippingCostLimit) {
    	this.shippingCosts = shippingCosts;
    	this.shippingCostLimit = shippingCostLimit;
    }

    public Cart() {
    }

	public boolean isAllowDownload() {
		return ! isDownloadCountExceeded() &&
				("paypal".equals(orderData.getPaymentType())  ||
				(categories != null && categories.get("paid").booleanValue()));
	}

	public boolean isDownloadCountExceeded() {
		return postProcessing.getDownloadCounter() >= 10;
	}

	public boolean containsVoucher() {
    	for (Position position : getPositions()) {
    		if (Position.TYPE_VOUCHER.equals(position.getType())) {
    			return true;
    		}
    	}
		return false;
	}

	public boolean containsDownloads() {
		return positions.stream().filter(position -> position.getDownloadLink() != null).count() > 0;
	}

	/**
	 * price reflecting the discounts and shipping
	 */
	public float getTotalPrice() {
		return getTotalPriceWithoutShipping() + getCalculatedShippingCosts();
	}
	
	/**
	 * price reflecting the discounts and shipping
	 */
	public float getTotalPriceWithoutShipping() {
		float sum = 0;
		for (Position position : positions) {
			if (position.getDiscountPercent() > 0) {
				sum += position.getPositionPrice() - (0.01f * position.getDiscountPercent() * position.getPositionPrice());
			} else {
				sum += position.getPositionPrice();				
			}
		}
		return sum;
	}

	public float getCalculatedShippingCosts() {
		if (getTotalPriceWithoutShipping() >= getShippingCostLimit()) {
			return 0;
		}
		boolean onlyDownloadableArticles = positions.stream().filter(position -> position.getDownloadLink() == null).count() == 0;
		if (onlyDownloadableArticles) {
			return 0;
		}
		return getShippingCosts();
	}
	
	public float getDiscountSaving() {
		float sum = 0;
		for (Position position : positions) {
			if (position.getDiscountPercent() > 0) {
				sum += 0.01f * position.getDiscountPercent() * position.getPositionPrice();
			}
		}
		return sum;
	}

	public String toString() {
		return "cartid="+id;
	}
	
	public void addDeleteOrUpdatePosition(Position position) {
		if (positions.size() == 0) {
			positions = new ArrayList<Position>(1);
		}
		for (ListIterator<Position> iter = positions.listIterator(); iter.hasNext();) {
			if (iter.next().getArticleId().equals(position.getArticleId())) {
				iter.set(position);
				if (position.getQuantity() == 0) {
					iter.remove();
				}
				return;
			}
		}
		// not found by iteration
		if (position.getQuantity() > 0) {
			positions.add(position);
		}
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Position> getPositions() {
		return positions;
	}
	public void setPositions(List<Position> position) {
		this.positions = position;
	}	

	public OrderData getOrderData() {
		return orderData;
	}
	public void setOrderData(OrderData orderData) {
		this.orderData = orderData;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public DateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}
	public DateTime getTimestampLastUpdated() {
		return timestampLastUpdated;
	}
	public void setTimestampLastUpdated(DateTime timestampLastUpdated) {
		this.timestampLastUpdated = timestampLastUpdated;
	}
	public float getShippingCosts() {
		return shippingCosts;
	}
	public void setShippingCosts(float shippingCosts) {
		this.shippingCosts = shippingCosts;
	}
	public float getShippingCostLimit() {
		return shippingCostLimit;
	}
	public void setShippingCostLimit(float shippingCostLimit) {
		this.shippingCostLimit = shippingCostLimit;
	}

	public PostProcessing getPostProcessing() {
		return postProcessing;
	}

	public void setPostProcessing(PostProcessing postProcessing) {
		this.postProcessing = postProcessing;
	}

	public Map<String, Boolean> getCategories() {
		return categories;
	}

	public void setCategories(Map<String, Boolean> categories) {
		this.categories = categories;
	}
}
