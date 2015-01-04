<!--# include virtual="/_header?title=Bestellung&page=shop" -->

<#setting locale="de_DE">

    <div class="form-container container">
    <h1>Bestellung</h1>

 	<form autocomplete="off" method="POST" action="/login">
 	  <input type="hidden" name="forwardURL" value="/shop/orderData">
      <section class="form-part">
        <h3 style="max-width: 420px"><small>Wenn Du schon einen Account hast, logge Dich ein und Du musst Deine Daten nicht nochmal eingeben.</small></h3>
        <ul class="form-fields">
          <li class="form-field-group">
             <div class="form-field">
	          <label for="username">E-Mail</label>
	          <input type="text" class="form-control" id="username" name="username"/>
			 </div>
	      </li>
          <li class="form-field-group">
             <div class="form-field">	
	          <label for="password" class="">Passwort</label>
	          <input type="password" class="form-control" id="password" name="password"/>           
			 </div>
          </li>
          <li class="form-field-group">
             <div class="form-field">	
	          <input type="submit" value="Login &gt;&gt;" class="btn btn-lg  btn-primary"/>          
			 </div>
          </li>
          <li class="form-field-group">
	          <div class="form-field">	
	          	<hr/>
	          </div>
          </li>
	   </ul>
	  </section>
    </form>
            
  	<form autocomplete="off" method="GET" action="/shop/orderData">
      <section class="form-part">
        <h3><small>Zur Dateneingabe</small></h3>
        <ul class="form-fields">
          <li class="form-field-group">
             <div class="form-field">	
	          <input type="submit" value="Ohne Account bestellen &gt;&gt;" class="btn btn-lg  btn-primary"/>          
			 </div>
          </li>          
	   </ul>
	  </section>
    </form>
        
    </div> 
    
<!--# include virtual="/_footer" -->
