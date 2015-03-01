<!--# include virtual="/_header?title=Einkaufswagen&page=shop" -->

<#setting locale="de_DE">

    <div class="container cart">
        <h1>Einkaufswagen <small></small></h1>        
        
        <#if cart.positions?size == 0 >
           <p class="bs-big-font">Es befinden sich keine Artikel in deinem Einkaufswagen</p> 
        <#else> 
	        <div class="row cart-bottom-row">
			        <div class="cart-order">
			          <a class="btn btn-primary btn-lg" href="/shop/orderRegister">Jetzt bestellen &gt;&gt;</a>
			        </div>		        		        		        
		    </div>
        </#if> 

        <#if cart.positions?size != 0 >
		        <div class="row cart-bottom-row">
		        		<#if (cart.shippingCostLimit-cart.totalPriceWithoutShipping) gt 0 && (cart.shippingCostLimit-cart.totalPriceWithoutShipping) lt 20>
					        <div class="cart-description">
					          <h3 class="onsale">Nur noch ${(cart.shippingCostLimit-cart.totalPriceWithoutShipping)?string.currency} und die Versandkosten entfallen!</h3>
					        </div>
					    </#if>
			    </div>
		        <#list cart.positions as position>
			        <div class="row cart-row">
				        <div class="cart-image">
				            <img src="${position.imageUrl}" alt="${position.title}" />
				        </div>
				        <div class="cart-description">
				          <h3>${position.title}</h3>
				        </div>
				        <div class="cart-quantity">
				             <form class="" action="/shop/my-cart/article" method="POST">
				                <input type="hidden" name="articleId" value="${position.articleId}">
		                        <div class="form-group">
					                <div class="input-group">
					                  <#setting locale="us_US">
					                  <input type="number" step="0.1" min="0.5" max="500" value="${position.quantity}" class="form-control" name="quantity" placeholder="m">
					                  <#setting locale="de_DE">
					                  <div class="input-group-addon">m</div>
					                  <div class="input-group-btn">
						                <button type="submit" class="btn btn-default">
						                  <span class="glyphicon glyphicon-refresh"></span> 		                  
						                </button>           
						                <button type="submit" name="action" value="removeArticle" class="btn btn-default">
						                  <span class="glyphicon glyphicon-remove"></span> 		                  
						                </button>           
						              </div>          
					                </div>
					            </div>			            
				              </form>
				        </div>
				        <div class="cart-price">
				          <h3>${position.pricePerUnit?string.currency}/m</h3>
				        </div>
			        </div>
				</#list>
		        <#if cart.discountSaving gt 0>
			        <div class="row cart-row">
				        <div class="cart-description">
				          <h3>Rabatt</h3>
				        </div>
				        <div class="cart-other-price onsale">
				          <h3>- ${cart.discountSaving?string.currency}</h3>
				        </div>
			        </div>
				</#if>
		        <div class="row cart-row">
			        <div class="cart-description">
			          <h3>Verpackung &amp; Versand <small>Versandkostenfrei ab ${cart.shippingCostLimit?string.currency} Warenwert</small></h3>
			        </div>
			        <div class="cart-other-price">
			          <h3>${cart.calculatedShippingCosts?string.currency}</h3>
			        </div>
		        </div>
		        <div class="row cart-bottom-row">
				        <div class="cart-price-total">
				          <h3>Summe: ${cart.totalPrice?string.currency}</h3>
				        </div>		        		        		        
			    </div>
			
		        <div class="row cart-bottom-row">
				        <div class="cart-order">
				          <a class="btn btn-primary btn-lg" href="/shop/orderRegister">Jetzt bestellen &gt;&gt;</a>
				        </div>		        		        		        
			    </div>
        </#if> 
    </div> 
    
<!--# include virtual="/_footer" -->
