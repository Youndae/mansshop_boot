import { axiosEnhanced } from '../../../common/utils/axios/axiosEnhanced';

const BASE_URL = 'my-page/review';

export const MyPageReviewApi = {
	getReviewList: async (queryString) => 
		axiosEnhanced.get(`${BASE_URL}${queryString}`),
	deleteReview: async (reviewId) => 
		axiosEnhanced.delete(`${BASE_URL}/${reviewId}`),
	getPatchReviewData: async (reviewId) => 
		axiosEnhanced.get(`${BASE_URL}/modify/${reviewId}`),
	patchReview: async (body) => 
		axiosEnhanced.patch(`${BASE_URL}`, body),
	postReview: async (body) => 
		axiosEnhanced.post(`${BASE_URL}`, body)
}