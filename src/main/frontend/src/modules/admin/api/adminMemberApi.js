import { axiosEnhanced } from "../../../common/utils/axios/axiosEnhanced";

const BASE_URL = 'admin/member';

export const AdminMemberApi = {
	getMemberList: (queryString) =>
		axiosEnhanced.get(`${BASE_URL}${queryString}`),
	postPoint: (body) =>
		axiosEnhanced.patch(`${BASE_URL}/point`, body),
}