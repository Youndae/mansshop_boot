import { buildQueryString } from "../../../common/utils/queryStringUtils";
import { MainApi } from "../api/mainApi";

export const getBestProductList = async () => 
	await MainApi.getBestProductList();

export const getNewProductList = async () =>
	await MainApi.getNewProductList();

export const getClassificationList = async (classification, page) => {
	const queryString = buildQueryString({ page });
	return MainApi.getClassificationList(classification, queryString);
}

export const getOrderList = async (page, term, recipient, phone) => {
	const queryString = buildQueryString({ page });
	return MainApi.getOrderList(queryString, term, recipient, phone);
}

export const getSearchProductList = async (page, keyword) => {
	const queryString = buildQueryString({ page, keyword });
	return MainApi.getSearchProductList(queryString);
}