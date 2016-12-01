package net.mancke.microcart;

import org.springframework.web.client.RestTemplate;

public class ArticleService {
	
	private static final String ARTICLE_RESOURCE = "/shop/article";
	
	private FrontConfiguration configuration;

	public ArticleService(FrontConfiguration cfg) {
		this.configuration = cfg;
	}
	
	public Article getArticle(String id) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(configuration.getBackendURL() + ARTICLE_RESOURCE + "/"+ id, Article.class);
	}
	
	public static class Article {
	
	    private String id;
	    private String title;
	    private String img_thumb;
	    private float price;
	    private float discount;
		private float quantityUnits;
		private float quantityMin;
		private boolean quantityFixed;
		private String downloadLink;
		boolean freeShipping;

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

		public String getId() {
	        return id;
	    }
	
	    public void setId(String id) {
	        this.id = id;
	    }
	
	    public String getTitle() {
	        return title;
	    }
	
	    public void setTitle(String title) {
	        this.title = title;
	    }
	
	    public String getImg_thumb() {
	        return img_thumb;
	    }
	
	    public void setImg_thumb(String img_thumb) {
	        this.img_thumb = img_thumb;
	    }
	
		public float getPrice() {
			return price;
		}
	
		public void setPrice(float price) {
			this.price = price;
		}

		public float getDiscount() {
			return discount;
		}

		public void setDiscount(float discount) {
			this.discount = discount;
		}

		public boolean isFreeShipping() {
			return freeShipping;
		}

		public void setFreeShipping(boolean freeShipping) {
			this.freeShipping = freeShipping;
		}
	}
}