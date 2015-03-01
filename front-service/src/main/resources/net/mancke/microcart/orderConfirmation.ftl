<!--# include virtual="/_header?title=Bestellung&page=orderConfirmation" -->

<#setting locale="de_DE">

    <div class="container">
	    <h1>Bestellung erfolgt!</h1>
		<p>Herzlichen Glückwunsch, die Bestellung wurde erfolgreich aufgegeben.</p>
		
		<#if paymentInfo??>${paymentInfo}</#if>
	</div>
<!--# include virtual="/_footer" -->
