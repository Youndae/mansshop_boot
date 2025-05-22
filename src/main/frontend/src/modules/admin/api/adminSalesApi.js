import { axiosEnhanced } from "../../../common/utils/axios/axiosEnhanced";

const BASE_URL = 'admin/sales/';

export const AdminSalesApi = {
	getYearSalesData: (year) =>
		axiosEnhanced.get(`${BASE_URL}period/${year}`),
	getMonthSalesData: (date) =>
		axiosEnhanced.get(`${BASE_URL}period/detail/${date}`),
	getMonthDailySalesData: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}period/detail/day${queryString}`),
	getMonthClassificationSalesData: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}period/detail/classification${queryString}`),
	getDailyOrderList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}period/order-list${queryString}`),
	getProductSalesList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}product${queryString}`),
	getProductSalesDetail: (productId) =>
		axiosEnhanced.get(`${BASE_URL}product/detail/${productId}`),
}