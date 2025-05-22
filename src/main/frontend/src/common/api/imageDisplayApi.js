import { axiosEnhanced } from '../utils/axios/axiosEnhanced';

export const ImageDisplayApi = {
    getImageData: (thumbnail) =>
        axiosEnhanced.get(
            `main/display/${thumbnail}`,
            { responseType: 'blob' }
        ),
}