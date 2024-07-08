import React, {useEffect, useState} from 'react';
import {axiosInstance} from "../../modules/customAxios";

function Image(props) {
    const { imageName, className } = props;
    const [imgSrc, setImgSrc] = useState('');

    useEffect(() => {
        if(imageName !== '')
            getImageDisplay();
    }, [imageName]);

    const getImageDisplay = async () => {

        await axiosInstance.get(`main/display/${imageName}`, {
            responseType: 'blob',
        })
            .then(res => {
                const url = window
                    .URL
                    .createObjectURL(
                        new Blob([res.data], {type: res.headers['content-type']})
                    );

                setImgSrc(url);
            })
    }

    return (
        <img src={imgSrc} alt={''} className={className}/>
    )
}

export default Image;