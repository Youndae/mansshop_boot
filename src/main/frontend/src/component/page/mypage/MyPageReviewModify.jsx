import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";
import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import DefaultBtn from "../../ui/DefaultBtn";
import MyPageReviewWriteForm from "./MyPageReviewWriteForm";

function MyPageReviewModify() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const { reviewId } = useParams();
    const [inputData, setInputData] = useState('');
    const [productName, setProductName] = useState('');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getPatchReview();
    }, [reviewId])

    const getPatchReview = async () => {
        await axiosInstance.get(`my-page/review/modify/${reviewId}`)
            .then(res => {
                setInputData(res.data.content.content);
                setProductName(res.data.content.productName);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('getModifyReview error : ', err);
            })
    }

    const handleInputOnChange = (e) => {
        setInputData(e.target.value);
    }

    const handleSubmit = async () => {
        await axiosInstance.patch(`my-page/review`, {
            reviewId: reviewId
            , content: inputData
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    navigate('/my-page/review');
            })
            .catch(err => {
                console.error('reviewModify error : ', err);
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