import { axiosSimple } from "../../../common/utils/axios/axiosSimple";

const ORDER_BASE_URL = 'order';

export const OrderApi = {
	postPayment: (val) =>
		axiosSimple.post(`payment/iamport/${val}`),
	postOrderData: (body) =>
		axiosSimple.post(`${ORDER_BASE_URL}/`, body),
	validateOrderData: (body) =>
		axiosSimple.post(`${ORDER_BASE_URL}/validate`, body),
}