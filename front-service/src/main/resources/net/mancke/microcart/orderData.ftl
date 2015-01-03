<!--# include virtual="/_header?title=Bestellung&page=shop" -->

<#setting locale="de_DE">
<#assign d=cart.orderData>

    <div class="form-container container">
        <h1>Bestellung</h1>
        
  	<form autocomplete="on" method="POST" name="/shop/orderData">
      <section class="form-part">
        <h3>Persönliche Daten</h3>
        <ul class="form-fields">
          <li class="form-field-group">
            <div class="form-field">	
	          	<label for="honorificPrefix">Anrede</label>
			    <select class="form-control" id="honorificPrefix" name="honorificPrefix" class="salutation-select">
	              <option value=""<#if d.honorificPrefix?? && d.honorificPrefix == ''> selected</#if>></option>
	              <option value="Herr"<#if d.honorificPrefix?? && d.honorificPrefix == 'Herr'> selected</#if>>Herr</option>
	              <option value="Frau"<#if d.honorificPrefix?? && d.honorificPrefix == 'Frau'> selected</#if>>Frau</option>
	            </select>
	        </div>
          </li>
          <li class="form-field-group">
             <div class="form-field">
	          <label for="givenName" class="required">Vorname</label>
	          <input type="text" class="form-control" id="givenName" name="givenName" value="${d.givenName!}"/>
			 </div>
             <div class="form-field">	
	          <label for="lastName" class="required">Nachname</label>
	          <input type="text" class="form-control" id="lastName" name="lastName" value="${d.lastName!}"/>           
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
	          <label for="streetAddress" class="required">Straße</label>
	          <input type="text" class="form-control" id="streetAddress" name="address.streetAddress" value="${d.streetAddress!}"/>
			 </div>
             <div class="form-field">		
	          <label for="hnr" class="required">Hausnummer</label>
	          <input type="text" class="form-control" id="hnr" name="address.hnr" value="${d.hnr!}"/>           
			 </div>
          </li>
          <li class="form-field-group">
             <div class="form-field">	
	          <label for="postalCode" class="required">Postleitzahl</label>
	          <input type="text" class="form-control" id="postalCode" name="address.postalCode" value="${d.postalCode!}"/>
			 </div>
             <div class="form-field">		
	          <label for="locality" class="required">Ort</label>
	          <input type="text" class="form-control" id="locality" name="address.locality" value="${d.locality!}"/>           
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
	          	<input type="radio" class="form-control" id="preCash" name="paymentType" value="preCash"<#if d.paymentType?? && d.paymentType == 'preCash'> checked</#if>/><label for="preCash">Überweisung/Vorkasse</label>
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
        <input type="submit" value="Weiter &gt;&gt;" class="btn btn-lg  btn-primary"/>
        <input type="submit" name="goBack" value="&lt;&lt; Zurück" class="btn btn-lg  btn-default"/>
      </section>
    </form>
        
    </div> 
    
<!--# include virtual="/_footer" -->
