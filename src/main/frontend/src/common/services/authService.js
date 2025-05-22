import {AuthAPI} from "../api/authApi";

export const getMemberStatus = async () => {
    const res = await AuthAPI.getMemberStatus();

    return res.data;
}