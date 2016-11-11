<html>
<head>
    <link uic-remove rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <title>Bilderbuch-Stoff.de - Bestelldaten</title>
</head>
<body>
<uic-fragment name="content">

<#setting locale="de_DE">
<#assign d=cart.orderData>

<#escape x as x?html>
    <div class="form-container container">
        <h1>Bestellung</h1>

  	<form autocomplete="on" method="POST" name="/shop/orderData" novalidate>
        
	    <#if validationErrors?? && validationErrors?size != 0>
	      <div class="alert alert-danger" >
	       Bitte überprüfe Deine Daten:
	        <ul>
	        <#noescape>
			    <#list validationErrors as error>
			    	<li>${error.message}</li>
			    </#list>
		    </#noescape>
		    </ul>
		    <#list validationErrors as error>
		        <#if error.fieldId == 'emailWarning'>
		        	<br>Sollt die E-Mail Adresse dennoch korrekt sein, bitte folgendes markieren:
		        	<br><input type="checkbox" class="form-control" id="forceEmail" name="forceEmail" value="true"/><label for="forceEmail">Die eingegebene E-Mail Adresse ist so korrekt.</label>
		    	</#if>
		    </#list>
		  </div>
		</#if>
      <section class="form-part">
        <h3>Persönliche Daten</h3>
        <ul class="form-fields">
          <li class="form-field-group">
             <div class="form-field">
	          <label for="givenName" class="required">Vorname</label>
	          <input type="text" class="form-control" id="givenName" name="givenName" value="${d.givenName!}"/>
			 </div>
             <div class="form-field">	
	          <label for="familyName" class="required">Nachname</label>
	          <input type="text" class="form-control" id="familyName" name="familyName" value="${d.familyName!}"/>           
			 </div>
          </li>
          <li class="form-field-group">
             <div class="form-field">
	          <label for="email" class="required">E-Mail</label>
	          <input type="email" class="form-control" id="email" name="email" value="${d.email!}"/>
			 </div>
             <div class="form-field">	
	          <label for="phoneNumber" class="">Telefon</label>
	          <input type="text" class="form-control" id="phoneNumber" name="phoneNumber" value="${d.phoneNumber!}"/>           
			 </div>
          </li>
        </ul>
      </section>
      <section class="form-part">
        <h3>Anschrift</h3>
        <ul class="form-fields">
          <li class="form-field-group">
             <div class="form-field">	
	          <label for="streetAddress" class="required">Straße, Hausnr.</label>
	          <input type="text" class="form-control" id="streetAddress" name="streetAddress" value="${d.streetAddress!}"/>
			 </div>
          </li>
          <li class="form-field-group">
             <div class="form-field">	
	          <label for="postalCode" class="required">Postleitzahl</label>
	          <input type="text" class="form-control" id="postalCode" name="postalCode" value="${d.postalCode!}"/>
			 </div>
             <div class="form-field">		
	          <label for="locality" class="required">Ort</label>
	          <input type="text" class="form-control" id="locality" name="locality" value="${d.locality!}"/>           
			 </div>
          </li>
        </ul>
      </section>

      <section class="form-part">
        <h3>Bezahlweg</h3>
        <ul class="form-fields">
          <li>
            <div class="form-field">
	          	<input type="radio" class="form-control" id="paypal" name="paymentType" value="paypal"<#if d.paymentType?? && d.paymentType == 'paypal'> checked</#if>/><label for="paypal"><img src="https://www.paypalobjects.com/webstatic/de_DE/i/de-pp-logo-100px.png" border="0" alt="PayPal Logo" /></label>
            </div>
          </li>
          <li>
            <div class="form-field">
	          	<input type="radio" class="form-control" id="paymentType" name="paymentType" value="preCash"<#if d.paymentType?? && d.paymentType == 'preCash'> checked</#if>/><label for="paymentType">Überweisung/Vorkasse</label>
            </div>
          </li>
        </ul>
      </section>
      
      <section class="form-part">
        <h3>Nachricht</h3>
        <ul class="form-fields">
          <li class="form-field-group">
		      <div class="form-group">
			    <label for="note">Zusatzinformationen und Sonderwünsche</label>
			    <textarea name="note" id="note" class="form-control" rows="4">${d.note!}</textarea>
			  </div>
          </li>
        </ul>
      </section>

      <section class="form-part">
        <ul class="form-fields">
          <li>
            <div class="form-field">
	          	<input type="checkbox" class="form-control" id="agb" name="agb" value="true"<#if d.agb> checked</#if>/><label for="agb" class="required">Ich habe die <a href="/agb">Allgemeinen Geschäftsbedingungen</a> gelesen und verstanden und akzeptiere sie.</label>
            </div>
          </li>
          <li>
            <p>* Pflichtfelder</p>
          </li>
        </ul>
      </section>

      <section class="call-to-action">
        <input type="submit" value="Jetzt kaufen &gt;&gt;" class="btn btn-lg  btn-primary"/>
        <input type="submit" name="goBack" value="&lt;&lt; Warenkorb" class="btn btn-lg  btn-default"/>
      </section>
    </form>
        
    </div> 
    
</#escape>
<!--# include virtual="/_footer" -->

<#if validationErrors?? && validationErrors?size != 0>
    <script type="text/javascript">
    	$(function() {
    	    $('.form-field').addClass('has-success');
	    	<#list validationErrors as error>
			   $('#${error.fieldId}').parent().removeClass('has-success');
	    	   $('#${error.fieldId}').parent().addClass('has-error');
		    </#list>
    	});
    </script>
</#if>

</uic-fragment>
</body>
</html>
