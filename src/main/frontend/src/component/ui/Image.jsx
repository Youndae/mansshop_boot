import React, {useEffect, useState} from 'react';
import {axiosInstance} from "../../modules/customAxios";

/*
    이미지 출력 폼
    UI 보다도 동일한 요청으로 이미지 데이터를 처리하기 때문에 그것을 분리하기 위함.
 */
function Image(props) {
    const { imageName, className } = props;
    const [imgSrc, setImgSrc] = useState('');

    useEffect(() => {
        if(imageName !== '')
            getImageDisplay();
    }, [imageName]);

    //이미지 데이터 요청
    const getImageDisplay = async () => {

        await axiosInstance.get(`main/display/${imageName}`, {
            responseType: 'blob',
        })
            .then(res => {
                const url = window.URL
                    .createObjectURL(res.data);

                setImgSrc(url);
            })
    }

    return (
        <img src={imgSrc} alt={''} className={className}/>
    )
}

export default Image;