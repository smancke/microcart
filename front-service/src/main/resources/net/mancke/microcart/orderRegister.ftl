<!--# include virtual="/_header?title=Bestellung&page=shop" -->

<#setting locale="de_DE">

    <div class="form-container container">
    <h1>Bestellung</h1>

 	<form autocomplete="off" method="POST" action="/login">
 	  <input type="hidden" name="forwardURL" value="/shop/orderData">
      <section class="form-part">
        <h3><small>Login, wenn Du schon einen Account hast.</small></h3>
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
<!--    
 	<form autocomplete="off" method="POST" action="/shop/pay">
      <section class="form-part">
        <h3><small>Registrieren, damit Deine Daten gespeichert werden.</small></h3>
        <ul class="form-fields">
          <li class="form-field-group">
             <div class="form-field">
	          <label for="email">E-Mail</label>
	          <input type="email" class="form-control" id="email" name="email"/>
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
	          <input type="submit" value="Jetzt Registrieren &gt;&gt;" class="btn btn-lg  btn-primary"/>          
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
-->
            
  	<form autocomplete="off" method="GET" action="/shop/orderData">
      <section class="form-part">
        <h3><small>Direkt zur Dateneingabe</small></h3>
        <ul class="form-fields">
          <li class="form-field-group">
             <div class="form-field">	
	          <input type="submit" value="Einfach bestellen &gt;&gt;" class="btn btn-lg  btn-primary"/>          
			 </div>
          </li>          
	   </ul>
	  </section>
    </form>
        
    </div> 
    
<!--# include virtual="/_footer" -->
