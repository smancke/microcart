Liebe(r) ${cart.orderData.givenName},

vielen dank für Deine Bestellung!

<#if paymentInfo??>${paymentInfo}</#if>

<#list cart.positions as position>
${position.quantity} x ${position.title} (${position.pricePerUnit?string.currency}/m)
</#list>

Gesamtbetrag: ${cart.totalPrice?string.currency}

Dein Bilderbuchstoff-Team