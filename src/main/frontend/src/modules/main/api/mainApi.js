import { axiosEnhanced } from "../../../common/utils/axios/axiosEnhanced";

const BASE_URL = 'main/';

export const MainApi = {
	getBestProductList: () =>
		axiosEnhanced.get(`${BASE_URL}`),
	getNewProductList: () =>
		axiosEnhanced.get(`${BASE_URL}new`),
	getClassificationList: (classification, queryString) =>
		axiosEnhanced.get(`${BASE_URL}${classification}${queryString}`),
	getOrderList: (queryString, term, recipient, phone) =>
		axiosEnhanced.get(
			`${BASE_URL}order/${term}${queryString}`,
			{ params: { recipient, phone } }
		),
	getSearchProductList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}search${queryString}`),
}
