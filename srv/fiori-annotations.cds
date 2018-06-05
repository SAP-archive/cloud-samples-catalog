using clouds.products.CatalogService as cats from './cat-service';

annotate cats.Products with @( // header-level annotations
// ---------------------------------------------------------------------------
// List Report
// ---------------------------------------------------------------------------
	// Filter Bar
	UI.SelectionFields: [ priceRange_code, stock.availability_code, category_ID ],

	// Product List
	UI: {
		LineItem: [
			{$Type: 'UI.DataField', Value: image, "@UI.Importance": #High},
			{$Type: 'UI.DataField', Value: ID, "@UI.Importance": #High},
			{$Type: 'UI.DataField', Value: category.name, "@UI.Importance": #Medium},
			{$Type: 'UI.DataFieldForAnnotation', Label: '{i18n>supplier}', Target: 'supplier/@Communication.Contact', "@UI.Importance": #Medium},
			{$Type: 'UI.DataField', Value: stock.availability.code, Criticality: stock.availability.code, "@UI.Importance": #High},
			{$Type: 'UI.DataField', Value: price, "@UI.Importance": #High}
		],
		PresentationVariant: {
			SortOrder: [ {$Type: 'Common.SortOrderType', Property: name, Descending: false} ]
		}
	},
// ---------------------------------------------------------------------------
// Object Page
// ---------------------------------------------------------------------------
	// Page Header
	UI: {
		HeaderInfo: {
			TypeName: '{i18n>product}',
			TypeNamePlural: '{i18n>product_Plural}',
			Title: {Value: name},
			Description: {Value: ID},
			ImageUrl: image
		},
		HeaderFacets: [
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>generalInformation}', Target: '@UI.FieldGroup#GeneralInformation', "@UI.Importance": #High},
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>description}', Target: '@UI.FieldGroup#ProductHeaderText', "@UI.Importance": #Medium},
			{$Type: 'UI.ReferenceFacet', Target: 'stock/@UI.DataPoint#StockAvailability', "@UI.Importance": #Medium},
			{$Type: 'UI.ReferenceFacet', Target: '@UI.DataPoint#Price', "@UI.Importance": #Medium}
		],
		FieldGroup#GeneralInformation: {
			Label: '{i18n>generalInformation}',
			Data: [
				{$Type: 'UI.DataField', Value: category_ID},
				{$Type: 'UI.DataField', Value: supplier_ID}
			]
		},
		FieldGroup#ProductHeaderText: {
			Label: '{i18n>description}',
			Data: [ {$Type: 'UI.DataField', Value: description} ]
		},
		DataPoint#Price: {
			Value: price,
			Title: '{i18n>price}'
		}
	},

	// Page Facets
	UI: {
		Facets: [
			{
				$Type: 'UI.CollectionFacet',
				ID: 'ProductDetails',
				Facets: [
					{$Type: 'UI.ReferenceFacet', Label: '{i18n>technicalData}', Target: '@UI.FieldGroup#TechnicalData'},
					{$Type: 'UI.ReferenceFacet', Label: '{i18n>administrativeData}', Target: '@UI.FieldGroup#AdministrativeData'},
				],
				Label: '{i18n>productDetails}'
			}
		],
		FieldGroup#TechnicalData: {
			Label: '{i18n>technicalData}',
			Data: [
				{$Type: 'UI.DataField', Value: baseUnit_code, "@UI.Importance": #High},
				{$Type: 'UI.DataField', Value: height, "@UI.Importance": #Medium},
				{$Type: 'UI.DataField', Value: width, "@UI.Importance": #Medium},
				{$Type: 'UI.DataField', Value: depth, "@UI.Importance": #Medium},
				{$Type: 'UI.DataField', Value: weight, "@UI.Importance": #Medium}
			]
		},
		FieldGroup#AdministrativeData: {
			Label: '{i18n>administrativeData}',
			Data: [
				{$Type: 'UI.DataField', Value: created_byUser, "@UI.Importance": #Medium},
				{$Type: 'UI.DataField', Value: created_at, "@UI.Importance": #Medium},
				{$Type: 'UI.DataField', Value: modified_byUser, "@UI.Importance": #Medium},
				{$Type: 'UI.DataField', Value: modified_at, "@UI.Importance": #Medium}
			]
		}
	}
){ // element-level annotations
	ID @UI.HiddenFilter;
	name @UI.HiddenFilter;
	description @UI.MultiLineText;
	created @UI.HiddenFilter;
	modified @UI.HiddenFilter;
	image @(
		UI: {
			IsImageURL,
			HiddenFilter
		}
	);
	dimensionUnit @UI.HiddenFilter;
	weightUnit @UI.HiddenFilter;
	baseUnit @UI.HiddenFilter;
}

annotate cats.Suppliers with @( // header-level annotations
	UI.Identification: [ { $Type: 'UI.DataField', Value: name } ]
){ // element-level annotations
	ID @UI.HiddenFilter;
	name @UI.HiddenFilter;
	emailAddress @UI.HiddenFilter;
	faxNumber @UI.HiddenFilter;
	phoneNumber @UI.HiddenFilter;
	created @UI.HiddenFilter;
	modified @UI.HiddenFilter;
}

annotate cats.Categories with @( // header-level annotations
	UI.Identification: [ { $Type: 'UI.DataField', Value: name } ]
){ // element-level annotations
	ID @UI.HiddenFilter;
	name @UI.HiddenFilter;
}

annotate cats.PriceRanges @( // header-level annotations
	UI.Identification: [ { $Type: 'UI.DataField', Value: name } ]
){ // element-level annotations
	code @UI.HiddenFilter;
	name @UI.HiddenFilter;
}

annotate cats.Stocks with @( // header-level annotations
	UI.DataPoint#StockAvailability: {
		Value: availability_code,
		Criticality: availability_code,
		Title: '{i18n>availability}'
	}
){ // element-level annotations
	ID @UI.HiddenFilter;
	quantity @UI.HiddenFilter;
	created @UI.HiddenFilter;
	modified @UI.HiddenFilter;
}

annotate cats.Currencies with @( // header-level annotations
	UI.Identification: [ { $Type: 'UI.DataField', Value: name } ]
){ // element-level annotations
}

annotate cats.DimensionUnits with @( // header-level annotations
	UI.Identification: [ { $Type: 'UI.DataField', Value: name } ]
){ // element-level annotations
}

annotate cats.WeightUnits with @( // header-level annotations
	UI.Identification: [ { $Type: 'UI.DataField', Value: name } ]
){ // element-level annotations
}

annotate cats.BaseUnits with @( // header-level annotations
	UI.Identification: [ { $Type: 'UI.DataField', Value: name } ]
){ // element-level annotations
}

annotate cats.StockAvailabilities with @( // header-level annotations
	UI.Identification: [ { $Type: 'UI.DataField', Value: name } ]
){ // element-level annotations
}
