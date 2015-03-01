Liebe(r) ${cart.orderData.givenName},

vielen Dank für deine Bestellung!

<#if paymentInfo??>${paymentInfo}</#if>
Positionen:
<#list cart.positions as position>
${position.quantity}m ${position.title} (${position.pricePerUnit?string.currency}/m)
</#list>

Gesamtbetrag: ${cart.totalPrice?string.currency}


Dein Bilderbuchstoff-Team