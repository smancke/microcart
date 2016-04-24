Liebe(r) ${cart.orderData.givenName},

vielen Dank für deine Bestellung!

<#if paymentInfo??>${paymentInfo}</#if>
Positionen:
<#list cart.positions as position>
 <#if position.type?? && (position.type == "voucher" || position.type == "download")>
${position.title} ${position.pricePerUnit?string.currency}
 <#else>
${position.quantity}m ${position.title} (${position.pricePerUnit?string.currency}/m)
 </#if>
</#list>
Verpackung & Versand ${cart.calculatedShippingCosts?string.currency}

Gesamtbetrag: ${cart.totalPrice?string.currency}

<#if cart.containsDownloads()>
 <#if cart.orderData.paymentType == "paypal">
Die Download Dateien können unter folgendem Link herunter geladen werden:
 <#else>
Die Download Dateien können nach Bestätigung der Zahlung unter folgendem Link herunter geladen werden:
 </#if>

    ${cfg.shopURLPrefix}/download/${cart.id}
</#if>


Dein Bilderbuchstoff-Team