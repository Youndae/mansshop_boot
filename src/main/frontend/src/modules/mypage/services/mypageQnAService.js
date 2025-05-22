import { MyPageQnAApi } from '../api/mypageQnAApi';

import { buildQueryString } from '../../../common/utils/queryStringUtils';

export const getMemberQnAList = async (page) => {
	const queryString = buildQueryString({ page });

	return await MyPageQnAApi.getMemberQnAList(queryString);
}

export const getProductQnAList = async (page) => {
	const queryString = buildQueryString({ page });

	return await MyPageQnAApi.getProductQnAList(queryString);
}

export const getProductQnADetail = async (qnaId) => 
	await MyPageQnAApi.getProductQnADetail(qnaId);


export const deleteProductQnA = async (qnaId) => 
	await MyPageQnAApi.deleteProductQnA(qnaId);

export const getMemberQnADetail = async (qnaId) =>
	await MyPageQnAApi.getMemberQnADetail(qnaId);

export const deleteMemberQnA = async (qnaId) =>
	await MyPageQnAApi.deleteMemberQnA(qnaId);

export const modifyReply = async (replyId, replyContent) =>{
	const body = {
		replyId: replyId,
		content: replyContent
	}

	return await MyPageQnAApi.modifyReply(body);
}
	

export const postReply = async (memberQnAId, replyContent) =>{
	const body = {
		qnaId: memberQnAId,
		content: replyContent
	}
	return await MyPageQnAApi.postReply(body);
}
	
export const getMemberQnAPatchData = async (qnaId) =>
	await MyPageQnAApi.getMemberQnAPatchData(qnaId);

export const patchMemberQnA = async (qnaId, inputData, classificationId) => {
	const body = {
		qnaId: qnaId,
		title: inputData.title,
		content: inputData.content,
		classificationId: classificationId
	}

	return await MyPageQnAApi.patchMemberQnA(body);
}

export const getQnAClassificationList = async () =>
	await MyPageQnAApi.getQnAClassificationList();

export const postMemberQnA = async (inputData, classificationId) => {
	const body = {
		title: inputData.title,
		content: inputData.content,
		classificationId: classificationId
	}

	return await MyPageQnAApi.postMemberQnA(body);
}

