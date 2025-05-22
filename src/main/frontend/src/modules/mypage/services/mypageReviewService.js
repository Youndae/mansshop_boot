import { MyPageReviewApi } from '../api/mypageReviewApi';

import { buildQueryString } from '../../../common/utils/queryStringUtils';

export const getReviewList = async (page) => {
	const queryString = buildQueryString({ page });

	return await MyPageReviewApi.getReviewList(queryString);
}

export const deleteReview = async (reviewId) => 
	await MyPageReviewApi.deleteReview(reviewId);

export const getPatchReviewData = async (reviewId) => 
	await MyPageReviewApi.getPatchReviewData(reviewId);

export const patchReview = async (reviewId, inputData) => {
	const body = {
		reviewId: reviewId,
		content: inputData,
	}

	return await MyPageReviewApi.patchReview(body);
}

export const postReview = async (state, inputData) => {
	const body = {
		productId: state.productId,
		content: inputData,
		optionId: state.optionId,
		detailId: state.detailId,
	}

	return await MyPageReviewApi.postReview(body);
}
	