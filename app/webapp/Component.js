jQuery.sap.declare("product-catalog.Component");
sap.ui.getCore().loadLibrary("sap.ui.generic.app");
jQuery.sap.require("sap.ui.generic.app.AppComponent");

sap.ui.generic.app.AppComponent.extend("product-catalog.Component", {
	metadata: {
		"manifest": "json"
	}
});