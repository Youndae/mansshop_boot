import { axiosEnhanced } from '../../../common/utils/axios/axiosEnhanced';

const BASE_URL = 'my-page/order/';

export const MyPageOrderApi = {
	getOrderList: async (term, queryString) => 
		axiosEnhanced.get(`${BASE_URL}${term}${queryString}`)
}