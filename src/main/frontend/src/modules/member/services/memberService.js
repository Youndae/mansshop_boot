import { MemberApi } from '../api/memberApi';
import { buildQueryString } from '../../../common/utils/queryStringUtils';

export const postLogout = async () => 
	await MemberApi.postLogout();

export const postLogin = async (userData) =>
	await MemberApi.postLogin(userData);

export const postJoin = async (userData, userEmail, userBirth) =>
	await MemberApi.postJoin(userData, userEmail, userBirth);

export const getUserIdCheck = async (userId) =>
	await MemberApi.getUserIdCheck(userId);

export const getNicknameCheck = async (nickname) =>
	await MemberApi.getNicknameCheck(nickname);

export const tokenRequest = async () =>
	await MemberApi.tokenRequest();

export const getSearchId = async (username, type, value) =>{
	const queryString = buildQueryString({
		username: username,
		[type]: value,
	});

	return await MemberApi.getSearchId(queryString);
}

export const getSearchPw = async (userId, username, email) => {
	const queryString = buildQueryString({
		id: userId,
		name: username,
		email: email,
	});

	return await MemberApi.getSearchPw(queryString);
}

export const postCertification = async (userId, certification) => {
	const queryString = buildQueryString({
		userId: userId,
		certification: certification,
	});

	return await MemberApi.postCertification(queryString);
}

export const postResetPassword = async (userId, certification, userPw) => 
	await MemberApi.postResetPassword(userId, certification, userPw);
