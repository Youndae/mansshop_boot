import { axiosEnhanced } from "../../../common/utils/axios/axiosEnhanced";

const BASE_URL = 'admin/message/';

export const AdminFailedQueueApi = {
	getFailedQueueList: () =>
		axiosEnhanced.get(`${BASE_URL}`),
	retryDLQMessages: (data) =>
		axiosEnhanced.post(`${BASE_URL}`, data),
}