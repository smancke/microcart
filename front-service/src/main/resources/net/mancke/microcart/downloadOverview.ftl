<!--# include virtual="/_header?title=Bestellung&page=orderConfirmation" -->

<#setting locale="de_DE">

    <div class="container">
	    <h1>Downloads für Bestellung ${cart.id[0..4]}</h1>

		<#if cart.containsDownloads()>
	    <br>
        <div class="container cart">
			<#list cart.positions as position>
				<#if position.type?? && position.type == "download">
					<div class="row cart-row">
						<div class="cart-image">
							<img src="${position.imageUrl}" alt="${position.title}" />
						</div>
						<div class="cart-description">
							<div class="cart-label">${position.title} <br>
								<#if cart.allowDownload>
									<a href="/shop/download/${cart.id}/${position.articleId}">&gt; Jetzt herunterladen</a>
								<#else>
								    Zahlungseingang noch nicht vermerkt.
								</#if>
							</div>
						</div>
					</div>
				</#if>
			</#list>
		</div>
		<#else>
            Die Bestellung beinhaltet keine Downloads.
		</#if>
	</div>

<!--# include virtual="/_footer" -->
