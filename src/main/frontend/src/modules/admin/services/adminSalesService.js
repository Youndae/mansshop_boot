import { AdminSalesApi } from '../api/adminSalesApi';
import { buildQueryString } from '../../../common/utils/queryStringUtils';
import { validateSearchType } from '../../../common/utils/paginationUtils';

export const getYearSalesData = async (year) => 
	await AdminSalesApi.getYearSalesData(year);

export const getMonthSalesData = async (date) =>
	await AdminSalesApi.getMonthSalesData(date);

export const getMonthDailySalesData = async (date) =>{

	const queryString = buildQueryString({
		term: date,
	});

	return await AdminSalesApi.getMonthDailySalesData(queryString);
}

export const getMonthClassificationSalesData = async (date, classificationName) => {
	const queryString = buildQueryString({
		term: date,
		classification: classificationName,
	});

	return await AdminSalesApi.getMonthClassificationSalesData(queryString);
}

export const getDailyOrderList = async (date, page) => {
	const queryString = buildQueryString({
		term: date,
		page: page,
	});

	return await AdminSalesApi.getDailyOrderList(queryString);
}

export const getProductSalesList = async (page, keyword) => {
	const queryString = buildQueryString({
		page: page,
		keyword: keyword,
	});

	return await AdminSalesApi.getProductSalesList(queryString);
}

export const getProductSalesDetail = async (productId) => 
	await AdminSalesApi.getProductSalesDetail(productId);