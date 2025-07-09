import { MyPageNotificationApi } from '../api/mypageNotificationApi';

import { buildQueryString } from '../../../common/utils/queryStringUtils';

export const getNotificationList = async (page) => {
	const queryString = buildQueryString({ page });

	return await MyPageNotificationApi.getNotificationList(queryString);
}