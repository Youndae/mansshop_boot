import React, {useEffect, useState} from 'react';
import {Link} from "react-router-dom";

import { axiosInstance } from "../../modules/customAxios";
import {numberComma} from "../../modules/numberCommaModule";

import '../css/main.css';

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

            await axiosInstance.get(`main/display/${thumb}`, {
                responseType: 'blob',
            })
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
                        originPrice: numberComma(data[i].originPrice),
                        discount: data[i].discount,
                        discountPrice: numberComma(data[i].discountPrice),
                        isSoldOut: data[i].isSoldOut
                    });
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
                                <ProductPrice data={image}/>
                            </div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}

function ProductPrice(props) {
    const { data } = props;

    let productName = <p className="product-name">{data.productName}</p>;
    if(data.isSoldOut)
        productName = <p className="product-name sold-out">{data.productName} (품절)</p>

    if(data.discount === 0) {
        return (
            <>
                {productName}
                <p className="product-price">{data.originPrice}원</p>
            </>

        )
    }else {
        return (
            <>
                {productName}
                <span className="discount-original-price">{data.originPrice}원</span>
                <span className="discount-percent">{data.discount}%</span>
                <span className="discount-price">{data.discountPrice}원</span>
            </>
        )
    }
}

export default MainContent;