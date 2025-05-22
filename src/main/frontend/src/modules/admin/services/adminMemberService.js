import { AdminMemberApi } from '../api/adminMemberApi';
import { buildQueryString } from '../../../common/utils/queryStringUtils';
import { validateSearchType } from '../../../common/utils/paginationUtils';

export const getMemberList = async (page, keyword, searchType) => {
	const searchTypeValue = validateSearchType({keyword, searchType});
	const queryString = buildQueryString({
		page,
		keyword,
		searchType: searchTypeValue,
	});

	return await AdminMemberApi.getMemberList(queryString);
}

export const postPoint = async (userId, point) => {
	const body = {
		userId: userId,
		point: point,
	}

	return await AdminMemberApi.postPoint(body);
}