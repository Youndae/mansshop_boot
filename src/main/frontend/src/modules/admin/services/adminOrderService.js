import { AdminOrderApi } from '../api/adminOrderApi';
import { buildQueryString } from '../../../common/utils/queryStringUtils';
import { validateSearchType } from '../../../common/utils/paginationUtils';

export const getNewOrderList = async (page, keyword, searchType) => {
	const searchTypeValue = validateSearchType({keyword, searchType});
	const queryString = buildQueryString({
		page,
		keyword,
		searchType: searchTypeValue,
	});
	
	return await AdminOrderApi.getNewOrderList(queryString);
}

export const patchOrderStatus = async (orderId) =>
	await AdminOrderApi.patchOrderStatus(orderId);

export const getAllOrderList = async (page, keyword, searchType) => {
	const searchTypeValue = validateSearchType({keyword, searchType});
	const queryString = buildQueryString({
		page,
		keyword,
		searchType: searchTypeValue,
	});

	return await AdminOrderApi.getAllOrderList(queryString);
}