import { axiosEnhanced } from '../../../common/utils/axios/axiosEnhanced';

export const MyPageNotificationApi = {
	getNotificationList: (queryString) =>
		axiosEnhanced.get(`my-page/notification${queryString}`),
}