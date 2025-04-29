import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";

import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import MemberQnAWriteForm from "../../ui/MemberQnAWriteForm";

// 회원 문의 작성 페이지
function MemberQnAWrite() {
    const [inputData, setInputData] = useState({
        title: '',
        content: '',
    });
    const [classification, setClassification] = useState([]);
    const [classificationId, setClassificationId] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        getQnAClassification();
    }, []);

    //문의 분류 리스트 조회
    const getQnAClassification = async () => {

        await axiosInstance.get('my-page/classification')
            .then(res => {
                setClassification(res.data.classificationList);
                setClassificationId(res.data.classificationList[0].id);
            })
    }

    //작성 이벤트
    const handleSubmit = async () => {

        await axiosInstance.post('my-page/qna/member', {
            title: inputData.title,
            content: inputData.content,
            classificationId: classificationId,
        })
            .then(res => {
                navigate(`/my-page/qna/member/detail/${res.data.id}`);
            })
    }

    //문의 분류 select box 이벤트
    const handleSelectOnChange = (e) => {
        const selectValue = e.target.value;
        setClassificationId(selectValue);
    }

    //제목 및 textarea 입력 이벤트
    const handleInputOnChange = (e) => {
        setInputData({
            ...inputData,
            [e.target.name]: e.target.value,
        })
    }

    return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
            <div className="mypage-content">
                <div className="mypage-qna-header">
                    <h1>문의하기</h1>
                </div>
                <MemberQnAWriteForm
                    inputData={inputData}
                    classificationId={classificationId}
                    classification={classification}
                    handleInputOnChange={handleInputOnChange}
                    handleSelectOnChange={handleSelectOnChange}
                    handleSubmit={handleSubmit}
                    btnText={'작성'}
                />
            </div>
        </div>
    )
}

export default MemberQnAWrite;