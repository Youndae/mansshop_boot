import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";

import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import MyPageReviewWriteForm from "./MyPageReviewWriteForm";

//리뷰 수정
function MyPageReviewModify() {
    const { reviewId } = useParams();

    const [inputData, setInputData] = useState('');
    const [productName, setProductName] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        getPatchReview();
    }, [reviewId])

    //수정할 리뷰 데이터 조회
    const getPatchReview = async () => {
        await axiosInstance.get(`my-page/review/modify/${reviewId}`)
            .then(res => {
                setInputData(res.data.content.content);
                setProductName(res.data.content.productName);
            })
    }

    //input 입력 이벤트
    const handleInputOnChange = (e) => {
        setInputData(e.target.value);
    }

    //리뷰 수정 요청 이벤트
    const handleSubmit = async () => {
        await axiosInstance.patch(`my-page/review`, {
            reviewId: reviewId
            , content: inputData
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    navigate('/my-page/review');
            })
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