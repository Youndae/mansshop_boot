import React, {useEffect, useState} from 'react';
import {axiosInstance} from "../../modules/customAxios";

function Image(props) {
    const { imageName, className } = props;
    const [imgSrc, setImgSrc] = useState('');

    useEffect(() => {
        if(imageName !== '')
            getImageDisplay();
    }, [imageName]);

    /*const getImageDisplay = async () => {

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
    }*/

    const getImageDisplay = async () => {

        await axiosInstance.get(`main/s3/display/${imageName}`, {
            responseType: 'blob',
        })
            .then(res => {
                console.log('res : ', res);
                const url = window.URL
                    .createObjectURL(res.data);

                console.log('url : ', url);

                setImgSrc(url);
            })
    }

    /*const getImageDisplay = async () => {

        await axiosInstance.get(`main/s3/display/${imageName}`)
            .then(res => {
                setImgSrc(res.data);
            })
    }*/

    return (
        <img src={imgSrc} alt={''} className={className}/>
    )
}

export default Image;