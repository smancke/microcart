<!--# include virtual="/_header?title=Einkaufswagen&page=shop" -->

    <div class="container">

        <h1>Einkaufswagen <small></small></h1>        
        <p class="bs-big-font">Folgende Stoffe befinden sich in Deinem Einkaufswagen</p> 

        <#list cart.positions as position>

	        <div class="row">
		        <div class="col-sm-8 col-md-8">
		          ${position.articleId}, ${position.quantity}
		        </div>
	        </div>
	        
		</#list>
    </div> 
<!--# include virtual="/_footer" -->
