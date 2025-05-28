import {AuthAPI} from "../api/authApi";

export const getMemberStatus = async () => {
    const res = await AuthAPI.getMemberStatus();
    console.log('memberStatus res : ', res);
    return res.data;
}

export const getReIssueToken = async () =>
    await AuthAPI.getReissueToken();