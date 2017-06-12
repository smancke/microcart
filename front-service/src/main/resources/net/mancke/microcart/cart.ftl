<html>
<head>
    <link uic-remove rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <title>Bilderbuch-Stoff.de - Einkaufswagen</title>
</head>
<body>
<uic-fragment name="content">

<#setting locale="de_DE">

    <div class="container cart">
        <h1>Einkaufswagen
            <small></small>
        </h1>

    <#if cart.positions?size == 0 >
        <p class="bs-big-font">Es befinden sich keine Artikel in deinem Einkaufswagen</p>
    <#else>
        <div class="row cart-bottom-row">
            <div class="cart-order">
                <a class="btn btn-primary btn-lg" href="/shop/orderRegister">Jetzt bestellen &gt;&gt;</a>
            </div>
        </div>
    </#if>

    <#if cart.positions?size != 0 >
        <div class="row cart-bottom-row">
            <#if (cart.calculatedShippingCosts) gt 0 && (cart.shippingCostLimit-cart.totalPriceWithoutShipping) gt 0 && (cart.shippingCostLimit-cart.totalPriceWithoutShipping) lt 20>
                <div class="cart-description">
                    <h3 class="onsale">Nur
                        noch ${(cart.shippingCostLimit-cart.totalPriceWithoutShipping)?string.currency} und die
                        Versandkosten entfallen!</h3>
                </div>
            </#if>
        </div>
        <#list cart.positions as position>
            <#if position.type?? && position.type == "voucher">
                <div class="row cart-row no-image">
                    <div class="cart-description cart-h">${position.title}</div>
                    <div class="cart-other-price onsale">${position.pricePerUnit?string.currency}</div>
                </div>
            <#else>
                <div class="row cart-row">
                    <div class="cart-image">
                        <img src="${position.imageUrl}" alt="${position.title}"/>
                    </div>
                    <div class="cart-description">
                        <div class="cart-label">${position.title}</div>
                    </div>
                    <div class="cart-price">
                        <span class="cart-label">${position.pricePerUnit?string.currency}<span
                            <#if (position.quantityUnits!0.1) == 1>style="visibility: hidden"</#if>>/m</span></span>

                        <div class="cart-quantity">
                            <form class="cart-quantity-form" action="/shop/my-cart/article" method="POST">
                                <input type="hidden" name="articleId" value="${position.articleId}">
                                <#setting locale="us_US">
                                <#if ! (position.quantityFixed!false)>
                                    <input type="number" value="${position.quantity}"
                                           step="${position.quantityUnits!"0.1"}" min="${position.quantityMin!"0.5"}"
                                           max="500" class="cart-quantity-form-field" name="quantity">
                                    <span
                                        <#if (position.quantityUnits!0.1) == 1>style="visibility: hidden"</#if>>m</span>
                                    <button type="submit" class="cart-quantity-form-field btn btn-default btn-sm">
                                        <span class="glyphicon glyphicon-refresh"></span>
                                    </button>
                                </#if>
                                <#setting locale="de_DE">
                                <button type="submit" name="action" value="removeArticle"
                                        class="cart-quantity-form-field btn btn-default btn-sm">
                                    <span class="glyphicon glyphicon-remove"></span>
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </#if>
        </#list>
        <#if cart.discountSaving gt 0>
            <div class="row cart-row no-image">
                <div class="cart-description cart-h">Rabatt</div>
                <div class="cart-other-price onsale">- ${cart.discountSaving?string.currency}</div>
            </div>
        </#if>
        <div class="row cart-row no-image">

            <div class="cart-description">
                <span class="cart-h">Versand innerhalb Deutschlands
                    <br><small>Versandkostenfrei ab ${cart.shippingCostLimit?string.currency} Warenwert.</small>
                </span>

                <br>
                <br>
                <#if ! cart.shippingBundleOrderId?has_content >
                  <a data-toggle="collapse" href="#shippingBundleCollapse">Mit offener Bestellung bündeln</a>
                <#else>
                    <br>
                    <span class="cart-h"><b>Wird verschickt mit: ${cart.shippingBundleOrderId}</b></span>
                    <form class="" action="/shop/my-cart/shippingBundle" method="POST">
                        <input type="hidden" name="shippingBundleOrderId" value="">
                        <button data-toggle="collapse" href="#shippingBundleCollapse" class="btn btn-default btn-sm">Ändern</button>
                        <button type="submit" name="action" value="shippingBundle" class="btn btn-default btn-sm">Verknüpfung entfernen</button>
                    </form>
                </#if>
                <div class="collapse" id="shippingBundleCollapse">
                    <br><small>Wenn Du eine noch nicht versandte Bestellung oder Vorbestellung hast, kannst Du beide Bestellungen zusammen legen und Versandkosten sparen.
                    Das Paket wird dann zusammen verschickt sobald alle Artikel vorliegen.</small>
                    <br>
                    <br>
                    <form class="" action="/shop/my-cart/shippingBundle" method="POST">
                        <input type="text" class="form-field" name="shippingBundleOrderId" placeholder="Bestell Nr" style="width: 150px">
                        <button type="submit" name="action" value="shippingBundle" class="btn btn-default btn-sm">Offene Bestellung suchen</button>
                    </form>
                </div>

                <br>
                <a data-toggle="collapse" href="#foreignShipping">Info zum Versand ins Ausland</a>
                <div class="collapse" id="foreignShipping">
                    <br><small>Die Versandkosten außerhalb Deutschlands werden nach der Bestellung gesondert berechnet. Bitte einfach bestellen und im Kommentarfeld nochmal
                    auf den Auslandsversand hinweisen. Wir melden uns dann mit den angepassten Versandkosten, so dass diese dann per Paypal oder Überweisung nachgezahlt
                    werden können.</small>
                </div>
            </div>
            <div class="cart-other-price">${cart.calculatedShippingCosts?string.currency}</div>
        </div>

        <div class="row cart-row no-image">
            <div class="cart-description">
                <form action="/shop/my-cart/voucher" method="POST">
                    <input type="text" class="form-field" name="voucherId" placeholder="Gutscheincode" style="width: 150px">
                    <button type="submit" name="action" value="removeArticle" class="btn btn-default btn-sm">Einlösen</button>
                </form>
            </div>
            <div class="cart-other-price onsale">
				<span class="cart-label">&nbsp;
				  <script>
                      if (window.location.search == '?invalidVoucher') {
                          document.write('Code ungültig');
                      }
                  </script>
		</span>
            </div>
        </div>
        <div class="row cart-bottom-row">
            <div class="cart-price-total">
                <h3>Summe: ${cart.totalPrice?string.currency}</h3>
            </div>
        </div>

        <div class="row cart-bottom-row">
            <div class="cart-order">
                <a class="btn btn-primary btn-lg" href="/shop/orderRegister">Jetzt bestellen &gt;&gt;</a>
            </div>
        </div>
    </#if>
    </div>

</uic-fragment>
</body>
</html>
