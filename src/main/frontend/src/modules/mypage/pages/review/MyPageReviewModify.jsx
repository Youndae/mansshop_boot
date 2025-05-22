import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { getPatchReviewData, patchReview } from '../../services/mypageReviewService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';

import MyPageSideNav from '../../components/MyPageSideNav';
import MyPageReviewWriteForm from '../../components/MyPageReviewWriteForm';

//리뷰 수정
function MyPageReviewModify() {
	const { reviewId } = useParams();

	const [inputData, setInputData] = useState('');
    const [productName, setProductName] = useState('');

    const navigate = useNavigate();

	useEffect(() => {
		const getReviewData = async() => {
			try {
				const res = await getPatchReviewData(reviewId);

				console.log('modify review res : ', res);

				setInputData(res.data.content);
                setProductName(res.data.productName);
			} catch (error) {
				console.log(error);
			}
		}

		getReviewData();
	}, [reviewId]);

	//input 입력 이벤트
	const handleInputOnChange = (e) => {
		setInputData(e.target.value);
	}

	//리뷰 수정 요청 이벤트
	const handleSubmit = async() => {
		try {
			const res = await patchReview(reviewId, inputData);

			if(res.data.message === RESPONSE_MESSAGE.OK)
				navigate('/my-page/review');
		} catch (error) {
			console.log(error);
		}
	}
	
	return (
		<div className="mypage">
            <MyPageSideNav/>
            <div className="mypage-content">
                <div className="mypage-qna-header">
                    <h1>리뷰 수정</h1>
                </div>
                <MyPageReviewWriteForm
                    productName={productName}
                    inputData={inputData}
                    handleInputOnChange={handleInputOnChange}
                    handleSubmit={handleSubmit}
                />
            </div>
        </div>
	)
}

export default MyPageReviewModify;