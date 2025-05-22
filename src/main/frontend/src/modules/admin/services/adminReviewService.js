import { AdminReviewApi } from '../api/adminReviewApi';
import { buildQueryString } from '../../../common/utils/queryStringUtils';
import { validateSearchType } from '../../../common/utils/paginationUtils';

export const getNewReviewList = async (page, keyword, searchType) => {
	const searchTypeValue = validateSearchType({keyword, searchType});
	const queryString = buildQueryString({
		page,
		keyword,
		searchType: searchTypeValue,
	});

	return await AdminReviewApi.getNewReviewList(queryString);
}

export const getAllReviewList = async (page, keyword, searchType) => {
	const searchTypeValue = validateSearchType({keyword, searchType});
	const queryString = buildQueryString({
		page,
		keyword,
		searchType: searchTypeValue,
	});

	return await AdminReviewApi.getAllReviewList(queryString);
}

export const getReviewDetail = async (reviewId) => 
	await AdminReviewApi.getReviewDetail(reviewId);

export const postReply = async (reviewId, replyContent) => {
	const body = {
		reviewId: reviewId,
		content: replyContent,
	}

	return await AdminReviewApi.postReply(body);
}