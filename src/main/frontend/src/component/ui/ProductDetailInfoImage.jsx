import React, {useState, useEffect} from 'react';

import { imageDisplayAxios } from "../../modules/customAxios";

function ProductDetailInfoImage(props) {
    const { imageInfo } = props;
    const [imgSrc, setImgSrc] = useState([]);

    useEffect(() => {
        getDisplayImage(imageInfo);
    }, [imageInfo]);

    const getDisplayImage = async (imageInfo) => {
        let imgSrcArr = [];

        if(imageInfo.length !== 0) {
            for(let i = 0; i < imageInfo.length; i++){
                await imageDisplayAxios.get(`display/${imageInfo[i]}`)
                    .then(res => {
                        const url = window.URL.createObjectURL(
                            new Blob ([res.data], { type: res.headers['content-type']})
                        );

                        imgSrcArr.push(url);
                    })
                    .catch(err => {
                        console.log('display axios err : ', err);
                    })
            }
            setImgSrc(imgSrcArr);
        }
    }

    return (
        <>
            {imgSrc.map((image, index) => {
                return (
                    <div key={index} className={'info-image-div'}>
                        <img className={'info-image'} alt={''} src={image}/>
                    </div>
                )
            })}
        </>

    )
}

export default ProductDetailInfoImage;