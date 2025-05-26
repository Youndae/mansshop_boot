import {AuthAPI} from "../api/authApi";

import { setToken } from "../utils/axios/tokenUtils";

export const getMemberStatus = async () => {
    const res = await AuthAPI.getMemberStatus();

    console.log('status res : ', res);

    return res.data;
}

export const getReIssueToken = async () => {
    const res = await AuthAPI.getReissueToken();

    setToken(res);
}