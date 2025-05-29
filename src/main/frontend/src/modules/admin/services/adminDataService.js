import { AdminDataApi } from '../api/adminDataApi';

export const getFailedQueueList = async () =>
	await AdminDataApi.getFailedQueueList();

export const retryDLQMessages = async (data) =>
	await AdminDataApi.retryDLQMessages(data);

export const getFailedOrderDataList = async () =>
	await AdminDataApi.getFailedOrderDataList();

export const postRetryOrderData = async () =>
	await AdminDataApi.postRetryOrderData();