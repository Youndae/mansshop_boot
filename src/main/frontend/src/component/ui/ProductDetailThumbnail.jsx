import React, {useState, useEffect} from 'react';

import {imageDisplayAxios} from "../../modules/customAxios";

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
                await imageDisplayAxios.get(`display/${imageName[i]}`)
                    .then(res => {
                        const url = window.URL.createObjectURL(
                            new Blob ([res.data], { type: res.headers['content-type']})
                        );

                        imageSrcArr.push(url);
                    })
                    .catch(err => {
                        console.log('display axios err : ', err);
                    })
            }

            setThumbnail(imageSrcArr);
        }

        setFirstThumb(imageSrcArr[0]);
    }

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
