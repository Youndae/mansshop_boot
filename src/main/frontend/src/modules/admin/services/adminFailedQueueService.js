import { AdminFailedQueueApi } from '../api/adminFailedQueueApi';

export const getFailedQueueList = async () =>
	await AdminFailedQueueApi.getFailedQueueList();

export const retryDLQMessages = async (data) =>
	await AdminFailedQueueApi.retryDLQMessages(data);