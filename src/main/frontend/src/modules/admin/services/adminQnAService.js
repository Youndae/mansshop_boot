import { AdminQnAApi } from '../api/adminQnAApi';
import { buildQueryString } from '../../../common/utils/queryStringUtils';

export const getProductQnAList = async (page, keyword, type) => {
	const queryString = buildQueryString({
		page,
		keyword,
		type,
	});

	return await AdminQnAApi.getProductQnAList(queryString);
}

export const getProductQnADetail = async (qnaId) => 
	await AdminQnAApi.getProductQnADetail(qnaId);

export const patchProductQnAReply = async (replyId, replyContent) => {
	const body = {
		replyId: replyId,
		content: replyContent,
	}

	return await AdminQnAApi.patchProductQnAReply(body);
}

export const postProductQnAReply = async (qnaId, replyContent) => {
	const body = {
		qnaId: qnaId,
		content: replyContent,
	}

	return await AdminQnAApi.postProductQnAReply(body);
}

export const patchProductQnAComplete = async (qnaId) => 
	await AdminQnAApi.patchProductQnAComplete(qnaId);

export const getMemberQnAList = async (page, keyword, type) => {
	const queryString = buildQueryString({
		page,
		keyword,
		type,
	});

	return await AdminQnAApi.getMemberQnAList(queryString);
}

export const getMemberQnADetail = async (qnaId) => 
	await AdminQnAApi.getMemberQnADetail(qnaId);

export const patchMemberQnAReply = async (replyId, replyContent) => {
	const body = {
		replyId: replyId,
		content: replyContent,
	}

	return await AdminQnAApi.patchMemberQnAReply(body);
}

export const postMemberQnAReply = async (qnaId, replyContent) => {
	const body = {
		qnaId: qnaId,
		content: replyContent,
	}

	return await AdminQnAApi.postMemberQnAReply(body);
}

export const patchMemberQnAComplete = async (qnaId) => 
	await AdminQnAApi.patchMemberQnAComplete(qnaId);

export const getQnAClassificationList = async () =>
	await AdminQnAApi.getQnAClassificationList();

export const postQnAClassification = async (classificationName) =>{
	const body = JSON.stringify(classificationName);

	return await AdminQnAApi.postQnAClassification(body);
}
	

export const deleteQnAClassification = async (classificationName) =>
	await AdminQnAApi.deleteQnAClassification(classificationName);