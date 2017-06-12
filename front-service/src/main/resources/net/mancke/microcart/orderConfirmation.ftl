<html>
<head>
    <link uic-remove rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <title>Bilderbuch-Stoff.de - Bestellbestätigung</title>
</head>
<body>
<uic-fragment name="content">

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

</uic-fragment>
<uic-tail>
<#setting locale="en_US">
    <script>
		if (fbq) {
            fbq('track', 'Purchase', {
                value: ${cart.totalPrice},
                currency: 'EUR'
            });
        }
    </script>
</uic-tail>
<#setting locale="de_DE">
</body>
</html>
