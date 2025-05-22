import { axiosEnhanced } from "../../../common/utils/axios/axiosEnhanced";

const BASE_URL = 'admin/qna';

export const AdminQnAApi = {
	getProductQnAList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}/product${queryString}`),
	getProductQnADetail: (qnaId) =>
		axiosEnhanced.get(`${BASE_URL}/product/${qnaId}`),
	patchProductQnAReply: (body) =>
		axiosEnhanced.patch(`${BASE_URL}/product/reply`, body),
	postProductQnAReply: (body) =>
		axiosEnhanced.post(`${BASE_URL}/product/reply`, body),
	patchProductQnAComplete: (qnaId) =>
		axiosEnhanced.patch(`${BASE_URL}/product/${qnaId}`),
	getMemberQnAList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}/member${queryString}`),
	getMemberQnADetail: (qnaId) =>
		axiosEnhanced.get(`${BASE_URL}/member/${qnaId}`),
	patchMemberQnAReply: (body) =>
		axiosEnhanced.patch(`${BASE_URL}/member/reply`, body),
	postMemberQnAReply: (body) =>
		axiosEnhanced.post(`${BASE_URL}/member/reply`, body),
	patchMemberQnAComplete: (qnaId) =>
		axiosEnhanced.patch(`${BASE_URL}/member/${qnaId}`),
	getQnAClassificationList: () =>
		axiosEnhanced.get(`${BASE_URL}/classification`),
	postQnAClassification: (body) =>
		axiosEnhanced.post(`${BASE_URL}/classification`, body),
	deleteQnAClassification: (classificationName) =>
		axiosEnhanced.delete(`${BASE_URL}/classification/${classificationName}`)
}