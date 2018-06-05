namespace clouds.products;

using clouds.products from '../db/model';
using clouds.foundation as fnd from '@sap/cloud-samples-foundation';

service CatalogService {
	entity Products @(
		title: '{i18n>product}',
		Common: {
			SemanticKey: [ID],
			SideEffects#BaseUnit: {
				SourceProperties: [baseUnit_code],
				TargetProperties: [baseUnit, modified_byUser, modified_at],
				EffectTypes: #ValueChange
			},
			SideEffects#DimensionUnit: {
				SourceProperties: [dimensionUnit_code],
				TargetProperties: [dimensionUnit, modified_byUser, modified_at],
				EffectTypes: #ValueChange
			},
			SideEffects#WeightUnit: {
				SourceProperties: [weightUnit_code],
				TargetProperties: [weightUnit, modified_byUser, modified_at],
				EffectTypes: #ValueChange
			},
			SideEffects#Category: {
				SourceProperties: [category_ID],
				TargetProperties: [category, modified_byUser, modified_at],
				EffectTypes: #ValueChange
			},
			SideEffects#Supplier: {
				SourceProperties: [supplier_ID],
				TargetProperties: [supplier, modified_byUser, modified_at],
				EffectTypes: #ValueChange
			},
			SideEffects#AdminData: {
				SourceProperties: [name, description, price, currency, height, width, depth, weight],
				TargetProperties: [modified_byUser, modified_at],
				EffectTypes: #ValueChange
			}
		}
	) as projection on products.Products;
	entity Categories @(title: '{i18n>category}') as projection on products.Categories;
	entity Suppliers @(
		title: '{i18n>supplier}',
		Communication.Contact: {
			fn: name,
			tel: [
				{type: #fax, uri: faxNumber},
				{type: #work, uri: phoneNumber}
			],
			email: [
				{type: #work, address: emailAddress}
			]}
	) as projection on products.Suppliers excluding { address };
	entity Stocks @(title: '{i18n>stock}') as projection on products.Stocks;

	// Value Helps --> should be autoexposed
	entity StockAvailabilities @(title: '{i18n>stockAvailability}') as projection on products.StockAvailabilities;
	entity PriceRanges @(title: '{i18n>priceRange}') as projection on products.PriceRanges;
	entity Currencies @(title: '{i18n>currency}') as projection on fnd.Currencies;
	entity DimensionUnits @(title: '{i18n>dimensionUnit}') as projection on fnd.Measures.Units.Lengths;
	entity WeightUnits @(title: '{i18n>weightUnit}') as projection on fnd.Measures.Units.Weights;
	entity BaseUnits @(title: '{i18n>baseUnit}') as projection on fnd.Measures.Units.Bases;
}