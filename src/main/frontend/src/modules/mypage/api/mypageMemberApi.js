import { axiosEnhanced } from '../../../common/utils/axios/axiosEnhanced';

const BASE_URL = 'my-page/';

export const MyPageMemberApi = {
	getUserData: async () => 
		axiosEnhanced.get(`${BASE_URL}info`),
	patchUserData: async (body) => 
		axiosEnhanced.patch(`${BASE_URL}info`, body),
}