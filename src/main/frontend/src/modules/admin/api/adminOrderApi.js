import { axiosEnhanced } from "../../../common/utils/axios/axiosEnhanced";

const BASE_URL = 'admin/order';

export const AdminOrderApi = {
	getNewOrderList: (queryString) => 
		axiosEnhanced.get(`${BASE_URL}/new${queryString}`),
	patchOrderStatus: (orderId) => 
		axiosEnhanced.patch(`${BASE_URL}/${orderId}`),
	getAllOrderList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}/all${queryString}`)
}