import { axiosEnhanced } from '../../../common/utils/axios/axiosEnhanced';


export const MyPageLikeApi = {
	getLikeProductList: (queryString) =>
		axiosEnhanced.get(`my-page/like${queryString}`),
}