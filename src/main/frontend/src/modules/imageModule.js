import {axiosInstance} from "./customAxios";


export const getImageSrc = (res) => {

    return window
        .URL
        .createObjectURL(
            new Blob([res.data], {type: res.headers['content-type']})
        );
}

export const imageDisplay = async (imageName) => {
    await axiosInstance.get(`main/display/${imageName}`, {
        responseType: 'blob',
    });
}