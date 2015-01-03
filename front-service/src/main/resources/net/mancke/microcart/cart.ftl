<!--# include virtual="/_header?title=Einkaufswagen&page=shop" -->

<#setting locale="de_DE">

    <div class="container cart">
        <h1>Einkaufswagen <small></small></h1>        
        
        <#if cart.positions?size == 0 >
           <p class="bs-big-font">Es befinden sich keine Artikel in Deinem Einkaufswagen</p> 
        <#else> 
	        <div class="row cart-bottom-row">
			        <div class="cart-order">
			          <a class="btn btn-primary btn-lg" href="/shop/orderRegister">Jetzt bestellen &gt;&gt;</a>
			        </div>		        		        		        
		    </div>
        </#if> 
        <#if cart.positions?size != 0 >
	        <div class="row cart-bottom-row">
			        <div class="cart-price-total">
			          <h3>Summe: ${cart.totalPrice?string.currency}</h3>
			        </div>		        		        		        
		    </div>
	    </#if>

        <#list cart.positions as position>
	        <div class="row cart-row">
		        <div class="cart-image">
		            <img src="${position.imageUrl}" alt="${position.title}" />
		        </div>
		        <div class="cart-description">
		          <h3>${position.title}</h3>
		        </div>
		        <div class="cart-price">
		          <h3>${position.pricePerUnit?string.currency}</h3>
		        </div>
		        <div class="cart-quantity">
		             <form class="" action="/shop/my-cart/article" method="POST">
		                <input type="hidden" name="articleId" value="${position.articleId}">
                        <div class="form-group">
			                <div class="input-group" style="width: 170px">
			                  <input type="quantity" value="${position.quantity}" class="form-control" name="quantity" placeholder="m">
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
	        </div>
		</#list>
		
        <#if cart.positions?size != 0 >
		        <div class="row cart-bottom-row">
				        <div class="cart-order">
				          <a class="btn btn-primary btn-lg" href="/shop/orderRegister">Jetzt bestellen &gt;&gt;</a>
				        </div>		        		        		        
			    </div>
        </#if> 
    </div> 
    
<!--# include virtual="/_footer" -->
