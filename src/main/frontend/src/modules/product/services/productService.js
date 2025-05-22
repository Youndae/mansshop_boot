import { ProductApi } from "../api/productApi";
import { buildQueryString } from "../../../common/utils/queryStringUtils";

export const getProductDetail = async (productId) => {
	const res = await ProductApi.getProductDetail(productId);
	return res.data;
}
	

export const getOrderData = async (selectData) => {
	const res = await ProductApi.getOrderData(selectData);
	return res.data;
}

export const addCart = async (addList) => 
	await ProductApi.addCart(addList);

export const likeProduct = async (productId) => {
	// const pid = JSON.stringify(productId);
	return await ProductApi.likeProduct(productId);
}

export const deLikeProduct = async (productId) => 
	await ProductApi.deLikeProduct(productId);

export const getProductReview = async (productId, page) => {
	const queryString = buildQueryString({ page });
	return await ProductApi.getProductReview(productId, queryString);
}

export const getProductQnA = async (productId, page) => {
	const queryString = buildQueryString({ page });
	return await ProductApi.getProductQnA(productId, queryString);
}

export const postProductQnA = async (productId, qnaInputValue) => 
	await ProductApi.postProductQnA(productId, qnaInputValue);
