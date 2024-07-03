import React, {useEffect, useState} from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import MyPageReviewWriteForm from "./MyPageReviewWriteForm";

function MyPageReviewWrite() {
    const location = useLocation();
    const state = location.state;
    const [productName, setProductName] = useState('');
    const [inputData, setInputData] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        setProductName(state.productName);
    }, [state]);

    const handleInputOnChange = (e) => {
        setInputData(e.target.value);
    }

    const handleSubmit = async () => {
        await axiosInstance.post(`my-page/review`, {
            productId: state.productId
            , content: inputData
            , optionId: state.optionId
            , detailId: state.detailId
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    navigate('/my-page/review');
            })
            .catch(err => {
                console.error('review post error : ', err);
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

export default MyPageReviewWrite;