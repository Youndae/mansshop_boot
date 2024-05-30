import React, {useEffect, useState} from 'react';

import {imageDisplayAxios} from "../../module/customAxios";

import '../css/main.css';
import {Link} from "react-router-dom";

function MainContent(props) {
    const { data, classification } = props;
    const [imageList, setImageList] = useState([]);

    useEffect(() => {
        getImageDisplay();
    }, [data]);

    const getImageDisplay = async() => {
        const imageArr = [];
        for(let i = 0; i < data.length; i++){
            const thumb = data[i].thumbnail;
            await imageDisplayAxios.get(`display/${thumb}`)
                .then(res => {
                    const url = window
                        .URL
                        .createObjectURL(
                            new Blob([res.data], {type: res.headers['content-type']})
                        );
                    imageArr.push({
                        productId: data[i].productId,
                        thumbnail: url,
                        productName: data[i].productName,
                        productPrice: data[i].productPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
                    });
                })
                .catch(err => {
                    console.error('display request error : ', err);
                })
        }

        setImageList(imageArr);
    }

    return (
        <div className="content product-main">
            <div className="product-header">
                <h1>{classification}</h1>
            </div>
            <div className="product-content">
                {imageList.map((image, index) => {
                    return (
                        <div key={index} className="product-img">
                            <div className="thumb-image">
                                <Link to={`/product/${image.productId}`}>
                                    <img className={'image-data'} src={image.thumbnail}  alt={''}/>
                                </Link>
                            </div>
                            <div className="product-info">
                                <span className="product-name">{image.productName}</span><br />
                                <span className="product-price">{image.productPrice} 원</span>
                            </div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}

export default MainContent;