import { MyPageOrderApi } from '../api/mypageOrderApi';

import { buildQueryString } from '../../../common/utils/queryStringUtils';

export const getOrderList = async (term, page) => {
	const queryString = buildQueryString({ page });

	return await MyPageOrderApi.getOrderList(term, queryString);
}