import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

import { postReview } from '../../services/mypageReviewService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';

import MyPageSideNav from '../../components/MyPageSideNav';
import MyPageReviewWriteForm from '../../components/MyPageReviewWriteForm';

//리뷰 작성
function MyPageReviewWrite() {
	const location = useLocation();
    const state = location.state;

    const [productName, setProductName] = useState('');
    const [inputData, setInputData] = useState('');

    const navigate = useNavigate();
	
	useEffect(() => {
		setProductName(state.productName);
	}, [state]);

	//내용 input 입력 이벤트
	const handleInputOnChange = (e) => {
		setInputData(e.target.value);
	}

	//리뷰 작성 요청 이벤트
	const handleSubmit = async() => {
		try {
			const res = await postReview(state, inputData);

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

export default MyPageReviewWrite;