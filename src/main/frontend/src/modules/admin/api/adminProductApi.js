import { axiosEnhanced } from '../../../common/utils/axios/axiosEnhanced';

const BASE_URL = 'admin/product';

export const AdminProductApi = {
    getProductList: (queryString = '') =>
        axiosEnhanced.get(`${BASE_URL}${queryString}`),
	getProductClassificationList: () =>
		axiosEnhanced.get(`${BASE_URL}/classification`),
	postProduct: (formData) =>
		axiosEnhanced.post(`${BASE_URL}`, formData, {
			headers: { 'Content-Type': 'multipart/form-data' }
		}),
	getPatchProductData: (productId) =>
		axiosEnhanced.get(`${BASE_URL}/patch/${productId}`),
	patchProduct: (productId, formData) =>
		axiosEnhanced.patch(`${BASE_URL}/${productId}`, formData, {
			headers: { 'Content-Type': 'multipart/form-data' }
		}),
	getProductDetail: (productId) =>
		axiosEnhanced.get(`${BASE_URL}/detail/${productId}`),
	getProductStockList: (queryString = '') =>
		axiosEnhanced.get(`${BASE_URL}/stock${queryString}`),
	getProductListByClassification: (classificationName) =>
		axiosEnhanced.get(`${BASE_URL}/discount/select/${classificationName}`),
	setProductDiscount: (body) =>
		axiosEnhanced.patch(`${BASE_URL}/discount`, body),
	getDiscountProductList: (queryString = '') =>
		axiosEnhanced.get(`${BASE_URL}/discount${queryString}`),
}