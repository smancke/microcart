package net.mancke.microcart.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.joda.time.DateTime;

public class Cart {
	
	private String id;
	private String userId;
	private List<Position> positions = Collections.EMPTY_LIST;
	private OrderData orderData = new OrderData();
    private DateTime timestamp = new DateTime();
    private DateTime timestampLastUpdated = new DateTime();
    
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
	
	public float getTotalPrice() {
		float sum = 0;
		for (Position position : positions) {
			sum += position.getPositionPrice();
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
}
