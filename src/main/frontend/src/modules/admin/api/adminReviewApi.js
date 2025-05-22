import { axiosEnhanced } from "../../../common/utils/axios/axiosEnhanced";

const BASE_URL = 'admin/review';

export const AdminReviewApi = {
	getNewReviewList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}${queryString}`),
	getAllReviewList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}/all${queryString}`),
	getReviewDetail: (reviewId) =>
		axiosEnhanced.get(`${BASE_URL}/detail/${reviewId}`),
	postReply: (body) =>
		axiosEnhanced.post(`${BASE_URL}/reply`, body)
}