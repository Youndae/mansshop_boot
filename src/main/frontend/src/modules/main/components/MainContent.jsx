import React, { useEffect, useState} from 'react';
import { Link } from 'react-router-dom';

import { getImageData } from '../../../common/services/imageService';
import { numberComma } from '../../../common/utils/formatNumberComma';

import '../../../styles/main.css';

function MainContent(props) {
    const { data, classification } = props;
    const [imageList, setImageList] = useState([]);

    useEffect(() => {
        const setImageData = async() => {
            try{
                const imageArr = [];
                for(let i = 0; i < data.length; i++) {
                    const thumb = data[i].thumbnail;
                    const res = await getImageData(thumb);

                    imageArr.push({
                        productId: data[i].productId,
                        thumbnail: res,
                        productName: data[i].productName,
                        originPrice: numberComma(data[i].originPrice),
                        discount: data[i].discount,
                        discountPrice: numberComma(data[i].discountPrice),
                        isSoldOut: data[i].isSoldOut
                    })
                }
                setImageList(imageArr);
            }catch(err) {
                console.log(err);
            }
        }
        setImageData();
    }, [data]);


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

function ProductPrice({ data }) {
    const {
        productName,
        isSoldOut,
        discount,
        originPrice,
        discountPrice
    } = data;

    const productNameClass = `product-price ${isSoldOut ? 'sold-out' : ''}`;
    const displayName = isSoldOut ? `${productName} (품절)` : productName;
    const isDiscounted = discount > 0;

    return (
        <>
            <p className={productNameClass}>{displayName}</p>
            {isDiscounted ? (
                <>
                    <span className="discount-original-price">{originPrice}원</span>
                    <span className="discount-percent">{discount}%</span>
                    <span className="discount-price">{discountPrice}원</span>
                </>
            ) : (
                <p className="product-price">{originPrice}원</p>
            )}
        </>
    )
}

export default MainContent;