<html>
<head>
    <link uic-remove rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <title>Bilderbuch-Stoff.de - Bestellen</title>
</head>
<body>
<uic-fragment name="content">

    <#setting locale="de_DE">

        <div class="form-container container order-register">
        <h1>Bestellung</h1>

        <form autocomplete="off" method="GET" action="/shop/orderData">
          <section class="form-part">
            <h3><small>Zur Dateneingabe</small></h3>
            <ul class="form-fields">
              <li class="form-field-group">
                 <div class="form-field clearfix">
                  <input type="submit" value="Ohne Account bestellen &gt;&gt;" class="btn btn-lg  btn-primary"/>
                 </div>
              </li>
              <li class="form-field-group">
                  <div class="form-field">
                    <hr/>
                  </div>
              </li>
           </ul>
          </section>
        </form>

        <form method="POST" action="/login">
          <input type="hidden" name="forwardURL" value="/shop/orderData">
          <section class="form-part">
            <h3 style="max-width: 420px"><small>Wenn Du schon einen Account hast, logge Dich ein und Du musst Deine Daten nicht nochmal eingeben.</small></h3>
            <ul class="form-fields">
              <li class="form-field-group">
                 <div class="form-field">
                  <label for="username">E-Mail</label>
                  <input type="email" class="form-control" id="username" name="username" required/>
                 </div>
              </li>
              <li class="form-field-group">
                 <div class="form-field">
                  <label for="password" class="">Passwort</label>
                  <input type="password" class="form-control" id="password" name="password" required/>
                 </div>
              </li>
              <li class="form-field-group">
                 <div class="form-field">
                  <input type="submit" value="Login &gt;&gt;" class="btn btn-lg  btn-primary"/>
                 </div>
              </li>
           </ul>
          </section>
        </form>

        </div>

</uic-fragment>
</body>
</html>
