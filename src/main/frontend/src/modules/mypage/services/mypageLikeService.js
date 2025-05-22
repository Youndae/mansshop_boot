import { MyPageLikeApi } from '../api/mypageLikeApi';
import { ProductApi } from '../../product/api/productApi';

import { buildQueryString } from '../../../common/utils/queryStringUtils';

export const getLikeProductList = async (page) => {
	const queryString = buildQueryString({ page });

	return await MyPageLikeApi.getLikeProductList(queryString);
}
	

export const deLikeProduct = async (productId) => 
	await ProductApi.deLikeProduct(productId);

export const getMemberQnAList = async (page) => {
	const queryString = buildQueryString({ page });

	return await MyPageLikeApi.getMemberQnAList(queryString);
}

