import { MyPageMemberApi } from '../api/mypageMemberApi';

export const getUserData = async () => 
	await MyPageMemberApi.getUserData();

export const patchUserData = async (userData, userEmail) => {
	const body = {
		nickname: userData.nickname,
		phone: userData.phone,
		mail: userEmail,
	}

	return await MyPageMemberApi.patchUserData(body);
}