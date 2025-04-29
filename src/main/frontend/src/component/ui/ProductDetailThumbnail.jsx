import React, {useState, useEffect} from 'react';

import {axiosInstance} from "../../modules/customAxios";

/*
    상품 상세 페이지에서 썸네일 폼
    MouseOver 시 상단의 대표 이미지 위치에 해당 이미지를 출력하는 이벤트 발생
 */
function ProductDetailThumbnail(props) {
    const { imageName } = props;
    const [firstThumb, setFirstThumb] = useState('');
    const [thumbnail, setThumbnail] = useState([]);

    useEffect(() => {
        getDisplayImage(imageName);
    }, [imageName]);

    const getDisplayImage = async (imageName) => {
        let imageSrcArr = [];

        if(imageName.length !== 0){
            for(let i = 0; i < imageName.length; i++) {
                await axiosInstance.get(`main/display/${imageName[i]}`, {
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    responseType: 'blob',
                })
                    .then(res => {
                        const url = window.URL.createObjectURL(
                            new Blob ([res.data], { type: res.headers['content-type']})
                        );

                        imageSrcArr.push(url);
                    })
            }

            setThumbnail(imageSrcArr);
        }

        setFirstThumb(imageSrcArr[0]);
    }

    //MouseOver 발생 시 대표 썸네일 위치의 이미지를 해당 썸네일로 변경하는 이벤트
    const handleThumbnailOnClick = (e) => {
        const idx = e.target.name;

        setFirstThumb(thumbnail.at(idx));
    }

    return (
        <>
            <div className="product-thumbnail product-detail-first-thumbnail">
                <div className="first-thumbnail">
                    <img src={firstThumb} alt={''}/>
                </div>
                <div className="thumbnail product-detail-thumbnail-list">
                    {thumbnail.map((image, index) => {
                        return(<img key={index} name={index} src={image} alt={''} onMouseOver={handleThumbnailOnClick}/>)
                    })}
                </div>
            </div>
        </>
    );

}

export default ProductDetailThumbnail;
