import { axiosEnhanced } from "../../../common/utils/axios/axiosEnhanced";

const BASE_URL = 'cart/';

export const CartApi = {
	getCartList: () =>
		axiosEnhanced.get(`${BASE_URL}`),
	increaseCartCount: (name) =>
		axiosEnhanced.patch(`${BASE_URL}count-up/${name}`),
	decreaseCartCount: (name) =>
		axiosEnhanced.patch(`${BASE_URL}count-down/${name}`),
	deleteSelectCartProduct: (selectValue) =>
		axiosEnhanced.delete(`${BASE_URL}select`, {data: selectValue}),
	deleteAllCartProduct: () =>
		axiosEnhanced.delete(`${BASE_URL}all`),
	getOrderProductInfo: (detailIds) =>
		axiosEnhanced.post(`order/cart`, detailIds)
}