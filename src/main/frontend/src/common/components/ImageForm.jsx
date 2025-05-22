import React, { useEffect, useState } from 'react';

import { getImageData } from '../services/imageService';

function ImageForm(props) {
	const { imageName, className } = props;
	const [imageSrc, setImageSrc] = useState('');

	useEffect(() => {
		const getImageDisplay = async() => {
			try{
				const res = await getImageData(imageName);
				setImageSrc(res);
			}catch(err) {
				console.log(err);
			}
		}

		if(imageName !== '')
			getImageDisplay();
	}, [imageName]);

	return (
        <img src={imageSrc} alt={''} className={className}/>
    )
}

export default ImageForm;