import { axiosEnhanced} from "../utils/axios/axiosEnhanced";

export const AuthAPI = {
    getMemberStatus: () =>
        axiosEnhanced.get('member/status'),
}