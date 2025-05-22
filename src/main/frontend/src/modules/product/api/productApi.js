import { axiosEnhanced } from "../../../common/utils/axios/axiosEnhanced";

const BASE_URL = 'product/';

export const ProductApi = {
	getProductDetail: (productId) =>
		axiosEnhanced.get(`${BASE_URL}${productId}`),
	getOrderData: (selectData) => 
		axiosEnhanced.post(`order/product`, selectData),
	addCart: (addList) =>
		axiosEnhanced.post(`cart/`, addList),
	likeProduct: (productId) =>
		axiosEnhanced.post(`${BASE_URL}like`, {productId}),
	deLikeProduct: (productId) =>
		axiosEnhanced.delete(`${BASE_URL}like/${productId}`),
	getProductReview: (productId, queryString) =>
		axiosEnhanced.get(`${BASE_URL}${productId}/review${queryString}`),
	getProductQnA: (productId, queryString) =>
		axiosEnhanced.get(`${BASE_URL}${productId}/qna${queryString}`),
	postProductQnA: (productId, qnaInputValue) =>
		axiosEnhanced.post(`${BASE_URL}qna`, 
			{
				productId: productId,
				content: qnaInputValue,
			}),
}