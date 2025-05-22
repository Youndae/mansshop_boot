import React, { useEffect, useState } from 'react';

import { getImageData } from '../../../common/services/imageService';

/*
    상품 상세 페이지에서 썸네일 폼
    MouseOver 시 상단의 대표 이미지 위치에 해당 이미지를 출력하는 이벤트 발생
 */
function ProductDetailThumbnail(props) {
	const { imageName } = props;
	const [firstThumb, setFirstThumb] = useState('');
	const [thumbnail, setThumbnail] = useState([]);

	useEffect(() => {
		const getImage = async() => {
			try {
				let imageArr = [];
				if(imageName.length !== 0) {
					for(let i = 0; i < imageName.length; i++) {
						const url = await getImageData(imageName[i]);
						imageArr.push(url);
					}
					setThumbnail(imageArr);
				}

				setFirstThumb(imageArr[0]);
			}catch(err) {
				console.log(err);
			}
		}

		getImage();
	}, [imageName]);
	
	//MouseOver 발생 시 대표 썸네일 위치의 이미지를 해당 썸네일로 변경하는 이벤트
	const handleThumbnailOnClick = (e) => {
		const idx = e.target.name;

		setFirstThumb(thumbnail.at(idx));
	}

	return (
		<div className="product-thumbnail product-detail-first-thumbnail">
			<div className="first-thumbnail">
				<img src={firstThumb} alt="" />
			</div>
			<div className="thumbnail product-detail-thumbnail-list">
				{thumbnail.map((image, index) => {
					return (
						<img key={index} name={index} src={image} alt="" onMouseOver={handleThumbnailOnClick} />
					)
				})}
			</div>
		</div>
	);
}

export default ProductDetailThumbnail;