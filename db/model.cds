namespace clouds.products;

using clouds.foundation as fnd from '@sap/cloud-samples-foundation';
using clouds.foundation.CodeList;

entity Products: fnd.BusinessObject {
	// general info
	key ID: String(36);
	name: localized String @(
		title: '{i18n>name}',
		Common.FieldControl: #Mandatory,
		Capabilities.SearchRestrictions.Searchable
	);
	description: localized String @(
		title: '{i18n>description}',
		Common.FieldControl: #Mandatory
	);
	category: Association to Categories @(
		title: '{i18n>category}',
		Common: {
			Text: {$value: category.name, "@UI.TextArrangement": #TextOnly},
			ValueList: {entity: 'Categories', type: #fixed},
			FieldControl: #Mandatory
		}
	);
	image: fnd.ImageURL;

	// price 
	price: Decimal(10, 3) @(
		title: '{i18n>pricePerUnit}',
		Measures.ISOCurrency: currency,
		Common.FieldControl: #Mandatory
	);
	currency: fnd.Currency @(
		title: '{i18n>currency}',
		Common.ValueList: {entity: 'Currencies', type: #fixed},
		Common.FieldControl: #Mandatory
	);
	priceRange: Association to PriceRanges @(
		title: '{i18n>priceRange}',
		Common: {
			Text: {$value: priceRange.name, "@UI.TextArrangement": #TextOnly},
			ValueList: {entity: 'PriceRanges', type: #fixed},
			ValueListWithFixedValues
		},
		Common.FieldControl: #ReadOnly
	);

	// dimensions & weight
	height: fnd.Measures.Length @(
		title: '{i18n>height}',
		Measures.Unit: dimensionUnit_code
	);
	width: fnd.Measures.Length @(
		title: '{i18n>width}',
		Measures.Unit: dimensionUnit_code
	);
	depth: fnd.Measures.Length @(
		title: '{i18n>depth}',
		Measures.Unit: dimensionUnit_code
	);
	weight: fnd.Measures.Weight @Measures.Unit: weightUnit_code;

	dimensionUnit: Association to fnd.Measures.Units.Lengths @(
		title: '{i18n>dimensionUnit}',
		Common: {
			Text: {$value: dimensionUnit.name, "@UI.TextArrangement": #TextOnly},
			ValueList: {entity: 'DimensionUnits', type: #fixed},
			ValueListWithFixedValues
		}
	);
	weightUnit: Association to fnd.Measures.Units.Weights @(
		title: '{i18n>weightUnit}',
		Common: {
			Text: {$value: weightUnit.name, "@UI.TextArrangement": #TextOnly},
			ValueList: {entity: 'WeightUnits', type: #fixed},
			ValueListWithFixedValues
		}
	);
	baseUnit: Association to fnd.Measures.Units.Bases @(
		title: '{i18n>baseUnit}',
		Common: {
			Text: {$value: baseUnit.name, "@UI.TextArrangement": #TextOnly},
			ValueList: {entity: 'BaseUnits', type: #fixed},
			FieldControl: #Mandatory
		}
	);

	// supply
	supplier: Association to Suppliers @(
		title: '{i18n>supplier}',
		Common: {
			Text: {$value: supplier.name, "@UI.TextArrangement": #TextOnly},
			ValueList: {entity: 'Suppliers', type: #fixed},
			FieldControl: #Mandatory
		}
	);
	stock: Association to Stocks @title: '{i18n>productStock}';
}

annotate Products with {
	ID @(
		title: '{i18n>product}', 
		Common: {
			SemanticObject: 'EPMProduct',
			Text: name
		},
		Core.Immutable
	);
}

entity Suppliers: fnd.BusinessPartner {
}

annotate Suppliers with {
	ID @title: '{i18n>supplier}';
	name @(
		title: '{i18n>supplierName}',
		Capabilities.SearchRestrictions.Searchable
	);
}

entity Categories: fnd.BusinessObject {
	name: localized String @(
		title: '{i18n>categoryName}',
		Common.FieldControl: #Mandatory
	);
	description: localized String @title: '{i18n>description}';
}

annotate Categories with {
	ID @title: '{i18n>category}';
}

entity Stocks: fnd.BusinessObject {
	quantity: Decimal(13, 3) @(
		title: '{i18n>quantity}',
		Common.FieldControl: #Mandatory
	);
	minimumQuantity: Decimal(13, 3) @title: '{i18n>minimumStock}';
	availability: Association to StockAvailabilities @(
		title: '{i18n>availability}',
		Common: {
			Text: {$value: availability.name, "@UI.TextArrangement": #TextOnly},
			ValueList: {entity: 'StockAvailabilities', type: #fixed},
			ValueListWithFixedValues
		},
		Common.FieldControl: #ReadOnly
	);
}

annotate Stocks with {
	ID @title: '{i18n>stock}';
}

entity StockAvailabilities: CodeList {
	key code: Integer @(
		title: '{i18n>availability}',
		description: '{i18n>availabilityIndicator}',
		Common.Text: {$value: name, "@UI.TextArrangement": #TextOnly}
	);
}

annotate StockAvailabilities with {
	name @title: '{i18n>availability}';
}

entity PriceRanges: CodeList {
	key code: Integer @(
		title: '{i18n>priceRange}',
		description: '{i18n>priceRangeIndicator}',
		Common.Text: {$value: name, "@UI.TextArrangement": #TextOnly}
	);
}

annotate PriceRanges with {
	name @title: '{i18n>priceRange}';
}
