import { ImageDisplayApi } from '../api/imageDisplayApi';

export const getImageData = async(thumbnail) => {
    const res = await ImageDisplayApi.getImageData(thumbnail);

	return window.URL.createObjectURL(res.data);
}
