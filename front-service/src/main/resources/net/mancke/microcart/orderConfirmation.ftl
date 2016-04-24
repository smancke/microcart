<!--# include virtual="/_header?title=Bestellung&page=orderConfirmation" -->

<#setting locale="de_DE">

    <div class="container">
	    <h1>Bestellung erfolgt!</h1>
		<p>Herzlichen Glückwunsch, die Bestellung wurde erfolgreich aufgegeben.</p>
		
		<#if paymentInfo??>${paymentInfo}</#if>

		<#if cart.containsDownloads()>
            <br><br><p>
				<#if cart.orderData.paymentType == "paypal">
                    Die Download Dateien können unter folgendem Link herunter geladen werden:
				<#else>
                    Die Download Dateien können nach Bestätigung der Zahlung unter folgendem Link herunter geladen werden:
				</#if><br>
				<a href="${cfg.shopURLPrefix}/download/${cart.id}">${cfg.shopURLPrefix}/download/${cart.id}</a>
			</p>
		</#if>
	</div>
<!--# include virtual="/_footer" -->
