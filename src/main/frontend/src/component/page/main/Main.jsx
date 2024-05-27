import React, {useEffect, useState} from 'react';

import dummyData from '../../db/mainDummy.json';
import {imageDisplayAxios} from "../../../module/customAxios";

import '../../css/main.css';

function Main() {

    const [imageList, setImageList] = useState([]);

    useEffect(() => {
        getImageDisplay();
    }, []);

    const getImageDisplay = async() => {
        const imageArr = [];

        for(let i = 0; i < dummyData.product.length; i++){
            const thumb = dummyData.product[i].thumbnail;

            await imageDisplayAxios.get(`/api/display/${thumb}`, {
                headers: {'Content-Type': 'application/json'},
                withCredentials: true,
                responseType: 'blob',
            })
                .then(res => {
                    const url = window
                        .URL
                        .createObjectURL(
                            new Blob([res.data], {type: res.headers['content-type']})
                        );

                    imageArr.push({
                        productId: dummyData.product[i].productId,
                        thumbnail: url,
                        productName: dummyData.product[i].productName,
                        productPrice: dummyData.product[i].productPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
                    });
                })
                .catch(err => {
                    console.error('display request error : ', err);
                })
        }

        setImageList(imageArr);


    }

    return (
        <div className="content">
            <div className="product-header">
                <h1>{dummyData.productClassification}</h1>
            </div>
            <div className="product-content">
                {imageList.map((image, index) => {
                    return (
                        <div className="product-img">
                            <div className="thumb-image">
                                <a href={'/product'} className={'productThumbnail'}>
                                    <img className={'image-data'} src={image.thumbnail}  alt={''}/>
                                </a>
                            </div>
                            <div className="product-info">
                                <span className="product-name">{image.productName}</span><br />
                                <span className="product-price">{image.productPrice} 원</span>
                            </div>
                        </div>
                    )
                })}
            </div>
            {/*paging*/}
        </div>
    )
}

export default Main;