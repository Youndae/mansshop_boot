import { axiosSimple } from '../../../common/utils/axios/axiosSimple';
import { axiosEnhanced } from '../../../common/utils/axios/axiosEnhanced';

const BASE_URL = 'member/';

export const MemberApi = {
	postLogout: () =>
		axiosSimple.post(`${BASE_URL}logout`),
	postLogin: (userData) =>
		axiosSimple.post(
			`${BASE_URL}login`, 
			{
				userId: userData.userId,
				userPw: userData.userPw,
			}
	),
	postJoin: (userData, userEmail, userBirth) =>
		axiosSimple.post(
			`${BASE_URL}join`,
			{
				userId: userData.userId,
                userPw: userData.userPw,
                userName: userData.userName,
                nickname: userData.nickname,
                phone: userData.phone,
                birth: userBirth,
                userEmail: userEmail,
			}
	),
	getUserIdCheck: (userId) =>
		axiosSimple.get(`${BASE_URL}check-id/?userId=${userId}`),
	getNicknameCheck: (nickname) =>
		axiosSimple.get(`${BASE_URL}check-nickname?nickname=${nickname}`),
	tokenRequest: () =>
		axiosEnhanced.get(`${BASE_URL}oAuth/token`),
	getSearchId: (queryString) =>
		axiosSimple.get(`${BASE_URL}search-id${queryString}`),
	getSearchPw: (queryString) =>
		axiosSimple.get(`${BASE_URL}search-pw${queryString}`),
	postCertification: (queryString) =>
		axiosSimple.post(`${BASE_URL}certification${queryString}`),
	postResetPassword: (userId, certification, userPw) =>
		axiosSimple.post(`${BASE_URL}reset-pw`, {
			userId: userId,
			certification: certification,
			userPw: userPw,
		}),
}