Liebe(r) ${cart.orderData.givenName},

vielen Dank für deine Bestellung!

<#if paymentInfo??>${paymentInfo}</#if>
Positionen:
<#list cart.positions as position>
 <#if position.type?? && position.type == "voucher">
${position.title} ${position.pricePerUnit?string.currency}
 <#else>
${position.quantity}m ${position.title} (${position.pricePerUnit?string.currency}/m)
 </#if>
</#list>

Gesamtbetrag: ${cart.totalPrice?string.currency}


Dein Bilderbuchstoff-Team