import { axiosEnhanced } from '../../../common/utils/axios/axiosEnhanced';

const BASE_URL = 'my-page/qna/';

export const MyPageQnAApi = {
	getMemberQnAList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}member${queryString}`),
	getProductQnAList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}product${queryString}`),
	getProductQnADetail: (qnaId) =>
		axiosEnhanced.get(`${BASE_URL}product/detail/${qnaId}`),
	deleteProductQnA: (qnaId) =>
		axiosEnhanced.delete(`${BASE_URL}product/${qnaId}`),
	getMemberQnADetail: (qnaId) =>
		axiosEnhanced.get(`${BASE_URL}member/detail/${qnaId}`),
	deleteMemberQnA: (qnaId) =>
		axiosEnhanced.delete(`${BASE_URL}member/${qnaId}`),
	modifyReply: (body) =>
		axiosEnhanced.patch(`${BASE_URL}member/reply`, body),
	postReply: (body) =>
		axiosEnhanced.post(`${BASE_URL}member/reply`, body),
	getMemberQnAPatchData: (qnaId) =>
		axiosEnhanced.get(`${BASE_URL}member/modify/${qnaId}`),
	patchMemberQnA: (body) =>
		axiosEnhanced.patch(`${BASE_URL}member`, body),
	getQnAClassificationList: () =>
		axiosEnhanced.get(`${BASE_URL}classification`),
	postMemberQnA: (body) =>
		axiosEnhanced.post(`${BASE_URL}member`, body)
}